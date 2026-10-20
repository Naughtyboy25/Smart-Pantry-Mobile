package com.example.smartpantry.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity representing a recipe stored in the local database.
 * Fields: id, name, description, servings, prepTimeMinutes.
 */
@Entity(tableName = "recipes")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "servings")
    public int servings;

    @ColumnInfo(name = "prep_time_minutes")
    public long prepTimeMinutes;
}