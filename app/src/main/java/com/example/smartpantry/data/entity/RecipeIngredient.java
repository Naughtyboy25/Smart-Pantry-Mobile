package com.example.smartpantry.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

/**
 * Entity representing a single ingredient required by a recipe.
 * Composite primary key (recipe_id, ingredient_name) with FK to Recipe.
 * Fields: recipeId, ingredientName (normalized lowercase singular), quantity, unit.
 */
@Entity(
    tableName = "recipe_ingredients",
    primaryKeys = {"recipe_id", "ingredient_name"},
    foreignKeys = @ForeignKey(
        entity = Recipe.class,
        parentColumns = "id",
        childColumns = "recipe_id",
        onDelete = ForeignKey.CASCADE
    )
)
public class RecipeIngredient {

    @ColumnInfo(name = "recipe_id")
    public long recipeId;

    @ColumnInfo(name = "ingredient_name")
    public String ingredientName; // normalized: lowercase, singular

    @ColumnInfo(name = "quantity")
    public double quantity;

    @ColumnInfo(name = "unit")
    public String unit; // same unit enum as PantryItem
}