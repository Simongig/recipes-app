package com.simongig.recipesapp;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.simongig.recipesapp.dao.UserDao;
import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.service.UserService;

@Configuration
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

        @Value("${app.admin.username}")
        private String adminUsername;

        @Value("${app.admin.password}")
        private String adminPassword;

        public DataInitializer(UserService userService, UserDao userDao) {
            this.userService = userService;
            this.userDao = userDao;
        }

        @Override
        public void run(String... args) {
            if (userDao.findById(adminUsername).isEmpty()) {
                User admin = new User(adminUsername, "Admin", "", "", adminPassword, null);
                userService.saveAsAdmin(admin); // see below
            }
        }
    }

}