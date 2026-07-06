package com.simongig.recipesapp.dao;

import java.util.Optional;

import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.ReplaceOptions;
import com.simongig.recipesapp.model.MealPlan;

import jakarta.annotation.PostConstruct;

@Repository("MongoAtlas-MealPlans")
public class MealPlanAccessService_MongoAtlas implements MealPlanDao {

    private final MongoClient client;
    private MongoCollection<MealPlan> mealPlanCollection;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    public MealPlanAccessService_MongoAtlas(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    @PostConstruct
    void init() {
        mealPlanCollection = client.getDatabase(databaseName).getCollection("MealPlan", MealPlan.class);
    }

    @Override
    public Optional<MealPlan> findByOwnerId(String ownerId) {
        Bson matchOwner = eq("ownerId", ownerId);
        return Optional.ofNullable(mealPlanCollection.find(matchOwner).first());
    }

    @Override
    public void save(MealPlan plan) {
        Bson matchOwner = eq("ownerId", plan.getOwnerId());
        mealPlanCollection.replaceOne(matchOwner, plan, new ReplaceOptions().upsert(true));
    }
}
