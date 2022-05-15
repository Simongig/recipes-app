package com.simongig.recipesapp.dao;

import com.simongig.recipesapp.model.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeDao {

    default int insertRecipe(Recipe recipe) {
        return insertRecipe(recipe);
    }

    List<Recipe> selectAllRecipes();

    Optional<Recipe> selectRecipeById(String id);

    int deleteRecipeById(String id);

    int updateRecipeById(String id, Recipe recipe);

    List<Recipe> selectRecipesByIngredients(String[] ingredients);
}
