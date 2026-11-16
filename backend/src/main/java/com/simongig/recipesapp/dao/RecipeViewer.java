package com.simongig.recipesapp.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import com.simongig.recipesapp.model.Recipe;
import com.simongig.recipesapp.model.Recipe.RecipeVisibility;

/**
 * Who is reading recipes, and therefore which recipes they may see.
 *
 * <p>A recipe is visible when it is PUBLIC, when it has no owner (curated platform recipe,
 * created by an admin — these carry an empty/missing ownerId regardless of their visibility
 * field), or when the viewer owns it. Admins see everything.
 *
 * <p>The rule exists twice — as a Mongo filter for list queries and as {@link #canView} for
 * single documents — and both must stay in sync.
 */
public record RecipeViewer(String username, boolean admin) {

    public static final RecipeViewer ANONYMOUS = new RecipeViewer(null, false);

    public boolean canView(Recipe recipe) {
        if (admin) {
            return true;
        }
        String ownerId = recipe.getOwnerId();
        return recipe.getVisibility() == RecipeVisibility.PUBLIC
                || ownerId == null || ownerId.isEmpty()
                || (username != null && username.equals(ownerId));
    }

    /** Mongo filter equivalent of {@link #canView}; {@code null} means "no restriction". */
    public Bson filter() {
        if (admin) {
            return null;
        }
        List<Bson> clauses = new ArrayList<>(List.of(
                eq("visibility", RecipeVisibility.PUBLIC.name()),
                eq("ownerId", null), // also matches documents without the field
                eq("ownerId", "")));
        if (username != null) {
            clauses.add(eq("ownerId", username));
        }
        return or(clauses);
    }
}
