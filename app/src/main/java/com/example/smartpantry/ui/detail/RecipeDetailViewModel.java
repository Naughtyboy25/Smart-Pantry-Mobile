package com.example.smartpantry.ui.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.smartpantry.data.dao.RecipeDao;
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
        recipeDetail = loadRecipeDetail(recipeId);
    }

    private LiveData<RecipeDetail> loadRecipeDetail(long recipeId) {
        MediatorLiveData<RecipeDetail> detail = new MediatorLiveData<>();
        LiveData<RecipeDao.RecipeWithDetails> source = repository.getRecipeWithDetails(recipeId);

        detail.addSource(source, withDetails -> {
            if (withDetails != null && withDetails.recipe != null) {
                RecipeDetail rd = new RecipeDetail();
                rd.recipe = withDetails.recipe;
                rd.ingredients = withDetails.ingredients;
                rd.steps = withDetails.steps;
                detail.setValue(rd);
            }
        });
        return detail;
    }

    public LiveData<RecipeDetail> getRecipeDetail() {
        return recipeDetail;
    }

    /** Deducts recipe ingredients from pantry (bonus "Cook It" feature). */
    public void cookRecipe() {
        // TODO: Implement - deduct ingredients from pantry via PantryRepository
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.shutdown();
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