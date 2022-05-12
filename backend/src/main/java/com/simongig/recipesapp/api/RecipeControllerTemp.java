package com.simongig.recipesapp.api;

import com.simongig.recipesapp.model.Recipe;
import com.simongig.recipesapp.service.RecipeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

@RequestMapping("api/v0/recipe")
@RestController
public class RecipeControllerTemp {
    private final RecipeService recipeService;
    @Autowired
    public RecipeControllerTemp(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    @PostMapping("/add")
    public void addRecipe(@RequestPart Recipe data, @RequestPart ArrayList<MultipartFile> images) throws Exception {
        System.out.println("---------- new Request at v0/add ----------");
        System.out.println(images.size());
        System.out.println(data);
        // System.out.println(data.getImagePaths());
        // for(MultipartFile image: images) {
        //     System.out.println(image.getOriginalFilename());
        //     data.addImagePath(image.getOriginalFilename());
        // }
        
        // recipeService.addRecipe(recipe)
        // JSONObject jsonObject = new JSONObject(data);
        // recipeService.addRecipe(new Recipe(name, duration, PreparationSteps,
        // Ingredients, imagePaths)));
    }
}
