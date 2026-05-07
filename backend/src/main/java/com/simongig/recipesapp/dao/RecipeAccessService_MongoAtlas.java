package com.simongig.recipesapp.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.PostConstruct;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.simongig.recipesapp.model.Recipe;


@Repository("MongoAtlas-Recipes")
public class RecipeAccessService_MongoAtlas implements RecipeDao {
    
    private final MongoClient client;
    private MongoCollection<Recipe> recipeCollection;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    public RecipeAccessService_MongoAtlas(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    @PostConstruct
    void init() {
        recipeCollection = client.getDatabase(databaseName).getCollection("Recipe", Recipe.class);
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
        System.out.println("------- Search Recipes By Name -------");
        Document search_options = new Document("query", search_term).append("path", "title").append("fuzzy", new Document());
        Document agg = new Document("$search", new Document("index", "Recipes").append("text",search_options));
        List<Recipe> search_results = recipeCollection.aggregate(Arrays.asList(agg)).into(new ArrayList<>());
        return search_results;
    }
    
    public List<Recipe> selectByIngredients(String[] ingredients) {
        System.out.println("------- Search Recipes By Ingredients -------");
        System.out.println(Arrays.toString(ingredients));
        Bson matchIngredients = all("ingredients._id", ingredients);
        List<Recipe> recipes = recipeCollection.find(matchIngredients, Recipe.class).into(new ArrayList<>());
        System.out.println(recipes);
        return recipes;
    }

    @Override
    public void deleteById(String id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateById(String id, Recipe recipe) {
    }

}
