package com.simongig.recipesapp.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.model.UserRole;

@Repository("MongoAtlas-User")
public interface UserDao {
    
    Optional<User> findByUsername(String username);

    Optional<User> findById(String id);

    List<User> findAll();

    void save(User user);

    void saveRole(UserRole role);

    void addRoleToUser(String username, String roleName);
}
