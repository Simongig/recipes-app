package com.simongig.recipesapp.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ingredient {

    @Id
    @BsonId
    private String name;
    private double quantity;
    private String unit;
    private String type;
    private String category;
    private Integer popularity;

    public Ingredient() {}

    public Ingredient(@JsonProperty("name") String name, @JsonProperty("quantity") double quantity,
            @JsonProperty("unit") String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.popularity = 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Ingredient other = (Ingredient) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    
    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public void increasePopularityBy(Integer increaseValue) {
        this.popularity += increaseValue;
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %s", unit, quantity, name);
    }
}
