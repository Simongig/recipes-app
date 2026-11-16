package com.simongig.recipesapp.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.all;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import com.mongodb.client.model.Aggregates;
import com.simongig.recipesapp.model.Recipe;
import com.simongig.recipesapp.util.IngredientNormalizer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
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
    public List<Recipe> findAll(RecipeViewer viewer) {
        Bson visible = viewer.filter();
        return (visible == null ? recipeCollection.find() : recipeCollection.find(visible)).into(new ArrayList<>());
    }

    @Override
    public Optional<Recipe> findById(String id) {
        Bson matchId = eq("_id", id);
        return Optional.ofNullable(recipeCollection.find(matchId).first());
    }

    @Override
    public List<Recipe> findByIds(Collection<String> ids, RecipeViewer viewer) {
        if (ids == null || ids.isEmpty()) return List.of();
        return recipeCollection.find(restrict(in("_id", ids), viewer)).into(new ArrayList<>());
    }

    @Override
    public List<Recipe> search(String searchTerm, RecipeViewer viewer) {
        log.info("Search Recipes By Name: {}", searchTerm);
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
        // $search must be the first stage; the visibility $match goes before $limit so that
        // hidden recipes don't eat into the 20 result slots.
        List<Bson> pipeline = new ArrayList<>(List.of(agg));
        Bson visible = viewer.filter();
        if (visible != null) {
            pipeline.add(Aggregates.match(visible));
        }
        pipeline.add(new Document("$limit", 20));
        return recipeCollection.aggregate(pipeline).into(new ArrayList<>());
    }
    
    @Override
    public List<Recipe> selectByIngredients(String[] ingredients, RecipeViewer viewer) {
        String[] normalizedIngredients = Arrays.stream(ingredients)
                .map(IngredientNormalizer::normalize)
                .toArray(String[]::new);
        Bson matchIngredients = all("ingredients.normalizedKey", normalizedIngredients);
        return recipeCollection.find(restrict(matchIngredients, viewer), Recipe.class).into(new ArrayList<>());
    }

    /** Combines a query filter with the viewer's visibility filter (if any). */
    protected static Bson restrict(Bson filter, RecipeViewer viewer) {
        Bson visible = viewer.filter();
        return visible == null ? filter : and(filter, visible);
    }

    @Override
    public List<String> selectDistinctIngredientNames() {
        List<Bson> pipeline = Arrays.asList(
                new Document("$unwind", "$ingredients"),
                new Document("$group", new Document("_id", "$ingredients.normalizedKey")
                        .append("name", new Document("$first", "$ingredients.name"))),
                new Document("$sort", new Document("name", 1)));
        return recipeCollection.aggregate(pipeline, Document.class).into(new ArrayList<>())
                .stream()
                .map(doc -> doc.getString("name"))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateById(String id, Recipe recipe) {
    }

    
}
