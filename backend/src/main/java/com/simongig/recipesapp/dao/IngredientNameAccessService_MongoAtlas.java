package com.simongig.recipesapp.dao;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.simongig.recipesapp.model.Ingredient;
import com.simongig.recipesapp.model.IngredientName;

@Repository("MongoAtlas-IngredientNames")
public class IngredientNameAccessService_MongoAtlas implements IngredientNameDao {

    private final MongoClient client;
    private MongoCollection<IngredientName> ingredientNameCollection;

    public IngredientNameAccessService_MongoAtlas(MongoClient mongoClient) {
        this.client = mongoClient;
    }
    
    @PostConstruct
    void init() {
        ingredientNameCollection = client.getDatabase("database").getCollection("IngredientName",IngredientName.class);
    }

    public void insertIngredientName(IngredientName ingredient) {
        ingredientNameCollection.insertOne(ingredient);
    }

    @Override
    public List<IngredientName> selectAllIngredientNames() {
        return ingredientNameCollection.find().into(new ArrayList<>());
    }

    @Override
    public Optional<IngredientName> selectIngredientNameByName(String name) {
        Bson matchName = eq("_id", name);
        return Optional.ofNullable(ingredientNameCollection.find(matchName).first());

    }

    @Override
    public void deleteIngredientNameById(String id) {
        Bson matchId = eq("_id", id);
        ingredientNameCollection.deleteOne(matchId);
    }

    @Override
    public void updateIngredientNamePopularityById(String id, int popularity) {
        Bson matchId = eq("_id", id);
        Bson changePopularity = Updates.set("popularity", popularity);
        UpdateOptions updateOptions = new UpdateOptions().upsert(false);
        ingredientNameCollection.updateOne(matchId, changePopularity, updateOptions);
    }

    @Override
    public void increaseIngredientNamePopularityById(String id, int popularityIncrease) {
        Optional<IngredientName> foundIngredient = selectIngredientNameByName(id);
        System.out.println("Ingredientpopularity: "+ foundIngredient.get().getPopularity());
        System.out.println(selectIngredientNameByName(id));
        foundIngredient.get().increasePopularity(popularityIncrease);
        // Update.update("popularity", foundIngredient.get().getPopularity()), Ingredient.class);
        System.out.println("new Ingredientpopularity: "+ foundIngredient.get().getPopularity());
        try {   
            UpdateOptions options = new UpdateOptions().upsert(true);
            System.out.println(ingredientNameCollection.updateOne(eq("_id", id), Updates.set("popularity", foundIngredient.get().getPopularity()), options));
        } catch (Exception e) {
            System.out.println("Error updating: " + e.getMessage());
        }
    }

    @Override
    public void incrementIngredientNamePopularityById(String id) {
        System.out.println("incrementIngredientNamePopularityById() " + id);
        increaseIngredientNamePopularityById(id, 1);
    }

}
