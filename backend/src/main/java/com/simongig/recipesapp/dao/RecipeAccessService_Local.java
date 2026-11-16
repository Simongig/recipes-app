package com.simongig.recipesapp.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
    public List<Recipe> search(String searchTerm, RecipeViewer viewer) {
        // Pattern.quote: the term is user input and must not be interpreted as a regex (ReDoS).
        Bson filter = regex("title", Pattern.quote(searchTerm), "i"); // case-insensitive
        return recipeCollection.find(restrict(filter, viewer)).limit(20).into(new ArrayList<>());
    }
}