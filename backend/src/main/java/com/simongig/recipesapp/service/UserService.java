package com.simongig.recipesapp.service;

import java.util.List;
import java.util.Optional;
import com.simongig.recipesapp.dao.UserDao;
import com.simongig.recipesapp.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private final UserDao userDao;

    @Autowired
    public UserService(@Qualifier("MongoAtlas-User") UserDao userDao) {
        this.userDao = userDao;
    }

    public void save(User user) {
        this.userDao.save(user);
    }

    public List<User> findAll() {
        return this.userDao.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }
}
