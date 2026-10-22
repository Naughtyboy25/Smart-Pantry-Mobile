package com.example.smartpantry.data.dao;

import com.example.smartpantry.data.database.AppDatabase;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RecipeDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private RecipeDao dao;

    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = database.recipeDao();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testInsertAndGetAll() {
        Recipe recipe1 = new Recipe();
        recipe1.name = "Bobotie";
        recipe1.description = "Cape Malay spiced mince";
        recipe1.servings = 6;
        recipe1.prepTimeMinutes = 60;

        Recipe recipe2 = new Recipe();
        recipe2.name = "Braai Broodjies";
        recipe2.description = "Grilled cheese sandwiches";
        recipe2.servings = 4;
        recipe2.prepTimeMinutes = 15;

        dao.insert(recipe1);
        dao.insert(recipe2);

        List<Recipe> all = dao.getAllLive().getValue();
        assertNotNull(all);
        assertEquals(2, all.size());
        assertEquals("Bobotie", all.get(0).name); // Ordered by name ASC
        assertEquals("Braai Broodjies", all.get(1).name);
    }

    @Test
    public void testInsertAndGetById() {
        Recipe recipe = new Recipe();
        recipe.name = "Bobotie";
        recipe.description = "Cape Malay spiced mince";
        recipe.servings = 6;
        recipe.prepTimeMinutes = 60;

        long id = dao.insert(recipe);
        assertTrue(id > 0);

        Recipe retrieved = dao.getByIdLive(id).getValue();
        assertNotNull(retrieved);
        assertEquals("Bobotie", retrieved.name);
        assertEquals("Cape Malay spiced mince", retrieved.description);
        assertEquals(6, retrieved.servings);
        assertEquals(60, retrieved.prepTimeMinutes);
    }

    @Test
    public void testInsertWithIngredientsAndSteps() {
        // Insert recipe
        Recipe recipe = new Recipe();
        recipe.name = "Test Recipe";
        recipe.servings = 4;
        recipe.prepTimeMinutes = 30;

        long recipeId = dao.insert(recipe);

        // Insert ingredients
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

        ingredients.add(ri1);
        ingredients.add(ri2);

        dao.insertIngredients(ingredients);

        // Insert steps
        List<RecipeStep> steps = new ArrayList<>();
        RecipeStep step1 = new RecipeStep();
        step1.recipeId = recipeId;
        step1.stepNumber = 1;
        step1.instruction = "Dice tomatoes and onion.";

        RecipeStep step2 = new RecipeStep();
        step2.recipeId = recipeId;
        step2.stepNumber = 2;
        step2.instruction = "Sauté in pan.";

        steps.add(step1);
        steps.add(step2);

        dao.insertSteps(steps);

        // Verify via getWithDetailsLive
        RecipeDao.RecipeWithDetails withDetails = dao.getWithDetailsLive(recipeId).getValue();
        assertNotNull(withDetails);
        assertNotNull(withDetails.recipe);
        assertEquals("Test Recipe", withDetails.recipe.name);
        assertNotNull(withDetails.ingredients);
        assertEquals(2, withDetails.ingredients.size());
        assertNotNull(withDetails.steps);
        assertEquals(2, withDetails.steps.size());
        assertEquals(1, withDetails.steps.get(0).stepNumber);
        assertEquals("Dice tomatoes and onion.", withDetails.steps.get(0).instruction);
    }

    @Test
    public void testCountRecipes() {
        assertEquals(0, dao.countRecipes());

        Recipe recipe1 = new Recipe();
        recipe1.name = "Recipe 1";
        dao.insert(recipe1);

        assertEquals(1, dao.countRecipes());

        Recipe recipe2 = new Recipe();
        recipe2.name = "Recipe 2";
        dao.insert(recipe2);

        assertEquals(2, dao.countRecipes());
    }

    @Test
    public void testClearAllRecipes() {
        Recipe recipe = new Recipe();
        recipe.name = "Test";
        dao.insert(recipe);

        assertEquals(1, dao.countRecipes());

        int cleared = dao.clearAllRecipes();
        assertEquals(1, cleared);
        assertEquals(0, dao.countRecipes());
    }

    @Test
    public void testClearAllIngredients() {
        Recipe recipe = new Recipe();
        recipe.name = "Test";
        long recipeId = dao.insert(recipe);

        List<RecipeIngredient> ingredients = new ArrayList<>();
        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = recipeId;
        ri.ingredientName = "tomato";
        ri.quantity = 4;
        ri.unit = "pcs";
        dao.insertIngredients(List.of(ri));

        // Verify inserted
        List<RecipeIngredient> retrieved = dao.getIngredientsForRecipe(recipeId);
        assertEquals(1, retrieved.size());

        int cleared = dao.clearAllIngredients();
        assertEquals(1, cleared);

        retrieved = dao.getIngredientsForRecipe(recipeId);
        assertTrue(retrieved.isEmpty());
    }

    @Test
    public void testClearAllSteps() {
        Recipe recipe = new Recipe();
        recipe.name = "Test";
        long recipeId = dao.insert(recipe);

        List<RecipeStep> steps = new ArrayList<>();
        RecipeStep step = new RecipeStep();
        step.recipeId = recipeId;
        step.stepNumber = 1;
        step.instruction = "Test step";
        dao.insertSteps(List.of(step));

        // Verify inserted
        List<RecipeStep> retrieved = dao.getStepsForRecipe(recipeId);
        assertEquals(1, retrieved.size());

        int cleared = dao.clearAllSteps();
        assertEquals(1, cleared);

        retrieved = dao.getStepsForRecipe(recipeId);
        assertTrue(retrieved.isEmpty());
    }

    @Test
    public void testCascadeDelete() {
        Recipe recipe = new Recipe();
        recipe.name = "Test Recipe";
        long recipeId = dao.insert(recipe);

        List<RecipeIngredient> ingredients = new ArrayList<>();
        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = recipeId;
        ri.ingredientName = "tomato";
        ri.quantity = 4;
        ri.unit = "pcs";
        dao.insertIngredients(List.of(ri));

        List<RecipeStep> steps = new ArrayList<>();
        RecipeStep step = new RecipeStep();
        step.recipeId = recipeId;
        step.stepNumber = 1;
        step.instruction = "Test step";
        dao.insertSteps(List.of(step));

        // Delete recipe - should cascade to ingredients and steps
        dao.clearAllRecipes(); // This deletes the recipe

        // Check ingredients and steps are gone
        List<RecipeIngredient> remainingIngredients = dao.getIngredientsForRecipe(recipeId);
        List<RecipeStep> remainingSteps = dao.getStepsForRecipe(recipeId);

        assertTrue(remainingIngredients.isEmpty());
        assertTrue(remainingSteps.isEmpty());
    }

    @Test
    public void testGetIngredientsForRecipe() {
        Recipe recipe = new Recipe();
        recipe.name = "Test";
        long recipeId = dao.insert(recipe);

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

        dao.insertIngredients(List.of(ri1, ri2));

        List<RecipeIngredient> retrieved = dao.getIngredientsForRecipe(recipeId);
        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
    }

    @Test
    public void testGetStepsForRecipe() {
        Recipe recipe = new Recipe();
        recipe.name = "Test";
        long recipeId = dao.insert(recipe);

        List<RecipeStep> steps = new ArrayList<>();
        RecipeStep step1 = new RecipeStep();
        step1.recipeId = recipeId;
        step1.stepNumber = 1;
        step1.instruction = "First step";

        RecipeStep step2 = new RecipeStep();
        step2.recipeId = recipeId;
        step2.stepNumber = 2;
        step2.instruction = "Second step";

        dao.insertSteps(List.of(step1, step2));

        List<RecipeStep> retrieved = dao.getStepsForRecipe(recipeId);
        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
        assertEquals(1, retrieved.get(0).stepNumber);
        assertEquals(2, retrieved.get(1).stepNumber);
    }

    @Test
    public void testRecipeWithDetailsLive() {
        Recipe recipe = new Recipe();
        recipe.name = "Live Test";
        recipe.servings = 4;
        long recipeId = dao.insert(recipe);

        // Add ingredient and step
        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = recipeId;
        ri.ingredientName = "tomato";
        ri.quantity = 2;
        ri.unit = "pcs";
        dao.insertIngredients(List.of(ri));

        RecipeStep step = new RecipeStep();
        step.recipeId = recipeId;
        step.stepNumber = 1;
        step.instruction = "Do something";
        dao.insertSteps(List.of(step));

        // Observe LiveData
        RecipeDao.RecipeWithDetails withDetails = dao.getWithDetailsLive(recipeId).getValue();
        assertNotNull(withDetails);
        assertEquals("Live Test", withDetails.recipe.name);
        assertEquals(1, withDetails.ingredients.size());
        assertEquals(1, withDetails.steps.size());
    }

    @Test
    public void testLiveDataUpdatesOnIngredientChange() {
        Recipe recipe = new Recipe();
        recipe.name = "Test";
        long recipeId = dao.insert(recipe);

        RecipeIngredient ri = new RecipeIngredient();
        ri.recipeId = recipeId;
        ri.ingredientName = "tomato";
        ri.quantity = 2;
        ri.unit = "pcs";
        dao.insertIngredients(List.of(ri));

        // Get LiveData
        RecipeDao.RecipeWithDetails initial = dao.getWithDetailsLive(recipeId).getValue();
        assertNotNull(initial);
        assertEquals(1, initial.ingredients.size());

        // Add another ingredient
        RecipeIngredient ri2 = new RecipeIngredient();
        ri2.recipeId = recipeId;
        ri2.ingredientName = "onion";
        ri2.quantity = 1;
        ri2.unit = "pcs";
        dao.insertIngredients(List.of(ri2));

        // LiveData should update
        RecipeDao.RecipeWithDetails updated = dao.getWithDetailsLive(recipeId).getValue();
        assertNotNull(updated);
        assertEquals(2, updated.ingredients.size());
    }
}