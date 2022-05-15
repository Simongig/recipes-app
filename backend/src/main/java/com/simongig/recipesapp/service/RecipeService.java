package com.simongig.recipesapp.service;

import com.simongig.recipesapp.dao.RecipeDao;
import com.simongig.recipesapp.model.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    private final RecipeDao recipeDao;

    @Autowired
    public RecipeService(@Qualifier("mongodb") RecipeDao recipeDao) {
        this.recipeDao = recipeDao;
    }

    public int addRecipe(Recipe recipe) {
        return this.recipeDao.insertRecipe(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return this.recipeDao.selectAllRecipes();
    }

    public Optional<Recipe> getRecipeById(String id) {
        return this.recipeDao.selectRecipeById(id);
    }

    public int deleteRecipe(String id) {
        return this.recipeDao.deleteRecipeById(id);
    }

    public int updateRecipe(String id, Recipe newRecipe) {
        return this.recipeDao.updateRecipeById(id, newRecipe);
    }

    public List<Recipe> findRecipeByIngredients(String[] ingredients) {
        return this.recipeDao.selectRecipesByIngredients(ingredients);
    }
}
