package com.simongig.recipesapp.api;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.simongig.recipesapp.model.MealPlan;
import com.simongig.recipesapp.model.MealPlanEntry;
import com.simongig.recipesapp.model.MealPlanGenerationJob;
import com.simongig.recipesapp.model.MealPlanGenerationJobStatus;
import com.simongig.recipesapp.service.MealPlanGenerationService;
import com.simongig.recipesapp.service.MealPlanService;
import com.simongig.recipesapp.service.MealPlanService.MealPlanDto;

import lombok.extern.slf4j.Slf4j;


@RequestMapping("api/v1/mealplans")
@RestController
@Slf4j
public class MealPlannerController {

    private final MealPlanService mealPlanService;
    private final MealPlanGenerationService mealPlanGenerationService;

    @Value("${ml-service.api-key}")
    private String mlServiceApiKey;

    public MealPlannerController(MealPlanService mealPlanService, MealPlanGenerationService mealPlanGenerationService) {
        this.mealPlanService = mealPlanService;
        this.mealPlanGenerationService = mealPlanGenerationService;
    }

    @GetMapping("/current")
    public Optional<MealPlanDto> getMealPlan() {
        MealPlan lastMealPlan = mealPlanService.getLastCreatedMealPlan().orElse(null);
        if (lastMealPlan == null) {
            return Optional.empty();
        }
        return Optional.of(mealPlanService.getMealPlan(lastMealPlan.getId()));
    }

    @GetMapping("/{mealPlanId}")
    public MealPlanDto getMealPlan(@PathVariable String mealPlanId) {
        return mealPlanService.getMealPlan(mealPlanId);
    }

    @PostMapping("/entries")
    public MealPlan upsertEntry(@RequestBody MealPlanEntry entry) {
        return mealPlanService.upsertEntry(entry);
    }

    @DeleteMapping("/entries")
    public MealPlan removeEntry(@RequestParam LocalDate date, @RequestParam MealPlanEntry.MealSlot mealSlot) {
        return mealPlanService.removeEntry(date, mealSlot);
    }

    @PostMapping
    public ResponseEntity<MealPlanGenerationJob> generate(@RequestBody GenerateMealPlanRequest request) {
        MealPlanGenerationJob job = mealPlanGenerationService.startGeneration(
                request.query(), request.startDate(), request.endDate());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(job);
    }

    @GetMapping("/jobs/{jobId}")
    public MealPlanGenerationJob getMealPlanGenerationJob(@PathVariable String jobId) {
        return mealPlanGenerationService.getJob(jobId);
    }

    // Called by ml-service to report generation results. Not user-authenticated (see
    // SecurityConfig — this route is permitAll'd) since ml-service has no user JWT to
    // present; a shared secret takes its place instead.
    @PatchMapping("/jobs/{jobId}")
    public ResponseEntity<Void> updateMealPlanGenerationJob(
            @PathVariable String jobId,
            @RequestHeader(value = "X-ML-Service-API-Key", required = false) String apiKey,
            @RequestBody MealPlanJobPatch patch) {
        if (apiKey == null || !MessageDigest.isEqual(
                apiKey.getBytes(StandardCharsets.UTF_8), mlServiceApiKey.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid ml-service API key");
        }
        log.info("Received meal plan generation job update for job {}: {}", jobId, patch);
        mealPlanGenerationService.updateJob(jobId, patch);
        return ResponseEntity.ok().build();
    }

    public record GenerateMealPlanRequest(String query, LocalDate startDate, LocalDate endDate) {}

    public record MealPlanJobPatch(
        MealPlanGenerationJobStatus status,
        String errorMessage,
        String decision,
        List<MealPlanEntry> mealPlanEntries
    ) {}
}
