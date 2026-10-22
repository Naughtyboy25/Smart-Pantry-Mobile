package com.example.smartpantry.data.repository;

import com.example.smartpantry.data.database.AppDatabase;
import com.example.smartpantry.data.dao.RecipeDao;
import com.example.smartpantry.data.dao.RecipeIngredientDao;
import com.example.smartpantry.data.dao.RecipeStepDao;
import com.example.smartpantry.data.entity.Recipe;
import com.example.smartpantry.data.entity.RecipeIngredient;
import com.example.smartpantry.data.entity.RecipeStep;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RecipeSeederTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private RecipeDao recipeDao;
    private RecipeIngredientDao ingredientDao;
    private RecipeStepDao stepDao;

    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        recipeDao = database.recipeDao();
        ingredientDao = database.recipeIngredientDao();
        stepDao = database.recipeStepDao();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testSeedFromAssets_createsRecipes() {
        RecipeSeeder.seedFromAssets(ApplicationProvider.getApplicationContext(), recipeDao, ingredientDao, stepDao);

        int count = recipeDao.countRecipes();
        assertTrue(count > 0);
        // Should have seeded 20 recipes from the JSON
        assertEquals(20, count);
    }

    @Test
    public void testSeedFromAssets_createsIngredients() {
        RecipeSeeder.seedFromAssets(ApplicationProvider.getApplicationContext(), recipeDao, ingredientDao, stepDao);

        List<Recipe> recipes = recipeDao.getAllLive().getValue();
        assertNotNull(recipes);

        for (Recipe recipe : recipes) {
            List<RecipeIngredient> ingredients = ingredientDao.getIngredientsForRecipe(recipe.id);
            assertNotNull(ingredients);
            assertTrue(ingredients.size() > 0);
        }
    }

    @Test
    public void testSeedFromAssets_createsSteps() {
        RecipeSeeder.seedFromAssets(ApplicationProvider.getApplicationContext(), recipeDao, ingredientDao, stepDao);

        List<Recipe> recipes = recipeDao.getAllLive().getValue();
        assertNotNull(recipes);

        for (Recipe recipe : recipes) {
            List<RecipeStep> steps = stepDao.getStepsForRecipe(recipe.id);
            assertNotNull(steps);
            assertTrue(steps.size() > 0);
        }
    }

    @Test
    public void testSeedFromAssets_recipeNames() {
        RecipeSeeder.seedFromAssets(ApplicationProvider.getApplicationContext(), recipeDao, ingredientDao, stepDao);

        List<Recipe> recipes = recipeDao.getAllLive().getValue();
        assertNotNull(recipes);

        // Check for known South African recipes
        boolean hasBobotie = false;
        boolean hasBraaiBroodjies = false;
        boolean hasBunnyChow = false;
        boolean hasMalvaPudding = false;

        for (Recipe recipe : recipes) {
            if (recipe.name.equals("Bobotie")) hasBobotie = true;
            if (recipe.name.equals("Braai Broodjies")) hasBraaiBroodjies = true;
            if (recipe.name.equals("Bunny Chow")) hasBunnyChow = true;
            if (recipe.name.equals("Malva Pudding")) hasMalvaPudding = true;
        }

        assertTrue("Should have Bobotie", hasBobotie);
        assertTrue("Should have Braai Broodjies", hasBraaiBroodjies);
        assertTrue("Should have Bunny Chow", hasBunnyChow);
        assertTrue("Should have Malva Pudding", hasMalvaPudding);
    }

    @Test
    public void testSeedFromAssets_ingredientsHaveNormalizedNames() {
        RecipeSeeder.seedFromAssets(ApplicationProvider.getApplicationContext(), recipeDao, ingredientDao, stepDao);

        List<Recipe> recipes = recipeDao.getAllLive().getValue();
        assertNotNull(recipes);

        for (Recipe recipe : recipes) {
            List<RecipeIngredient> ingredients = ingredientDao.getIngredientsForRecipe(recipe.id);
            for (RecipeIngredient ingredient : ingredients) {
                // Names should be normalized (lowercase)
                assertEquals(ingredient.ingredientName.toLowerCase(), ingredient.ingredientName);
                // No trailing/leading spaces
                assertEquals(ingredient.ingredientName.trim(), ingredient.ingredientName);
            }
        }
    }

    @Test
    public void testSeedFromAssets_stepsAreNumbered() {
        RecipeSeeder.seedFromAssets(ApplicationProvider.getApplicationContext(), recipeDao, ingredientDao, stepDao);

        List<Recipe> recipes = recipeDao.getAllLive().getValue();
        assertNotNull(recipes);

        for (Recipe recipe : recipes) {
            List<RecipeStep> steps = stepDao.getStepsForRecipe(recipe.id);
            for (int i = 0; i < steps.size(); i++) {
                assertEquals(i + 1, steps.get(i).stepNumber);
                assertFalse(steps.get(i).instruction.isEmpty());
            }
        }
    }

    @Test
    public void testLoadJSONFromAsset_validFile() {
        String json = RecipeSeeder.loadJSONFromAsset(ApplicationProvider.getApplicationContext(), "recipes.json");
        assertNotNull(json);
        assertFalse(json.isEmpty());
        assertTrue(json.startsWith("["));
    }

    @Test
    public void testLoadJSONFromAsset_invalidFile() {
        String json = RecipeSeeder.loadJSONFromAsset(ApplicationProvider.getApplicationContext(), "nonexistent.json");
        assertEquals("[]", json);
    }
}