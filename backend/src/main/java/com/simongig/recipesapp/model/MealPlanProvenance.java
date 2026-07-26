package com.simongig.recipesapp.model;

// Present only on meal plans produced by a generation job; null for manually
// built plans. Keeps MealPlan itself generation-agnostic.
public class MealPlanProvenance {

    private String jobId;
    private String query;
    private String decision;

    public MealPlanProvenance() {}

    public MealPlanProvenance(String jobId, String query, String decision) {
        this.jobId = jobId;
        this.query = query;
        this.decision = decision;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
}
