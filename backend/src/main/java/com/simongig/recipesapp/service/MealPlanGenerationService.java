package com.simongig.recipesapp.service;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.simongig.recipesapp.dao.MealPlanGenerationJobDao;
import com.simongig.recipesapp.model.MealPlanGenerationJob;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MealPlanGenerationService {

    private final MealPlanGenerationJobDao jobDao;
    private final MlServiceClient mlServiceClient;

    public MealPlanGenerationService(
            @Qualifier("MongoAtlas-MealPlanGenerationJobs") MealPlanGenerationJobDao jobDao,
            MlServiceClient mlServiceClient) {
        this.jobDao = jobDao;
        this.mlServiceClient = mlServiceClient;
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public MealPlanGenerationJob startGeneration(String query) {
        String ownerId = currentUsername();
        MealPlanGenerationJob job = new MealPlanGenerationJob(ownerId, query);
        jobDao.insert(job);
        log.info("Created meal plan generation job {} for user {}", job.getId(), ownerId);

        // Dispatched off the request thread: the ml-service call only needs to hand the
        // job off (it processes it in the background itself), but a slow/unreachable
        // ml-service must not delay the 202 response.
        CompletableFuture.runAsync(() -> {
            try {
                mlServiceClient.triggerGeneration(job.getId(), ownerId, query);
            } catch (Exception e) {
                jobDao.updateStatus(job.getId(), MealPlanGenerationJob.STATUS_FAILED,
                        "Failed to reach ml-service: " + e.getMessage());
                log.error("Failed to reach ml-service for job {}: {}", job.getId(), e.getMessage());
            }
        });

        return job;
    }

    // TODO: user can only start one generation job at a time, so we should check for existing jobs in progress and return 409 Conflict if one exists.
    public MealPlanGenerationJob getJob(String jobId) {
        String ownerId = currentUsername();
        MealPlanGenerationJob job = jobDao.findById(jobId)
                .orElseThrow(() -> new NoSuchElementException("Job not found: " + jobId));
        log.info("User {} job status: {}", ownerId, job.getStatus());
        if (!ownerId.equals(job.getOwnerId())) {
            throw new AccessDeniedException("Not your job");
        }
        return job;
    }
}
