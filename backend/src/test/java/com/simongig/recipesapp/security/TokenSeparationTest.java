package com.simongig.recipesapp.security;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mongodb.client.MongoClient;
import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.model.UserRole;
import com.simongig.recipesapp.model.UserRole.RoleName;
import com.simongig.recipesapp.service.UserService;

/**
 * Access and refresh tokens share one signing key; these tests pin that each is only accepted
 * where it belongs. Runs the real security filter chain, with the database mocked away.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TokenSeparationTest {

    @MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
    private MongoClient mongoClient;
    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenService tokenService;

    private Map<String, String> tokens;

    @BeforeEach
    void setUp() {
        tokens = tokenService.issueTokens("alice", List.of(RoleName.ROLE_USER.name()));
        User alice = new User("alice", "Alice", "", "", "hash", List.of(new UserRole(RoleName.ROLE_USER)));
        when(userService.findByUsername("alice")).thenReturn(Optional.of(alice));
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    @Test
    void accessTokenAuthenticatesApiRequests() throws Exception {
        mockMvc.perform(get("/api/v1/user/me").header("Authorization", bearer(tokens.get("access_token"))))
                .andExpect(status().isOk());
    }

    @Test
    void refreshTokenIsRejectedAsBearerCredential() throws Exception {
        mockMvc.perform(get("/api/v1/user/me").header("Authorization", bearer(tokens.get("refresh_token"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshTokenIsExchangedForANewTokenPair() throws Exception {
        mockMvc.perform(post("/api/v1/auth/token/refresh")
                        .header("Authorization", bearer(tokens.get("refresh_token"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andExpect(jsonPath("$.refresh_token").isNotEmpty());
    }

    @Test
    void accessTokenCannotBeUsedToRefresh() throws Exception {
        mockMvc.perform(post("/api/v1/auth/token/refresh")
                        .header("Authorization", bearer(tokens.get("access_token"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void refreshForADeletedUserIsRejected() throws Exception {
        when(userService.findByUsername("alice")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/auth/token/refresh")
                        .header("Authorization", bearer(tokens.get("refresh_token"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void refreshWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/auth/token/refresh")).andExpect(status().isUnauthorized());
    }

    @Test
    void shortSecretsAreRefusedAtStartup() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> new JwtTokenService("too-short"));
        assertEquals(true, e.getMessage().contains("at least 32 bytes"));
    }
}
