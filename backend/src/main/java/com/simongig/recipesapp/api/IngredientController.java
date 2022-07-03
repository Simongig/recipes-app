package com.simongig.recipesapp.api;

import java.util.List;

import com.simongig.recipesapp.model.IngredientName;
import com.simongig.recipesapp.service.IngredientNameService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/ingredient")
@RestController
public class IngredientController {
    private final IngredientNameService ingredientService;

    @Autowired
    public IngredientController(IngredientNameService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/all")
    public List<IngredientName> getAllRecipes() {
        return ingredientService.getAllIngredientNames();
    }

    @GetMapping("/{id}")
    public IngredientName getIngredientName(@PathVariable String id) {
        return ingredientService.getIngredientNameById(id)
                .orElse(null);
    }

    // @PutMapping("/{id}")
    // public int incrementIngredientNamePopularityByName(@PathVariable("id") String id) {
    //     return ingredientService.incrementIngredientNamePopularityByName(id);
    // }

    @PutMapping("/{id}")
    public void increaseIngredientNamePopularityByName(@PathVariable("name") String id, @RequestBody int popularityIncrease) {
        ingredientService.increaseIngredientNamePopularityByName(id, popularityIncrease);
    }
}
