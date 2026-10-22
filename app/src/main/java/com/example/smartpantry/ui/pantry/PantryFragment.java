package com.example.smartpantry.ui.pantry;

import android.app.AlertDialog;
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

import com.example.smartpantry.R;
import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.databinding.FragmentPantryBinding;
import com.example.smartpantry.util.SnackbarUtils;

/**
 * Fragment for Pantry List screen (first tab).
 * Displays all pantry items in a RecyclerView.
 * FAB navigates to AddEditPantryFragment for adding new items.
 * Click item to edit, long-press to delete.
 */
public class PantryFragment extends Fragment implements PantryAdapter.OnItemClickListener, PantryAdapter.OnItemDeleteListener {

    private FragmentPantryBinding binding;
    private PantryViewModel viewModel;
    private PantryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPantryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PantryViewModel.class);
        setupRecyclerView();
        observePantryItems();
        setupFab();
    }

    private void setupRecyclerView() {
        adapter = new PantryAdapter(this, this);
        binding.rvPantry.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPantry.setAdapter(adapter);
    }

    private void observePantryItems() {
        viewModel.getAllPantryItems().observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);
            boolean empty = items == null || items.isEmpty();
            binding.emptyStateContainer.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.rvPantry.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
    }

    private void setupFab() {
        binding.fabAdd.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_pantryFragment_to_addEditPantryFragment);
        });
    }

    @Override
    public void onItemClick(PantryItem item) {
        Bundle args = new Bundle();
        args.putString("mode", "EDIT");
        args.putLong("itemId", item.id);
        Navigation.findNavController(requireView()).navigate(R.id.action_pantryFragment_to_addEditPantryFragment, args);
    }

    @Override
    public void onItemDelete(PantryItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Item")
                .setMessage(getString(R.string.confirm_delete_item, item.ingredientName))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deleteItem(item.id);
                    SnackbarUtils.showSuccess(requireView(), R.string.snack_item_deleted, SnackbarUtils.DURATION_SHORT);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}