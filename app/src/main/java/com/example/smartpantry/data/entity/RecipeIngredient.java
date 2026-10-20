package com.example.smartpantry.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

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
    ),
    indices = @Index("recipe_id")
)
public class RecipeIngredient {

    @ColumnInfo(name = "recipe_id")
    public long recipeId;

    @ColumnInfo(name = "ingredient_name")
    @NonNull
    public String ingredientName; // normalized: lowercase, singular

    @ColumnInfo(name = "quantity")
    public double quantity;

    @ColumnInfo(name = "unit")
    @NonNull
    public String unit; // same unit enum as PantryItem
}