package com.simongig.recipesapp.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.simongig.recipesapp.model.Recipe;


@Repository("MongoAtlas-Recipes")
public class RecipeAccessService_MongoAtlas implements RecipeDao {
    
    private final MongoClient client;
    private MongoCollection<Recipe> recipeCollection;

    public RecipeAccessService_MongoAtlas(MongoClient mongoClient) {
        this.client = mongoClient;
    }
    
    @PostConstruct
    void init() {
        recipeCollection = client.getDatabase("database").getCollection("Recipe",Recipe.class);
    }

    
    @Override
    public void insert(Recipe recipe) {
        recipeCollection.insertOne(recipe);
    }

    @Override
    public List<Recipe> findAll() {
        List<Recipe> allRecipes = recipeCollection.find().into(new ArrayList<>());
        for (Recipe recipe: allRecipes) {
            System.out.println(recipe.getImagePaths());
            System.out.println(recipe.getPreparationSteps());
        }
        return recipeCollection.find().into(new ArrayList<>());
    }

    @Override
    public Optional<Recipe> findById(String id) {
        Bson matchId = eq("_id", id);
        return Optional.ofNullable(recipeCollection.find(matchId).first());
    }

    public List<Recipe> search(String search_term) {
        Document search_options = new Document("query", search_term).append("path", "title").append("fuzzy", new Document());
        Document agg = new Document("$search", new Document("index", "Recipes").append("text",search_options));
        System.out.println(agg);
        List<Recipe> search_results = recipeCollection.aggregate(Arrays.asList(agg)).into(new ArrayList<>());
        return search_results;
    }
    
    public List<Recipe> selectByIngredients(String[] ingredients) {
        System.out.println("------- Search Recipes -------");
        for(String i: ingredients) {
            System.out.println(i);
        }
        Bson matchIngredients = in("ingredients._id", ingredients);
        return recipeCollection.find(matchIngredients, Recipe.class).into(new ArrayList<>());
    }

    @Override
    public void deleteById(String id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateById(String id, Recipe recipe) {
    }

}
