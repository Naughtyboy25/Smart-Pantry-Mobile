package com.example.smartpantry.data.dao;

import com.example.smartpantry.data.database.AppDatabase;
import com.example.smartpantry.data.entity.Recipe;
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
public class RecipeStepDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private RecipeStepDao stepDao;
    private RecipeDao recipeDao;

    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        stepDao = database.recipeStepDao();
        recipeDao = database.recipeDao();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testInsertAndGetSteps() {
        Recipe recipe = new Recipe();
        recipe.name = "Test Recipe";
        long recipeId = database.recipeDao().insert(recipe);

        List<RecipeStep> steps = new java.util.ArrayList<>();
        RecipeStep step1 = new RecipeStep();
        step1.recipeId = recipeId;
        step1.stepNumber = 1;
        step1.instruction = "First step: chop ingredients";

        RecipeStep step2 = new RecipeStep();
        step2.recipeId = recipeId;
        step2.stepNumber = 2;
        step2.instruction = "Second step: cook everything";

        RecipeStep step3 = new RecipeStep();
        step3.recipeId = recipeId;
        step3.stepNumber = 3;
        step3.instruction = "Third step: serve hot";

        database.recipeStepDao().insertSteps(List.of(step1, step2, step3));

        List<RecipeStep> retrieved = stepDao.getStepsForRecipe(recipeId);

        assertEquals(3, retrieved.size());
        assertEquals(1, retrieved.get(0).stepNumber);
        assertEquals("First step: chop ingredients", retrieved.get(0).instruction);
        assertEquals(2, retrieved.get(1).stepNumber);
        assertEquals("Second step: cook everything", retrieved.get(1).instruction);
        assertEquals(3, retrieved.get(2).stepNumber);
        assertEquals("Third step: serve hot", retrieved.get(2).instruction);
    }

    @Test
    public void testGetStepsForRecipeLive() {
        Recipe recipe = new Recipe();
        recipe.name = "Live Test";
        long recipeId = database.recipeDao().insert(recipe);

        RecipeStep step = new RecipeStep();
        step.recipeId = recipeId;
        step.stepNumber = 1;
        step.instruction = "Test step";
        database.recipeStepDao().insertSteps(List.of(step));

        List<RecipeStep> liveSteps = stepDao.getStepsForRecipeLive(recipeId).getValue();

        assertNotNull(liveSteps);
        assertEquals(1, liveSteps.size());
        assertEquals(1, liveSteps.get(0).stepNumber);
        assertEquals("Test step", liveSteps.get(0).instruction);
    }

    @Test
    public void testGetStepsForRecipe_empty() {
        Recipe recipe = new Recipe();
        recipe.name = "No Steps";
        long recipeId = database.recipeDao().insert(recipe);

        List<RecipeStep> retrieved = stepDao.getStepsForRecipe(recipeId);
        assertTrue(retrieved.isEmpty());
    }

    @Test
    public void testGetStepsForRecipe_nonExistentRecipe() {
        List<RecipeStep> retrieved = stepDao.getStepsForRecipe(99999);
        assertTrue(retrieved.isEmpty());
    }

    @Test
    public void testStepsOrderedByStepNumber() {
        Recipe recipe = new Recipe();
        recipe.name = "Ordered Test";
        long recipeId = database.recipeDao().insert(recipe);

        // Insert out of order
        RecipeStep step3 = new RecipeStep();
        step3.recipeId = recipeId;
        step3.stepNumber = 3;
        step3.instruction = "Third";

        RecipeStep step1 = new RecipeStep();
        step1.recipeId = recipeId;
        step1.stepNumber = 1;
        step1.instruction = "First";

        RecipeStep step2 = new RecipeStep();
        step2.recipeId = recipeId;
        step2.stepNumber = 2;
        step2.instruction = "Second";

        database.recipeStepDao().insertSteps(List.of(step3, step1, step2));

        List<RecipeStep> retrieved = stepDao.getStepsForRecipe(recipeId);

        assertEquals(3, retrieved.size());
        assertEquals(1, retrieved.get(0).stepNumber);
        assertEquals(2, retrieved.get(1).stepNumber);
        assertEquals(3, retrieved.get(2).stepNumber);
    }

    @Test
    public void testCascadeDelete() {
        Recipe recipe = new Recipe();
        recipe.name = "Cascade Test";
        long recipeId = database.recipeDao().insert(recipe);

        RecipeStep step = new RecipeStep();
        step.recipeId = recipeId;
        step.stepNumber = 1;
        step.instruction = "Test step";
        database.recipeStepDao().insertSteps(List.of(step));

        // Verify step exists
        List<RecipeStep> beforeDelete = stepDao.getStepsForRecipe(recipeId);
        assertEquals(1, beforeDelete.size());

        // Delete recipe - should cascade delete steps
        database.recipeDao().clearAllRecipes();

        // Steps should be gone
        List<RecipeStep> afterDelete = stepDao.getStepsForRecipe(recipeId);
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    public void testLiveDataUpdates() {
        Recipe recipe = new Recipe();
        recipe.name = "Live Update Test";
        long recipeId = database.recipeDao().insert(recipe);

        RecipeStep step1 = new RecipeStep();
        step1.recipeId = recipeId;
        step1.stepNumber = 1;
        step1.instruction = "First step";
        database.recipeStepDao().insertSteps(List.of(step1));

        // Get initial LiveData
        List<RecipeStep> initial = stepDao.getStepsForRecipeLive(recipeId).getValue();
        assertNotNull(initial);
        assertEquals(1, initial.size());

        // Add another step
        RecipeStep step2 = new RecipeStep();
        step2.recipeId = recipeId;
        step2.stepNumber = 2;
        step2.instruction = "Second step";
        database.recipeStepDao().insertSteps(List.of(step2));

        // LiveData should update
        List<RecipeStep> updated = stepDao.getStepsForRecipeLive(recipeId).getValue();
        assertNotNull(updated);
        assertEquals(2, updated.size());
    }

    @Test
    public void testMultipleRecipesStepsIsolation() {
        Recipe recipe1 = new Recipe();
        recipe1.name = "Recipe 1";
        long recipeId1 = database.recipeDao().insert(recipe1);

        Recipe recipe2 = new Recipe();
        recipe2.name = "Recipe 2";
        long recipeId2 = database.recipeDao().insert(recipe2);

        RecipeStep step1 = new RecipeStep();
        step1.recipeId = recipeId1;
        step1.stepNumber = 1;
        step1.instruction = "Recipe 1 step";

        RecipeStep step2 = new RecipeStep();
        step2.recipeId = recipeId2;
        step2.stepNumber = 1;
        step2.instruction = "Recipe 2 step";

        database.recipeStepDao().insertSteps(List.of(step1, step2));

        List<RecipeStep> steps1 = stepDao.getStepsForRecipe(recipeId1);
        List<RecipeStep> steps2 = stepDao.getStepsForRecipe(recipeId2);

        assertEquals(1, steps1.size());
        assertEquals(1, steps2.size());
        assertEquals("Recipe 1 step", steps1.get(0).instruction);
        assertEquals("Recipe 2 step", steps2.get(0).instruction);
    }
}