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

import com.example.smartpantry.databinding.FragmentSuggestedBinding;

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
    }

    private void setupRecyclerView() {
        adapter = new SuggestedAdapter(this);
        binding.rvSuggested.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSuggested.setAdapter(adapter);
    }

    private void observeSuggestedRecipes() {
        viewModel.getSuggestedRecipes().observe(getViewLifecycleOwner(), recipes -> {
            adapter.submitList(recipes);
            boolean empty = recipes == null || recipes.isEmpty();
            binding.emptyStateContainer.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.rvSuggested.setVisibility(empty ? View.GONE : View.VISIBLE);
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