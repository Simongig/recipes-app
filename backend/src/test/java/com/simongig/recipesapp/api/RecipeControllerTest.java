package com.simongig.recipesapp.api;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import org.mockito.Captor;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.simongig.recipesapp.model.Recipe;
import com.simongig.recipesapp.service.RecipeService;

/**
 * Standalone MockMvc: no Spring context (and no Mongo), but the real MVC request mapping and
 * multipart/JSON argument resolution. Building the mapping also fails on duplicate
 * "/add" handlers, which is how the controller used to break at startup.
 */
@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    private static final String RECIPE_JSON = """
            {"title":"Pasta","duration":20,"portions":2,"preparationSteps":[],"ingredients":[],
             "imagePaths":["https://images.unsplash.com/stock.jpg"]}""";

    @Mock
    private RecipeService recipeService;

    @Captor
    private ArgumentCaptor<List<MultipartFile>> imagesCaptor;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RecipeController(recipeService)).build();
    }

    private static MockMultipartFile dataPart() {
        return new MockMultipartFile("data", "blob", "application/json", RECIPE_JSON.getBytes());
    }

    @Test
    void passesTheRecipeAndAllUploadedImagesToTheService() throws Exception {
        mockMvc.perform(multipart("/api/v1/recipe/add")
                        .file(dataPart())
                        .file(new MockMultipartFile("images", "a.png", "image/png", new byte[] {1}))
                        .file(new MockMultipartFile("images", "b.jpg", "image/jpeg", new byte[] {2})))
                .andExpect(status().isOk());

        ArgumentCaptor<Recipe> recipe = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeService).addRecipe(recipe.capture(), imagesCaptor.capture());

        assertEquals("Pasta", recipe.getValue().getTitle());
        assertEquals(List.of("https://images.unsplash.com/stock.jpg"), recipe.getValue().getImagePaths());
        assertEquals(List.of("a.png", "b.jpg"),
                imagesCaptor.getValue().stream().map(MultipartFile::getOriginalFilename).toList());
    }

    @Test
    void acceptsARecipeWithoutAnyImageParts() throws Exception {
        mockMvc.perform(multipart("/api/v1/recipe/add").file(dataPart()))
                .andExpect(status().isOk());

        verify(recipeService).addRecipe(any(Recipe.class), isNull());
    }
}
