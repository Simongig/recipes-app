package com.simongig.recipesapp.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class MealPlan {

    @Id
    @BsonId
    private String id;

    private String ownerId; // username; one plan document per user
    private LocalDate startDate;
    private LocalDate endDate;
    private List<MealPlanEntry> entries;
    private MealPlanProvenance provenance; // null for manually built plans

    public MealPlan() {}

    public MealPlan(String ownerId, String id, List<MealPlanEntry> entries, LocalDate startDate, LocalDate endDate, MealPlanProvenance provenance) {
        this.id = id != null && !id.isEmpty() ? id : new ObjectId().toString();
        this.ownerId = ownerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.entries = entries != null ? entries : new ArrayList<>();
        this.provenance = provenance;
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

    public MealPlanProvenance getProvenance() {
        return provenance;
    }

    public void setProvenance(MealPlanProvenance provenance) {
        this.provenance = provenance;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
