package com.simongig.recipesapp.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.simongig.recipesapp.dao.RecipeDao;
import com.simongig.recipesapp.dao.RecipeRepository;
import com.simongig.recipesapp.dao.RecipeSummary;
import com.simongig.recipesapp.model.Recipe;
import com.simongig.recipesapp.model.UserRole;

@Service
public class RecipeService {

    private final RecipeDao recipeDao;
    private final RecipeRepository recipeRepository;

    public RecipeService(@Qualifier("MongoAtlas-Recipes") RecipeDao recipeDao, RecipeRepository recipeRepository) {
        this.recipeDao = recipeDao;
        this.recipeRepository = recipeRepository;
    }

    public void addRecipe(Recipe recipe) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String callerUsername = auth.getName(); // the JWT subject
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(UserRole.RoleName.ROLE_ADMIN.name()));
        
        recipe.setCreatedBy(callerUsername);
        if (!isAdmin) { // Curated recipes from the platform don't get a specific ownerId, only custom recipes
            recipe.setOwnerId(callerUsername);
        }
        this.recipeDao.insert(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return this.recipeDao.findAll();
    }

    public Optional<Recipe> getRecipeById(String id) {
        return this.recipeDao.findById(id);
    }

    public List<RecipeSummary> getRecipeSummaryByIds(Collection<String> ids) {
        return this.recipeRepository.findByIdIn(ids);
    }

    public List<Recipe> search(String searchTerm){
        return this.recipeDao.search(searchTerm);
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

    public List<String> getDistinctIngredientNames() {
        return this.recipeDao.selectDistinctIngredientNames();
    }
}
