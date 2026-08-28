package com.simongig.recipesapp.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

// Fetches the built SPA's index.html from the frontend service so the backend can inject
// per-recipe Open Graph meta tags into it for social-media crawlers. See RecipeSocialPreviewController.
@Component
@Slf4j
public class SpaShellClient {

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    private final RestClient restClient;
    private volatile String cachedShell;
    private volatile long cachedAtMillis;

    public SpaShellClient(@Value("${frontend.internal-url}") @NonNull String baseUrl) {
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(
                ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(Duration.ofSeconds(2))
                        .withReadTimeout(Duration.ofSeconds(3)));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public String fetchIndexHtml() {
        String cached = cachedShell;
        long now = System.currentTimeMillis();
        if (cached != null && now - cachedAtMillis < CACHE_TTL.toMillis()) {
            return cached;
        }
        try {
            String fresh = restClient.get().uri("/index.html").retrieve().body(String.class);
            cachedShell = fresh;
            cachedAtMillis = now;
            return fresh;
        } catch (Exception e) {
            log.warn("Could not fetch SPA shell from frontend service: {}", e.getMessage());
            if (cached != null) {
                return cached;
            }
            throw e;
        }
    }
}
