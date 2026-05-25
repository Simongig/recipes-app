package com.simongig.recipesapp.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.all;
import static com.mongodb.client.model.Filters.eq;
import com.simongig.recipesapp.model.Recipe;

import jakarta.annotation.PostConstruct;


@Repository("MongoAtlas-Recipes")
@Profile("!dev")
public class RecipeAccessService_MongoAtlas implements RecipeDao {

    private final MongoClient client;
    protected MongoCollection<Recipe> recipeCollection;

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
        return recipeCollection.find().into(new ArrayList<>());
    }

    @Override
    public Optional<Recipe> findById(String id) {
        Bson matchId = eq("_id", id);
        return Optional.ofNullable(recipeCollection.find(matchId).first());
    }

    @Override
    public List<Recipe> search(String searchTerm) {
        System.out.println("------- Search Recipes By Name -------");
        Document compoundClause = new Document()
            .append("minimumShouldMatch", 1)
            .append("should", Arrays.asList(
                new Document("autocomplete", new Document()
                    .append("query", searchTerm)
                    .append("path", "title")
                    .append("score", new Document("boost", new Document("value", 3)))),
                new Document("text", new Document()
                    .append("query", searchTerm)
                    .append("path", "description")
                    .append("fuzzy", new Document().append("maxEdits", 1).append("prefixLength", 2)))
            ));
        Document agg = new Document("$search", new Document("index", "Recipes").append("compound", compoundClause));
        List<Recipe> search_results = recipeCollection.aggregate(Arrays.asList(agg, new Document("$limit", 20))).into(new ArrayList<>());
        return search_results;
    }
    
    @Override
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
