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
    public RecipeService(@Qualifier("MongoAtlas-Recipes") RecipeDao recipeDao) {
        this.recipeDao = recipeDao;
    }

    public void addRecipe(Recipe recipe) {
        this.recipeDao.insertRecipe(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return this.recipeDao.findAll();
    }

    public Optional<Recipe> getRecipeById(String id) {
        return this.recipeDao.findById(id);
    }

    public void deleteRecipe(String id) {
        this.recipeDao.deleteById(id);
    }

    public void updateRecipe(String id, Recipe newRecipe) {
        this.recipeDao.updateById(id, newRecipe);
    }

    public List<Recipe> findRecipeByIngredients(String[] ingredients) {
        return this.recipeDao.selectRecipesByIngredients(ingredients);
    }
}
