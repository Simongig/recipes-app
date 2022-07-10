package com.simongig.recipesapp.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
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
    public void insertRecipe(Recipe recipe) {
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

    
    public List<Recipe> selectRecipesByIngredients(String[] ingredients) {
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
        // TODO Auto-generated method stub
    }

}
