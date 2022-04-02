package com.simongig.recipesapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

import java.util.Map;
import java.util.Set;

public class Recipe {

    @Id
    public String id;

    private String title;
    private int duration;
    private Map<String, String> PreparationSteps;
    private Set<Ingredient> Ingredients;
    private String[] imagePaths;
    private Set<String> tags;

    public Recipe(
            @JsonProperty("title") String title,
            @JsonProperty("duration") int duration,
            @JsonProperty("preparationSteps") Map<String, String> PreparationSteps,
            @JsonProperty("ingredients") Set<Ingredient> Ingredients,
            @JsonProperty String[] imagePaths) {
        this.title = title;
        this.duration = duration;
        this.PreparationSteps = PreparationSteps;
        this.Ingredients = Ingredients;
        this.imagePaths = imagePaths;
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

    public void updateIngredients(Set<Ingredient> updatedIngredients) {
        this.Ingredients = updatedIngredients;
    }

    public String[] getImagePaths() {
        return imagePaths;
    }

    @Override
    public String toString() {
        return String.format("Recipe[id=%s, title='%s', ingredients='%s'", id, title, Ingredients);
    }
}
