package com.simongig.recipesapp.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MealPlan {

    @Id
    @BsonId
    private String id;

    private String ownerId; // username; one plan document per user
    private List<MealPlanEntry> entries;

    public MealPlan() {}

    public MealPlan(String ownerId) {
        this.id = new ObjectId().toString();
        this.ownerId = ownerId;
        this.entries = new ArrayList<>();
    }

    public MealPlan(
            @JsonProperty("ownerId") String ownerId,
            @JsonProperty("entries") List<MealPlanEntry> entries) {
        this.id = new ObjectId().toString();
        this.ownerId = ownerId;
        this.entries = entries != null ? entries : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<MealPlanEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<MealPlanEntry> entries) {
        this.entries = entries;
    }
}
