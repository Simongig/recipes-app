package com.simongig.recipesapp.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.model.UserRole;
import com.simongig.recipesapp.service.UserService;

@RequestMapping("api/v1/auth")
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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

    class addRoleToUserForm {
        private String username;
        private String role;
        public addRoleToUserForm(@JsonProperty String username,@JsonProperty String role) {
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
