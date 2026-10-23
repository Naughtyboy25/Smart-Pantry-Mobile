package com.example.smartpantry.ui.suggested;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.databinding.ItemSuggestedRecipeBinding;

/**
 * RecyclerView Adapter for Suggested Recipes screen.
 * Binds RecipeWithMatch data to item_suggested_recipe.xml layout.
 * Click navigates to RecipeDetailFragment.
 */
public class SuggestedAdapter extends ListAdapter<SuggestedViewModel.RecipeWithMatch, SuggestedAdapter.SuggestedViewHolder> {

    private final OnRecipeClickListener clickListener;

    public interface OnRecipeClickListener {
        void onRecipeClick(long recipeId);
    }

    public SuggestedAdapter(OnRecipeClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<SuggestedViewModel.RecipeWithMatch> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<SuggestedViewModel.RecipeWithMatch>() {
                @Override
                public boolean areItemsTheSame(@NonNull SuggestedViewModel.RecipeWithMatch oldItem, @NonNull SuggestedViewModel.RecipeWithMatch newItem) {
                    return oldItem.recipeId == newItem.recipeId;
                }

                @Override
                public boolean areContentsTheSame(@NonNull SuggestedViewModel.RecipeWithMatch oldItem, @NonNull SuggestedViewModel.RecipeWithMatch newItem) {
                    return oldItem.name.equals(newItem.name)
                            && oldItem.totalIngredients == newItem.totalIngredients
                            && oldItem.matchedIngredients == newItem.matchedIngredients
                            && oldItem.isStrictMatch == newItem.isStrictMatch;
                }
            };

    @NonNull
    @Override
    public SuggestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSuggestedRecipeBinding binding = ItemSuggestedRecipeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SuggestedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestedViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class SuggestedViewHolder extends RecyclerView.ViewHolder {
        private final ItemSuggestedRecipeBinding binding;

        SuggestedViewHolder(@NonNull ItemSuggestedRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && pos < getCurrentList().size()) {
                    // Click handled by Fragment via interface
                }
            });
        }

        void bind(SuggestedViewModel.RecipeWithMatch item) {
            binding.tvRecipeName.setText(item.name);
            binding.tvIngredientCount.setText(item.totalIngredients + " ingredients");
            binding.tvMatchIndicator.setText(item.isStrictMatch ? "✓ All ingredients available" : "Missing " + (item.totalIngredients - item.matchedIngredients) + " ingredient(s)");
            binding.tvMatchIndicator.setTextColor(item.isStrictMatch ? 0xFF4CAF50 : 0xFFFF9800); // Green for match, orange for almost
        }
    }
}