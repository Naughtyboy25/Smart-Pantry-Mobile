package com.example.smartpantry.data.repository;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Repository for user preferences (Settings screen).
 * Backed by DataStore for modern, async, and transactional persistence.
 * Exposes preferences as LiveData for reactive UI updates.
 */
public class PreferencesRepository {

    private static final String DATASTORE_NAME = "smart_pantry_prefs";

    // DataStore preference keys
    private static final Preferences.Key<Boolean> KEY_EXPIRY_ALERTS = PreferencesKeys.booleanKey("expiry_alerts_enabled");
    private static final Preferences.Key<String> KEY_UNIT_SYSTEM = PreferencesKeys.stringKey("unit_system");

    private final RxDataStore<Preferences> dataStore;
    private final MutableLiveData<AppPreferences> preferencesLiveData = new MutableLiveData<>();

    public PreferencesRepository(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context, DATASTORE_NAME).build();
        loadInitialPreferences();
    }

    /** Returns current preferences as observable LiveData. */
    public LiveData<AppPreferences> getPreferences() {
        return preferencesLiveData;
    }

    /** Enables/disables expiring-soon alerts. */
    public void setExpiryAlertsEnabled(boolean enabled) {
        dataStore.updateDataAsync(prefs -> prefs.toMutablePreferences().apply {
            putBoolean(KEY_EXPIRY_ALERTS, enabled)
        }).subscribeOn(Schedulers.io())
                .subscribe(
                        unused -> loadInitialPreferences(),
                        throwable -> throwable.printStackTrace()
                );
    }

    /** Sets default unit system (METRIC or IMPERIAL). */
    public void setDefaultUnits(UnitSystem unitSystem) {
        dataStore.updateDataAsync(prefs -> prefs.toMutablePreferences().apply {
            putString(KEY_UNIT_SYSTEM, unitSystem.name())
        }).subscribeOn(Schedulers.io())
                .subscribe(
                        unused -> loadInitialPreferences(),
                        throwable -> throwable.printStackTrace()
                );
    }

    private void loadInitialPreferences() {
        Flowable<Preferences> prefsFlow = dataStore.data()
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(throwable -> {
                    throwable.printStackTrace();
                    return Flowable.just(Preferences.empty());
                });

        prefsFlow.firstOrError()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        prefs -> {
                            AppPreferences current = new AppPreferences();
                            current.expiryAlertsEnabled = prefs.get(KEY_EXPIRY_ALERTS) != null ? prefs.get(KEY_EXPIRY_ALERTS) : true;
                            String unitStr = prefs.get(KEY_UNIT_SYSTEM);
                            current.defaultUnits = unitStr != null ? UnitSystem.valueOf(unitStr) : UnitSystem.METRIC;
                            preferencesLiveData.postValue(current);
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            AppPreferences defaults = new AppPreferences();
                            preferencesLiveData.postValue(defaults);
                        }
                );
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

    /** No-op shutdown for consistency with other repositories. DataStore doesn't require explicit shutdown. */
    public void shutdown() {
        // DataStore handles cleanup automatically
    }
}