package com.simongig.recipesapp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongig.recipesapp.model.Recipe;
import com.simongig.recipesapp.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("api/v1/recipe")
@RestController
public class RecipeController {
    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @RequestMapping(value = "/add", consumes = "multipart/form-data")
    public void addRecipe(@RequestPart String data, @RequestPart MultipartFile[] images) throws Exception {
        System.out.println("---------- new Request ----------");
        System.out.println(images.length);
        System.out.println(data);
        // JSONObject jsonObject = new JSONObject(data);
        // recipeService.addRecipe(new Recipe(name, duration, PreparationSteps,
        // Ingredients, imagePaths)));
    }

    @GetMapping("/all")
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("{id}")
    public Recipe getRecipe(@PathVariable("id") String id) {
        return recipeService.getRecipeById(id)
                .orElse(null);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteRecipeById(@PathVariable("id") String id) {
        recipeService.deleteRecipe(id);
    }

    @PutMapping("/update/{id}")
    public void updateRecipeById(@PathVariable("id") String id, @RequestBody Recipe recipeToUpdate) {
        recipeService.updateRecipe(id, recipeToUpdate);
    }
}
