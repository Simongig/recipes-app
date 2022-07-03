package com.simongig.recipesapp.service;

import java.util.List;
import java.util.Optional;
import com.simongig.recipesapp.dao.UserDao;
import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.model.UserRole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(@Qualifier("MongoAtlas-User") UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.userDao.save(user);
    }

    public List<User> findAll() {
        return this.userDao.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public void saveUserRole(UserRole role) {
        userDao.saveRole(role);
    }

    public void addRoleToUser(String username, String roleName) {
        userDao.addRoleToUser(username, roleName);
    }
}
