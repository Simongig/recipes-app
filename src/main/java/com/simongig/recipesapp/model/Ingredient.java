package com.simongig.recipesapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ingredient {
    private String name;
    private double quantity;
    private String unit;

    public Ingredient(@JsonProperty("name") String name, @JsonProperty("quantity") double quantity, @JsonProperty("unit") String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
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
    
    @Override
    public String toString() {
        return String.format("%s, %s, %s", unit, quantity, name);
    }
}
