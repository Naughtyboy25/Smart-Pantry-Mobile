package com.example.smartpantry.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity representing a single pantry item stored in the local database.
 * Fields: id, ingredientName, quantity, unit, expiryDate (optional).
 * Indexed on ingredient_name for fast strict-matching lookups.
 */
@Entity(
    tableName = "pantry_items",
    indices = @Index("ingredient_name")
)
public class PantryItem {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "ingredient_name")
    @NonNull
    public String ingredientName;

    @ColumnInfo(name = "quantity")
    public double quantity;

    @ColumnInfo(name = "unit")
    @NonNull
    public String unit;

    @ColumnInfo(name = "expiry_date")
    public Long expiryDate; // epoch milliseconds, nullable
}