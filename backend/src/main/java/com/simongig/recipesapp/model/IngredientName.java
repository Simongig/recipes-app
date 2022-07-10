package com.simongig.recipesapp.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.annotation.Id;


public class IngredientName {


    @Id
    @BsonId
    private String name;
    private int popularity;
    private String category;


    public IngredientName() {}

    public IngredientName(String name) {
        this.setName(name);
        this.setPopularity(1);
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public void increasePopularity(int increaseValue) {
        this.popularity += increaseValue;
    }

    public void increasePopularity() {
        this.popularity++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
        
    @Override
    public String toString() {
        return String.format("IngredientName: %s with Popularity: %s", name, popularity);
    }

    public void increasePopularityBy(int i) {
        this.popularity += i;
    }
}
