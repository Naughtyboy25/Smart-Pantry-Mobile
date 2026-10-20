package com.example.smartpantry.ui.detail;

import android.app.Application;

import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smartpantry.data.entity.Recipe;
import com.example.smartpantry.data.entity.RecipeIngredient;
import com.example.smartpantry.data.entity.RecipeStep;
import com.example.smartpantry.data.repository.RecipeRepository;

import java.util.List;

/**
 * ViewModel for Recipe Detail screen.
 * Loads full recipe details (ingredients + steps) for a given recipeId.
 * Optionally handles "Cook It" action to deduct used ingredients from pantry.
 */
public class RecipeDetailViewModel extends AndroidViewModel {

    private final RecipeRepository repository;
    private final LiveData<RecipeDetail> recipeDetail;

    public RecipeDetailViewModel(@NonNull Application application, long recipeId) {
        super(application);
        repository = new RecipeRepository(application);
        // TODO: Load recipe detail via repository.getRecipeWithDetails(recipeId)
        recipeDetail = null;
    }

    public LiveData<RecipeDetail> getRecipeDetail() {
        return recipeDetail;
    }

    /** Deducts recipe ingredients from pantry (bonus "Cook It" feature). */
    public void cookRecipe() {
        // TODO: Implement
    }

    /**
     * UI data class combining recipe, ingredients, and steps.
     */
    public static class RecipeDetail {
        public Recipe recipe;
        public List<RecipeIngredient> ingredients;
        public List<RecipeStep> steps;
    }
}