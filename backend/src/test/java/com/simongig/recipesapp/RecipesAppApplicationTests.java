package com.simongig.recipesapp;

import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import com.mongodb.client.MongoClient;

/**
 * Smoke test: the whole application context wires up. Runs on the "test" profile
 * (src/test/resources/application-test.yaml), so it needs neither a database nor real secrets.
 */
@SpringBootTest
@ActiveProfiles("test")
class RecipesAppApplicationTests {

    // Replaces the real client from SpringConfiguration: a real one starts the driver's
    // background monitor, which keeps trying to reach localhost:27017 ("Connection refused").
    // Deep stubs because the DAOs call getDatabase(..).getCollection(..) in @PostConstruct.
    @MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
    private MongoClient mongoClient;

    @Test
    void contextLoads() {
    }

}
