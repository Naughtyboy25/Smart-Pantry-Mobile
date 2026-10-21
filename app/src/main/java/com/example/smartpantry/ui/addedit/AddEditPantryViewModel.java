package com.example.smartpantry.ui.addedit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.data.repository.PantryRepository;
import com.example.smartpantry.util.Constants;

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
            loadItem(itemId);
        } else {
            isEditMode = false;
            editingItemId = -1;
            itemLiveData.setValue(new PantryItem());
        }
    }

    /** Loads existing pantry item for editing. */
    private void loadItem(long itemId) {
        repository.getPantryItemById(itemId).observeForever(item -> {
            if (item != null) {
                itemLiveData.setValue(item);
            }
        });
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
        // Reset error
        errorMessage.setValue(null);

        // Validate name
        if (name == null || name.trim().isEmpty()) {
            errorMessage.setValue("Ingredient name is required");
            return;
        }
        if (name.trim().length() > Constants.MAX_INGREDIENT_NAME_LENGTH) {
            errorMessage.setValue("Name too long (max " + Constants.MAX_INGREDIENT_NAME_LENGTH + " characters)");
            return;
        }

        // Validate quantity
        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            errorMessage.setValue("Quantity is required");
            return;
        }
        double quantity;
        try {
            quantity = Double.parseDouble(quantityStr.trim());
        } catch (NumberFormatException e) {
            errorMessage.setValue("Invalid quantity");
            return;
        }
        if (quantity < Constants.MIN_QUANTITY) {
            errorMessage.setValue("Quantity must be at least " + Constants.MIN_QUANTITY);
            return;
        }

        // Validate unit
        if (unit == null || unit.trim().isEmpty()) {
            errorMessage.setValue("Unit is required");
            return;
        }

        // All valid - create/update item
        PantryItem item = new PantryItem();
        item.ingredientName = name.trim();
        item.quantity = quantity;
        item.unit = unit;
        item.expiryDate = expiryDate;

        if (isEditMode) {
            item.id = editingItemId;
            repository.update(item);
        } else {
            repository.insert(item);
        }
        saveSuccess.setValue(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.shutdown();
    }
}