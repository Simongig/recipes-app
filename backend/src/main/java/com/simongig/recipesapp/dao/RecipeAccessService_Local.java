package com.simongig.recipesapp.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoClient;
import static com.mongodb.client.model.Filters.regex;
import com.simongig.recipesapp.model.Recipe;

@Repository("MongoAtlas-Recipes")
@Profile("dev")  // overrides the prod bean when running locally
public class RecipeAccessService_Local extends RecipeAccessService_MongoAtlas {

    public RecipeAccessService_Local(MongoClient mongoClient) {
        super(mongoClient);
    }

    @Override
    public List<Recipe> search(String searchTerm) {
        Bson filter = regex("title", searchTerm, "i"); // case-insensitive regex
        return recipeCollection.find(filter).limit(20).into(new ArrayList<>());
    }
}