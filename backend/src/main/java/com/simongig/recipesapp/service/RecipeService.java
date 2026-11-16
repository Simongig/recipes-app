package com.simongig.recipesapp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.simongig.recipesapp.dao.RecipeDao;
import com.simongig.recipesapp.dao.RecipeRepository;
import com.simongig.recipesapp.dao.RecipeSummary;
import com.simongig.recipesapp.dao.RecipeViewer;
import com.simongig.recipesapp.model.Recipe;
import com.simongig.recipesapp.model.UserRole;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
public class RecipeService {

    private final RecipeDao recipeDao;
    private final RecipeRepository recipeRepository;
    private final ImageStorageService imageStorageService;

    public RecipeService(@Qualifier("MongoAtlas-Recipes") RecipeDao recipeDao, RecipeRepository recipeRepository,
                         ImageStorageService imageStorageService) {
        this.recipeDao = recipeDao;
        this.recipeRepository = recipeRepository;
        this.imageStorageService = imageStorageService;
    }

    public void addRecipe(Recipe recipe, List<MultipartFile> images) throws IOException {
        RecipeViewer caller = currentViewer();
        String callerUsername = caller.username();
        boolean isAdmin = caller.admin();

        recipe.setCreatedBy(callerUsername);
        if (!isAdmin) { // Curated recipes from the platform don't get a specific ownerId, only custom recipes
            recipe.setOwnerId(callerUsername);
        }

        log.info("Adding recipe {} by user {} (admin: {})", recipe.getTitle(), callerUsername, isAdmin);
        if (images != null && !images.isEmpty()) {
            log.info("Adding {} image files for recipe \"{}\"", images.size(), recipe.getTitle());
            // Keep the image URLs the client already sent (e.g. stock photos) and append the uploaded ones.
            List<String> imagePaths = recipe.getImagePaths() == null
                    ? new ArrayList<>()
                    : new ArrayList<>(recipe.getImagePaths());
            imagePaths.addAll(imageStorageService.upload(images));
            log.info("Adding image paths to recipe \"{}\": {}", recipe.getTitle(), imagePaths);
            recipe.setImagePaths(imagePaths);
        }
        this.recipeDao.insert(recipe);
    }

    /** The current caller as a recipe viewer; anonymous requests get {@link RecipeViewer#ANONYMOUS}. */
    private RecipeViewer currentViewer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return RecipeViewer.ANONYMOUS;
        }
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(UserRole.RoleName.ROLE_ADMIN.name()));
        return new RecipeViewer(auth.getName(), isAdmin); // name = the JWT subject
    }

    public List<Recipe> getAllRecipes() {
        return this.recipeDao.findAll(currentViewer());
    }

    /** Empty when the recipe doesn't exist or the caller may not see it (indistinguishable on purpose). */
    public Optional<Recipe> getRecipeById(String id) {
        RecipeViewer viewer = currentViewer();
        return this.recipeDao.findById(id).filter(viewer::canView);
    }

    public List<RecipeSummary> getRecipeSummaryByIds(Collection<String> ids) {
        // The summary projection carries no ownerId/visibility, so resolve the visible ids first.
        Set<String> visibleIds = this.recipeDao.findByIds(ids, currentViewer()).stream()
            .map(Recipe::getId)
            .collect(Collectors.toSet());
        return visibleIds.isEmpty() ? List.of() : this.recipeRepository.findByIdIn(visibleIds);
    }

    public List<Recipe> search(String searchTerm){
        return this.recipeDao.search(searchTerm, currentViewer());
    } 

    public void deleteRecipe(String id) {
        this.recipeDao.deleteById(id);
    }

    public void updateRecipe(String id, Recipe newRecipe) {
        this.recipeDao.updateById(id, newRecipe);
    }

    public List<Recipe> findRecipeByIngredients(String[] ingredients) {
        return this.recipeDao.selectByIngredients(ingredients, currentViewer());
    }

    public List<String> getDistinctIngredientNames() {
        return this.recipeDao.selectDistinctIngredientNames();
    }
}
