package com.example.smartpantry;

import android.app.Application;

/**
 * Application class for Smart Pantry Manager.
 * Initializes any global singletons if needed (e.g., Room database pre-warming).
 */
public class SmartPantryApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: Optionally trigger database creation here: AppDatabase.getInstance(this);
    }
}