package com.example.smartpantry.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * Fragment for Settings screen (third tab).
 * Displays toggles for: expiry alerts, default unit system.
 * Buttons for: clear all pantry data, reset recipe database.
 * Shows app version info.
 */
public class SettingsFragment extends Fragment {

    private SettingsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(0, container, false); // TODO: R.layout.fragment_settings
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // TODO: Bind preference LiveData to UI switches/radio buttons
        // TODO: Set click listeners for clear pantry / reset recipes buttons
        // TODO: Display app version from BuildConfig.VERSION_NAME
    }
}