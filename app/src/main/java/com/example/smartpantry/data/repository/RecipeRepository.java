package com.example.smartpantry.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.smartpantry.data.dao.RecipeDao;
import com.example.smartpantry.data.dao.RecipeIngredientDao;
import com.example.smartpantry.data.dao.RecipeStepDao;
import com.example.smartpantry.data.database.AppDatabase;
import com.example.smartpantry.data.entity.Recipe;
import com.example.smartpantry.data.entity.RecipeIngredient;
import com.example.smartpantry.data.entity.RecipeStep;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository for recipes, ingredients, and steps.
 * Handles seeding initial recipe data from assets/recipes.json on first run.
 * Provides observable queries for recipe lists and full recipe details.
 */
public class RecipeRepository {

    private final RecipeDao recipeDao;
    private final RecipeIngredientDao ingredientDao;
    private final RecipeStepDao stepDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public RecipeRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        recipeDao = db.recipeDao();
        ingredientDao = db.recipeIngredientDao();
        stepDao = db.recipeStepDao();
    }

    /** Returns all recipes as observable LiveData. */
    public LiveData<List<Recipe>> getAllRecipes() {
        return recipeDao.getAllLive();
    }

    /** Returns full recipe detail (recipe + ingredients + steps) as observable LiveData. */
    public LiveData<RecipeDao.RecipeWithDetails> getRecipeWithDetails(long recipeId) {
        return recipeDao.getWithDetailsLive(recipeId);
    }

    /** Returns ingredient list for a recipe (non-observable, for matching algorithm). */
    public List<RecipeIngredient> getIngredientsForRecipe(long recipeId) {
        return ingredientDao.getIngredientsForRecipe(recipeId);
    }

    /** Returns step list for a recipe (non-observable). */
    public List<RecipeStep> getStepsForRecipe(long recipeId) {
        return stepDao.getStepsForRecipe(recipeId);
    }

    /** Returns count of recipes in database (used to check if seeding needed). */
    public int countRecipes() {
        return recipeDao.countRecipes();
    }

    /**
     * Seeds the database with initial recipes from assets/recipes.json
     * if the recipe table is empty. Runs on background thread.
     */
    public void seedIfEmpty(Application application) {
        executor.execute(() -> {
            if (recipeDao.countRecipes() == 0) {
                RecipeSeeder.seedFromAssets(application, recipeDao);
            }
        });
    }

    /** Resets recipe database to defaults (clears and re-seeds). */
    public void resetToDefaults(Application application) {
        executor.execute(() -> {
            recipeDao.clearAllSteps();
            recipeDao.clearAllIngredients();
            recipeDao.clearAllRecipes();
            RecipeSeeder.seedFromAssets(application, recipeDao);
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}