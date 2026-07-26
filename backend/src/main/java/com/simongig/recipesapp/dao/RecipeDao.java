package com.simongig.recipesapp.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.simongig.recipesapp.model.Recipe;

public interface RecipeDao {

    void insert(Recipe recipe);

    List<Recipe> findAll();

    Optional<Recipe> findById(String id);

    List<Recipe> findByIds(Collection<String> ids);

    List<Recipe> search(String search_term);

    void deleteById(String id);

    void updateById(String id, Recipe recipe);

    List<Recipe> selectByIngredients(String[] ingredients);

    List<String> selectDistinctIngredientNames();
}
