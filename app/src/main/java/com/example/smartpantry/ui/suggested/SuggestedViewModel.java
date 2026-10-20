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

    public SuggestedViewModel(@NonNull Application application) {
        super(application);
        pantryRepository = new PantryRepository(application);
        recipeRepository = new RecipeRepository(application);

        // Seed recipes on first run
        recipeRepository.seedIfEmpty(application);

        // TODO: Combine pantry + recipes reactively using MediatorLiveData
        // and IngredientMatcher to compute suggested recipes
    }

    /** Returns strictly matchable recipes as observable LiveData. */
    public LiveData<List<RecipeWithMatch>> getSuggestedRecipes() {
        return suggestedRecipes;
    }

    /** Returns "Almost There" recipes (missing 1 ingredient) for bonus feature. */
    public LiveData<List<RecipeWithMatch>> getAlmostThereRecipes() {
        // TODO: Implement similar MediatorLiveData for almost-there logic
        return new MediatorLiveData<>();
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