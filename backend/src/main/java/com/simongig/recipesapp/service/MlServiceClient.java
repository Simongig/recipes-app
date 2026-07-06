package com.simongig.recipesapp.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MlServiceClient {

    private final RestClient restClient;

    public MlServiceClient(@Value("${ml-service.base-url}") String baseUrl) {
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(
                ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(Duration.ofSeconds(2))
                        .withReadTimeout(Duration.ofSeconds(5)));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    // Fire-and-forget trigger: ml-service is expected to accept the job and process it
    // in the background, writing status/results back into the shared Mongo job document.
    public void triggerGeneration(String jobId, String ownerId, String query) {
        log.info("Triggering meal plan generation for job {} (user: {}, query: {})", jobId, ownerId, query);
        restClient.post()
                .uri("/v1/mealplans/generate")
                .body(new GenerateRequest(jobId, ownerId, query))
                .retrieve()
                .toBodilessEntity();
    }

    private record GenerateRequest(String jobId, String ownerId, String query) {}
}
