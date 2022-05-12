package com.simongig.recipesapp.dao;

// import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.simongig.recipesapp.model.Ingredient;
import com.simongig.recipesapp.model.IngredientName;
import com.simongig.recipesapp.model.Recipe;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

// import javax.management.Query;
import java.util.List;
import java.util.Optional;

@Repository("mongodb")
public class RecipeAccessService implements RecipeDao {

    MongoOperations mongoOps = new MongoTemplate(MongoClients.create(), "database");
    IngredientAccessService ingredientAccessService = new IngredientAccessService();

    @Override
    public int insertRecipe(Recipe recipe) {
        System.out.println(recipe.getImagePaths());
        for (Ingredient ingredient : recipe.getIngredients()) {
            System.out.println(ingredient);
            System.out.print(ingredient.getName());
            Optional<IngredientName> foundIngredientName = ingredientAccessService
                    .selectIngredientNameById(ingredient.getName());
            if (foundIngredientName.isPresent()) {
                ingredientAccessService.incrementIngredientNamePopularityById(ingredient.getName());
            } else {
                IngredientName newIngredientName = new IngredientName(ingredient.getName());
                ingredientAccessService.insertIngredient(newIngredientName);
            }
        }
        mongoOps.insert(recipe);
        return 1;
    }

    @Override
    public List<Recipe> selectAllRecipes() {
        return mongoOps.findAll(Recipe.class);
    }

    @Override
    public Optional<Recipe> selectRecipeById(String id) {
        return Optional.ofNullable(mongoOps.findById(id, Recipe.class));
    }

    @Override
    public int deleteRecipeById(String id) {
        Recipe recipeToRemove = mongoOps.findById(id, Recipe.class);
        Boolean wasRemoved = mongoOps.remove(recipeToRemove).wasAcknowledged();
        if (wasRemoved) {
            return 1;
        }
        return 0;
    }

    @Override
    public int updateRecipeById(String id, Recipe recipe) {

        return 0;
    }

}
