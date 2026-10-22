package com.example.smartpantry.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartpantry.BuildConfig;
import com.example.smartpantry.R;
import com.example.smartpantry.databinding.FragmentSettingsBinding;
import com.example.smartpantry.util.SnackbarUtils;

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
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.clear_pantry)
                    .setMessage(R.string.confirm_clear_pantry)
                    .setPositiveButton(R.string.delete, (dialog, which) -> {
                        viewModel.clearAllPantryData();
                        SnackbarUtils.showSuccess(requireView(), R.string.snack_pantry_cleared, SnackbarUtils.DURATION_SHORT);
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });

        binding.btnResetRecipes.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.reset_recipes)
                    .setMessage(R.string.confirm_reset_recipes)
                    .setPositiveButton(R.string.delete, (dialog, which) -> {
                        viewModel.resetRecipesToDefaults();
                        SnackbarUtils.showSuccess(requireView(), R.string.snack_recipes_reset, SnackbarUtils.DURATION_SHORT);
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });
    }

    private void displayVersion() {
        String versionName = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;
        binding.tvVersion.setText(getString(R.string.app_version_format, versionName, versionCode));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}