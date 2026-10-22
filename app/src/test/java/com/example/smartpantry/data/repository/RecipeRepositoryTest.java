package com.example.smartpantry.data.repository;

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

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RecipeRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private RecipeRepository repository;

    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        repository = new RecipeRepository(ApplicationProvider.getApplicationContext());
    }

    @After
    public void tearDown() {
        repository.shutdown();
        database.close();
    }

    @Test
    public void testGetAllRecipes() {
        assertNotNull(repository.getAllRecipes());
    }

    @Test
    public void testGetRecipeWithDetails() {
        assertNotNull(repository.getRecipeWithDetails(1));
    }

    @Test
    public void testGetIngredientsForRecipe() {
        List<RecipeIngredient> ingredients = repository.getIngredientsForRecipe(1);
        assertNotNull(ingredients);
    }

    @Test
    public void testGetStepsForRecipe() {
        List<RecipeStep> steps = repository.getStepsForRecipe(1);
        assertNotNull(steps);
    }

    @Test
    public void testCountRecipes() {
        assertEquals(0, repository.countRecipes());
    }

    @Test
    public void testSeedIfEmpty() {
        repository.seedIfEmpty(ApplicationProvider.getApplicationContext());
        // Seed runs on background thread
    }

    @Test
    public void testResetToDefaults() {
        repository.resetToDefaults(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void testShutdown() {
        repository.shutdown();
        // Should not throw
    }
}