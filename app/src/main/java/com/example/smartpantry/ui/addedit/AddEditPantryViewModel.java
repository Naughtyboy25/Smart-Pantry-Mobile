package com.example.smartpantry.ui.addedit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.data.repository.PantryRepository;

/**
 * ViewModel for Add/Edit Pantry Item screen.
 * Handles loading existing item (EDIT mode) and saving new/updated items.
 * Exposes validation state and save result via LiveData.
 */
public class AddEditPantryViewModel extends AndroidViewModel {

    private final PantryRepository repository;
    private final MutableLiveData<PantryItem> itemLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private long editingItemId = -1;
    private boolean isEditMode = false;

    public AddEditPantryViewModel(@NonNull Application application) {
        super(application);
        repository = new PantryRepository(application);
    }

    /** Initializes ViewModel for ADD or EDIT mode. */
    public void init(String mode, long itemId) {
        if ("EDIT".equals(mode) && itemId > 0) {
            isEditMode = true;
            editingItemId = itemId;
            // TODO: Load item via repository.getPantryItemById(itemId)
        } else {
            isEditMode = false;
            itemLiveData.setValue(new PantryItem());
        }
    }

    /** Returns the current item (editable in ADD mode, pre-filled in EDIT mode). */
    public LiveData<PantryItem> getItem() {
        return itemLiveData;
    }

    /** Returns save success status (true/false/null). */
    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    /** Returns validation error message if any. */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Validates and saves the pantry item.
     * Runs on background thread via repository.
     */
    public void saveItem(String name, String quantityStr, String unit, Long expiryDate) {
        // TODO: Validate inputs, create/update item, call repository
    }
}