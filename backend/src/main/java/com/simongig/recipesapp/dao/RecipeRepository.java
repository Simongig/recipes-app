package com.simongig.recipesapp.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.simongig.recipesapp.model.Recipe;

public interface RecipeRepository extends MongoRepository<Recipe, String> {

    List<RecipeSummary> findByIdIn(Collection<String> ids);

    <T> List<T> findByIdIn(Collection<String> ids, Class<T> type);
}
