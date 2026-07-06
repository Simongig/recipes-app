package com.simongig.recipesapp.dao;

import java.util.Optional;

import com.simongig.recipesapp.model.MealPlan;

public interface MealPlanDao {

    Optional<MealPlan> findByOwnerId(String ownerId);

    void save(MealPlan plan);
}
