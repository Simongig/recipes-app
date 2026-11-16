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
    private List<String> recipes;
    private List<String> favorites;
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

    public List<String> getRecipes() {
        return this.recipes;
    }

    public void addRecipe(String recipeId) {
        if (this.recipes == null) {
            this.recipes = new ArrayList<>();
        }
        this.recipes.add(recipeId);
    }

    public void removeRecipe(String recipeId) {
        if (this.recipes == null) {
            return;
        }
        this.recipes.remove(recipeId);
    }

    public void setRecipes(List<String> recipes) {
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

    public List<String> getFavorites() {
        if (this.favorites == null) {
            this.favorites = new ArrayList<>();
        }
        return favorites;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }

    public void addFavorite(String recipeId) {
        if (this.favorites == null) {
            this.favorites = new ArrayList<>();
        }
        this.favorites.add(recipeId);
    }

    public void removeFavorite(String recipeId) {
        if (this.favorites == null) {
            return;
        }
        this.favorites.remove(recipeId);
    }
}
