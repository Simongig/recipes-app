package com.simongig.recipesapp.model;

public enum Unit {
    PIECE("Stück", "Stk."),
    TEASPOON("Teelöffel", "TL"),
    TABLESPOON("Esslöffel", "EL"),
    GRAM("Gramm", "g"),
    KILOGRAM("Kilo", "kg"),
    POUND("Pfund", "Pfd."),
    LITER("Liter", "l"),
    MILLILITER("Milliliter", "ml"),
    CENTILITER("Zentiliter", "cl"),
    PINCH("Prise", "Prise"),
    BUNCH("Bündel", "Bund"),
    CLOVE("Zehe", "Zehe"),
    SLICE("Scheibe", "Scheibe"),
    CAN("Dose", "Dose"),
    PACKAGE("Packung", "Pck."),
    CUP("Tasse", "Tasse"),
    SPRIG("Zweig", "Zweig"),
    STICK("Stange", "Stange"),
    HANDFUL("Handvoll", "Handvoll"),
    LEAF("Blatt", "Blatt"),
    CUBE("Würfel", "Würfel");

    private final String label;
    private final String abbreviation;

    Unit(String label, String abbreviation) {
        this.label = label;
        this.abbreviation = abbreviation;
    }

    public String getLabel() {
        return label;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
