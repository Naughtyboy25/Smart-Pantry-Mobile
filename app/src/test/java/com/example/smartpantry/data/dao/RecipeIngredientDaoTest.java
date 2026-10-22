package com.example.smartpantry.data.dao;

import com.example.smartpantry.data.database.AppDatabase;
import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.data.entity.Recipe;
import com.example.smartpantry.data.entity.RecipeIngredient;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RecipeIngredientDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private RecipeIngredientDao ingredientDao;
    private RecipeDao recipeDao;
    private PantryItemDao pantryDao;

    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        ingredientDao = database.recipeIngredientDao();
        recipeDao = database.recipeDao();
        pantryDao = database.pantryItemDao();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testFindStrictlyMatchableRecipeIds_exactMatch() {
        // Create recipe with ingredients
        Recipe recipe = new Recipe();
        recipe.name = "Tomato Pasta";
        long recipeId = database.recipeDao().insert(recipe);

        List<RecipeIngredient> ingredients = new ArrayList<>();
        RecipeIngredient ri1 = new RecipeIngredient();
        ri1.recipeId = recipeId;
        ri1.ingredientName = "tomato";
        ri1.quantity = 2;
        ri1.unit = "pcs";

        RecipeIngredient ri2 = new RecipeIngredient();
        ri2.recipeId = recipeId;
        ri2.ingredientName = "pasta";
        ri2.quantity = 200;
        ri2.unit = "g";

        database.recipeIngredientDao().insertIngredients(List.of(ri1, ri2));

        // Add pantry items with sufficient quantities
        PantryItem pantryTomato = new PantryItem();
        pantryTomato.ingredientName = "tomato";
        pantryTomato.quantity = 5;
        pantryTomato.unit = "pcs";
        database.pantryItemDao().insert(pantryTomato);

        PantryItem pantryPasta = new PantryItem();
        pantryPasta.ingredientName = "pasta";
        pantryPasta.quantity = 500;
        pantryPasta.unit = "g";
        database.pantryItemDao().insert(pantryPasta);

        List<Long> candidateIds = List.of(recipeId);
        List<Long> matchable = ingredientDao.findStrictlyMatchableRecipeIds(candidateIds);

        assertEquals(1, matchable.size());
        assertEquals(recipeId, matchable.get(0));
    }

    @Test
    public void testFindStrictlyMatchableRecipeIds_insufficientQuantity() {
        Recipe recipe = new Recipe();
        recipe.name = "Lots of Tomatoes";
        long recipeId = database.recipeDao().insert(recipe);

        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = recipeId;
        ri.ingredientName = "tomato";
        ri.quantity = 10;
        ri.unit = "pcs";

        database.recipeIngredientDao().insertIngredients(List.of(ri));

        // Pantry only has 5, recipe needs 10
        PantryItem pantryTomato = new PantryItem();
        pantryTomato.ingredientName = "tomato";
        pantryTomato.quantity = 5;
        pantryTomato.unit = "pcs";
        database.pantryItemDao().insert(pantryTomato);

        List<Long> candidateIds = List.of(recipeId);
        List<Long> matchable = ingredientDao.findStrictlyMatchableRecipeIds(candidateIds);

        assertTrue(matchable.isEmpty());
    }

    @Test
    public void testFindStrictlyMatchableRecipeIds_missingIngredient() {
        Recipe recipe = new Recipe();
        recipe.name = "Missing Ingredient";
        long recipeId = database.recipeDao().insert(recipe);

        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = recipeId;
        ri.ingredientName = "chocolate";
        ri.quantity = 100;
        ri.unit = "g";

        database.recipeIngredientDao().insertIngredients(List.of(ri));

        // No chocolate in pantry
        List<Long> candidateIds = List.of(recipeId);
        List<Long> matchable = ingredientDao.findStrictlyMatchableRecipeIds(candidateIds);

        assertTrue(matchable.isEmpty());
    }

    @Test
    public void testFindStrictlyMatchableRecipeIds_unitMismatch() {
        Recipe recipe = new Recipe();
        recipe.name = "Unit Mismatch";
        long recipeId = database.recipeDao().insert(recipe);

        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = recipeId;
        ri.ingredientName = "tomato";
        ri.quantity = 100;
        ri.unit = "g";

        database.recipeIngredientDao().insertIngredients(List.of(ri));

        // Pantry has tomato in pcs, not grams
        PantryItem pantryTomato = new PantryItem();
        pantryTomato.ingredientName = "tomato";
        pantryTomato.quantity = 5;
        pantryTomato.unit = "pcs";
        database.pantryItemDao().insert(pantryTomato);

        List<Long> candidateIds = List.of(recipeId);
        List<Long> matchable = ingredientDao.findStrictlyMatchableRecipeIds(candidateIds);

        assertTrue(matchable.isEmpty());
    }

    @Test
    public void testFindStrictlyMatchableRecipeIds_multipleRecipes() {
        // Recipe 1: matchable
        Recipe recipe1 = new Recipe();
        recipe1.name = "Matchable";
        long recipeId1 = database.recipeDao().insert(recipe1);
        RecipeIngredient ri1 = new RecipeIngredient();
        ri1.recipeId = recipeId1;
        ri1.ingredientName = "tomato";
        ri1.quantity = 2;
        ri1.unit = "pcs";
        database.recipeIngredientDao().insertIngredients(List.of(ri1));

        // Recipe 2: not matchable (insufficient)
        Recipe recipe2 = new Recipe();
        recipe2.name = "Not Matchable";
        long recipeId2 = database.recipeDao().insert(recipe2);
        RecipeIngredient ri2 = new RecipeIngredient();
        ri2.recipeId = recipeId2;
        ri2.ingredientName = "tomato";
        ri2.quantity = 10;
        ri2.unit = "pcs";
        database.recipeIngredientDao().insertIngredients(List.of(ri2));

        // Pantry has 5 tomatoes
        PantryItem pantryTomato = new PantryItem();
        pantryTomato.ingredientName = "tomato";
        pantryTomato.quantity = 5;
        pantryTomato.unit = "pcs";
        database.pantryItemDao().insert(pantryTomato);

        List<Long> candidateIds = List.of(recipeId1, recipeId2);
        List<Long> matchable = ingredientDao.findStrictlyMatchableRecipeIds(candidateIds);

        assertEquals(1, matchable.size());
        assertEquals(recipeId1, matchable.get(0));
    }

    @Test
    public void testFindAlmostThereRecipeIds_oneMissing() {
        Recipe recipe = new Recipe();
        recipe.name = "Almost There";
        long recipeId = database.recipeDao().insert(recipe);

        // 2 ingredients needed
        RecipeIngredient ri1 = new RecipeIngredient();
        ri1.recipeId = recipeId;
        ri1.ingredientName = "tomato";
        ri1.quantity = 2;
        ri1.unit = "pcs";

        RecipeIngredient ri2 = new RecipeIngredient();
        ri2.recipeId = recipeId;
        ri2.ingredientName = "onion";
        ri2.quantity = 1;
        ri2.unit = "pcs";

        database.recipeIngredientDao().insertIngredients(List.of(ri1, ri2));

        // Pantry only has tomato
        PantryItem pantryTomato = new PantryItem();
        pantryTomato.ingredientName = "tomato";
        pantryTomato.quantity = 5;
        pantryTomato.unit = "pcs";
        database.pantryItemDao().insert(pantryTomato);

        List<Long> candidateIds = List.of(recipeId);
        List<RecipeIngredientDao.AlmostThereResult> almostThere = ingredientDao.findAlmostThereRecipeIds(candidateIds);

        assertEquals(1, almostThere.size());
        assertEquals(recipeId, almostThere.get(0).recipeId);
        assertEquals(1, almostThere.get(0).matchedCount);
        assertEquals(2, almostThere.get(0).totalCount);
    }

    @Test
    public void testFindAlmostThereRecipeIds_notAlmostThere() {
        Recipe recipe = new Recipe();
        recipe.name = "Not Almost";
        long recipeId = database.recipeDao().insert(recipe);

        // 3 ingredients needed
        RecipeIngredient ri1 = new RecipeIngredient();
        ri1.recipeId = recipeId;
        ri1.ingredientName = "tomato";
        ri1.quantity = 2;
        ri1.unit = "pcs";

        RecipeIngredient ri2 = new RecipeIngredient();
        ri2.recipeId = recipeId;
        ri2.ingredientName = "onion";
        ri2.quantity = 1;
        ri2.unit = "pcs";

        RecipeIngredient ri3 = new RecipeIngredient();
        ri3.recipeId = recipeId;
        ri3.ingredientName = "garlic";
        ri3.quantity = 1;
        ri3.unit = "pcs";

        database.recipeIngredientDao().insertIngredients(List.of(ri1, ri2, ri3));

        // Pantry only has tomato
        PantryItem pantryTomato = new PantryItem();
        pantryTomato.ingredientName = "tomato";
        pantryTomato.quantity = 5;
        pantryTomato.unit = "pcs";
        database.pantryItemDao().insert(pantryTomato);

        List<Long> candidateIds = List.of(recipeId);
        List<RecipeIngredientDao.AlmostThereResult> almostThere = ingredientDao.findAlmostThereRecipeIds(candidateIds);

        assertTrue(almostThere.isEmpty()); // Missing 2, not 1
    }

    @Test
    public void testFindAlmostThereRecipeIds_exactMatchExcluded() {
        Recipe recipe = new Recipe();
        recipe.name = "Exact Match";
        long recipeId = database.recipeDao().insert(recipe);

        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = recipeId;
        ri.ingredientName = "tomato";
        ri.quantity = 2;
        ri.unit = "pcs";
        database.recipeIngredientDao().insertIngredients(List.of(ri));

        PantryItem pantryTomato = new PantryItem();
        pantryTomato.ingredientName = "tomato";
        pantryTomato.quantity = 5;
        pantryTomato.unit = "pcs";
        database.pantryItemDao().insert(pantryTomato);

        List<Long> candidateIds = List.of(recipeId);
        List<RecipeIngredientDao.AlmostThereResult> almostThere = ingredientDao.findAlmostThereRecipeIds(candidateIds);

        assertTrue(almostThere.isEmpty()); // Exact match, not "almost there"
    }

    @Test
    public void testGetIngredientsForRecipe() {
        Recipe recipe = new Recipe();
        recipe.name = "Test";
        long recipeId = database.recipeDao().insert(recipe);

        List<RecipeIngredient> ingredients = new ArrayList<>();
        RecipeIngredient ri1 = new RecipeIngredient();
        ri1.recipeId = recipeId;
        ri1.ingredientName = "tomato";
        ri1.quantity = 4;
        ri1.unit = "pcs";

        RecipeIngredient ri2 = new RecipeIngredient();
        ri2.recipeId = recipeId;
        ri2.ingredientName = "onion";
        ri2.quantity = 1;
        ri2.unit = "pcs";

        database.recipeIngredientDao().insertIngredients(List.of(ri1, ri2));

        List<RecipeIngredient> retrieved = ingredientDao.getIngredientsForRecipe(recipeId);

        assertEquals(2, retrieved.size());
        assertEquals("tomato", retrieved.get(0).ingredientName);
        assertEquals("onion", retrieved.get(1).ingredientName);
    }

    @Test
    public void testAlmostThereResult_constructor() {
        RecipeIngredientDao.AlmostThereResult result = new RecipeIngredientDao.AlmostThereResult();
        result.recipeId = 42;
        result.matchedCount = 1;
        result.totalCount = 2;

        assertEquals(42, result.recipeId);
        assertEquals(1, result.matchedCount);
        assertEquals(2, result.totalCount);
    }
}