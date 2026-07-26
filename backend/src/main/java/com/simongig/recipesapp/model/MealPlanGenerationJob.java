package com.simongig.recipesapp.model;

import java.time.Instant;
import java.time.LocalDate;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

// Shared with ml-service: FastAPI writes status/result fields directly into this
// same MongoDB document (collection "MealPlanGenerationJob") via pymongo, using the
// exact field names below. Keep the two in sync if this shape changes.

public class MealPlanGenerationJob {

    @Id
    @BsonId
    private String id;

    private String ownerId;
    private String query;
    private String MealPlanId;
    private MealPlanGenerationJobStatus status;
    private String error;
    private Instant createdAt;
    private Instant updatedAt;
    private LocalDate startDate;
    private LocalDate endDate;

    public MealPlanGenerationJob() {}

    public MealPlanGenerationJob(String ownerId, String query, LocalDate startDate, LocalDate endDate) {
        this.id = new ObjectId().toString();
        this.ownerId = ownerId;
        this.MealPlanId = new ObjectId().toString();
        this.query = query;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = MealPlanGenerationJobStatus.PENDING;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @JsonProperty("jobId")
    public String getId() {
        return id;
    }

    @JsonProperty("jobId")
    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public MealPlanGenerationJobStatus getStatus() {
        return status;
    }

    public void setStatus(MealPlanGenerationJobStatus status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.setUpdatedNow();
        this.error = error;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUpdatedNow() {
        this.updatedAt = Instant.now();
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

    public String getMealPlanId() {
        return MealPlanId;
    }

    public void setMealPlanId(String MealPlanId) {
        this.MealPlanId = MealPlanId;
    }
}