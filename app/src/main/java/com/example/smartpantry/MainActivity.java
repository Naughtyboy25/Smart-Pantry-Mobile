package com.example.smartpantry;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity - Single Activity host for all screens.
 * Uses Navigation Component with BottomNavigationView for 3 main tabs:
 * 1. Pantry List
 * 2. Suggested Recipes
 * 3. Settings
 * Add/Edit Pantry and Recipe Detail are navigated as nested destinations.
 */
public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Set content view via ViewBinding (activity_main.xml)
        // TODO: Setup Navigation with BottomNavigationView
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }
}