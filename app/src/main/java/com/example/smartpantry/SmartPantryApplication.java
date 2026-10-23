package com.example.smartpantry;

import android.app.Application;

import com.example.smartpantry.data.database.AppDatabase;

/**
 * Application class for Smart Pantry Manager.
 * Initializes any global singletons if needed (e.g., Room database pre-warming).
 */
public class SmartPantryApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Pre-create database instance to avoid first-access latency
        AppDatabase.getInstance(this);
    }
}