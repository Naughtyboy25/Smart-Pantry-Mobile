package com.example.smartpantry.ui.suggested;

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
import com.example.smartpantry.databinding.FragmentSuggestedBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for Suggested Recipes screen (second tab).
 * Displays strictly matchable recipes based on current pantry.
 * Empty state shows friendly message when no recipes match.
 * Click recipe to navigate to RecipeDetailFragment.
 */
public class SuggestedFragment extends Fragment implements SuggestedAdapter.OnRecipeClickListener {

    private FragmentSuggestedBinding binding;
    private SuggestedViewModel viewModel;
    private SuggestedAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSuggestedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SuggestedViewModel.class);
        setupRecyclerView();
        observeSuggestedRecipes();
        setupEmptyStateCta();
    }

    private void setupRecyclerView() {
        adapter = new SuggestedAdapter(this);
        binding.rvSuggested.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSuggested.setAdapter(adapter);
    }

    private void observeSuggestedRecipes() {
        // Combine the two sections (strict matches + almost-there) into one list with headers
        viewModel.getSuggestedRecipes().observe(getViewLifecycleOwner(), strict -> {
            updateAdapter(strict, viewModel.getAlmostThereRecipes().getValue());
        });
        viewModel.getAlmostThereRecipes().observe(getViewLifecycleOwner(), almost -> {
            updateAdapter(viewModel.getSuggestedRecipes().getValue(), almost);
        });
    }

    private void updateAdapter(List<SuggestedViewModel.RecipeWithMatch> strict,
                               List<SuggestedViewModel.RecipeWithMatch> almost) {
        List<SuggestedAdapter.ListItem> items = new ArrayList<>();

        if (strict != null && !strict.isEmpty()) {
            items.add(new SuggestedAdapter.HeaderItem(
                    getString(R.string.suggested_header_ready), 0));
            for (SuggestedViewModel.RecipeWithMatch r : strict) {
                items.add(new SuggestedAdapter.RecipeItem(r));
            }
        }

        if (almost != null && !almost.isEmpty()) {
            items.add(new SuggestedAdapter.HeaderItem(
                    getString(R.string.suggested_header_almost), 1));
            for (SuggestedViewModel.RecipeWithMatch r : almost) {
                items.add(new SuggestedAdapter.RecipeItem(r));
            }
        }

        adapter.submitList(items);
        boolean empty = items.isEmpty();
        binding.emptyStateContainer.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.rvSuggested.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void setupEmptyStateCta() {
        binding.btnEmptyGoToPantry.setOnClickListener(v -> {
            // Switch to pantry tab via bottom navigation
            if (getActivity() != null) {
                com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                        getActivity().findViewById(R.id.bottomNavView);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.pantryFragment);
                }
            }
        });
    }

    @Override
    public void onRecipeClick(long recipeId) {
        Bundle args = new Bundle();
        args.putLong("recipeId", recipeId);
        Navigation.findNavController(requireView()).navigate(R.id.action_suggestedFragment_to_recipeDetailFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}