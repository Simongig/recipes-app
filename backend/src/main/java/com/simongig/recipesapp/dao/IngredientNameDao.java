package com.simongig.recipesapp.dao;

import java.util.List;
import java.util.Optional;

import com.simongig.recipesapp.model.IngredientName;

public interface IngredientNameDao {

    default void insertIngredientName(IngredientName ingredientName) {
        insertIngredientName(ingredientName);
    }

    List<IngredientName> selectAllIngredientNames();

    Optional<IngredientName> selectIngredientNameByName(String name);

    void deleteIngredientNameById(String id);

    void updateIngredientNamePopularityById(String id, int popularity);

    void increaseIngredientNamePopularityById(String id, int popularityIncrease);

    void incrementIngredientNamePopularityById(String id);
}
