package com.example.smartpantry.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.smartpantry.data.entity.Recipe;
import com.example.smartpantry.data.entity.RecipeIngredient;
import com.example.smartpantry.data.entity.RecipeStep;

import java.util.List;

/**
 * DAO for Recipe entity and its related ingredients/steps.
 * Provides queries for recipes with full detail loading via @Transaction.
 */
@Dao
public interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Recipe recipe);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIngredients(List<RecipeIngredient> ingredients);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSteps(List<RecipeStep> steps);

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    LiveData<List<Recipe>> getAllLive();

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    LiveData<Recipe> getByIdLive(long recipeId);

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    LiveData<RecipeWithDetails> getWithDetailsLive(long recipeId);

    @Query("SELECT COUNT(*) FROM recipes")
    int countRecipes();

    @Query("DELETE FROM recipes")
    int clearAllRecipes();

    @Query("DELETE FROM recipe_ingredients")
    int clearAllIngredients();

    @Query("DELETE FROM recipe_steps")
    int clearAllSteps();

    /**
     * POJO for Recipe with nested ingredients and steps.
     * Populated by Room via @Transaction.
     */
    class RecipeWithDetails {
        public Recipe recipe;
        public List<RecipeIngredient> ingredients;
        public List<RecipeStep> steps;
    }
}