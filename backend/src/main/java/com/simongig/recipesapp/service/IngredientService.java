package com.simongig.recipesapp.service;

import java.util.List;
import java.util.Optional;

import com.simongig.recipesapp.dao.IngredientDao;
import com.simongig.recipesapp.model.IngredientName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public class IngredientService {
    
    private final IngredientDao ingredientDao;

    @Autowired
    public IngredientService(@Qualifier("MongoAtlas-Ingredients") IngredientDao ingredientDao) {
        this.ingredientDao = ingredientDao;
    }

    public void addIngredientName(IngredientName ingredientName) {
        this.ingredientDao.insertIngredientName(ingredientName);
    }

    public List<IngredientName> getAllIngredientNames() {
        for(IngredientName ingredient: this.ingredientDao.selectAllIngredientNames()) {
            System.out.println(ingredient);
        }
        
        return this.ingredientDao.selectAllIngredientNames();
    }

    public Optional<IngredientName> getIngredientNameById(String id) {
        return this.ingredientDao.selectIngredientNameById(id);
    }

    public void deleteIngredientName(String id) {
        this.ingredientDao.deleteIngredientNameById(id);
    }

    public void updateIngredientName(String id, int popularity) {
        //@TODO maybe implement
    }

    public void increaseIngredientNamePopularityByName(String id, int popularityIncrease) {
        this.ingredientDao.increaseIngredientNamePopularityById(id, popularityIncrease);
    }
    
    public void incrementIngredientNamePopularityByName(String id) {
        this.ingredientDao.incrementIngredientNamePopularityById(id);
    }

}
