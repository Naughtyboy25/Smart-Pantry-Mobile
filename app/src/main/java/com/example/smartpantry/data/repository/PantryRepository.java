package com.example.smartpantry.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.smartpantry.data.dao.PantryItemDao;
import com.example.smartpantry.data.database.AppDatabase;
import com.example.smartpantry.data.entity.PantryItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository for pantry items.
 * Mediates between ViewModels and PantryItemDao.
 * Runs write operations on background thread via ExecutorService.
 * Seeds initial pantry data on first run.
 */
public class PantryRepository {

    private final PantryItemDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PantryRepository(Application application) {
        dao = AppDatabase.getInstance(application).pantryItemDao();
        seedIfEmpty(application);
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
        executor.execute(() -> dao.insert(item));
    }

    /** Updates an existing pantry item on background thread. */
    public void update(PantryItem item) {
        executor.execute(() -> dao.update(item));
    }

    /** Deletes a pantry item by ID on background thread. */
    public void deleteById(long itemId) {
        executor.execute(() -> dao.deleteById(itemId));
    }

    /** Deletes all expired pantry items (expiry_date < now). */
    public void deleteExpired(long now) {
        executor.execute(() -> dao.deleteExpired(now));
    }

    /** Clears all pantry data (used in Settings reset). */
    public void clearAll() {
        executor.execute(() -> dao.clearAll());
    }

    /** Seeds initial pantry data if empty. */
    public void seedIfEmpty(Application application) {
        executor.execute(() -> {
            if (dao.countItems() == 0) {
                PantrySeeder.seedIfEmpty(application, dao);
            }
        });
    }

    /** Shuts down the background executor. Call in Application onTerminate if needed. */
    public void shutdown() {
        executor.shutdown();
    }
}