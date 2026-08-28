package com.simongig.recipesapp.api;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.simongig.recipesapp.model.Ingredient;
import com.simongig.recipesapp.model.Recipe;
import com.simongig.recipesapp.model.Recipe.RecipeVisibility;
import com.simongig.recipesapp.service.RecipeService;
import com.simongig.recipesapp.service.SpaShellClient;

// Only reached for known social-media crawler user-agents (nginx routes those requests here for
// /recipe/id/*, everyone else gets the static SPA shell directly). Injects per-recipe Open Graph /
// Twitter Card meta tags into the SPA's index.html so link previews show the recipe's title/image
// instead of the generic site defaults.
@RestController
public class RecipeSocialPreviewController {

    private final RecipeService recipeService;
    private final SpaShellClient spaShellClient;
    private final String baseUrl;

    public RecipeSocialPreviewController(RecipeService recipeService, SpaShellClient spaShellClient,
            @Value("${app.base-url}") String baseUrl) {
        this.recipeService = recipeService;
        this.spaShellClient = spaShellClient;
        this.baseUrl = baseUrl;
    }

    @GetMapping(value = "/recipe/id/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> recipeDetail(@PathVariable String id) {
        String shell = spaShellClient.fetchIndexHtml();
        Optional<Recipe> recipeOptional = recipeService.getRecipeById(id);

        if (recipeOptional.isEmpty() || recipeOptional.get().getVisibility() != RecipeVisibility.PUBLIC) {
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(shell);
        }

        Recipe recipe = recipeOptional.get();
        String title = recipe.getTitle() + " - kochbuch.io";
        String description = buildDescription(recipe);
        String image = recipe.getImagePaths() == null || recipe.getImagePaths().isEmpty()
                ? null
                : recipe.getImagePaths().get(0);
        String url = baseUrl + "/recipe/id/" + id;

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML)
                .body(injectMetaTags(shell, title, description, image, url));
    }

    private String buildDescription(Recipe recipe) {
        String ingredientNames = recipe.getIngredients() == null
                ? ""
                : recipe.getIngredients().stream()
                        .map(Ingredient::getName)
                        .filter(name -> name != null && !name.isBlank())
                        .limit(4)
                        .collect(Collectors.joining(", "));

        StringBuilder description = new StringBuilder();
        if (!ingredientNames.isEmpty()) {
            description.append("Mit ").append(ingredientNames).append(". ");
        }
        if (recipe.getDuration() > 0) {
            description.append("Fertig in ").append(recipe.getDuration()).append(" Minuten.");
        }
        return description.length() == 0 ? "Entdecke dieses Rezept auf kochbuch.io" : description.toString().trim();
    }

    private String injectMetaTags(String shell, String title, String description, String image, String url) {
        StringBuilder tags = new StringBuilder();
        tags.append("<title>").append(HtmlUtils.htmlEscape(title)).append("</title>\n");
        tags.append(metaProperty("og:type", "article"));
        tags.append(metaProperty("og:site_name", "kochbuch.io"));
        tags.append(metaProperty("og:title", title));
        tags.append(metaProperty("og:description", description));
        tags.append(metaProperty("og:url", url));
        tags.append(metaName("twitter:card", image != null ? "summary_large_image" : "summary"));
        tags.append(metaName("twitter:title", title));
        tags.append(metaName("twitter:description", description));
        if (image != null) {
            tags.append(metaProperty("og:image", image));
            tags.append(metaName("twitter:image", image));
        }

        String withoutOriginalTitle = shell.replaceFirst("(?is)<title>.*?</title>", "");
        return withoutOriginalTitle.replaceFirst("(?is)</head>", Matcher.quoteReplacement(tags.toString()) + "</head>");
    }

    private String metaProperty(String property, String content) {
        return "<meta property=\"" + property + "\" content=\"" + HtmlUtils.htmlEscape(content) + "\">\n";
    }

    private String metaName(String name, String content) {
        return "<meta name=\"" + name + "\" content=\"" + HtmlUtils.htmlEscape(content) + "\">\n";
    }
}
