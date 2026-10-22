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
import java.util.List;

/**
 * ViewModel for Suggested Recipes screen.
 * Reactively combines pantry items + all recipes through IngredientMatcher
 * to produce a live list of strictly matchable recipes.
 * Uses MediatorLiveData to recompute when either pantry or recipes change.
 */
public class SuggestedViewModel extends AndroidViewModel {

    private final PantryRepository pantryRepository;
    private final RecipeRepository recipeRepository;
    private final MediatorLiveData<List<RecipeWithMatch>> suggestedRecipes = new MediatorLiveData<>();
    private final MediatorLiveData<List<RecipeWithMatch>> almostThereRecipes = new MediatorLiveData<>();

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
                computeMatches(pantryItems, recipes);
            }
        });

        // Source 2: Recipes changes (seeding complete)
        suggestedRecipes.addSource(recipesLive, recipes -> {
            List<PantryItem> pantry = pantryLive.getValue();
            if (pantry != null) {
                computeMatches(pantry, recipes);
            }
        });

        // Almost There recipes - same sources
        almostThereRecipes.addSource(pantryLive, pantryItems -> {
            List<Recipe> recipes = recipesLive.getValue();
            if (recipes != null) {
                computeAlmostThere(pantryItems, recipes);
            }
        });

        almostThereRecipes.addSource(recipesLive, recipes -> {
            List<PantryItem> pantry = pantryLive.getValue();
            if (pantry != null) {
                computeAlmostThere(pantry, recipes);
            }
        });
    }

    private void computeMatches(List<PantryItem> pantryItems, List<Recipe> recipes) {
        List<IngredientMatcher.MatchResult> results = IngredientMatcher.matchAllRecipes(recipes, pantryItems, recipeRepository);

        List<RecipeWithMatch> strictMatches = new ArrayList<>();
        for (IngredientMatcher.MatchResult mr : results) {
            if (mr.isStrictMatch) {
                // Find recipe name
                for (Recipe r : recipes) {
                    if (r.id == mr.recipeId) {
                        strictMatches.add(new RecipeWithMatch(r.id, r.name, mr.missingCount + (r.ingredients != null ? r.ingredients.size() : 0), r.ingredients != null ? r.ingredients.size() : 0, true));
                        break;
                    }
                }
            }
        }
        suggestedRecipes.setValue(strictMatches);
    }

    private void computeAlmostThere(List<PantryItem> pantryItems, List<Recipe> recipes) {
        List<IngredientMatcher.MatchResult> results = IngredientMatcher.matchAllRecipes(recipes, pantryItems, recipeRepository);

        List<RecipeWithMatch> almostMatches = new ArrayList<>();
        for (IngredientMatcher.MatchResult mr : results) {
            if (!mr.isStrictMatch && mr.missingCount == 1) {
                for (Recipe r : recipes) {
                    if (r.id == mr.recipeId) {
                        int totalIng = r.ingredients != null ? r.ingredients.size() : 0;
                        almostMatches.add(new RecipeWithMatch(r.id, r.name, totalIng, totalIng - 1, false));
                        break;
                    }
                }
            }
        }
        almostThereRecipes.setValue(almostMatches);
    }

    /** Returns strictly matchable recipes as observable LiveData. */
    public LiveData<List<RecipeWithMatch>> getSuggestedRecipes() {
        return suggestedRecipes;
    }

    /** Returns "Almost There" recipes (missing 1 ingredient) for bonus feature. */
    public LiveData<List<RecipeWithMatch>> getAlmostThereRecipes() {
        return almostThereRecipes;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        pantryRepository.shutdown();
        recipeRepository.shutdown();
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

        public RecipeWithMatch(long recipeId, String name, int totalIngredients, int matchedIngredients, boolean isStrictMatch) {
            this.recipeId = recipeId;
            this.name = name;
            this.totalIngredients = totalIngredients;
            this.matchedIngredients = matchedIngredients;
            this.isStrictMatch = isStrictMatch;
        }
    }
}