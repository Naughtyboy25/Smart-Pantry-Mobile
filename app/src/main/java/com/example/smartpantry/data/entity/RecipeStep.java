package com.example.smartpantry.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity representing a single preparation step for a recipe.
 * Fields: id, recipeId, stepNumber, instruction.
 */
@Entity(
    tableName = "recipe_steps",
    foreignKeys = @ForeignKey(
        entity = Recipe.class,
        parentColumns = "id",
        childColumns = "recipe_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = @Index("recipe_id")
)
public class RecipeStep {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "recipe_id")
    public long recipeId;

    @ColumnInfo(name = "step_number")
    public int stepNumber;

    @ColumnInfo(name = "instruction")
    @NonNull
    public String instruction;
}