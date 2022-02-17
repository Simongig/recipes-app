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
    public IngredientService(@Qualifier("mongodb-IngredientDB") IngredientDao ingredientDao) {
        this.ingredientDao = ingredientDao;
    }

    public int addIngredientName(IngredientName ingredientName) {
        return this.ingredientDao.insertIngredientName(ingredientName);
    }

    public List<IngredientName> getAllRIngredientNames() {
        for(IngredientName ingredient: this.ingredientDao.selectAllIngredientNames()) {
            System.out.println(ingredient);
        }
        
        return this.ingredientDao.selectAllIngredientNames();
    }

    public Optional<IngredientName> getIngredientNameById(String id) {
        return this.ingredientDao.selectIngredientNameById(id);
    }

    public int deleteIngredientName(String id) {
        return this.ingredientDao.deleteIngredientNameById(id);
    }

    public int updateIngredientName(String id, int popularity) {
        //@TODO maybe implement
        return 0;
    }

    public int increaseIngredientNamePopularityByName(String id, int popularityIncrease) {
        return this.ingredientDao.increaseIngredientNamePopularityById(id, popularityIncrease);
    }
    
    public int incrementIngredientNamePopularityByName(String id) {
        return this.ingredientDao.incrementIngredientNamePopularityById(id);
    }

}
