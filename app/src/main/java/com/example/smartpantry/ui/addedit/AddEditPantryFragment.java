package com.example.smartpantry.ui.addedit;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.smartpantry.R;
import com.example.smartpantry.databinding.FragmentAddEditPantryBinding;
import com.example.smartpantry.util.Constants;
import com.example.smartpantry.util.SnackbarUtils;

import java.util.Calendar;
import java.util.Locale;

/**
 * Fragment for Add/Edit Pantry Item screen.
 * Handles both ADD and EDIT modes via navigation arguments.
 * Validates input, shows date picker for expiry, saves via ViewModel.
 * Uses Material Design exposed dropdown menu (AutoCompleteTextView) for unit selection.
 */
public class AddEditPantryFragment extends Fragment {

    private FragmentAddEditPantryBinding binding;
    private AddEditPantryViewModel viewModel;
    private Long selectedExpiryDate = null;
    private ArrayAdapter<String> unitAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddEditPantryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddEditPantryViewModel.class);
        setupUnitDropdown();
        setupExpiryPicker();
        observeViewModel();
        loadArguments();
        setupButtons();
    }

    private void setupUnitDropdown() {
        // Create adapter for the exposed dropdown menu (AutoCompleteTextView)
        unitAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, Constants.UNIT_LABELS);

        AutoCompleteTextView autoCompleteUnit = binding.etUnit;
        autoCompleteUnit.setAdapter(unitAdapter);
        autoCompleteUnit.setThreshold(0); // Show dropdown immediately on click

        // Show dropdown on click
        autoCompleteUnit.setOnClickListener(v -> {
            if (unitAdapter != null) {
                autoCompleteUnit.showDropDown();
            }
        });

        // Prevent keyboard from showing, show dropdown instead
        autoCompleteUnit.setFocusable(false);
        autoCompleteUnit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                autoCompleteUnit.showDropDown();
            }
        });
    }

    private void setupExpiryPicker() {
        binding.etExpiryDate.setOnClickListener(v -> showDatePicker());
        binding.etExpiryDate.setFocusable(false);
        binding.etExpiryDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDatePicker();
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (selectedExpiryDate != null) {
            cal.setTimeInMillis(selectedExpiryDate);
        }
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            cal.set(year, month, dayOfMonth, 23, 59, 59);
            selectedExpiryDate = cal.getTimeInMillis();
            binding.etExpiryDate.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadArguments() {
        Bundle args = getArguments();
        String mode = args != null ? args.getString("mode", "ADD") : "ADD";
        long itemId = args != null ? args.getLong("itemId", -1) : -1;
        viewModel.init(mode, itemId);

        // Update toolbar title
        if ("ADD".equals(mode)) {
            binding.toolbar.setTitle(R.string.add_ingredient);
        } else {
            binding.toolbar.setTitle("Edit Ingredient");
        }
    }

    private void observeViewModel() {
        viewModel.getItem().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                binding.etIngredientName.setText(item.ingredientName);
                binding.etQuantity.setText(String.valueOf(item.quantity));
                if (item.unit != null) {
                    // Find unit position in adapter and set selection
                    for (int i = 0; i < Constants.UNITS.length; i++) {
                        if (Constants.UNITS[i].equals(item.unit)) {
                            binding.etUnit.setText(Constants.UNIT_LABELS[i]);
                            break;
                        }
                    }
                }
                if (item.expiryDate != null) {
                    selectedExpiryDate = item.expiryDate;
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(item.expiryDate);
                    binding.etExpiryDate.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));
                }
            }
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                SnackbarUtils.showSuccess(requireView(), R.string.snack_item_saved, SnackbarUtils.DURATION_SHORT);
                NavController navController = Navigation.findNavController(requireView());
                navController.popBackStack();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                SnackbarUtils.showError(requireView(), error, SnackbarUtils.DURATION_LONG);
            }
        });
    }

    private void setupButtons() {
        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etIngredientName.getText().toString().trim();
            String quantity = binding.etQuantity.getText().toString().trim();
            String unitLabel = binding.etUnit.getText().toString().trim();

            // Convert label back to unit code
            String unit = unitLabel;
            for (int i = 0; i < Constants.UNIT_LABELS.length; i++) {
                if (Constants.UNIT_LABELS[i].equals(unitLabel)) {
                    unit = Constants.UNITS[i];
                    break;
                }
            }

            viewModel.saveItem(name, quantity, unit, selectedExpiryDate);
        });

        binding.btnCancel.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        binding.toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}