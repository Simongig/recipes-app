package com.simongig.recipesapp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongig.recipesapp.dao.UserDao;
import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.model.UserRole;
import com.simongig.recipesapp.model.UserRole.RoleName;
import com.simongig.recipesapp.service.UserService.UserOverview;
import com.simongig.recipesapp.util.UserMapper;

/** The bcrypt hash stored on {@link User} must not reach any JSON response. */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String HASH = "$2a$10$abcdefghijklmnopqrstuv";

    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void userListMapsIdentityAndRolesWithoutTheHash() throws Exception {
        User bob = new User("bob", "Bob", "Baker", "bob@example.org", HASH,
                new ArrayList<>(List.of(new UserRole(RoleName.ROLE_USER))));
        when(userDao.findAll()).thenReturn(List.of(bob));

        List<UserOverview> users = userService.listUsers();

        assertEquals(List.of(new UserOverview("bob", "Bob", "Baker", "bob@example.org", List.of(RoleName.ROLE_USER))),
                users);
        assertFalse(objectMapper.writeValueAsString(users).contains(HASH));
    }

    @Test
    void userListToleratesMissingAndNullRoles() {
        User noRolesField = new User();
        noRolesField.setUsername("legacy");
        User nullRole = new User("carol", "Carol", "", "", HASH, new ArrayList<>(Arrays.asList((UserRole) null)));
        when(userDao.findAll()).thenReturn(List.of(noRolesField, nullRole));

        List<UserOverview> users = userService.listUsers();

        assertEquals(List.of(), users.get(0).roles());
        assertEquals(List.of(), users.get(1).roles());
    }

    @Test
    void passwordIsReadFromJsonButNeverWritten() throws Exception {
        User parsed = objectMapper.readValue("""
                {"username":"bob","password":"secret-from-request"}""", User.class);
        assertEquals("secret-from-request", parsed.getPassword());

        parsed.setPassword(HASH);
        String json = objectMapper.writeValueAsString(parsed);
        assertFalse(json.contains(HASH), json);
        assertFalse(json.contains("password"), json);
    }
}
