package com.example.smartpantry.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartpantry.data.entity.PantryItem;

import java.util.List;

/**
 * DAO for PantryItem entity.
 * Provides CRUD operations and observable queries for pantry items.
 */
@Dao
public interface PantryItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PantryItem item);

    @Update
    int update(PantryItem item);

    @Delete
    int delete(PantryItem item);

    @Query("DELETE FROM pantry_items WHERE id = :itemId")
    int deleteById(long itemId);

    @Query("SELECT * FROM pantry_items ORDER BY ingredient_name ASC")
    LiveData<List<PantryItem>> getAllLive();

    @Query("SELECT * FROM pantry_items WHERE id = :itemId")
    LiveData<PantryItem> getByIdLive(long itemId);

    @Query("SELECT * FROM pantry_items WHERE ingredient_name = :name")
    PantryItem findByName(String name);

    @Query("DELETE FROM pantry_items WHERE expiry_date < :now AND expiry_date IS NOT NULL")
    int deleteExpired(long now);

    @Query("DELETE FROM pantry_items")
    int clearAll();
}