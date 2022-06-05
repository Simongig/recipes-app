package com.simongig.recipesapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    @Id
    @BsonId
    private String id;

    private String title;
    private int duration;
    private List<PreparationStep> PreparationSteps;
    private List<Ingredient> Ingredients;
    private List<String> imagePaths;
    private int portions;
    // private Set<String> tags;

    public Recipe() {}

    public Recipe(
            @JsonProperty("title") String title,
            @JsonProperty("duration") int duration,
            @JsonProperty("preparationSteps") List<PreparationStep> PreparationSteps,
            @JsonProperty("ingredients") List<Ingredient> Ingredients,
            @JsonProperty("portions") int portions,
            @JsonProperty("imagePaths") List<String> imagePaths) {
        this.id = new ObjectId().toString();
        this.title = title;
        this.duration = duration;
        this.PreparationSteps = PreparationSteps;
        this.Ingredients = Ingredients;
        this.portions = portions;
        if (null == imagePaths || 0 == imagePaths.size()) {
            this.imagePaths = new ArrayList<String>();
        } else {
            this.imagePaths = imagePaths;
        }
    }

    // public Set<String> getTags() {
    // return tags;
    // }

    // public void setTags(Set<String> tags) {
    // this.tags = tags;
    // }

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
        return PreparationSteps;
    }

    public void setPreparationSteps(List<PreparationStep> preparationSteps) {
        PreparationSteps = preparationSteps;
    }

    public List<Ingredient> getIngredients() {
        return Ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        Ingredients = ingredients;
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

    
    @Override
    public String toString() {
        return "Recipe [Ingredients=" + Ingredients + ", PreparationSteps=" + PreparationSteps + ", id=" + id
                + ", title=" + title + "]";
    }
}
