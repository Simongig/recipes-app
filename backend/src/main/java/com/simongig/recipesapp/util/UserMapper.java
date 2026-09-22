package com.simongig.recipesapp.util;
import org.springframework.stereotype.Component;

import com.simongig.recipesapp.dao.RecipeRepository;
import com.simongig.recipesapp.model.User;
import com.simongig.recipesapp.service.UserService.UserDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RecipeRepository recipeRepository;

    public UserDTO toDto(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getFullName(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                recipeRepository.findByIdIn(user.getRecipes()),
                recipeRepository.findByIdIn(user.getFavorites())
        );
    }
}