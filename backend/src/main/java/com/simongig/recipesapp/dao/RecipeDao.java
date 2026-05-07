package com.simongig.recipesapp.dao;

import com.simongig.recipesapp.model.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeDao {

    void insert(Recipe recipe);

    List<Recipe> findAll();

    Optional<Recipe> findById(String id);

    List<Recipe> search(String search_term);

    void deleteById(String id);

    void updateById(String id, Recipe recipe);

    List<Recipe> selectByIngredients(String[] ingredients);
}
