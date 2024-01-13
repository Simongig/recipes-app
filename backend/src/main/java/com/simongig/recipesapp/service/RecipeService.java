package com.simongig.recipesapp.service;

import com.simongig.recipesapp.dao.IngredientNameDao;
import com.simongig.recipesapp.dao.RecipeDao;
import com.simongig.recipesapp.model.Ingredient;
import com.simongig.recipesapp.model.IngredientName;
import com.simongig.recipesapp.model.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeDao recipeDao;
    private final IngredientNameService ingredientNameService;

    @Autowired
    public RecipeService(@Qualifier("MongoAtlas-Recipes") RecipeDao recipeDao, @Qualifier("MongoAtlas-IngredientNames") IngredientNameDao ingredientNameDao) {
        this.recipeDao = recipeDao;
        this.ingredientNameService = new IngredientNameService(ingredientNameDao);
    }

    public void addRecipe(Recipe recipe) {
        List<IngredientName> aIngredientNames = ingredientNameService.getAllIngredientNames();
        for(Ingredient ingredient: recipe.getIngredients()) {
            System.out.println("Ingredient:" + ingredient);
            List<IngredientName> filteredIngredients = aIngredientNames.stream().filter(c -> c.getName().equals(ingredient.getName())).collect(Collectors.toList());
            System.out.println("All IngredientNames: " + filteredIngredients);
            if(0 < filteredIngredients.size()) {
                ingredientNameService.incrementIngredientNamePopularityByName(ingredient.getName());
            } else {
                ingredientNameService.addIngredientName(new IngredientName(ingredient.getName()));
            }
        }
        this.recipeDao.insert(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return this.recipeDao.findAll();
    }

    public List<Recipe> searchByName(String input) {
        return recipeDao.searchByName(input);
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
        return this.recipeDao.selectByIngredients(ingredients);
    }
}
