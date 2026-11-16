package com.simongig.recipesapp.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.simongig.recipesapp.model.Recipe;

public interface RecipeDao {

    void insert(Recipe recipe);

    List<Recipe> findAll(RecipeViewer viewer);

    // Unrestricted: callers must check RecipeViewer.canView themselves.
    Optional<Recipe> findById(String id);

    List<Recipe> findByIds(Collection<String> ids, RecipeViewer viewer);

    List<Recipe> search(String search_term, RecipeViewer viewer);

    void deleteById(String id);

    void updateById(String id, Recipe recipe);

    List<Recipe> selectByIngredients(String[] ingredients, RecipeViewer viewer);

    List<String> selectDistinctIngredientNames();
}
