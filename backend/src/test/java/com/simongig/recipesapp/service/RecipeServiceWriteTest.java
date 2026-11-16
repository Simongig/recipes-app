package com.simongig.recipesapp.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import com.simongig.recipesapp.dao.RecipeDao;
import com.simongig.recipesapp.dao.RecipeRepository;
import com.simongig.recipesapp.model.Recipe;
import com.simongig.recipesapp.model.Recipe.RecipeVisibility;

@ExtendWith(MockitoExtension.class)
class RecipeServiceWriteTest {

    private static final String BUCKET = "https://images.example.test";

    @Mock
    private RecipeDao recipeDao;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private ImageStorageService imageStorageService;

    private RecipeService service;

    @BeforeEach
    void setUp() {
        service = new RecipeService(recipeDao, recipeRepository, imageStorageService);
        lenient().when(imageStorageService.isHostedUrl(any()))
                .thenAnswer(call -> String.valueOf(call.<String>getArgument(0)).startsWith(BUCKET + "/"));
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private static void loginAs(String username, String... roles) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.createAuthorityList(roles)));
    }

    private static Recipe recipe(String ownerId, RecipeVisibility visibility, String... imagePaths) {
        Recipe recipe = new Recipe("Pasta", 20, List.of(), List.of(), 2, List.of(imagePaths));
        recipe.setOwnerId(ownerId);
        recipe.setVisibility(visibility);
        return recipe;
    }

    private HttpStatus statusOf(Runnable call) {
        return HttpStatus.valueOf(assertThrows(ResponseStatusException.class, call::run).getStatusCode().value());
    }

    // --- delete ---

    @Test
    void ownerMayDeleteOwnRecipe() {
        loginAs("alice", "ROLE_USER");
        when(recipeDao.findById("1")).thenReturn(Optional.of(recipe("alice", RecipeVisibility.PRIVATE)));

        service.deleteRecipe("1");

        verify(recipeDao).deleteById("1");
    }

    @Test
    void userMayNotDeleteSomeoneElsesPublicRecipe() {
        loginAs("alice", "ROLE_USER");
        when(recipeDao.findById("1")).thenReturn(Optional.of(recipe("bob", RecipeVisibility.PUBLIC)));

        assertEquals(HttpStatus.FORBIDDEN, statusOf(() -> service.deleteRecipe("1")));
        verify(recipeDao, never()).deleteById(any());
    }

    @Test
    void userMayNotDeleteCuratedRecipes() {
        loginAs("alice", "ROLE_USER");
        when(recipeDao.findById("1")).thenReturn(Optional.of(recipe("", RecipeVisibility.PRIVATE)));

        assertEquals(HttpStatus.FORBIDDEN, statusOf(() -> service.deleteRecipe("1")));
        verify(recipeDao, never()).deleteById(any());
    }

    @Test
    void someoneElsesPrivateRecipeLooksNonExistent() {
        loginAs("alice", "ROLE_USER");
        when(recipeDao.findById("1")).thenReturn(Optional.of(recipe("bob", RecipeVisibility.PRIVATE)));

        assertEquals(HttpStatus.NOT_FOUND, statusOf(() -> service.deleteRecipe("1")));
        verify(recipeDao, never()).deleteById(any());
    }

    @Test
    void adminMayDeleteAnyRecipe() {
        loginAs("admin", "ROLE_ADMIN");
        when(recipeDao.findById("1")).thenReturn(Optional.of(recipe("bob", RecipeVisibility.PRIVATE)));

        service.deleteRecipe("1");

        verify(recipeDao).deleteById("1");
    }

    // --- add ---

    @Test
    void addReplacesAClientSuppliedIdAndSetsTheOwner() throws Exception {
        loginAs("alice", "ROLE_USER");
        Recipe incoming = recipe("bob", RecipeVisibility.PRIVATE);
        incoming.setId("existing-id-of-another-recipe");

        service.addRecipe(incoming, null);

        ArgumentCaptor<Recipe> saved = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeDao).insert(saved.capture());
        assertNotEquals("existing-id-of-another-recipe", saved.getValue().getId());
        assertEquals("alice", saved.getValue().getOwnerId());
    }

    @Test
    void addAcceptsBucketAndStockImageUrls() throws Exception {
        loginAs("alice", "ROLE_USER");

        service.addRecipe(recipe("", RecipeVisibility.PRIVATE,
                BUCKET + "/recipes/a.png", "https://images.unsplash.com/photo-1"), null);

        verify(recipeDao).insert(any());
    }

    @Test
    void addRejectsForeignImageUrls() {
        loginAs("alice", "ROLE_USER");

        for (String url : List.of("https://evil.example/x.png", "javascript:alert(1)",
                "https://images.unsplash.com.evil.example/x.png")) {
            assertEquals(HttpStatus.BAD_REQUEST, statusOf(() -> {
                try {
                    service.addRecipe(recipe("", RecipeVisibility.PRIVATE, url), null);
                } catch (java.io.IOException e) {
                    throw new IllegalStateException(e);
                }
            }));
        }
        verify(recipeDao, never()).insert(any());
    }
}
