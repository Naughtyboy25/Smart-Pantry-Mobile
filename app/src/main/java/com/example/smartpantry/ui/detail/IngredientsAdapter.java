package com.example.smartpantry.ui.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.data.entity.RecipeIngredient;
import com.example.smartpantry.databinding.ItemRecipeIngredientBinding;

/**
 * RecyclerView Adapter for Recipe Ingredients list.
 * Binds ingredient name and quantity/unit to item_recipe_ingredient.xml layout.
 */
public class IngredientsAdapter extends ListAdapter<RecipeIngredient, IngredientsAdapter.IngredientViewHolder> {

    public IngredientsAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<RecipeIngredient> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<RecipeIngredient>() {
                @Override
                public boolean areItemsTheSame(@NonNull RecipeIngredient oldItem, @NonNull RecipeIngredient newItem) {
                    return oldItem.recipeId == newItem.recipeId
                            && oldItem.ingredientName.equals(newItem.ingredientName);
                }

                @Override
                public boolean areContentsTheSame(@NonNull RecipeIngredient oldItem, @NonNull RecipeIngredient newItem) {
                    return oldItem.quantity == newItem.quantity
                            && oldItem.unit.equals(newItem.unit);
                }
            };

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeIngredientBinding binding = ItemRecipeIngredientBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new IngredientViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecipeIngredientBinding binding;

        IngredientViewHolder(@NonNull ItemRecipeIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(RecipeIngredient ingredient) {
            binding.tvIngredientName.setText(ingredient.ingredientName);
            binding.tvQuantityUnit.setText(String.format("%.1f %s", ingredient.quantity, ingredient.unit));
        }
    }
}