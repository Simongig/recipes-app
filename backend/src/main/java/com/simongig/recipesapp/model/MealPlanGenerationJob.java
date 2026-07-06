package com.simongig.recipesapp.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

// Shared with ml-service: FastAPI writes status/result fields directly into this
// same MongoDB document (collection "MealPlanGenerationJob") via pymongo, using the
// exact field names below. Keep the two in sync if this shape changes.
public class MealPlanGenerationJob {

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_DONE = "done";
    public static final String STATUS_FAILED = "failed";

    @Id
    @BsonId
    private String id;

    private String ownerId;
    private String query;
    private String status;
    private List<String> recipeIds;
    private String decision;
    private String error;
    private Instant createdAt;
    private Instant updatedAt;

    public MealPlanGenerationJob() {}

    public MealPlanGenerationJob(String ownerId, String query) {
        this.id = new ObjectId().toString();
        this.ownerId = ownerId;
        this.query = query;
        this.status = STATUS_PENDING;
        this.recipeIds = new ArrayList<>();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getRecipeIds() {
        return recipeIds;
    }

    public void setRecipeIds(List<String> recipeIds) {
        this.recipeIds = recipeIds;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
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
}
