package com.simongig.recipesapp.dao;

import java.util.Optional;

import com.simongig.recipesapp.model.MealPlanGenerationJob;

public interface MealPlanGenerationJobDao {

    void insert(MealPlanGenerationJob job);

    Optional<MealPlanGenerationJob> findById(String jobId);

    void updateStatus(String jobId, String status, String error);
}
