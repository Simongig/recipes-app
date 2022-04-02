package com.simongig.recipesapp.api;

import com.simongig.recipesapp.model.Recipe;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("api/v0/ingredient")
@RestController
public class RecipeControllerTemp {
    @RequestMapping(value = "/add", consumes = "multipart/form-data")
    public void addRecipe(@RequestPart Recipe data, @RequestPart MultipartFile[] images) throws Exception {
        System.out.println("---------- new Request at v0/add ----------");
        System.out.println(images.length);
        System.out.println(data);
        // JSONObject jsonObject = new JSONObject(data);
        // recipeService.addRecipe(new Recipe(name, duration, PreparationSteps,
        // Ingredients, imagePaths)));
    }
}
