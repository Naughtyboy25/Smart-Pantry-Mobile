package com.example.smartpantry.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.smartpantry.data.entity.RecipeStep;

import java.util.List;

/**
 * DAO for RecipeStep entity.
 * Provides ordered step retrieval for a given recipe.
 */
@Dao
public interface RecipeStepDao {

    @Query("SELECT * FROM recipe_steps WHERE recipe_id = :recipeId ORDER BY step_number ASC")
    LiveData<List<RecipeStep>> getStepsForRecipeLive(long recipeId);

    @Query("SELECT * FROM recipe_steps WHERE recipe_id = :recipeId ORDER BY step_number ASC")
    List<RecipeStep> getStepsForRecipe(long recipeId);
}