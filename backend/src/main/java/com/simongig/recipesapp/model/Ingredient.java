package com.simongig.recipesapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.simongig.recipesapp.util.IngredientNormalizer;

public class Ingredient {

    private String name;
    private String normalizedKey;
    private double quantity;
    private Unit unit;

    public Ingredient() {}

    public Ingredient(@JsonProperty("name") String name, @JsonProperty("quantity") double quantity,
            @JsonProperty("unit") Unit unit) {
        this.setName(name);
        this.quantity = quantity;
        this.unit = unit;
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

    public void setName(String name) {
        this.name = name;
        this.normalizedKey = IngredientNormalizer.normalize(name);
    }

    @JsonIgnore
    public String getNormalizedKey() {
        return normalizedKey;
    }

    public void setNormalizedKey(String normalizedKey) {
        this.normalizedKey = normalizedKey;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    // Both computed, not persisted (no backing field, so the Mongo POJO codec ignores them):
    // PIECE reads more naturally without a unit word ("1 Tomate" rather than "1 Stück Tomate").
    public String getUnitLabel() {
        if (unit == null || unit == Unit.PIECE) {
            return "";
        }
        return unit.getLabel();
    }

    public String getUnitAbbreviation() {
        if (unit == null || unit == Unit.PIECE) {
            return "";
        }
        return unit.getAbbreviation();
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %s", unit, quantity, name);
    }
}
