package com.example.smartpantry.util;

import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.data.entity.Recipe;
import com.example.smartpantry.data.entity.RecipeIngredient;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for IngredientMatcher - strict recipe-to-pantry matching logic.
 * Tests cover: name normalization, unit conversion, unit compatibility,
 * pantry lookup building, strict matching, and "almost there" detection.
 */
public class IngredientMatcherTest {

    private Map<String, PantryItem> pantryLookup;
    private List<PantryItem> pantryItems;

    @Before
    public void setUp() {
        pantryItems = new ArrayList<>();

        // Add test pantry items
        PantryItem tomato = new PantryItem();
        tomato.id = 1;
        tomato.ingredientName = "Tomato";
        tomato.quantity = 5;
        tomato.unit = "pcs";
        pantryItems.add(tomato);

        PantryItem onion = new PantryItem();
        onion.id = 2;
        onion.ingredientName = "Onion";
        onion.quantity = 2;
        onion.unit = "pcs";
        pantryItems.add(onion);

        PantryItem flour = new PantryItem();
        flour.id = 3;
        flour.ingredientName = "Flour";
        flour.quantity = 1000;
        flour.unit = "g";
        pantryItems.add(flour);

        PantryItem milk = new PantryItem();
        milk.id = 4;
        milk.ingredientName = "Milk";
        milk.quantity = 500;
        milk.unit = "ml";
        pantryItems.add(milk);

        PantryItem butter = new PantryItem();
        butter.id = 5;
        butter.ingredientName = "Butter";
        butter.quantity = 200;
        butter.unit = "g";
        pantryItems.add(butter);

        PantryItem oil = new PantryItem();
        oil.id = 6;
        oil.ingredientName = "Olive Oil";
        oil.quantity = 100;
        oil.unit = "ml";
        pantryItems.add(oil);

        pantryLookup = IngredientMatcher.buildPantryLookup(pantryItems);
    }

    // ==================== Name Normalization Tests ====================

    @Test
    public void testNormalizeName_lowercase() {
        String result = IngredientMatcher.normalizeName("TOMATO");
        assertEquals("tomato", result);
    }

    @Test
    public void testNormalizeName_trim() {
        String result = IngredientMatcher.normalizeName("  tomato  ");
        assertEquals("tomato", result);
    }

    @Test
    public void testNormalizeName_singularize_tomatoes() {
        String result = IngredientMatcher.normalizeName("tomatoes");
        assertEquals("tomato", result);
    }

    @Test
    public void testNormalizeName_singularize_onions() {
        String result = IngredientMatcher.normalizeName("onions");
        assertEquals("onion", result);
    }

    @Test
    public void testNormalizeName_singularize_potatoes() {
        String result = IngredientMatcher.normalizeName("potatoes");
        assertEquals("potato", result);
    }

    @Test
    public void testNormalizeName_singularize_carrots() {
        String result = IngredientMatcher.normalizeName("carrots");
        assertEquals("carrot", result);
    }

    @Test
    public void testNormalizeName_singularize_berries() {
        String result = IngredientMatcher.normalizeName("berries");
        assertEquals("berry", result);
    }

    @Test
    public void testNormalizeName_singularize_peaches() {
        String result = IngredientMatcher.normalizeName("peaches");
        assertEquals("peach", result);
    }

    @Test
    public void testNormalizeName_already_singular() {
        String result = IngredientMatcher.normalizeName("tomato");
        assertEquals("tomato", result);
    }

    @Test
    public void testNormalizeName_null() {
        String result = IngredientMatcher.normalizeName(null);
        assertEquals("", result);
    }

    @Test
    public void testNormalizeName_empty() {
        String result = IngredientMatcher.normalizeName("");
        assertEquals("", result);
    }

    // ==================== Unit Conversion Tests ====================

    @Test
    public void testConvertToBaseUnit_grams() {
        assertEquals(100, IngredientMatcher.convertToBaseUnit(100, "g"), 0.001);
        assertEquals(100, IngredientMatcher.convertToBaseUnit(100, "gram"), 0.001);
        assertEquals(100, IngredientMatcher.convertToBaseUnit(100, "grams"), 0.001);
    }

    @Test
    public void testConvertToBaseUnit_kilograms() {
        assertEquals(1000, IngredientMatcher.convertToBaseUnit(1, "kg"), 0.001);
        assertEquals(1000, IngredientMatcher.convertToBaseUnit(1, "kilogram"), 0.001);
        assertEquals(1000, IngredientMatcher.convertToBaseUnit(1, "kilograms"), 0.001);
        assertEquals(500, IngredientMatcher.convertToBaseUnit(0.5, "kg"), 0.001);
    }

    @Test
    public void testConvertToBaseUnit_milliliters() {
        assertEquals(100, IngredientMatcher.convertToBaseUnit(100, "ml"), 0.001);
        assertEquals(100, IngredientMatcher.convertToBaseUnit(100, "milliliter"), 0.001);
        assertEquals(100, IngredientMatcher.convertToBaseUnit(100, "milliliters"), 0.001);
    }

    @Test
    public void testConvertToBaseUnit_liters() {
        assertEquals(1000, IngredientMatcher.convertToBaseUnit(1, "l"), 0.001);
        assertEquals(1000, IngredientMatcher.convertToBaseUnit(1, "liter"), 0.001);
        assertEquals(1000, IngredientMatcher.convertToBaseUnit(1, "liters"), 0.001);
        assertEquals(500, IngredientMatcher.convertToBaseUnit(0.5, "l"), 0.001);
    }

    @Test
    public void testConvertToBaseUnit_tablespoons() {
        assertEquals(15, IngredientMatcher.convertToBaseUnit(1, "tbsp"), 0.001);
        assertEquals(15, IngredientMatcher.convertToBaseUnit(1, "tablespoon"), 0.001);
        assertEquals(15, IngredientMatcher.convertToBaseUnit(1, "tablespoons"), 0.001);
        assertEquals(30, IngredientMatcher.convertToBaseUnit(2, "tbsp"), 0.001);
    }

    @Test
    public void testConvertToBaseUnit_teaspoons() {
        assertEquals(5, IngredientMatcher.convertToBaseUnit(1, "tsp"), 0.001);
        assertEquals(5, IngredientMatcher.convertToBaseUnit(1, "teaspoon"), 0.001);
        assertEquals(5, IngredientMatcher.convertToBaseUnit(1, "teaspoons"), 0.001);
        assertEquals(15, IngredientMatcher.convertToBaseUnit(3, "tsp"), 0.001);
    }

    @Test
    public void testConvertToBaseUnit_cups() {
        assertEquals(240, IngredientMatcher.convertToBaseUnit(1, "cup"), 0.001);
        assertEquals(240, IngredientMatcher.convertToBaseUnit(1, "cups"), 0.001);
        assertEquals(480, IngredientMatcher.convertToBaseUnit(2, "cup"), 0.001);
    }

    @Test
    public void testConvertToBaseUnit_pieces() {
        assertEquals(5, IngredientMatcher.convertToBaseUnit(5, "pcs"), 0.001);
        assertEquals(5, IngredientMatcher.convertToBaseUnit(5, "piece"), 0.001);
        assertEquals(5, IngredientMatcher.convertToBaseUnit(5, "pieces"), 0.001);
        assertEquals(5, IngredientMatcher.convertToBaseUnit(5, "item"), 0.001);
        assertEquals(5, IngredientMatcher.convertToBaseUnit(5, "items"), 0.001);
        assertEquals(5, IngredientMatcher.convertToBaseUnit(5, "each"), 0.001);
    }

    @Test
    public void testConvertToBaseUnit_unknown() {
        assertEquals(-1, IngredientMatcher.convertToBaseUnit(100, "unknown"), 0.001);
        assertEquals(-1, IngredientMatcher.convertToBaseUnit(100, ""), 0.001);
    }

    @Test
    public void testConvertToBaseUnit_null() {
        assertEquals(-1, IngredientMatcher.convertToBaseUnit(100, null), 0.001);
    }

    // ==================== Unit Compatibility Tests ====================

    @Test
    public void testAreUnitsCompatible_mass() {
        assertTrue(IngredientMatcher.areUnitsCompatible("g", "kg"));
        assertTrue(IngredientMatcher.areUnitsCompatible("kg", "g"));
        assertTrue(IngredientMatcher.areUnitsCompatible("gram", "kilogram"));
    }

    @Test
    public void testAreUnitsCompatible_volume() {
        assertTrue(IngredientMatcher.areUnitsCompatible("ml", "l"));
        assertTrue(IngredientMatcher.areUnitsCompatible("tbsp", "cup"));
        assertTrue(IngredientMatcher.areUnitsCompatible("tsp", "ml"));
    }

    @Test
    public void testAreUnitsCompatible_count() {
        assertTrue(IngredientMatcher.areUnitsCompatible("pcs", "piece"));
        assertTrue(IngredientMatcher.areUnitsCompatible("items", "each"));
    }

    @Test
    public void testAreUnitsCompatible_mismatch() {
        assertFalse(IngredientMatcher.areUnitsCompatible("g", "ml"));
        assertFalse(IngredientMatcher.areUnitsCompatible("kg", "pcs"));
        assertFalse(IngredientMatcher.areUnitsCompatible("l", "pcs"));
    }

    @Test
    public void testAreUnitsCompatible_null() {
        assertFalse(IngredientMatcher.areUnitsCompatible(null, "g"));
        assertFalse(IngredientMatcher.areUnitsCompatible("g", null));
        assertFalse(IngredientMatcher.areUnitsCompatible(null, null));
    }

    // ==================== Pantry Lookup Tests ====================

    @Test
    public void testBuildPantryLookup_basic() {
        Map<String, PantryItem> lookup = IngredientMatcher.buildPantryLookup(pantryItems);

        assertNotNull(lookup.get("tomato"));
        assertNotNull(lookup.get("onion"));
        assertNotNull(lookup.get("flour"));
        assertNotNull(lookup.get("milk"));
        assertNotNull(lookup.get("butter"));
        assertNotNull(lookup.get("olive oil"));
    }

    @Test
    public void testBuildPantryLookup_duplicate_names_keeps_larger() {
        List<PantryItem> items = new ArrayList<>();

        PantryItem item1 = new PantryItem();
        item1.id = 1;
        item1.ingredientName = "Tomato";
        item1.quantity = 3;
        item1.unit = "pcs";
        items.add(item1);

        PantryItem item2 = new PantryItem();
        item2.id = 2;
        item2.ingredientName = "tomato"; // same normalized name
        item2.quantity = 5;
        item2.unit = "pcs";
        items.add(item2);

        Map<String, PantryItem> lookup = IngredientMatcher.buildPantryLookup(items);
        assertEquals(5, lookup.get("tomato").quantity, 0.001); // Should keep larger
    }

    @Test
    public void testBuildPantryLookup_different_units_keeps_larger_base() {
        List<PantryItem> items = new ArrayList<>();

        PantryItem item1 = new PantryItem();
        item1.id = 1;
        item1.ingredientName = "Flour";
        item1.quantity = 500;
        item1.unit = "g";
        items.add(item1);

        PantryItem item2 = new PantryItem();
        item2.id = 2;
        item2.ingredientName = "flour";
        item2.quantity = 1;
        item2.unit = "kg"; // 1000g > 500g
        items.add(item2);

        Map<String, PantryItem> lookup = IngredientMatcher.buildPantryLookup(items);
        assertEquals(1, lookup.get("flour").quantity, 0.001); // Should keep 1kg
        assertEquals("kg", lookup.get("flour").unit);
    }

    @Test
    public void testBuildPantryLookup_empty_list() {
        Map<String, PantryItem> lookup = IngredientMatcher.buildPantryLookup(new ArrayList<>());
        assertTrue(lookup.isEmpty());
    }

    @Test
    public void testBuildPantryLookup_null_list() {
        Map<String, PantryItem> lookup = IngredientMatcher.buildPantryLookup(null);
        assertTrue(lookup.isEmpty());
    }

    // ==================== Strict Matching Tests ====================

    @Test
    public void testCheckRecipeMatch_exact_match() {
        Recipe recipe = new Recipe();
        recipe.id = 1;
        recipe.name = "Simple Tomato";

        List<RecipeIngredient> ingredients = new ArrayList<>();
        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = 1;
        ri.ingredientName = "tomato";
        ri.quantity = 3;
        ri.unit = "pcs";
        ingredients.add(ri);

        IngredientMatcher.MatchResult result = IngredientMatcher.checkRecipeMatch(recipe, ingredients, pantryLookup);

        assertTrue(result.isStrictMatch);
        assertEquals(0, result.missingCount);
        assertTrue(result.missingIngredients.isEmpty());
    }

    @Test
    public void testCheckRecipeMatch_insufficient_quantity() {
        Recipe recipe = new Recipe();
        recipe.id = 1;
        recipe.name = "Lots of Tomatoes";

        List<RecipeIngredient> ingredients = new ArrayList<>();
        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = 1;
        ri.ingredientName = "tomato";
        ri.quantity = 10; // Need 10, have 5
        ri.unit = "pcs";
        ingredients.add(ri);

        IngredientMatcher.MatchResult result = IngredientMatcher.checkRecipeMatch(recipe, ingredients, pantryLookup);

        assertFalse(result.isStrictMatch);
        assertEquals(1, result.missingCount);
        assertEquals(1, result.missingIngredients.size());
        assertTrue(result.missingIngredients.get(0).contains("need 10"));
        assertTrue(result.missingIngredients.get(0).contains("have 5"));
    }

    @Test
    public void testCheckRecipeMatch_missing_ingredient() {
        Recipe recipe = new Recipe();
        recipe.id = 1;
        recipe.name = "Missing Ingredient";

        List<RecipeIngredient> ingredients = new ArrayList<>();
        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = 1;
        ri.ingredientName = "chocolate"; // Not in pantry
        ri.quantity = 100;
        ri.unit = "g";
        ingredients.add(ri);

        IngredientMatcher.MatchResult result = IngredientMatcher.checkRecipeMatch(recipe, ingredients, pantryLookup);

        assertFalse(result.isStrictMatch);
        assertEquals(1, result.missingCount);
        assertTrue(result.missingIngredients.get(0).contains("not in pantry"));
    }

    @Test
    public void testCheckRecipeMatch_unit_mismatch() {
        Recipe recipe = new Recipe();
        recipe.id = 1;
        recipe.name = "Unit Mismatch";

        List<RecipeIngredient> ingredients = new ArrayList<>();
        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = 1;
        ri.ingredientName = "tomato";
        ri.quantity = 100;
        ri.unit = "g"; // Need grams, have pieces
        ingredients.add(ri);

        IngredientMatcher.MatchResult result = IngredientMatcher.checkRecipeMatch(recipe, ingredients, pantryLookup);

        assertFalse(result.isStrictMatch);
        assertEquals(1, result.missingCount);
        assertTrue(result.missingIngredients.get(0).contains("unit mismatch"));
    }

    @Test
    public void testCheckRecipeMatch_multiple_ingredients_all_match() {
        Recipe recipe = new Recipe();
        recipe.id = 1;
        recipe.name = "Multiple Match";

        List<RecipeIngredient> ingredients = new ArrayList<>();

        RecipeIngredient ri1 = new RecipeIngredient();
        ri1.recipeId = 1;
        ri1.ingredientName = "tomato";
        ri1.quantity = 2;
        ri1.unit = "pcs";
        ingredients.add(ri1);

        RecipeIngredient ri2 = new RecipeIngredient();
        ri2.recipeId = 1;
        ri2.ingredientName = "onion";
        ri2.quantity = 1;
        ri2.unit = "pcs";
        ingredients.add(ri2);

        IngredientMatcher.MatchResult result = IngredientMatcher.checkRecipeMatch(recipe, ingredients, pantryLookup);

        assertTrue(result.isStrictMatch);
        assertEquals(0, result.missingCount);
    }

    @Test
    public void testCheckRecipeMatch_multiple_ingredients_one_missing() {
        Recipe recipe = new Recipe();
        recipe.id = 1;
        recipe.name = "Partial Match";

        List<RecipeIngredient> ingredients = new ArrayList<>();

        RecipeIngredient ri1 = new RecipeIngredient();
        ri1.recipeId = 1;
        ri1.ingredientName = "tomato";
        ri1.quantity = 2;
        ri1.unit = "pcs";
        ingredients.add(ri1);

        RecipeIngredient ri2 = new RecipeIngredient();
        ri2.recipeId = 1;
        ri2.ingredientName = "chocolate"; // Missing
        ri2.quantity = 100;
        ri2.unit = "g";
        ingredients.add(ri2);

        IngredientMatcher.MatchResult result = IngredientMatcher.checkRecipeMatch(recipe, ingredients, pantryLookup);

        assertFalse(result.isStrictMatch);
        assertEquals(1, result.missingCount);
    }

    @Test
    public void testCheckRecipeMatch_unit_conversion_kg_to_g() {
        Recipe recipe = new Recipe();
        recipe.id = 1;
        recipe.name = "Kg to G";

        List<RecipeIngredient> ingredients = new ArrayList<>();
        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = 1;
        ri.ingredientName = "flour";
        ri.quantity = 500;
        ri.unit = "g"; // Recipe needs 500g
        ingredients.add(ri);

        // Pantry has 1000g (1kg equivalent in g)
        IngredientMatcher.MatchResult result = IngredientMatcher.checkRecipeMatch(recipe, ingredients, pantryLookup);

        assertTrue(result.isStrictMatch);
    }

    @Test
    public void testCheckRecipeMatch_unit_conversion_ml_to_l() {
        Recipe recipe = new Recipe();
        recipe.id = 1;
        recipe.name = "Ml to L";

        List<RecipeIngredient> ingredients = new ArrayList<>();
        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = 1;
        ri.ingredientName = "milk";
        ri.quantity = 0.25;
        ri.unit = "l"; // Recipe needs 250ml = 0.25L
        ingredients.add(ri);

        IngredientMatcher.MatchResult result = IngredientMatcher.checkRecipeMatch(recipe, ingredients, pantryLookup);

        assertTrue(result.isStrictMatch); // Pantry has 500ml
    }

    @Test
    public void testCheckRecipeMatch_tablespoons_conversion() {
        Recipe recipe = new Recipe();
        recipe.id = 1;
        recipe.name = "Tbsp conversion";

        List<RecipeIngredient> ingredients = new ArrayList<>();
        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = 1;
        ri.ingredientName = "olive oil";
        ri.quantity = 4;
        ri.unit = "tbsp"; // 4 tbsp = 60ml
        ingredients.add(ri);

        IngredientMatcher.MatchResult result = IngredientMatcher.checkRecipeMatch(recipe, ingredients, pantryLookup);

        assertTrue(result.isStrictMatch); // Pantry has 100ml
    }

    // ==================== Edge Cases ====================

    @Test
    public void testCheckRecipeMatch_empty_ingredients() {
        Recipe recipe = new Recipe();
        recipe.id = 1;
        recipe.name = "No Ingredients";

        List<RecipeIngredient> ingredients = new ArrayList<>();

        IngredientMatcher.MatchResult result = IngredientMatcher.checkRecipeMatch(recipe, ingredients, pantryLookup);

        assertTrue(result.isStrictMatch); // Empty recipe = trivially matchable
        assertEquals(0, result.missingCount);
    }

    @Test
    public void testCheckRecipeMatch_null_recipe() {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        IngredientMatcher.MatchResult result = IngredientMatcher.checkRecipeMatch(null, ingredients, pantryLookup);
        // Should not crash, just treat as no match
        assertNotNull(result);
    }

    @Test
    public void testCheckRecipeMatch_null_pantryLookup() {
        Recipe recipe = new Recipe();
        recipe.id = 1;
        List<RecipeIngredient> ingredients = new ArrayList<>();

        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = 1;
        ri.ingredientName = "tomato";
        ri.quantity = 1;
        ri.unit = "pcs";
        ingredients.add(ri);

        IngredientMatcher.MatchResult result = IngredientMatcher.checkRecipeMatch(recipe, ingredients, null);
        assertFalse(result.isStrictMatch);
    }

    @Test
    public void testCheckRecipeMatch_empty_pantryLookup() {
        Recipe recipe = new Recipe();
        recipe.id = 1;
        List<RecipeIngredient> ingredients = new ArrayList<>();

        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = 1;
        ri.ingredientName = "tomato";
        ri.quantity = 1;
        ri.unit = "pcs";
        ingredients.add(ri);

        Map<String, PantryItem> emptyLookup = new HashMap<>();
        IngredientMatcher.MatchResult result = IngredientMatcher.checkRecipeMatch(recipe, ingredients, emptyLookup);
        assertFalse(result.isStrictMatch);
    }

    @Test
    public void testMatchResult_constructor() {
        List<String> missing = new ArrayList<>();
        missing.add("tomato");
        missing.add("onion");

        IngredientMatcher.MatchResult result = new IngredientMatcher.MatchResult(
                42, false, 2, missing);

        assertEquals(42, result.recipeId);
        assertFalse(result.isStrictMatch);
        assertEquals(2, result.missingCount);
        assertEquals(2, result.missingIngredients.size());
    }
}