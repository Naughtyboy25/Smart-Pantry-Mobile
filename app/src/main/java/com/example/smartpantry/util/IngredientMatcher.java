package com.example.smartpantry.util;

import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.data.entity.Recipe;
import com.example.smartpantry.data.entity.RecipeIngredient;
import com.example.smartpantry.data.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Class for strict recipe-to-pantry matching.
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

    /** Unit categories for compatibility checking. */
    private enum UnitCategory {
        MASS, VOLUME, COUNT, UNKNOWN
    }

    /**
     * Normalizes an ingredient name for matching:
     * - lowercase
     * - trim whitespace
     * - singularize common plurals (tomatoes -> tomato, onions -> onion, etc.)
     */
    public static String normalizeName(String name) {
        if (name == null) return "";
        String normalized = name.trim().toLowerCase(Locale.getDefault());
        return singularize(normalized);
    }

    /**
     * Converts quantity + unit to base unit (grams for solids, ml for liquids, pcs for count).
     * Returns -1 if unit conversion not supported.
     */
    public static double convertToBaseUnit(double quantity, String unit) {
        if (unit == null) return -1;
        String u = unit.trim().toLowerCase(Locale.getDefault());

        // Mass (base: grams)
        switch (u) {
            case "g":
            case "gram":
            case "grams":
                return quantity;
            case "kg":
            case "kilogram":
            case "kilograms":
                return quantity * 1000;
        }

        // Volume (base: milliliters)
        switch (u) {
            case "ml":
            case "milliliter":
            case "milliliters":
                return quantity;
            case "l":
            case "liter":
            case "liters":
                return quantity * 1000;
            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return quantity * 15;
            case "tsp":
            case "teaspoon":
            case "teaspoons":
                return quantity * 5;
            case "cup":
            case "cups":
                return quantity * 240;
        }

        // Count (base: pieces)
        switch (u) {
            case "pcs":
            case "piece":
            case "pieces":
            case "item":
            case "items":
            case "each":
                return quantity;
        }

        return -1; // Unknown unit
    }

    /** Returns the category of a unit for compatibility checking. */
    private static UnitCategory getUnitCategory(String unit) {
        if (unit == null) return UnitCategory.UNKNOWN;
        String u = unit.trim().toLowerCase(Locale.getDefault());

        switch (u) {
            case "g":
            case "gram":
            case "grams":
            case "kg":
            case "kilogram":
            case "kilograms":
                return UnitCategory.MASS;
            case "ml":
            case "milliliter":
            case "milliliters":
            case "l":
            case "liter":
            case "liters":
            case "tbsp":
            case "tablespoon":
            case "tablespoons":
            case "tsp":
            case "teaspoon":
            case "teaspoons":
            case "cup":
            case "cups":
                return UnitCategory.VOLUME;
            case "pcs":
            case "piece":
            case "pieces":
            case "item":
            case "items":
            case "each":
                return UnitCategory.COUNT;
            default:
                return UnitCategory.UNKNOWN;
        }
    }

    /** Checks if two units are compatible (both mass, both volume, or both count). */
    public static boolean areUnitsCompatible(String unit1, String unit2) {
        UnitCategory cat1 = getUnitCategory(unit1);
        UnitCategory cat2 = getUnitCategory(unit2);
        return cat1 != UnitCategory.UNKNOWN && cat1 == cat2;
    }

    /**
     * Simple plural-to-singular mapping for common food items.
     * Extend as needed for seeded recipe list.
     */
    private static String singularize(String word) {
        if (word == null || word.isEmpty()) return word;

        // Common irregular plurals
        Map<String, String> irregulars = Map.ofEntries(
            Map.entry("tomatoes", "tomato"),
            Map.entry("onions", "onion"),
            Map.entry("potatoes", "potato"),
            Map.entry("carrots", "carrot"),
            Map.entry("peppers", "pepper"),
            Map.entry("garlic", "garlic"),
            Map.entry("eggs", "egg"),
            Map.entry("apples", "apple"),
            Map.entry("bananas", "banana"),
            Map.entry("lemons", "lemon"),
            Map.entry("limes", "lime"),
            Map.entry("berries", "berry"),
            Map.entry("peaches", "peach"),
            Map.entry("mushrooms", "mushroom"),
            Map.entry("beans", "bean"),
            Map.entry("peas", "pea"),
            Map.entry("chillies", "chilli"),
            Map.entry("chilies", "chilli"),
            Map.entry("biscuits", "biscuit"),
            Map.entry("slices", "slice"),
            Map.entry("cloves", "clove"),
            Map.entry("leaves", "leaf"),
            Map.entry("sprigs", "sprig")
        );

        if (irregulars.containsKey(word)) return irregulars.get(word);

        // Regular plural rules (simple heuristic)
        if (word.endsWith("ies")) {
            return word.substring(0, word.length() - 3) + "y"; // berries -> berry
        }
        if (word.endsWith("es") && (word.endsWith("ches") || word.endsWith("shes") || word.endsWith("xes"))) {
            return word.substring(0, word.length() - 2); // peaches -> peach
        }
        if (word.endsWith("s") && word.length() > 3) {
            // Avoid over-singularizing short words like "gas", "bus"
            String withoutS = word.substring(0, word.length() - 1);
            // Check if it's a known food item that ends with s
            if (!word.equals("gas") && !word.equals("bus") && !word.equals("yes")) {
                return withoutS;
            }
        }

        return word;
    }

    /**
     * Builds a lookup map of pantry items by normalized name for fast matching.
     * If duplicate normalized names exist, keeps the one with largest base quantity.
     */
    public static Map<String, PantryItem> buildPantryLookup(List<PantryItem> pantryItems) {
        Map<String, PantryItem> lookup = new HashMap<>();
        if (pantryItems == null) return lookup;

        for (PantryItem item : pantryItems) {
            String key = normalizeName(item.ingredientName);
            PantryItem existing = lookup.get(key);

            double existingBase = (existing != null) ? convertToBaseUnit(existing.quantity, existing.unit) : -1;
            double currentBase = convertToBaseUnit(item.quantity, item.unit);

            // Keep the item with larger base quantity
            if (existing == null || (currentBase >= 0 && currentBase > existingBase)) {
                lookup.put(key, item);
            }
        }
        return lookup;
    }

    /**
     * Checks if a single recipe can be made from the pantry (strict match).
     * Returns MatchResult with details.
     */
    public static MatchResult checkRecipeMatch(Recipe recipe, List<RecipeIngredient> recipeIngredients, Map<String, PantryItem> pantryLookup) {
        List<String> missing = new ArrayList<>();

        for (RecipeIngredient ri : recipeIngredients) {
            String normName = normalizeName(ri.ingredientName);
            PantryItem pantryItem = pantryLookup.get(normName);

            if (pantryItem == null) {
                missing.add(ri.ingredientName + " (not in pantry)");
                continue;
            }

            // Check unit compatibility
            if (!areUnitsCompatible(pantryItem.unit, ri.unit)) {
                missing.add(ri.ingredientName + " (unit mismatch: have " + pantryItem.unit + ", need " + ri.unit + ")");
                continue;
            }

            // Compare in base units
            double haveBase = convertToBaseUnit(pantryItem.quantity, pantryItem.unit);
            double needBase = convertToBaseUnit(ri.quantity, ri.unit);

            if (haveBase < 0 || needBase < 0) {
                missing.add(ri.ingredientName + " (unknown unit: have " + pantryItem.unit + ", need " + ri.unit + ")");
                continue;
            }

            if (haveBase + 1e-9 < needBase) { // Small epsilon for floating point
                missing.add(String.format(Locale.getDefault(),
                    "%s (need %.1f %s, have %.1f %s)",
                    ri.ingredientName, ri.quantity, ri.unit, pantryItem.quantity, pantryItem.unit));
            }
        }

        boolean strictMatch = missing.isEmpty();
        return new MatchResult(recipe.id, strictMatch, missing.size(), missing);
    }

    /**
     * Evaluates all recipes against current pantry.
     * Returns list of MatchResult for each recipe.
     */
    public static List<MatchResult> matchAllRecipes(List<Recipe> recipes, List<PantryItem> pantryItems, RecipeRepository recipeRepository) {
        Map<String, PantryItem> pantryLookup = buildPantryLookup(pantryItems);
        List<MatchResult> results = new ArrayList<>();

        for (Recipe recipe : recipes) {
            List<RecipeIngredient> ingredients = recipeRepository.getIngredientsForRecipe(recipe.id);
            MatchResult result = checkRecipeMatch(recipe, ingredients, pantryLookup);
            results.add(result);
        }

        return results;
    }
}