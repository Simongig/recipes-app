package com.simongig.recipesapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Set;

public class Recipe {

    @Id
    public String id;

    private String title;
    private int duration;
    private Set<PreparationStep> PreparationSteps;
    private Set<Ingredient> Ingredients;
    private ArrayList<String> imagePaths;
    private int portions;
    private Set<String> tags;


    public Recipe(
            @JsonProperty("title") String title,
            @JsonProperty("duration") int duration,
            @JsonProperty("preparationSteps") Set<PreparationStep> PreparationSteps,
            @JsonProperty("ingredients") Set<Ingredient> Ingredients,
            @JsonProperty("portions") int portions,
            @JsonProperty("imagePaths") ArrayList<String> imagePaths) {
        this.title = title;
        this.duration = duration;
        this.PreparationSteps = PreparationSteps;
        this.Ingredients = Ingredients;
        this.portions = portions;
        if(null == imagePaths  || 0 == imagePaths.size()) {
            this.imagePaths = new ArrayList<String>();
        } else {
            this.imagePaths = imagePaths;
        }
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public int getPortions() {
        return portions;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }

    public Set<Ingredient> getIngredients() {
        return Ingredients;
    }

    public void setTitle(String newtitle) {
        this.title = newtitle;
    }

    public void setDuration(int newDuration) {
        this.duration = newDuration;
    }

    public Set<PreparationStep> getPreparationSteps() {
        return this.PreparationSteps;
    }

    public void updateIngredients(Set<Ingredient> updatedIngredients) {
        this.Ingredients = updatedIngredients;
    }

    public void addImagePath(String path) {
        imagePaths.add(path);
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    @Override
    public String toString() {
        return String.format("\n Recipe: id=%s,\n title='%s',\n ingredients:'%s',\n PreparationSteps:'%s'", id, title,
                Ingredients, PreparationSteps);
    }
}
