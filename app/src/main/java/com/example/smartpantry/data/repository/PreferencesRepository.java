package com.example.smartpantry.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository for user preferences (Settings screen).
 * Backed by SharedPreferences for simple, reliable persistence.
 * Exposes preferences as LiveData for reactive UI updates.
 */
public class PreferencesRepository {

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_UNIT_SYSTEM = "unit_system";

    private final SharedPreferences prefs;
    private final MutableLiveData<AppPreferences> preferencesLiveData = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PreferencesRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadInitialPreferences();
    }

    /** Returns current preferences as observable LiveData. */
    public LiveData<AppPreferences> getPreferences() {
        return preferencesLiveData;
    }

    /** Enables/disables expiring-soon alerts. */
    public void setExpiryAlertsEnabled(boolean enabled) {
        executor.execute(() -> {
            prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
            loadInitialPreferences();
        });
    }

    /** Sets default unit system (METRIC or IMPERIAL). */
    public void setDefaultUnits(UnitSystem unitSystem) {
        executor.execute(() -> {
            prefs.edit().putString(KEY_UNIT_SYSTEM, unitSystem.name()).apply();
            loadInitialPreferences();
        });
    }

    private void loadInitialPreferences() {
        executor.execute(() -> {
            AppPreferences current = new AppPreferences();
            current.expiryAlertsEnabled = prefs.getBoolean(KEY_EXPIRY_ALERTS, true);
            String unitStr = prefs.getString(KEY_UNIT_SYSTEM, UnitSystem.METRIC.name());
            current.defaultUnits = UnitSystem.valueOf(unitStr);
            preferencesLiveData.postValue(current);
        });
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

    /** Shuts down the background executor. */
    public void shutdown() {
        executor.shutdown();
    }
}