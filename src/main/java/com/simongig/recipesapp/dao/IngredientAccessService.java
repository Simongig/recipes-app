package com.simongig.recipesapp.dao;

import java.util.List;
import java.util.Optional;

import com.mongodb.client.MongoClients;
import com.simongig.recipesapp.model.IngredientName;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository("mongodb-IngredientDB")
public class IngredientAccessService implements IngredientDao {

    MongoOperations mongoOps = new MongoTemplate(MongoClients.create(), "database");

    public int insertIngredient(IngredientName ingredientName) {
        mongoOps.insert(ingredientName);
        return 1;

    }

    @Override
    public List<IngredientName> selectAllIngredientNames() {
        return mongoOps.findAll(IngredientName.class);
    }

    @Override
    public Optional<IngredientName> selectIngredientNameById(String id) {
        Optional<IngredientName> foundIngredientName = Optional.of(mongoOps.find(new Query(Criteria.where("name").is(id)), IngredientName.class).get(0));
        return foundIngredientName;
    }

    @Override
    public int deleteIngredientNameById(String id) {
        // Optional<IngredientName> foundIngredientName = selectIngredientNameById(id);

        return 0;
    }

    @Override
    public int updateIngredientNamePopularityById(String id, int popularity) {
        Optional<IngredientName> foundIngredientName = selectIngredientNameById(id);
        foundIngredientName.get().setPopularity(popularity);
        mongoOps.updateFirst(new Query(Criteria.where("name").is(id)), Update.update("popularity", foundIngredientName.get().getPopularity()), IngredientName.class);
        return 1;
    }

    @Override
    public int increaseIngredientNamePopularityById(String id, int popularityIncrease) {
        Optional<IngredientName> foundIngredientName = selectIngredientNameById(id);
        foundIngredientName.get().increasePopularity(popularityIncrease);
        mongoOps.updateFirst(new Query(Criteria.where("name").is(id)), Update.update("popularity", foundIngredientName.get().getPopularity()), IngredientName.class);
        return 1;
    }

    @Override
    public int incrementIngredientNamePopularityById(String id) {
        increaseIngredientNamePopularityById(id, 1);
        return 1;
    }
    
}
