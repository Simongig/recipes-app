package com.simongig.recipesapp.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.model.UserRole;
import com.simongig.recipesapp.security.JwtTokenService;
import com.simongig.recipesapp.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequestMapping("api/v1")
@RestController
public class UserController {

    private final UserService userService;
    private final JwtTokenService tokenService;

    public UserController(UserService userService, JwtTokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GetMapping("/user/me")
    public Optional<UserService.UserDTO> getProfile() {
        return userService.getProfile();
    }

    @GetMapping("/users")
    public List<UserService.UserOverview> getAllUsers() {
        return userService.listUsers();
    }

    @PostMapping("/user/add")
    public void saveUser(@RequestBody User user) {
        userService.save(user);
    }

    @PostMapping("/role/save")
    public void saveRole(@RequestBody UserRole role) {
        userService.saveUserRole(role);
    }

    @PatchMapping("/user/updateFavorite")
    public void updateFavorite(@RequestBody UpdateFavoriteForm form) {
        userService.updateFavoriteInUser(form.recipeId(), form.add());
    }

    public record UpdateFavoriteForm(String recipeId, boolean add) {
    }

    @PostMapping("/user/addRole")
    public void addRole(@RequestBody addRoleToUserForm form) {
        userService.addRoleToUser(form.getUsername(), form.getRole());
    }

    // Exchanges a refresh token (sent as Bearer) for a new token pair. This route is permitAll'd
    // and reads the header itself: the resource server only accepts access tokens.
    @PostMapping("/auth/token/refresh")
    public Map<String, String> refreshTokens(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token missing");
        }
        try {
            String username = tokenService.verifyRefreshToken(authorizationHeader.substring("Bearer ".length()));
            // Roles are re-read from the database, and deleted users get no new tokens.
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token"));
            return tokenService.issueTokens(user.getUsername(),
                    user.getRoles().stream().map(role -> role.getName().name()).toList());
        } catch (JWTVerificationException exception) {
            log.debug("Rejected refresh token: {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token");
        }
    }

    public class addRoleToUserForm {
        private String username;
        private String role;

        public addRoleToUserForm() {
        }

        public addRoleToUserForm(@JsonProperty String username, @JsonProperty String role) {
            this.username = username;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
