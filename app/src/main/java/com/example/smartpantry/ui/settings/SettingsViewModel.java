package com.example.smartpantry.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smartpantry.data.repository.PantryRepository;
import com.example.smartpantry.data.repository.PreferencesRepository;
import com.example.smartpantry.data.repository.RecipeRepository;

/**
 * ViewModel for Settings screen.
 * Exposes user preferences as LiveData and handles preference changes.
 * Preferences: expiry alerts toggle, default unit system (metric/imperial).
 * Actions: clear all pantry data, reset recipe database.
 */
public class SettingsViewModel extends AndroidViewModel {

    private final PreferencesRepository preferencesRepository;
    private final PantryRepository pantryRepository;
    private final RecipeRepository recipeRepository;

    private final LiveData<PreferencesRepository.AppPreferences> preferences;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        preferencesRepository = new PreferencesRepository(application);
        pantryRepository = new PantryRepository(application);
        recipeRepository = new RecipeRepository(application);
        preferences = preferencesRepository.getPreferences();
    }

    public LiveData<PreferencesRepository.AppPreferences> getPreferences() {
        return preferences;
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        preferencesRepository.setExpiryAlertsEnabled(enabled);
    }

    public void setDefaultUnits(PreferencesRepository.UnitSystem unitSystem) {
        preferencesRepository.setDefaultUnits(unitSystem);
    }

    /** Clears all pantry data (delegates to PantryRepository). */
    public void clearAllPantryData() {
        pantryRepository.clearAll();
    }

    /** Resets recipe database to seeded defaults. */
    public void resetRecipesToDefaults() {
        recipeRepository.resetToDefaults(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        preferencesRepository.shutdown();
        pantryRepository.shutdown();
        recipeRepository.shutdown();
    }
}