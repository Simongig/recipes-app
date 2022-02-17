package com.simongig.recipesapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

import java.util.Map;
import java.util.Set;


public class Recipe {

    @Id
    public String id;

    private String name;
    private int duration;
    private Map<String, String> preparationSteps;
    private Set<Ingredient> Ingredients;
    private String[] imagePaths;
    private Set<String> tags;



    public Recipe(@JsonProperty("name") String name, @JsonProperty("duration") int duration, @JsonProperty("ingredients") Set<Ingredient> Ingredients, @JsonProperty String[] imagePaths) {
        this.name = name;
        this.duration = duration;
        this.Ingredients = Ingredients;
        this.imagePaths = imagePaths;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public Set<Ingredient> getIngredients() {
        return Ingredients;
    }

    public void setName(String newName) {
        this.name = newName;
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
        return String.format("Recipe[id=%s, name='%s', ingredients='%s'", id, name, Ingredients);
    }
}
