package com.simongig.recipesapp.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simongig.recipesapp.model.MealPlan;
import com.simongig.recipesapp.model.MealPlanEntry;
import com.simongig.recipesapp.model.MealPlanGenerationJob;
import com.simongig.recipesapp.service.MealPlanGenerationService;
import com.simongig.recipesapp.service.MealPlanService;

@RequestMapping("api/v1/mealplans")
@RestController
public class MealPlannerController {

    private final MealPlanService mealPlanService;
    private final MealPlanGenerationService mealPlanGenerationService;

    public MealPlannerController(MealPlanService mealPlanService, MealPlanGenerationService mealPlanGenerationService) {
        this.mealPlanService = mealPlanService;
        this.mealPlanGenerationService = mealPlanGenerationService;
    }

    @GetMapping
    public MealPlan getMealPlan() {
        return mealPlanService.getPlanForCurrentUser();
    }

    @PostMapping("/entries")
    public MealPlan upsertEntry(@RequestBody MealPlanEntry entry) {
        return mealPlanService.upsertEntry(entry);
    }

    @DeleteMapping("/entries")
    public MealPlan removeEntry(@RequestParam String date, @RequestParam MealPlanEntry.MealSlot mealSlot) {
        return mealPlanService.removeEntry(date, mealSlot);
    }

    @PostMapping
    public ResponseEntity<MealPlanGenerationJob> generate(@RequestBody GenerateMealPlanRequest request) {
        MealPlanGenerationJob job = mealPlanGenerationService.startGeneration(request.query());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(job);
    }

    @GetMapping("/{jobId}")
    public MealPlanGenerationJob getGenerationJob(@PathVariable String jobId) {
        return mealPlanGenerationService.getJob(jobId);
    }

    public record GenerateMealPlanRequest(String query) {}
}
