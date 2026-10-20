package com.example.smartpantry.util;

import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.data.entity.Recipe;
import com.example.smartpantry.data.entity.RecipeIngredient;
import com.example.smartpantry.data.repository.RecipeRepository;

import java.util.List;
import java.util.Map;

/**
 * Pure logic class for strict recipe-to-pantry matching.
 * Handles:
 *  - Unit normalization (convert all to base units: g, ml, pcs)
 *  - Name normalization (lowercase, singularize plurals)
 *  - Strict matching: ALL recipe ingredients must be present in pantry with sufficient quantity
 *  - "Almost There" matching: recipes missing exactly 1 ingredient
 */
public class IngredientMatcher {

    /** Result of strict matching check for a single recipe. */
    public static class MatchResult {
        public final long recipeId;
        public final boolean isStrictMatch;
        public final int missingCount;
        public final List<String> missingIngredients;

        public MatchResult(long recipeId, boolean isStrictMatch, int missingCount, List<String> missingIngredients) {
            this.recipeId = recipeId;
            this.isStrictMatch = isStrictMatch;
            this.missingCount = missingCount;
            this.missingIngredients = missingIngredients;
        }
    }

    /**
     * Normalizes an ingredient name for matching:
     * - lowercase
     * - trim whitespace
     * - singularize common plurals (tomatoes -> tomato, onions -> onion, etc.)
     */
    public static String normalizeName(String name) {
        // TODO: Implement normalization
        return name != null ? name.trim().toLowerCase() : "";
    }

    /**
     * Converts quantity + unit to base unit (grams for solids, ml for liquids, pcs for count).
     * Returns -1 if unit conversion not supported.
     */
    public static double convertToBaseUnit(double quantity, String unit) {
        // TODO: Implement unit conversion
        return -1;
    }

    /** Checks if two units are compatible (both mass, both volume, or both count). */
    public static boolean areUnitsCompatible(String unit1, String unit2) {
        // TODO: Implement
        return false;
    }

    /** Simple plural-to-singular mapping for common food items. */
    private static String singularize(String word) {
        // TODO: Implement pluralization rules
        return word;
    }

    /** Builds a lookup map of pantry items by normalized name for fast matching. */
    public static Map<String, PantryItem> buildPantryLookup(List<PantryItem> pantryItems) {
        // TODO: Implement
        return null;
    }

    /** Checks if a single recipe can be made from the pantry (strict match). */
    public static MatchResult checkRecipeMatch(Recipe recipe, List<RecipeIngredient> recipeIngredients, Map<String, PantryItem> pantryLookup) {
        // TODO: Implement strict matching logic
        return new MatchResult(recipe.id, false, 0, null);
    }

    /** Evaluates all recipes against current pantry. */
    public static List<MatchResult> matchAllRecipes(List<Recipe> recipes, List<PantryItem> pantryItems, RecipeRepository recipeRepository) {
        // TODO: Implement
        return null;
    }
}