package com.simongig.recipesapp.api;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simongig.recipesapp.model.Unit;
import com.simongig.recipesapp.service.RecipeService;

@RequestMapping("api/v1/ingredient")
@RestController
public class IngredientController {
    private final RecipeService recipeService;

    public IngredientController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    public record UnitOption(String value, String label, String abbreviation) {}

    @GetMapping("/all")
    public List<String> getAllIngredientNames() {
        return recipeService.getDistinctIngredientNames();
    }

    @GetMapping("/units")
    public List<UnitOption> getUnitOptions() {
        return Arrays.stream(Unit.values())
                .map(unit -> new UnitOption(unit.name(), unit.getLabel(), unit.getAbbreviation()))
                .toList();
    }
}
