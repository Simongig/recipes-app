package com.simongig.recipesapp.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import com.simongig.recipesapp.dao.RecipeDao;
import com.simongig.recipesapp.dao.RecipeRepository;
import com.simongig.recipesapp.dao.RecipeViewer;
import com.simongig.recipesapp.model.Recipe;
import com.simongig.recipesapp.model.Recipe.RecipeVisibility;

@ExtendWith(MockitoExtension.class)
class RecipeServiceVisibilityTest {

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
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private static Recipe recipe(String ownerId, RecipeVisibility visibility) {
        Recipe recipe = new Recipe("Pasta", 20, List.of(), List.of(), 2, List.of());
        recipe.setOwnerId(ownerId);
        recipe.setVisibility(visibility);
        return recipe;
    }

    private static void loginAs(String username, String... roles) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.createAuthorityList(roles)));
    }

    private static void anonymous() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
    }

    // --- the rule itself ---

    @Test
    void anonymousSeesPublicAndCuratedButNotPrivateUserRecipes() {
        RecipeViewer viewer = RecipeViewer.ANONYMOUS;
        assertTrue(viewer.canView(recipe("alice", RecipeVisibility.PUBLIC)));
        assertTrue(viewer.canView(recipe("", RecipeVisibility.PRIVATE)));
        assertTrue(viewer.canView(recipe(null, RecipeVisibility.PRIVATE)));
        assertFalse(viewer.canView(recipe("alice", RecipeVisibility.PRIVATE)));
        assertFalse(viewer.canView(recipe("alice", RecipeVisibility.STAGING)));
        assertFalse(viewer.canView(recipe("alice", null))); // missing field counts as PRIVATE
    }

    @Test
    void ownerSeesOwnPrivateRecipeButNotSomeoneElses() {
        RecipeViewer alice = new RecipeViewer("alice", false);
        assertTrue(alice.canView(recipe("alice", RecipeVisibility.PRIVATE)));
        assertFalse(alice.canView(recipe("bob", RecipeVisibility.PRIVATE)));
    }

    @Test
    void adminSeesEverythingAndListQueriesAreUnfiltered() {
        RecipeViewer admin = new RecipeViewer("admin", true);
        assertTrue(admin.canView(recipe("bob", RecipeVisibility.PRIVATE)));
        assertNull(admin.filter());
    }

    // --- service wiring ---

    @Test
    void getRecipeByIdHidesPrivateRecipesFromAnonymousCallers() {
        anonymous();
        when(recipeDao.findById("1")).thenReturn(Optional.of(recipe("alice", RecipeVisibility.PRIVATE)));

        assertTrue(service.getRecipeById("1").isEmpty());
    }

    @Test
    void getRecipeByIdReturnsOwnPrivateRecipe() {
        loginAs("alice", "ROLE_USER");
        Recipe own = recipe("alice", RecipeVisibility.PRIVATE);
        when(recipeDao.findById("1")).thenReturn(Optional.of(own));

        assertEquals(Optional.of(own), service.getRecipeById("1"));
    }

    @Test
    void listQueriesPassTheCallerAsViewer() {
        loginAs("alice", "ROLE_USER");
        ArgumentCaptor<RecipeViewer> viewer = ArgumentCaptor.forClass(RecipeViewer.class);

        service.getAllRecipes();
        service.search("pasta");
        service.findRecipeByIngredients(new String[] {"tomate"});

        verify(recipeDao).findAll(viewer.capture());
        verify(recipeDao).search(any(), viewer.capture());
        verify(recipeDao).selectByIngredients(any(), viewer.capture());
        viewer.getAllValues().forEach(v -> assertEquals(new RecipeViewer("alice", false), v));
    }

    @Test
    void listQueriesWithoutAuthenticationUseTheAnonymousViewer() {
        anonymous();
        service.getAllRecipes();
        verify(recipeDao).findAll(RecipeViewer.ANONYMOUS);
    }

    @Test
    void summariesByIdsOnlyIncludeVisibleRecipes() {
        anonymous();
        when(recipeDao.findByIds(anyCollection(), any())).thenReturn(List.of());

        assertEquals(List.of(), service.getRecipeSummaryByIds(List.of("hidden")));
        verify(recipeDao).findByIds(List.of("hidden"), RecipeViewer.ANONYMOUS);
    }
}
