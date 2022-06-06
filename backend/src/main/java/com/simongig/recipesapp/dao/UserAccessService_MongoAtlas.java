package com.simongig.recipesapp.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.simongig.recipesapp.model.User;


@Repository("MongoAtlas-User")
public class UserAccessService_MongoAtlas implements UserDao {

    private final MongoClient client;
    private MongoCollection<User> userCollection;

    public UserAccessService_MongoAtlas(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    @PostConstruct
    void init() {
        userCollection = client.getDatabase("database").getCollection("User", User.class);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Bson filter = eq("username", username);
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

}
