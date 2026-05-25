package com.simongig.recipesapp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.NonNull;
import com.simongig.recipesapp.model.UserRole.RoleName;

public class User {

    @Id
    @BsonId
    private String username;

    private String name;
    private String lastName;
    private String email;
    private String password;
    private List<Recipe> recipes;
    private Collection<UserRole> roles;

    public User(
            @JsonProperty("username") @NonNull String username,
            @JsonProperty("name") String name,
            @JsonProperty("last_name") String lastName,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password,
            @JsonProperty("role") List<UserRole> roles) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        if (null == roles || roles.isEmpty()) {
            this.roles = new ArrayList<>();
        } else {
            this.roles = roles;
        }
    }

    public User() {
    }

    public String getFullName() {
        return String.format("%s %s", getName(), getLastName());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Recipe> getRecipes() {
        return this.recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public Collection<UserRole> getRoles() {
        return this.roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public void addRole(UserRole newRole) {
        this.roles.add(newRole);
    }

    public boolean isAdmin() {
        return this.roles.stream().anyMatch(r -> r.getName() == RoleName.ROLE_ADMIN);
    }
}
