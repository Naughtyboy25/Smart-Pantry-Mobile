package com.example.smartpantry.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.smartpantry.data.dao.PantryItemDao;
import com.example.smartpantry.data.database.AppDatabase;
import com.example.smartpantry.data.entity.PantryItem;

import java.util.List;

/**
 * Repository for pantry items.
 * Mediates between ViewModels and PantryItemDao.
 * Runs write operations on background thread.
 */
public class PantryRepository {

    private final PantryItemDao dao;

    public PantryRepository(Application application) {
        dao = AppDatabase.getInstance(application).pantryItemDao();
    }

    /** Returns all pantry items as observable LiveData. */
    public LiveData<List<PantryItem>> getAllPantryItems() {
        return dao.getAllLive();
    }

    /** Returns a single pantry item by ID as observable LiveData. */
    public LiveData<PantryItem> getPantryItemById(long itemId) {
        return dao.getByIdLive(itemId);
    }

    /** Inserts a new pantry item on background thread. */
    public void insert(PantryItem item) {
        // TODO: Execute on background thread
    }

    /** Updates an existing pantry item on background thread. */
    public void update(PantryItem item) {
        // TODO: Execute on background thread
    }

    /** Deletes a pantry item by ID on background thread. */
    public void deleteById(long itemId) {
        // TODO: Execute on background thread
    }

    /** Deletes all expired pantry items (expiry_date < now). */
    public void deleteExpired(long now) {
        // TODO: Execute on background thread
    }

    /** Clears all pantry data (used in Settings reset). */
    public void clearAll() {
        // TODO: Execute on background thread
    }
}