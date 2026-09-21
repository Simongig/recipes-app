package com.simongig.recipesapp;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.simongig.recipesapp.dao.MealPlanDao;
import com.simongig.recipesapp.dao.UserDao;
import com.simongig.recipesapp.model.MealPlanEntry;
import com.simongig.recipesapp.model.MealPlanEntry.MealSlot;
import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.service.MealPlanService;
import com.simongig.recipesapp.service.UserService;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
@EnableMongoRepositories(basePackages = "com.simongig.recipesapp.dao")
@Slf4j
public class SpringConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Bean
    public MongoClient mongoClient() {
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        return MongoClients.create(MongoClientSettings.builder()
                                                      .applyConnectionString(new ConnectionString(connectionString))
                                                      .codecRegistry(codecRegistry)
                                                      .build());
    }

    @Value("${cloudflare.r2.endpoint}")
    private String r2Endpoint;
    @Value("${cloudflare.r2.access-key}")
    private String r2AccessKey;
    @Value("${cloudflare.r2.secret-key}")
    private String r2SecretKey;

    // Cloudflare R2 speaks the S3 API, so the AWS SDK works against it with three tweaks:
    // a custom endpoint, the fixed pseudo-region "auto", and path-style addressing.
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(r2AccessKey, r2SecretKey);

        log.info("Creating S3Client for R2 with endpoint {}, access key {}, secret key {}",
                r2Endpoint, r2AccessKey, r2SecretKey.replaceAll(".", "*"));

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(r2Endpoint))
                .region(Region.of("auto"))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                // Since SDK 2.30 the default is to add CRC32 trailer checksums to every upload,
                // which R2 rejects. Only send/validate checksums when an operation requires them.
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .build();
    }

    @Component
    public class DataInitializer implements CommandLineRunner {

        private final UserService userService;
        private final UserDao userDao;
        private final MealPlanService mealPlanService;
        private final MealPlanDao mealPlanDao;

        @Value("${app.admin.username}")
        private String adminUsername;

        @Value("${app.admin.password}")
        private String adminPassword;

        private final String mealPlanId = "default-meal-plan";
        private final Map<String, String> defaultMeals = Map.of(
            MealSlot.BREAKFAST.name(), "6a4bce9c8d5ae780d0814a51",
            MealSlot.LUNCH.name(), "6a4bce9c8d5ae780d0814a52",
            MealSlot.DINNER.name(), "6a4bce9c8d5ae780d0814a50",
            MealSlot.SNACK.name(), "6a4bce9c8d5ae780d0814a53"
        );

        private final int defaultPlanLength = 7; // days
        
        public DataInitializer(UserService userService, UserDao userDao, MealPlanService mealPlanService, MealPlanDao mealPlanDao) {
            this.userService = userService;
            this.userDao = userDao;
            this.mealPlanService = mealPlanService;
            this.mealPlanDao = mealPlanDao;
        }

        private LocalDate getNextMonday() {
            LocalDate today = LocalDate.now();
            return today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        }

        private List<MealPlanEntry> createDefaultEntries(LocalDate startDate) {
            List<MealPlanEntry> entries = new ArrayList<>();
            for (int i = 0; i < defaultPlanLength; i++) {
                LocalDate date = startDate.plusDays(i);
                entries.add(new MealPlanEntry(date, new HashMap<>(defaultMeals)));
            }
            return entries;
        }

        @Override
        public void run(String... args) {
            log.info("Checking for admin user...");
            if (userDao.findById(adminUsername).isEmpty()) {
                log.info("Creating Admin user with username: {}", this.adminUsername);
                User admin = new User(adminUsername, "Admin", "", "", adminPassword, null);
                userService.saveAsAdmin(admin); // see below
            }

            if (mealPlanDao.findById(mealPlanId).isEmpty()) {
                log.info("Creating default meal plan with ID: {}", mealPlanId);
                LocalDate startDate = getNextMonday();
                LocalDate endDate = startDate.plusDays(defaultPlanLength - 1);
                mealPlanService.createMealPlan(adminUsername, mealPlanId, createDefaultEntries(startDate),
                        startDate, endDate, null);
            }
        }
    }

}