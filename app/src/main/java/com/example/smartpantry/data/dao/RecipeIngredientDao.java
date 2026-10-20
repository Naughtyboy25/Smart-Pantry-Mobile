package com.example.smartpantry.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

/**
 * DAO for RecipeIngredient entity.
 * Contains the strict-matching SQL query to find recipes fully covered by current pantry.
 */
@Dao
public interface RecipeIngredientDao {

    /**
     * Returns recipe IDs that can be STRICTLY matched by current pantry.
     * A recipe matches ONLY IF every required ingredient exists in pantry
     * with quantity >= required quantity (after unit normalization).
     *
     * @param candidateRecipeIds list of recipe IDs to check (all recipes)
     * @return list of recipe IDs that are fully matchable
     */
    @Query("""
        SELECT DISTINCT ri.recipe_id
        FROM recipe_ingredients ri
        JOIN pantry_items pi
          ON LOWER(pi.ingredient_name) = LOWER(ri.ingredient_name)
         AND pi.quantity >= ri.quantity
        WHERE ri.recipe_id IN (:candidateRecipeIds)
        GROUP BY ri.recipe_id
        HAVING COUNT(*) = (
            SELECT COUNT(*) FROM recipe_ingredients WHERE recipe_id = ri.recipe_id
        )
    """)
    List<Long> findStrictlyMatchableRecipeIds(List<Long> candidateRecipeIds);

    /**
     * Returns recipe IDs missing exactly ONE ingredient (for "Almost There" bonus feature).
     */
    @Query("""
        SELECT ri.recipe_id, COUNT(*) as matched_count,
               (SELECT COUNT(*) FROM recipe_ingredients WHERE recipe_id = ri.recipe_id) as total_count
        FROM recipe_ingredients ri
        LEFT JOIN pantry_items pi
          ON LOWER(pi.ingredient_name) = LOWER(ri.ingredient_name)
         AND pi.quantity >= ri.quantity
        WHERE ri.recipe_id IN (:candidateRecipeIds)
        GROUP BY ri.recipe_id
        HAVING matched_count = total_count - 1
    """)
    List<AlmostThereResult> findAlmostThereRecipeIds(List<Long> candidateRecipeIds);

    /**
     * POJO for "Almost There" query result.
     */
    class AlmostThereResult {
        public long recipeId;
        public int matchedCount;
        public int totalCount;
    }

    @Query("SELECT * FROM recipe_ingredients WHERE recipe_id = :recipeId")
    List<com.example.smartpantry.data.entity.RecipeIngredient> getIngredientsForRecipe(long recipeId);
}