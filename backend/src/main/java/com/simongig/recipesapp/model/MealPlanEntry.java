package com.simongig.recipesapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MealPlanEntry {

    public enum MealSlot { BREAKFAST, LUNCH, DINNER }

    private String date; // ISO-8601, e.g. "2026-07-06"
    private MealSlot mealSlot;
    private String recipeId;

    public MealPlanEntry() {}

    public MealPlanEntry(
            @JsonProperty("date") String date,
            @JsonProperty("mealSlot") MealSlot mealSlot,
            @JsonProperty("recipeId") String recipeId) {
        this.date = date;
        this.mealSlot = mealSlot;
        this.recipeId = recipeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public MealSlot getMealSlot() {
        return mealSlot;
    }

    public void setMealSlot(MealSlot mealSlot) {
        this.mealSlot = mealSlot;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public String toString() {
        return "MealPlanEntry [date=" + date + ", mealSlot=" + mealSlot + ", recipeId=" + recipeId + "]";
    }
}
