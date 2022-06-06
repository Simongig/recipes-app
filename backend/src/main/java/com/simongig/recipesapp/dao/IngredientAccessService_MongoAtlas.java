package com.simongig.recipesapp.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.simongig.recipesapp.model.IngredientName;

import org.bson.conversions.Bson;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository("MongoAtlas-Ingredients")
public class IngredientAccessService_MongoAtlas implements IngredientDao {

    private final MongoClient client;
    private MongoCollection<IngredientName> IngredientNameCollection;

    public IngredientAccessService_MongoAtlas(MongoClient mongoClient) {
        this.client = mongoClient;
    }
    
    @PostConstruct
    void init() {
        IngredientNameCollection = client.getDatabase("database").getCollection("Ingredient",IngredientName.class);
    }

    public void insertIngredient(IngredientName ingredientName) {
        IngredientNameCollection.insertOne(ingredientName);
    }

    @Override
    public List<IngredientName> selectAllIngredientNames() {
        return IngredientNameCollection.find().into(new ArrayList<>());
    }

    @Override
    public Optional<IngredientName> selectIngredientNameById(String id) {
        Bson matchId = eq("name", id);
        return Optional.ofNullable(IngredientNameCollection.find(matchId).first());

    }

    @Override
    public void deleteIngredientNameById(String id) {
        Bson matchId = eq("_id", id);
        IngredientNameCollection.deleteOne(matchId);
    }

    @Override
    public void updateIngredientNamePopularityById(String id, int popularity) {
        Bson matchId = eq("_id", id);
        Bson changePopularity = Updates.set("popularity", popularity);
        UpdateOptions updateOptions = new UpdateOptions().upsert(false);
        IngredientNameCollection.updateOne(matchId, changePopularity, updateOptions);
    }

    @Override
    public void increaseIngredientNamePopularityById(String id, int popularityIncrease) {
        // Optional<IngredientName> foundIngredientName = selectIngredientNameById(id);
        // foundIngredientName.get().increasePopularity(popularityIncrease);
        // mongoOps.updateFirst(new Query(Criteria.where("name").is(id)),
        //         Update.update("popularity", foundIngredientName.get().getPopularity()), IngredientName.class);
    }

    @Override
    public void incrementIngredientNamePopularityById(String id) {
        increaseIngredientNamePopularityById(id, 1);
    }

}
