package com.example.smartpantry.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartpantry.BuildConfig;
import com.example.smartpantry.databinding.FragmentSettingsBinding;

/**
 * Fragment for Settings screen (third tab).
 * Displays toggles for: expiry alerts, default unit system.
 * Buttons for: clear all pantry data, reset recipe database.
 * Shows app version info.
 */
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        bindPreferences();
        setupClickListeners();
        displayVersion();
    }

    private void bindPreferences() {
        viewModel.getPreferences().observe(getViewLifecycleOwner(), prefs -> {
            if (prefs != null) {
                binding.switchExpiryAlerts.setChecked(prefs.expiryAlertsEnabled);
                binding.rbMetric.setChecked(prefs.defaultUnits == PreferencesRepository.UnitSystem.METRIC);
                binding.rbImperial.setChecked(prefs.defaultUnits == PreferencesRepository.UnitSystem.IMPERIAL);
            }
        });

        binding.switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setExpiryAlertsEnabled(isChecked);
        });

        binding.rgUnitSystem.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbMetric) {
                viewModel.setDefaultUnits(PreferencesRepository.UnitSystem.METRIC);
            } else if (checkedId == R.id.rbImperial) {
                viewModel.setDefaultUnits(PreferencesRepository.UnitSystem.IMPERIAL);
            }
        });
    }

    private void setupClickListeners() {
        binding.btnClearPantry.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Clear All Pantry Data")
                    .setMessage("This will permanently delete all your pantry items. This action cannot be undone.")
                    .setPositiveButton("Clear", (dialog, which) -> {
                        viewModel.clearAllPantryData();
                        Toast.makeText(requireContext(), "Pantry data cleared", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        binding.btnResetRecipes.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Reset Recipe Database")
                    .setMessage("This will reset all recipes to the default 20 South African recipes. Any custom recipes will be lost.")
                    .setPositiveButton("Reset", (dialog, which) -> {
                        viewModel.resetRecipesToDefaults();
                        Toast.makeText(requireContext(), "Recipe database reset to defaults", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void displayVersion() {
        String versionName = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;
        binding.tvVersion.setText(String.format("Version %s (%d)", versionName, versionCode));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}