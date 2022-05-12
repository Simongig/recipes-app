package com.simongig.recipesapp.dao;

import java.util.List;
import java.util.Optional;

import com.simongig.recipesapp.model.IngredientName;

public interface IngredientDao {

    default int insertIngredientName(IngredientName ingredientName) {
        return insertIngredientName(ingredientName);
    }

    List<IngredientName> selectAllIngredientNames();

    Optional<IngredientName> selectIngredientNameById(String id);

    int deleteIngredientNameById(String id);

    int updateIngredientNamePopularityById(String id, int popularity);

    int increaseIngredientNamePopularityById(String id, int popularityIncrease);

    int incrementIngredientNamePopularityById(String id);
}
