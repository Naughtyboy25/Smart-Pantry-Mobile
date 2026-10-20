package com.example.smartpantry.ui.addedit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

/**
 * Fragment for Add/Edit Pantry Item screen.
 * Handles both ADD and EDIT modes via navigation arguments.
 * Validates input, shows date picker for expiry, saves via ViewModel.
 */
public class AddEditPantryFragment extends Fragment {

    private AddEditPantryViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Inflate fragment_add_edit_pantry.xml via ViewBinding
        return inflater.inflate(0, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddEditPantryViewModel.class);
        // TODO: setup unit spinner with Constants.UNIT_LABELS
        // TODO: setup expiry date picker
        // TODO: observe ViewModel LiveData (item, saveSuccess, errorMessage)
        // TODO: load navigation arguments (mode, itemId)
        // TODO: setup save/cancel button click listeners
    }
}