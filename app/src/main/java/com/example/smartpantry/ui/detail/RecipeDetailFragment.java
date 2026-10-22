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

import com.example.smartpantry.databinding.FragmentRecipeDetailBinding;

/**
 * Fragment for Recipe Detail screen.
 * Displays full ingredient list and preparation steps for a recipe.
 * Receives recipeId via navigation arguments.
 * Optional "Cook It" button to deduct ingredients from pantry (bonus).
 */
public class RecipeDetailFragment extends Fragment {

    private FragmentRecipeDetailBinding binding;
    private RecipeDetailViewModel viewModel;
    private IngredientsAdapter ingredientsAdapter;
    private StepsAdapter stepsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
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

        setupRecyclerViews();
        observeRecipeDetail();
        setupToolbar();
    }

    private void setupRecyclerViews() {
        ingredientsAdapter = new IngredientsAdapter();
        binding.rvIngredients.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvIngredients.setAdapter(ingredientsAdapter);

        stepsAdapter = new StepsAdapter();
        binding.rvSteps.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSteps.setAdapter(stepsAdapter);
    }

    private void observeRecipeDetail() {
        viewModel.getRecipeDetail().observe(getViewLifecycleOwner(), detail -> {
            if (detail != null) {
                binding.tvRecipeName.setText(detail.recipe.name);
                ingredientsAdapter.submitList(detail.ingredients);
                stepsAdapter.submitList(detail.steps);
            }
        });
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}