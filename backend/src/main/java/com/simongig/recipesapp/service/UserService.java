package com.simongig.recipesapp.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.simongig.recipesapp.dao.UserDao;
import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.model.UserRole;
import com.simongig.recipesapp.model.UserRole.RoleName;


@Service
public class UserService {
    
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserService(@Qualifier("MongoAtlas-User") UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Collection<UserRole> roles = user.getRoles();
        if (user.isAdmin()) {
            throw new IllegalArgumentException("Cannot self-assign admin role");
        } 
        else if(roles.isEmpty()) {
            user.addRole(new UserRole(RoleName.ROLE_USER));
        }
        this.userDao.save(user);
    }

    public void saveAsAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRoles().isEmpty()) {
            user.addRole(new UserRole(RoleName.ROLE_ADMIN));
        }
        this.userDao.save(user);
    }

    public Optional<User> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String callerUsername = auth.getName(); // the JWT subject
        System.out.println("getProfile - username: " + callerUsername);
        return findByUsername(callerUsername);
    }

    public List<User> findAll() {
        return this.userDao.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userDao.findById(username);
    }

    public void saveUserRole(UserRole role) {
        userDao.saveRole(role);
    }

    public void addRoleToUser(String username, String roleName) {
        userDao.addRoleToUser(username, roleName);
    }
}
