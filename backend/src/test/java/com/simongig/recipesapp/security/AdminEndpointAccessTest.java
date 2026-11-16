package com.simongig.recipesapp.security;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mongodb.client.MongoClient;
import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.model.UserRole;
import com.simongig.recipesapp.model.UserRole.RoleName;
import com.simongig.recipesapp.service.UserService;
import com.simongig.recipesapp.service.UserService.UserOverview;

/**
 * User administration (listing users, creating accounts, granting roles) is for admins only.
 * Runs the real security filter chain, with the database mocked away.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminEndpointAccessTest {

    @MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
    private MongoClient mongoClient;
    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenService tokenService;

    static Stream<MockHttpServletRequestBuilder> adminRequests() {
        return Stream.of(
                get("/api/v1/users"),
                json(post("/api/v1/user/add"), """
                        {"username":"mallory","password":"long-enough-password"}"""),
                json(post("/api/v1/user/addRole"), """
                        {"username":"mallory","role":"ROLE_ADMIN"}"""),
                json(post("/api/v1/role/save"), """
                        {"name":"ROLE_ADMIN"}"""));
    }

    private static MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder request, String body) {
        return request.contentType(MediaType.APPLICATION_JSON).content(body);
    }

    private String bearerFor(RoleName role) {
        return "Bearer " + tokenService.issueTokens("caller", List.of(role.name())).get("access_token");
    }

    @ParameterizedTest
    @MethodSource("adminRequests")
    void anonymousCallersAreUnauthorized(MockHttpServletRequestBuilder request) throws Exception {
        mockMvc.perform(request).andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

    @ParameterizedTest
    @MethodSource("adminRequests")
    void regularUsersAreForbidden(MockHttpServletRequestBuilder request) throws Exception {
        mockMvc.perform(request.header("Authorization", bearerFor(RoleName.ROLE_USER)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    // No "admin is allowed" case for /user/addRole: its request body class cannot be
    // deserialized at all (non-static inner class), so the endpoint fails for every caller.
    @Test
    void adminCanSaveARole() throws Exception {
        mockMvc.perform(json(post("/api/v1/role/save"), """
                        {"name":"ROLE_EDITOR"}""")
                        .header("Authorization", bearerFor(RoleName.ROLE_ADMIN)))
                .andExpect(status().isOk());

        verify(userService).saveUserRole(any(UserRole.class));
    }

    @Test
    void adminCanCreateAnAccount() throws Exception {
        mockMvc.perform(json(post("/api/v1/user/add"), """
                        {"username":"bob","password":"long-enough-password"}""")
                        .header("Authorization", bearerFor(RoleName.ROLE_ADMIN)))
                .andExpect(status().isOk());

        verify(userService).save(any(User.class));
    }

    @Test
    void userListCarriesRolesButNoPassword() throws Exception {
        when(userService.listUsers()).thenReturn(List.of(
                new UserOverview("bob", "Bob", "Baker", "bob@example.org", List.of(RoleName.ROLE_USER))));

        mockMvc.perform(get("/api/v1/users").header("Authorization", bearerFor(RoleName.ROLE_ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("bob"))
                .andExpect(jsonPath("$[0].roles[0]").value("ROLE_USER"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    // The admin account carries only ROLE_ADMIN; it must keep access to its own profile.
    @ParameterizedTest
    @MethodSource("profileRoles")
    void ownProfileIsOpenToUsersAndAdmins(RoleName role) throws Exception {
        when(userService.getProfile()).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/user/me").header("Authorization", bearerFor(role)))
                .andExpect(status().isOk());
    }

    static Stream<RoleName> profileRoles() {
        return Stream.of(RoleName.ROLE_USER, RoleName.ROLE_ADMIN);
    }

    @Test
    void ownProfileNeedsALogin() throws Exception {
        mockMvc.perform(get("/api/v1/user/me")).andExpect(status().isUnauthorized());
    }
}
