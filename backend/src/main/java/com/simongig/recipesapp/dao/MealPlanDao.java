package com.simongig.recipesapp.dao;

import java.util.Optional;

import com.simongig.recipesapp.model.MealPlan;

public interface MealPlanDao {

    Optional<MealPlan> findByOwnerId(String ownerId);

    Optional<MealPlan> findById(String id);

    void save(MealPlan plan);

    Optional<MealPlan> findLastCreatedByOwnerId(String ownerId);
}
