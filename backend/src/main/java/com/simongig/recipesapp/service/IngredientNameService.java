package com.simongig.recipesapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.simongig.recipesapp.dao.IngredientNameDao;
import com.simongig.recipesapp.model.IngredientName;


@Service
public class IngredientNameService {
    
    private final IngredientNameDao ingredientNameDao;

    public IngredientNameService(@Qualifier("MongoAtlas-IngredientNames") IngredientNameDao ingredientNameDao) {
        this.ingredientNameDao = ingredientNameDao;
    }

    public void addIngredientName(IngredientName ingredientName) {
        this.ingredientNameDao.insertIngredientName(ingredientName);
    }

    public List<IngredientName> getAllIngredientNames() {
        for(IngredientName ingredient: this.ingredientNameDao.selectAllIngredientNames()) {
            System.out.println(ingredient);
        }
        
        return this.ingredientNameDao.selectAllIngredientNames();
    }

    public Optional<IngredientName> getIngredientNameById(String name) {
        return this.ingredientNameDao.selectIngredientNameByName(name);
    }

    public void deleteIngredientName(String id) {
        this.ingredientNameDao.deleteIngredientNameById(id);
    }

    public void updateIngredientName(String id, int popularity) {
        //@TODO maybe implement
    }

    public void increaseIngredientNamePopularityByName(String id, int popularityIncrease) {
        this.ingredientNameDao.increaseIngredientNamePopularityById(id, popularityIncrease);
    }
    
    public void incrementIngredientNamePopularityByName(String id) {
        this.ingredientNameDao.incrementIngredientNamePopularityById(id);
    }

}
