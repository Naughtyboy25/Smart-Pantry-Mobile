package com.example.smartpantry.ui.addedit;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
 */
public class AddEditPantryFragment extends Fragment {

    private FragmentAddEditPantryBinding binding;
    private AddEditPantryViewModel viewModel;
    private Long selectedExpiryDate = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddEditPantryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddEditPantryViewModel.class);
        setupUnitSpinner();
        setupExpiryPicker();
        observeViewModel();
        loadArguments();
        setupButtons();
    }

    private void setupUnitSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, Constants.UNIT_LABELS);
        binding.spinnerUnit.setAdapter(adapter);
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
        binding.toolbar.setTitle("ADD".equals(mode) ? R.string.add_ingredient : "Edit Ingredient");
    }

    private void observeViewModel() {
        viewModel.getItem().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                binding.etIngredientName.setText(item.ingredientName);
                binding.etQuantity.setText(String.valueOf(item.quantity));
                if (item.unit != null) {
                    // Find unit position in spinner
                    for (int i = 0; i < Constants.UNITS.length; i++) {
                        if (Constants.UNITS[i].equals(item.unit)) {
                            binding.spinnerUnit.setSelection(i);
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
            String name = binding.etIngredientName.getText().toString();
            String quantity = binding.etQuantity.getText().toString();
            String unit = Constants.UNITS[binding.spinnerUnit.getSelectedItemPosition()];
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