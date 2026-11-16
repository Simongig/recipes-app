package com.simongig.recipesapp.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.simongig.recipesapp.dao.RecipeSummary;
import com.simongig.recipesapp.dao.UserDao;
import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.model.UserRole;
import com.simongig.recipesapp.model.UserRole.RoleName;
import com.simongig.recipesapp.util.UserMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
public class UserService {
    
    private final UserMapper userMapper;
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserService(@Qualifier("MongoAtlas-User") UserDao userDao, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
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

    public UserDTO updateFavoriteInUser(String recipeId, boolean add) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String callerUsername = auth.getName(); // the JWT subject
        Optional<User> userOptional = findByUsername(callerUsername);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (add && user.getFavorites().contains(recipeId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recipe already added to user.");
            }
            if (add) {
                user.addFavorite(recipeId);
            } else {
                user.removeFavorite(recipeId);
            }
            this.userDao.update(user);
            return userMapper.toDto(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No matching user found.");
        }
    }

    public void saveAsAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRoles().isEmpty()) {
            user.addRole(new UserRole(RoleName.ROLE_ADMIN));
        }
        this.userDao.save(user);
    }
    
    public record UserDTO(
            String username,
            String fullName,
            String firstName,
            String lastName,
            String email,
            List<RecipeSummary> recipes,
            List<RecipeSummary> favorites
    ) {}

    public Optional<UserDTO> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String callerUsername = auth.getName(); // the JWT subject
        log.debug("getProfile - username: " + callerUsername);

        User user = userDao.findById(callerUsername).orElse(null);
        if (user == null) {
            log.warn("User not found for username: " + callerUsername);
            return Optional.empty();
        }

        UserDTO userDTO = userMapper.toDto(user);

        return Optional.ofNullable(userDTO);
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
