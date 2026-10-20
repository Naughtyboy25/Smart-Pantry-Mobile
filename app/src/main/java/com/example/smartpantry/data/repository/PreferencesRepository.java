package com.example.smartpantry.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Repository for user preferences (Settings screen).
 * Backed by SharedPreferences (or DataStore for modern approach).
 * Exposes preferences as LiveData for reactive UI updates.
 */
public class PreferencesRepository {

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_UNIT_SYSTEM = "unit_system";

    private final SharedPreferences prefs;
    private final MutableLiveData<AppPreferences> preferencesLiveData = new MutableLiveData<>();

    public PreferencesRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // TODO: Load initial preferences
    }

    /** Returns current preferences as observable LiveData. */
    public LiveData<AppPreferences> getPreferences() {
        return preferencesLiveData;
    }

    /** Enables/disables expiring-soon alerts. */
    public void setExpiryAlertsEnabled(boolean enabled) {
        // TODO: Save to SharedPreferences and update LiveData
    }

    /** Sets default unit system (METRIC or IMPERIAL). */
    public void setDefaultUnits(UnitSystem unitSystem) {
        // TODO: Save to SharedPreferences and update LiveData
    }

    /** Data class holding all user preferences. */
    public static class AppPreferences {
        public boolean expiryAlertsEnabled = true;
        public UnitSystem defaultUnits = UnitSystem.METRIC;
    }

    /** Unit system enumeration. */
    public enum UnitSystem {
        METRIC,
        IMPERIAL
    }
}