package com.example.smartpantry.ui.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.smartpantry.data.dao.RecipeDao;
import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.data.entity.Recipe;
import com.example.smartpantry.data.entity.RecipeIngredient;
import com.example.smartpantry.data.entity.RecipeStep;
import com.example.smartpantry.data.repository.PantryRepository;
import com.example.smartpantry.data.repository.RecipeRepository;
import com.example.smartpantry.util.IngredientMatcher;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for Recipe Detail screen.
 * Loads full recipe details (ingredients + steps) for a given recipeId.
 * Optionally handles "Cook It" action to deduct used ingredients from pantry.
 */
public class RecipeDetailViewModel extends AndroidViewModel {

    private final RecipeRepository recipeRepository;
    private final PantryRepository pantryRepository;
    private final LiveData<RecipeDetail> recipeDetail;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public RecipeDetailViewModel(@NonNull Application application, long recipeId) {
        super(application);
        recipeRepository = new RecipeRepository(application);
        pantryRepository = new PantryRepository(application);
        recipeDetail = loadRecipeDetail(recipeId);
    }

    private LiveData<RecipeDetail> loadRecipeDetail(long recipeId) {
        MediatorLiveData<RecipeDetail> detail = new MediatorLiveData<>();
        LiveData<RecipeDao.RecipeWithDetails> source = recipeRepository.getRecipeWithDetails(recipeId);

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

    /**
     * Deducts recipe ingredients from pantry (bonus "Cook It" feature).
     * Finds matching pantry items by normalized name and reduces quantity.
     * Runs on background thread via ExecutorService.
     */
    public void cookRecipe(Runnable onComplete) {
        executor.execute(() -> {
            RecipeDetail detail = recipeDetail.getValue();
            if (detail == null || detail.ingredients == null) {
                if (onComplete != null) onComplete.run();
                return;
            }

            for (RecipeIngredient ri : detail.ingredients) {
                String normName = IngredientMatcher.normalizeName(ri.ingredientName);
                PantryItem pantryItem = findPantryItemByNormalizedName(normName);

                if (pantryItem != null && IngredientMatcher.areUnitsCompatible(pantryItem.unit, ri.unit)) {
                    double haveBase = IngredientMatcher.convertToBaseUnit(pantryItem.quantity, pantryItem.unit);
                    double needBase = IngredientMatcher.convertToBaseUnit(ri.quantity, ri.unit);

                    if (haveBase >= 0 && needBase >= 0 && haveBase >= needBase) {
                        double newHaveBase = haveBase - needBase;
                        pantryItem.quantity = convertBaseToUnit(newHaveBase, pantryItem.unit);
                        if (pantryItem.quantity <= 0) {
                            pantryRepository.deleteById(pantryItem.id);
                        } else {
                            pantryRepository.update(pantryItem);
                        }
                    }
                }
            }

            if (onComplete != null) onComplete.run();
        });
    }

    /**
     * Finds a pantry item by normalized name (blocking call for use in background thread).
     */
    private PantryItem findPantryItemByNormalizedName(String normalizedName) {
        try {
            // Get all pantry items and find matching one
            List<PantryItem> allItems = pantryRepository.getAllPantryItems().getValue();
            if (allItems == null) return null;

            for (PantryItem item : allItems) {
                if (IngredientMatcher.normalizeName(item.ingredientName).equals(normalizedName)) {
                    return item;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts base unit quantity back to the target unit.
     */
    private double convertBaseToUnit(double baseQuantity, String targetUnit) {
        if (targetUnit == null) return baseQuantity;
        String u = targetUnit.toLowerCase(java.util.Locale.getDefault()).trim();

        switch (u) {
            case "kg":
            case "kilogram":
            case "kilograms":
                return baseQuantity / 1000;
            case "l":
            case "liter":
            case "liters":
                return baseQuantity / 1000;
            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return baseQuantity / 15;
            case "tsp":
            case "teaspoon":
            case "teaspoons":
                return baseQuantity / 5;
            case "cup":
            case "cups":
                return baseQuantity / 240;
            default:
                return baseQuantity; // g, ml, pcs
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        recipeRepository.shutdown();
        pantryRepository.shutdown();
        executor.shutdown();
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