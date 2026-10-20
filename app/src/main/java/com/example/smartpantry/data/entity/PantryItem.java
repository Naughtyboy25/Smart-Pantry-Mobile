package com.example.smartpantry.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity representing a single pantry item stored in the local database.
 * Fields: id, ingredientName, quantity, unit, expiryDate (optional).
 */
@Entity(tableName = "pantry_items")
public class PantryItem {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "ingredient_name")
    public String ingredientName;

    @ColumnInfo(name = "quantity")
    public double quantity;

    @ColumnInfo(name = "unit")
    public String unit;

    @ColumnInfo(name = "expiry_date")
    public Long expiryDate; // epoch milliseconds, nullable
}