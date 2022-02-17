package com.simongig.recipesapp.api;

import java.util.List;

import com.simongig.recipesapp.model.IngredientName;
import com.simongig.recipesapp.service.IngredientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/ingredient")
@RestController
public class IngredientController {
    private final IngredientService ingredientService;

    @Autowired
    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/all")
    public List<IngredientName> getAllRecipes() {
        return ingredientService.getAllRIngredientNames();
    }

    @GetMapping("/{id}")
    public IngredientName getIngredientName(@PathVariable("name") String id) {
        return ingredientService.getIngredientNameById(id)
                .orElse(null);
    }

    // @PutMapping("/{id}")
    // public int incrementIngredientNamePopularityByName(@PathVariable("id") String id) {
    //     return ingredientService.incrementIngredientNamePopularityByName(id);
    // }

    @PutMapping("/{id}")
    public int increaseIngredientNamePopularityByName(@PathVariable("name") String id, @RequestBody int popularityIncrease) {
        return ingredientService.increaseIngredientNamePopularityByName(id, popularityIncrease);
    }
}
