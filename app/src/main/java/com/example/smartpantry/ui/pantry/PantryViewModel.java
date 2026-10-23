package com.example.smartpantry.ui.pantry;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.data.repository.PantryRepository;

import java.util.List;

/**
 * ViewModel for Pantry List screen.
 * Exposes observable list of all pantry items.
 * Handles delete/refresh actions by delegating to PantryRepository.
 */
public class PantryViewModel extends AndroidViewModel {

    private final PantryRepository repository;
    private final LiveData<List<PantryItem>> allPantryItems;

    public PantryViewModel(@NonNull Application application) {
        super(application);
        repository = new PantryRepository(application);
        allPantryItems = repository.getAllPantryItems();
    }

    /** Returns all pantry items as LiveData for RecyclerView observation. */
    public LiveData<List<PantryItem>> getAllPantryItems() {
        return allPantryItems;
    }

    /** Deletes a pantry item by ID. */
    public void deleteItem(long itemId) {
        repository.deleteById(itemId);
    }

    /** Re-inserts a previously deleted item (used by Undo). */
    public void restoreItem(PantryItem item) {
        if (item != null) {
            repository.insert(item);
        }
    }

    /** Deletes all expired items (expiry_date < now). */
    public void deleteExpiredItems() {
        repository.deleteExpired(System.currentTimeMillis());
    }

    /** Clears all pantry data (used from Settings). */
    public void clearAllData() {
        repository.clearAll();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.shutdown();
    }
}