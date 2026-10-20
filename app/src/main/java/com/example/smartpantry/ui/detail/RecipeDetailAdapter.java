package com.example.smartpantry.ui.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.data.entity.RecipeIngredient;
import com.example.smartpantry.data.entity.RecipeStep;

/**
 * RecyclerView Adapter for Recipe Detail screen.
 * Two view types: ingredients list and steps list.
 * Binds to item_recipe_ingredient.xml and item_recipe_step.xml layouts.
 */
public class RecipeDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_INGREDIENT = 0;
    private static final int TYPE_STEP = 1;

    private RecipeDetailViewModel.RecipeDetail recipeDetail;

    public void setRecipeDetail(RecipeDetailViewModel.RecipeDetail detail) {
        // TODO: Set detail and notifyDataSetChanged
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_INGREDIENT; // TODO: Implement logic
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null; // TODO: Inflate layout based on viewType
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // TODO: Bind ingredient or step data
    }

    @Override
    public int getItemCount() {
        return 0; // TODO: Return total count
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        IngredientViewHolder(@NonNull View itemView) { super(itemView); }
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        StepViewHolder(@NonNull View itemView) { super(itemView); }
    }
}