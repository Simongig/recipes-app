package com.simongig.recipesapp;

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