package com.simongig.recipesapp.model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MealPlanEntry {

    public enum MealSlot { BREAKFAST, LUNCH, DINNER, SNACK }

    private LocalDate date;
    private Map<String, String> meals; // keyed by MealSlot.name() — BSON map keys must be strings

    public MealPlanEntry() {}

    public MealPlanEntry(
            @JsonProperty("date") LocalDate date,
            @JsonProperty("meals") Map<String, String> meals) {
        this.date = date;
        this.meals = new TreeMap<>(Comparator.comparingInt(key -> MealSlot.valueOf(key).ordinal()));
        if (meals != null) {
            this.meals.putAll(meals);
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, String> getMeals() {
        return meals;
    }

    public void setMeals(Map<String, String> meals) {
        this.meals = new TreeMap<>(Comparator.comparingInt(key -> MealSlot.valueOf(key).ordinal()));
        if (meals != null) {
            this.meals.putAll(meals);
        }
    }

    public void setMeal(MealSlot slot, String recipeId) {
        this.meals.put(slot.name(), recipeId);
    }

    @Override
    public String toString() {
        return "MealPlanEntry [date=" + date + ", meals=" + meals + "]";
    }
}
