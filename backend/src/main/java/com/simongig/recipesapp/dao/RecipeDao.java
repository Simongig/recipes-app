package com.simongig.recipesapp.dao;

import com.simongig.recipesapp.model.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeDao {

    default void insert(Recipe recipe) {
        insert(recipe);
    }

    List<Recipe> findAll();

    Optional<Recipe> findById(String id);

    void deleteById(String id);

    void updateById(String id, Recipe recipe);
    
    List<Recipe> searchByName(String input);

    List<Recipe> selectByIngredients(String[] ingredients);
}
