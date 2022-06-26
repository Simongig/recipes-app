package com.simongig.recipesapp.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.bson.conversions.Bson;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.model.UserRole;

@Repository("MongoAtlas-User")
public class UserAccessService_MongoAtlas implements UserDao, UserDetailsService {

    private final MongoClient client;
    private MongoCollection<User> userCollection;
    private MongoCollection<UserRole> userRoleCollection;

    public UserAccessService_MongoAtlas(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    @PostConstruct
    void init() {
        userCollection = client.getDatabase("database").getCollection("User", User.class);
        userRoleCollection = client.getDatabase("database").getCollection("UserRole", UserRole.class);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Bson filter = eq("username", username);
        return Optional.ofNullable(userCollection.find(filter, User.class).first());
    }

    @Override
    public Optional<User> findById(String id) {
        Bson filter = eq("_id", id);
        return Optional.ofNullable(userCollection.find(filter, User.class).first());
    }

    @Override
    public List<User> findAll() {
        return userCollection.find().into(new ArrayList<>());
    }

    @Override
    public void save(User user) {
        userCollection.insertOne(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = findById(username);
        if (!userOptional.isPresent()) {
            System.out.println("User not found");
            throw new UsernameNotFoundException("User was not found in the Database");
        }
        User user = userOptional.get();
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                authorities);
    }

    @Override
    public void saveRole(UserRole role) {
        System.out.println(role);
        userRoleCollection.insertOne(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        
        Bson roleFilter =  eq("name", roleName);
        UserRole role = userRoleCollection.find(roleFilter).first();
        findById(username).get().getRoles().add(role);
    }
}
