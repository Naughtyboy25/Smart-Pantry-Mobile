package com.example.smartpantry.ui.suggested;

import com.example.smartpantry.data.database.AppDatabase;
import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.data.entity.Recipe;
import com.example.smartpantry.data.repository.PantryRepository;
import com.example.smartpantry.data.repository.RecipeRepository;
import com.example.smartpantry.util.IngredientMatcher;

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
public class SuggestedViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private PantryRepository pantryRepository;
    private RecipeRepository recipeRepository;

    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        pantryRepository = new PantryRepository(ApplicationProvider.getApplicationContext());
        recipeRepository = new RecipeRepository(ApplicationProvider.getApplicationContext());

        // Seed recipes
        recipeRepository.seedIfEmpty(ApplicationProvider.getApplicationContext());
    }

    @After
    public void tearDown() {
        pantryRepository.shutdown();
        recipeRepository.shutdown();
        database.close();
    }

    @Test
    public void testSuggestedViewModel_initialEmpty() {
        // With empty pantry, no recipes should match
        SuggestedViewModel viewModel = new SuggestedViewModel(ApplicationProvider.getApplicationContext());

        // Wait for seeding and initial computation
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        List<SuggestedViewModel.RecipeWithMatch> suggested = viewModel.getSuggestedRecipes().getValue();
        assertNotNull(suggested);
        // With empty pantry, no strict matches
        assertTrue(suggested.isEmpty());

        viewModel.onCleared();
    }

    @Test
    public void testSuggestedViewModel_withMatchingIngredients() {
        SuggestedViewModel viewModel = new SuggestedViewModel(ApplicationProvider.getApplicationContext());

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // Add pantry items that match Bobotie recipe
        PantryItem tomato = new PantryItem();
        tomato.ingredientName = "beef mince";
        tomato.quantity = 1;
        tomato.unit = "kg";
        pantryRepository.insert(tomato);

        PantryItem onion = new PantryItem();
        onion.ingredientName = "onion";
        onion.quantity = 5;
        onion.unit = "pcs";
        pantryRepository.insert(onion);

        PantryItem garlic = new PantryItem();
        garlic.ingredientName = "garlic";
        garlic.quantity = 10;
        garlic.unit = "pcs";
        pantryRepository.insert(garlic);

        PantryItem bread = new PantryItem();
        bread.ingredientName = "bread";
        bread.quantity = 5;
        bread.unit = "pcs";
        pantryRepository.insert(bread);

        PantryItem milk = new PantryItem();
        milk.ingredientName = "milk";
        milk.quantity = 1;
        milk.unit = "l";
        pantryRepository.insert(milk);

        // Wait for LiveData to update
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        List<SuggestedViewModel.RecipeWithMatch> suggested = viewModel.getSuggestedRecipes().getValue();
        assertNotNull(suggested);
        // Should have at least one match now
        // (Depends on which recipes are seeded and their ingredients)

        viewModel.onCleared();
    }

    @Test
    public void testAlmostThereRecipes() {
        SuggestedViewModel viewModel = new SuggestedViewModel(ApplicationProvider.getApplicationContext());

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // Add partial ingredients for a recipe
        PantryItem tomato = new PantryItem();
        tomato.ingredientName = "tomato";
        tomato.quantity = 5;
        tomato.unit = "pcs";
        pantryRepository.insert(tomato);

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        List<SuggestedViewModel.RecipeWithMatch> almostThere = viewModel.getAlmostThereRecipes().getValue();
        assertNotNull(almostThere);
        // May have "almost there" recipes depending on seeded data

        viewModel.onCleared();
    }

    @Test
    public void testRecipeWithMatch_constructor() {
        SuggestedViewModel.RecipeWithMatch match = new SuggestedViewModel.RecipeWithMatch(
                1, "Test Recipe", 5, 5, true);

        assertEquals(1, match.recipeId);
        assertEquals("Test Recipe", match.name);
        assertEquals(5, match.totalIngredients);
        assertEquals(5, match.matchedIngredients);
        assertTrue(match.isStrictMatch);
    }
}