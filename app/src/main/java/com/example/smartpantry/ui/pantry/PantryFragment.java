package com.example.smartpantry.ui.pantry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smartpantry.data.entity.PantryItem;

/**
 * Fragment for Pantry List screen (first tab).
 * Displays all pantry items in a RecyclerView.
 * Navigates to AddEditPantryFragment for adding new items.
 * Click item to edit, long-press to delete.
 */
public class PantryFragment extends Fragment implements PantryAdapter.OnItemClickListener, PantryAdapter.OnItemDeleteListener {

    private PantryViewModel viewModel;
    private PantryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Inflate fragment_pantry.xml via ViewBinding
        return inflater.inflate(0, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PantryViewModel.class);
        // TODO: setup RecyclerView with adapter
        // TODO: observe pantry items LiveData
        // TODO: setup FAB click listener for navigation
    }

    @Override
    public void onItemClick(PantryItem item) {
        // TODO: Navigate to AddEditPantryFragment in EDIT mode with itemId
    }

    @Override
    public void onItemDelete(PantryItem item) {
        // TODO: Show confirmation dialog then call viewModel.deleteItem(item.id)
    }
}