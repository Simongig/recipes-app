package com.simongig.recipesapp.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.simongig.recipesapp.dao.MealPlanDao;
import com.simongig.recipesapp.model.MealPlan;
import com.simongig.recipesapp.model.MealPlanEntry;

@Service
public class MealPlanService {

    private final MealPlanDao mealPlanDao;

    public MealPlanService(@Qualifier("MongoAtlas-MealPlans") MealPlanDao mealPlanDao) {
        this.mealPlanDao = mealPlanDao;
    }

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public MealPlan getPlanForCurrentUser() {
        String ownerId = currentUsername();
        return mealPlanDao.findByOwnerId(ownerId).orElseGet(() -> new MealPlan(ownerId));
    }

    public MealPlan upsertEntry(MealPlanEntry entry) {
        MealPlan plan = getPlanForCurrentUser();
        plan.getEntries().removeIf(e -> e.getDate().equals(entry.getDate()) && e.getMealSlot() == entry.getMealSlot());
        plan.getEntries().add(entry);
        mealPlanDao.save(plan);
        return plan;
    }

    public MealPlan removeEntry(String date, MealPlanEntry.MealSlot mealSlot) {
        MealPlan plan = getPlanForCurrentUser();
        plan.getEntries().removeIf(e -> e.getDate().equals(date) && e.getMealSlot() == mealSlot);
        mealPlanDao.save(plan);
        return plan;
    }
}
