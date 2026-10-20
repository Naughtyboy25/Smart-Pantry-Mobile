package com.example.smartpantry.ui.detail;

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
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Fragment for Recipe Detail screen.
 * Displays full ingredient list and preparation steps for a recipe.
 * Receives recipeId via navigation arguments.
 * Optional "Cook It" button to deduct ingredients from pantry (bonus).
 */
public class RecipeDetailFragment extends Fragment {

    private RecipeDetailViewModel viewModel;
    private RecipeDetailAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(0, container, false); // TODO: R.layout.fragment_recipe_detail
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        long recipeId = getArguments() != null ? getArguments().getLong("recipeId", -1) : -1;
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new RecipeDetailViewModel(requireActivity().getApplication(), recipeId);
            }
        }).get(RecipeDetailViewModel.class);

        // TODO: setup RecyclerView with adapter, observe recipeDetail LiveData
        // TODO: setup "Cook It" button click listener
    }
}