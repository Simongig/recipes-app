package com.simongig.recipesapp.dao;

import java.util.Optional;

import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import com.simongig.recipesapp.model.MealPlanGenerationJob;

import jakarta.annotation.PostConstruct;

@Repository("MongoAtlas-MealPlanGenerationJobs")
public class MealPlanGenerationJobAccessService_MongoAtlas implements MealPlanGenerationJobDao {

    private final MongoClient client;
    private MongoCollection<MealPlanGenerationJob> jobCollection;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    public MealPlanGenerationJobAccessService_MongoAtlas(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    @PostConstruct
    void init() {
        jobCollection = client.getDatabase(databaseName).getCollection("MealPlanGenerationJob", MealPlanGenerationJob.class);
        if (jobCollection == null) {
            throw new RuntimeException("Failed to get MealPlanGenerationJob collection from MongoDB");
        }
    }

    @Override
    public void insert(MealPlanGenerationJob job) {
        jobCollection.insertOne(job);
    }

    @Override
    public Optional<MealPlanGenerationJob> findById(String jobId) {
        Bson matchId = eq("_id", jobId);
        return Optional.ofNullable(jobCollection.find(matchId).first());
    }

    @Override
    public void save(MealPlanGenerationJob job) {
        Bson matchId = eq("_id", job.getId());
        jobCollection.replaceOne(matchId, job);
    }
}
