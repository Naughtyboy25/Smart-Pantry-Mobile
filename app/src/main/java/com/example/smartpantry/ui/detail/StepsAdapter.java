package com.example.smartpantry.ui.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.data.entity.RecipeStep;
import com.example.smartpantry.databinding.ItemRecipeStepBinding;

/**
 * RecyclerView Adapter for Recipe Steps list.
 * Binds step number and instruction to item_recipe_step.xml layout.
 */
public class StepsAdapter extends ListAdapter<RecipeStep, StepsAdapter.StepViewHolder> {

    public StepsAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<RecipeStep> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<RecipeStep>() {
                @Override
                public boolean areItemsTheSame(@NonNull RecipeStep oldItem, @NonNull RecipeStep newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull RecipeStep oldItem, @NonNull RecipeStep newItem) {
                    return oldItem.stepNumber == newItem.stepNumber
                            && oldItem.instruction.equals(newItem.instruction);
                }
            };

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeStepBinding binding = ItemRecipeStepBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new StepViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecipeStepBinding binding;

        StepViewHolder(@NonNull ItemRecipeStepBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(RecipeStep step) {
            binding.tvStepNumber.setText(step.stepNumber + ".");
            binding.tvStepInstruction.setText(step.instruction);
        }
    }
}