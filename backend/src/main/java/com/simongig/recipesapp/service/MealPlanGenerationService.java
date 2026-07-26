package com.simongig.recipesapp.service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.simongig.recipesapp.api.MealPlannerController.MealPlanJobPatch;
import com.simongig.recipesapp.dao.MealPlanGenerationJobDao;
import com.simongig.recipesapp.model.MealPlanGenerationJob;
import com.simongig.recipesapp.model.MealPlanGenerationJobStatus;
import com.simongig.recipesapp.model.MealPlanProvenance;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MealPlanGenerationService {

    private final MealPlanService mealPlanService;
    private final MealPlanGenerationJobDao jobDao;
    private final MlServiceClient mlServiceClient;

    public MealPlanGenerationService(
            @Qualifier("MongoAtlas-MealPlanGenerationJobs") MealPlanGenerationJobDao jobDao,
            MlServiceClient mlServiceClient, 
            MealPlanService mealPlanService
        ) {
        this.jobDao = jobDao;
        this.mlServiceClient = mlServiceClient;
        this.mealPlanService = mealPlanService;
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public MealPlanGenerationJob startGeneration(String query, LocalDate startDate, LocalDate endDate) {
        String ownerId = currentUsername();
        MealPlanGenerationJob job = new MealPlanGenerationJob(ownerId, query, startDate, endDate);
        jobDao.insert(job);
        log.info("Created meal plan generation job {} for user {}", job.getId(), ownerId);

        // Dispatched off the request thread: the ml-service call only needs to hand the
        // job off (it processes it in the background itself), but a slow/unreachable
        // ml-service must not delay the 202 response.
        CompletableFuture.runAsync(() -> {
            try {
                mlServiceClient.triggerGeneration(job.getId(), ownerId, query);
            } catch (Exception e) {
                job.setStatus(MealPlanGenerationJobStatus.FAILED);
                job.setError("Failed to reach ml-service: " + e.getMessage());
                jobDao.save(job);
                log.error("Failed to reach ml-service for job {}: {}", job.getId(), e.getMessage());
            }
        });

        return job;
    }

    // TODO: user can only start one generation job at a time, so we should check for existing jobs in progress and return 409 Conflict if one exists.
    public MealPlanGenerationJob getJob(String jobId) {
        String ownerId = currentUsername();
        Optional<MealPlanGenerationJob> jobOpt = jobDao.findById(jobId);
        if (jobOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found: " + jobId);
        }
        MealPlanGenerationJob job = jobOpt.get();
        log.info("User {} job status: {}", ownerId, job.getStatus());
        if (!ownerId.equals(job.getOwnerId())) {
            throw new AccessDeniedException("Not your job");
        }
        return job;
    }

    // Called by ml-service to report generation results — no owner in the request
    // context to check against, since the caller isn't a logged-in user.
    public MealPlanGenerationJob updateJob(String jobId, MealPlanJobPatch patch) {
        log.info("Updating job {} with patch: {}", jobId, patch);
        MealPlanGenerationJob job = jobDao.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found: " + jobId));

        if (patch.status() != null) {
            job.setStatus(patch.status());
        }
        if (patch.errorMessage() != null) {
            job.setError(patch.errorMessage());
        }
        jobDao.save(job);

        if (job.getStatus() == MealPlanGenerationJobStatus.COMPLETED) {
            log.info("Job {} completed, saving meal plan for user {}", jobId, job.getOwnerId());
            // Save the generated meal plan to the user's account
            String mealPlanId = job.getMealPlanId();
            MealPlanProvenance provenance = new MealPlanProvenance(job.getId(), job.getQuery(), patch.decision());
            mealPlanService.createMealPlan(job.getOwnerId(), mealPlanId, patch.mealPlanEntries(), job.getStartDate(), job.getEndDate(), provenance);
        }

        log.info("Job {} updated, status now {}", jobId, job.getStatus());
        return job;
    }
}
