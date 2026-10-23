package com.example.smartpantry.util;

/**
 * Centralized constants used across the app.
 * Includes unit options, navigation keys, and shared strings.
 */
public final class Constants {

    private Constants() {} // Prevent instantiation

    /** Supported unit options for pantry items and recipe ingredients. */
    public static final String[] UNITS = {
        "pcs",   // pieces/count
        "g",     // grams
        "kg",    // kilograms
        "ml",    // milliliters
        "l",     // liters
        "tbsp",  // tablespoons
        "tsp",   // teaspoons
        "cup"    // cups
    };

    /** Unit display names for UI (same order as UNITS). */
    public static final String[] UNIT_LABELS = {
        "Pieces (pcs)",
        "Grams (g)",
        "Kilograms (kg)",
        "Milliliters (ml)",
        "Liters (l)",
        "Tablespoons (tbsp)",
        "Teaspoons (tsp)",
        "Cups (cup)"
    };

    /** Navigation argument keys. */
    public static final String ARG_MODE = "mode";
    public static final String ARG_ITEM_ID = "itemId";
    public static final String ARG_RECIPE_ID = "recipeId";

    /** Fragment mode values. */
    public static final String MODE_ADD = "ADD";
    public static final String MODE_EDIT = "EDIT";

    /** Database name. */
    public static final String DATABASE_NAME = "smart_pantry.db";

    /** Preferences keys. */
    public static final String PREFS_NAME = "smart_pantry_prefs";
    public static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    public static final String KEY_UNIT_SYSTEM = "unit_system";

    /** Default values. */
    public static final boolean DEFAULT_EXPIRY_ALERTS = true;
    public static final String DEFAULT_UNIT_SYSTEM = "METRIC";

    /** Asset file name for seed recipes. */
    public static final String RECIPES_ASSET_FILE = "recipes.json";

    /** Minimum quantity allowed for pantry items. */
    public static final double MIN_QUANTITY = 0.1;

    /** Maximum ingredient name length. */
    public static final int MAX_INGREDIENT_NAME_LENGTH = 50;

    /**
     * Normalizes an ingredient name for matching (delegates to IngredientMatcher).
     * - lowercase
     * - trim whitespace
     * - singularize common plurals
     */
    public static String normalizeName(String name) {
        return IngredientMatcher.normalizeName(name);
    }
}