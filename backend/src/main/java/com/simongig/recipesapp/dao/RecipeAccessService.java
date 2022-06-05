package com.simongig.recipesapp.dao;

// import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.simongig.recipesapp.model.Ingredient;
import com.simongig.recipesapp.model.IngredientName;
import com.simongig.recipesapp.model.Recipe;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

// import javax.management.Query;
import java.util.List;
import java.util.Optional;

@Repository("mongodb")
public class RecipeAccessService implements RecipeDao {

    MongoOperations mongoOps = new MongoTemplate(MongoClients.create(), "database");
    IngredientAccessService ingredientAccessService = new IngredientAccessService();

    @Override
    public void insertRecipe(Recipe recipe) {
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
    }

    @Override
    public List<Recipe> selectRecipesByIngredients(String[] ingredients) {
        Query query = new Query();
        System.out.println(ingredients[0]);
        Object[] ingredientObjs = (Object[]) ingredients;
        query.addCriteria(Criteria.where("Ingredients.name").in(ingredientObjs));

        System.out.println(mongoOps.find(query, Recipe.class, "recipe"));
        return mongoOps.find(query, Recipe.class, "recipe");
    }

    @Override
    public List<Recipe> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Recipe> findById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteById(String id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int updateById(String id, Recipe recipe) {
        // TODO Auto-generated method stub
        return 0;
    }

}
