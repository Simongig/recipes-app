package com.simongig.recipesapp.api;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.model.UserRole;
import com.simongig.recipesapp.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("api/v1/auth")
@RestController
public class UserController {

    private final UserService userService;
    private final Algorithm algorithm;

    public UserController(UserService userService, Algorithm algorithm) {
        this.userService = userService;
        this.algorithm = algorithm;
    }

    @GetMapping("/users")
    public List<User> getAllRecipes() {
        return userService.findAll();
    }

    @PostMapping("/user/add")
    public void saveUser(@RequestBody User user) {
        userService.save(user);
    }

    @PostMapping("/role/save")
    public void saveRole(@RequestBody UserRole role) {
        userService.saveUserRole(role);
    }

    @PostMapping("/user/addRole")
    public void addRole(@RequestBody addRoleToUserForm form) {
        userService.addRoleToUser(form.getUsername(), form.getRole());

    }

    @PostMapping("/token/refresh")
    public void generateRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                JWTVerifier verifier = JWT.require(this.algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                String username = decodedJWT.getSubject();
                Optional<User> user_opt = userService.findByUsername(username);
                if (user_opt.isPresent()) {
                    User user = user_opt.get();
                    String access_token = JWT.create()
                            .withSubject(user.getUsername())
                            .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                            .withIssuer(request.getRequestURL().toString())
                            .withClaim("roles",
                                    user.getRoles().stream().map(UserRole::getName).collect(Collectors.toList()))
                            .sign(algorithm);

                    String refresh_token = JWT.create()
                            .withSubject(user.getUsername())
                            .withExpiresAt(new Date(System.currentTimeMillis() + 100 * 60 * 1000))
                            .withIssuer(request.getRequestURL().toString())
                            .sign(algorithm);

                    // response.setHeader("access_token", access_token);
                    // response.setHeader("refresh_token", refresh_token);

                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("access_token", access_token);
                    tokens.put("refresh_token", refresh_token);
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                }
            } catch (Exception exception) {
                System.out.println("Error loggin in: " + exception.getMessage());
                response.setHeader("error", exception.getMessage());
                response.sendError(FORBIDDEN.value());
            }
        } else

        {
            throw new RuntimeException("Refreshtoken missing");
        }
    }

    class addRoleToUserForm {
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
