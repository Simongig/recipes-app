package com.simongig.recipesapp.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.simongig.recipesapp.model.Recipe;
import com.simongig.recipesapp.service.RecipeService;

@RequestMapping("api/v1/recipe")
@RestController
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("/add")
    //public void addRecipe(@RequestPart Recipe data, @RequestPart ArrayList<MultipartFile> images) throws Exception {
    public void addRecipe(@RequestPart Recipe data,@Nullable @RequestPart ArrayList<MultipartFile> images) throws Exception {
        recipeService.addRecipe(data);
    }

    @GetMapping("/all")
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @PostMapping("/find")
    public List<Recipe> findRecipesByIngredient(@RequestBody String[] ingredients) {
        return recipeService.findRecipeByIngredients(ingredients);
    }

    @PostMapping("/search")
    public List<Recipe> search(@RequestBody String search_term) {
        System.out.println(search_term);
        return recipeService.search(search_term);
    }

    @GetMapping("/id/{id}")
    public Recipe getRecipe(@PathVariable("id") String id) {
        System.out.println(recipeService.getRecipeById(id));
        return recipeService.getRecipeById(id)
                .orElse(null);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteRecipeById(@PathVariable("id") String id) {
        recipeService.deleteRecipe(id);
    }

    // @PutMapping("/update/{id}")
    // public void updateRecipeById(@PathVariable("id") String id, @RequestBody Recipe recipeToUpdate) {
    //     recipeService.updateRecipe(id, recipeToUpdate);
    // }
}
