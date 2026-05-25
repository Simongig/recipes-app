package com.simongig.recipesapp.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Recipe {

    @Id
    @BsonId
    private String id;

    // @TODO: ownerId oder ownerType 
    private String ownerId; // null = public/admin recipe; username for user recipes
    private RecipeVisibility visibility; // PUBLIC, STAGING, PRIVATE, SHARED
    private String sourceRecipeId; // set when forked from a public recipe
    private String createdBy;
    
    private String title;
    private int duration;
    private List<PreparationStep> preparationSteps;
    private List<Ingredient> ingredients;
    private List<String> imagePaths;
    private int portions;

    public enum RecipeVisibility { PUBLIC, STAGING, PRIVATE, SHARED }

    public Recipe() {}

    public Recipe(
            @JsonProperty("title") String title,
            @JsonProperty("duration") int duration,
            @JsonProperty("preparationSteps") List<PreparationStep> preparationSteps,
            @JsonProperty("ingredients") List<Ingredient> ingredients,
            @JsonProperty("portions") int portions,
            @JsonProperty("imagePaths") List<String> imagePaths
                ) {
        this.id = new ObjectId().toString();
        this.ownerId = "";
        this.title = title;
        this.duration = duration;
        this.preparationSteps = preparationSteps;
        this.ingredients = ingredients;
        this.portions = portions;
        this.visibility = RecipeVisibility.PRIVATE; // initally always private until changed
        if (null == imagePaths || imagePaths.isEmpty()) {
            this.imagePaths = new ArrayList<>();
        } else {
            this.imagePaths = imagePaths;
        }
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public RecipeVisibility getVisibility() {
        return visibility != null ? visibility : RecipeVisibility.PRIVATE;
    }

    public void setVisibility(RecipeVisibility visibility) {
        this.visibility = visibility;
    }

    public String getSourceRecipeId() {
        return sourceRecipeId;
    }

    public void setSourceRecipeId(String sourceRecipeId) {
        this.sourceRecipeId = sourceRecipeId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<PreparationStep> getPreparationSteps() {
        return preparationSteps;
    }

    public void setPreparationSteps(List<PreparationStep> preparationSteps) {
        this.preparationSteps = preparationSteps;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public int getPortions() {
        return portions;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }

    //TODO add a clone functionality
    
    @Override
    public String toString() {
        return "Recipe [Ingredients=" + ingredients + ", preparationSteps=" + preparationSteps + ", id=" + id
                + ", title=" + title + "]";
    }
}
