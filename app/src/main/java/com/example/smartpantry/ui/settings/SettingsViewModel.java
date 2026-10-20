package com.example.smartpantry.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smartpantry.data.repository.PreferencesRepository;

/**
 * ViewModel for Settings screen.
 * Exposes user preferences as LiveData and handles preference changes.
 * Preferences: expiry alerts toggle, default unit system (metric/imperial).
 */
public class SettingsViewModel extends AndroidViewModel {

    private final PreferencesRepository repository;
    private final LiveData<PreferencesRepository.AppPreferences> preferences;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        repository = new PreferencesRepository(application);
        preferences = repository.getPreferences();
    }

    public LiveData<PreferencesRepository.AppPreferences> getPreferences() {
        return preferences;
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        repository.setExpiryAlertsEnabled(enabled);
    }

    public void setDefaultUnits(PreferencesRepository.UnitSystem unitSystem) {
        repository.setDefaultUnits(unitSystem);
    }

    /** Clears all pantry data (delegates to PantryRepository). */
    public void clearAllPantryData() {
        // TODO: Use PantryRepository to clear all
    }

    /** Resets recipe database to seeded defaults. */
    public void resetRecipesToDefaults() {
        // TODO: Use RecipeRepository to reset
    }
}