package com.simongig.recipesapp.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.simongig.recipesapp.model.User;

@Repository("MongoAtlas-User")
public interface UserDao {
    
    Optional<User> findByUsername(String username);

    List<User> findAll();

    void save(User user);
}
