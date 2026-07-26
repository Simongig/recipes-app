package com.simongig.recipesapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.simongig.recipesapp.dao.MealPlanDao;
import com.simongig.recipesapp.dao.RecipeRepository;
import com.simongig.recipesapp.dao.RecipeSummary;
import com.simongig.recipesapp.model.MealPlan;
import com.simongig.recipesapp.model.MealPlanEntry;
import com.simongig.recipesapp.model.MealPlanProvenance;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MealPlanService {

    private final MealPlanDao mealPlanDao;
    private final RecipeRepository recipeRepository;

    public MealPlanService(@Qualifier("MongoAtlas-MealPlans") MealPlanDao mealPlanDao, RecipeRepository recipeRepository) {
        this.mealPlanDao = mealPlanDao;
        this.recipeRepository = recipeRepository;
    }

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public MealPlan createMealPlan(String ownerId, String id, List<MealPlanEntry> entries, LocalDate startDate, LocalDate endDate, MealPlanProvenance provenance) {
        MealPlan newPlan = new MealPlan(ownerId, id, entries, startDate, endDate, provenance);
        mealPlanDao.save(newPlan);
        return newPlan;
    }

    public record MealPlanDto(
            String id,
            LocalDate startDate,
            LocalDate endDate,
            List<MealPlanEntry> entries,
            MealPlanProvenance provenance,
            Map<String, RecipeSummary> recipes
    ) {
        public static MealPlanDto of(MealPlan plan, Map<String, RecipeSummary> recipes) {
            return new MealPlanDto(
                    plan.getId(),
                    plan.getStartDate(),
                    plan.getEndDate(),
                    plan.getEntries(),
                    plan.getProvenance(),
                    recipes
            );
        }
    }

    public MealPlanDto getMealPlan(String mealPlanId) {
        Optional<MealPlan> planOptional = mealPlanDao.findById(mealPlanId);
        if (!planOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal plan not found");
        }
        MealPlan plan = planOptional.get();
        if (!plan.getOwnerId().equals(currentUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this meal plan");
        }

        // Map Recipes to IDs in the MealPlanEntry
        Set<String> recipeIds = plan.getEntries().stream()
            .flatMap(e -> e.getMeals().values().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        
        Map<String, RecipeSummary> recipes = recipeRepository.findByIdIn(recipeIds)
            .stream()
            .collect(Collectors.toMap(RecipeSummary::getId, Function.identity()));
        
        MealPlanDto dto = MealPlanDto.of(plan, recipes);
        return dto;
    }

    public Optional<MealPlan> getLastCreatedMealPlan() {
        String ownerId = currentUsername();
        return mealPlanDao.findLastCreatedByOwnerId(ownerId);
    }

    public MealPlan upsertEntry(MealPlanEntry newEntry) {
        Optional<MealPlan> planOptional = getLastCreatedMealPlan();
        if (!planOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No meal plan found for current user");
        }

        MealPlan plan = planOptional.get();
        plan.getEntries().removeIf(e -> e.getDate().equals(newEntry.getDate()));
        plan.getEntries().add(newEntry);
        mealPlanDao.save(plan);
        return plan;
    }

    public MealPlan removeEntry(LocalDate date, MealPlanEntry.MealSlot mealSlot) {
        Optional<MealPlan> planOptional = getLastCreatedMealPlan();
        if (!planOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No meal plan found for current user");
        }

        MealPlan plan = planOptional.get();
        plan.getEntries().stream()
            .filter(e -> e.getDate().isEqual(date))
            .findFirst()
            .ifPresent(entry -> entry.getMeals().remove(mealSlot.name()));
        mealPlanDao.save(plan);
        return plan;
    }
}
