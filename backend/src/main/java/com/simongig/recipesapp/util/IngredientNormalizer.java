package com.simongig.recipesapp.util;

import java.util.Locale;

public final class IngredientNormalizer {

    private IngredientNormalizer() {}

    public static String normalize(String rawName) {
        return rawName == null ? null : rawName.trim().toLowerCase(Locale.ROOT);
    }
}
