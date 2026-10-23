package com.example.smartpantry.ui.suggested;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.data.entity.Recipe;
import com.example.smartpantry.data.repository.PantryRepository;
import com.example.smartpantry.data.repository.RecipeRepository;
import com.example.smartpantry.util.IngredientMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for Suggested Recipes screen.
 * Reactively combines pantry items + all recipes through IngredientMatcher.
 *
 * Per the assignment brief (Section 2.3) the MAIN suggestions list contains ONLY
 * strictly matchable recipes: every required ingredient must be present in the
 * pantry in at least the required quantity. Partial matches are excluded.
 *
 * As an optional bonus, a clearly separated "Almost There" list shows recipes
 * missing exactly ONE ingredient.
 *
 * Uses MediatorLiveData to recompute when either pantry or recipes change.
 * Matching runs on a background thread to avoid blocking the main thread.
 */
public class SuggestedViewModel extends AndroidViewModel {

    private final PantryRepository pantryRepository;
    private final RecipeRepository recipeRepository;
    private final MediatorLiveData<List<RecipeWithMatch>> suggestedRecipes = new MediatorLiveData<>();
    private final MediatorLiveData<List<RecipeWithMatch>> almostThereRecipes = new MediatorLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SuggestedViewModel(@NonNull Application application) {
        super(application);
        pantryRepository = new PantryRepository(application);
        recipeRepository = new RecipeRepository(application);

        // Seed recipes on first run
        recipeRepository.seedIfEmpty(application);

        // Combine pantry + recipes reactively using MediatorLiveData
        setupMediatorLiveData();
    }

    private void setupMediatorLiveData() {
        LiveData<List<PantryItem>> pantryLive = pantryRepository.getAllPantryItems();
        LiveData<List<Recipe>> recipesLive = recipeRepository.getAllRecipes();

        // Source 1: Pantry changes
        suggestedRecipes.addSource(pantryLive, pantryItems -> {
            List<Recipe> recipes = recipesLive.getValue();
            if (recipes != null) {
                computeMatchesAsync(pantryItems, recipes);
            }
        });
        almostThereRecipes.addSource(pantryLive, pantryItems -> {
            List<Recipe> recipes = recipesLive.getValue();
            if (recipes != null) {
                computeMatchesAsync(pantryItems, recipes);
            }
        });

        // Source 2: Recipes changes (seeding complete)
        suggestedRecipes.addSource(recipesLive, recipes -> {
            List<PantryItem> pantry = pantryLive.getValue();
            if (pantry != null) {
                computeMatchesAsync(pantry, recipes);
            }
        });
        almostThereRecipes.addSource(recipesLive, recipes -> {
            List<PantryItem> pantry = pantryLive.getValue();
            if (pantry != null) {
                computeMatchesAsync(pantry, recipes);
            }
        });
    }

    private void computeMatchesAsync(List<PantryItem> pantryItems, List<Recipe> recipes) {
        executor.execute(() -> {
            List<IngredientMatcher.MatchResult> results = IngredientMatcher.matchAllRecipes(recipes, pantryItems, recipeRepository);

            List<RecipeWithMatch> strictMatches = new ArrayList<>();
            List<RecipeWithMatch> almostMatches = new ArrayList<>();

            for (IngredientMatcher.MatchResult mr : results) {
                for (Recipe r : recipes) {
                    if (r.id == mr.recipeId) {
                        int totalIng = recipeRepository.getIngredientsForRecipe(r.id).size();
                        if (mr.isStrictMatch) {
                            // MAIN list: strictly matchable recipes only
                            strictMatches.add(new RecipeWithMatch(
                                    r.id, r.name, totalIng, totalIng,
                                    true, 0, mr.missingIngredients));
                        } else if (mr.missingCount == 1) {
                            // Bonus list: recipes missing exactly one ingredient,
                            // clearly separated from the strict suggestions.
                            almostMatches.add(new RecipeWithMatch(
                                    r.id, r.name, totalIng, totalIng - 1,
                                    false, 1, mr.missingIngredients));
                        }
                        break;
                    }
                }
            }

            // Sort both lists alphabetically by recipe name
            Collections.sort(strictMatches, (a, b) -> a.name.compareToIgnoreCase(b.name));
            Collections.sort(almostMatches, (a, b) -> a.name.compareToIgnoreCase(b.name));

            // Post results back to main thread
            suggestedRecipes.postValue(strictMatches);
            almostThereRecipes.postValue(almostMatches);
        });
    }

    /** Returns strictly matchable recipes (main suggestions list) as observable LiveData. */
    public LiveData<List<RecipeWithMatch>> getSuggestedRecipes() {
        return suggestedRecipes;
    }

    /** Returns "Almost There" recipes (missing exactly 1 ingredient) as observable LiveData. */
    public LiveData<List<RecipeWithMatch>> getAlmostThereRecipes() {
        return almostThereRecipes;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        pantryRepository.shutdown();
        recipeRepository.shutdown();
        executor.shutdown();
    }

    /**
     * UI data class for suggested recipe list items.
     */
    public static class RecipeWithMatch {
        public final long recipeId;
        public final String name;
        public final int totalIngredients;
        public final int matchedIngredients;
        public final boolean isStrictMatch;
        public final int missingCount;
        public final List<String> missingIngredients;

        public RecipeWithMatch(long recipeId, String name, int totalIngredients, int matchedIngredients,
                               boolean isStrictMatch, int missingCount, List<String> missingIngredients) {
            this.recipeId = recipeId;
            this.name = name;
            this.totalIngredients = totalIngredients;
            this.matchedIngredients = matchedIngredients;
            this.isStrictMatch = isStrictMatch;
            this.missingCount = missingCount;
            this.missingIngredients = missingIngredients != null ? missingIngredients : new ArrayList<>();
        }
    }
}
