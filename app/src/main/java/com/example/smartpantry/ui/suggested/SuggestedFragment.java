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

/**
 * Fragment for Suggested Recipes screen (second tab).
 * Displays strictly matchable recipes based on current pantry.
 * Empty state shows friendly message when no recipes match.
 * Click recipe to navigate to RecipeDetailFragment.
 */
public class SuggestedFragment extends Fragment implements SuggestedAdapter.OnRecipeClickListener {

    private SuggestedViewModel viewModel;
    private SuggestedAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(0, container, false); // TODO: R.layout.fragment_suggested
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SuggestedViewModel.class);
        // TODO: setup RecyclerView, observe suggestedRecipes, setup empty state
    }

    @Override
    public void onRecipeClick(long recipeId) {
        // TODO: Navigation.findNavController(...).navigate(action, bundle with recipeId)
    }
}