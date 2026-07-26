package com.simongig.recipesapp.dao;

import java.util.List;

// Closed Spring Data projection: getter names must match Recipe's property
// names exactly, or Spring Data won't populate them.
public interface RecipeSummary {
    String getId();
    String getTitle();
    List<String> getImagePaths();
    int getDuration();
    int getPortions();
}
