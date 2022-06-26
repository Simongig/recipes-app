package com.simongig.recipesapp;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.model.UserRole;
import com.simongig.recipesapp.service.UserService;

@SpringBootApplication
public class RecipesAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipesAppApplication.class, args);
    }
}