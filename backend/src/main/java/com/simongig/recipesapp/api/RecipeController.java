package com.simongig.recipesapp.api;

import com.simongig.recipesapp.model.Recipe;
import com.simongig.recipesapp.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("api/v1/recipe")
@RestController
public class RecipeController {
    private final RecipeService recipeService;

    @Autowired
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
