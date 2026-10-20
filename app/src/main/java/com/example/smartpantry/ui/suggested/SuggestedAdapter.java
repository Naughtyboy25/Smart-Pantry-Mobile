package com.example.smartpantry.ui.suggested;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView Adapter for Suggested Recipes screen.
 * Binds RecipeWithMatch data to item layout.
 * Click navigates to RecipeDetailFragment.
 */
public class SuggestedAdapter extends ListAdapter<SuggestedViewModel.RecipeWithMatch, SuggestedAdapter.SuggestedViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(long recipeId);
    }

    public SuggestedAdapter(OnRecipeClickListener clickListener) {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<SuggestedViewModel.RecipeWithMatch> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<SuggestedViewModel.RecipeWithMatch>() {
                @Override
                public boolean areItemsTheSame(@NonNull SuggestedViewModel.RecipeWithMatch oldItem, @NonNull SuggestedViewModel.RecipeWithMatch newItem) {
                    return false; // TODO
                }

                @Override
                public boolean areContentsTheSame(@NonNull SuggestedViewModel.RecipeWithMatch oldItem, @NonNull SuggestedViewModel.RecipeWithMatch newItem) {
                    return false; // TODO
                }
            };

    @NonNull
    @Override
    public SuggestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null; // TODO
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestedViewHolder holder, int position) {
        // TODO
    }

    static class SuggestedViewHolder extends RecyclerView.ViewHolder {
        SuggestedViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}