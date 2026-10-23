package com.example.smartpantry.ui.suggested;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.databinding.ItemSectionHeaderBinding;
import com.example.smartpantry.databinding.ItemSuggestedRecipeBinding;

import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for the Suggested Recipes screen.
 * Shows two clearly separated sections:
 *   - "Ready to cook": strictly matchable recipes (every ingredient available).
 *   - "Almost there": recipes missing exactly one ingredient (bonus feature).
 * Section headers use view type HEADER; recipes use view type RECIPE.
 * Clicking a recipe navigates to RecipeDetailFragment.
 */
public class SuggestedAdapter extends ListAdapter<SuggestedAdapter.ListItem, RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_RECIPE = 1;

    private final OnRecipeClickListener clickListener;

    public interface OnRecipeClickListener {
        void onRecipeClick(long recipeId);
    }

    /** Item in the combined sectioned list: either a section header or a recipe. */
    public static abstract class ListItem {
        public abstract int getType();
    }

    /** Section header item. */
    public static class HeaderItem extends ListItem {
        public final String title;
        public final int headerType; // 0 = ready, 1 = almost there

        public HeaderItem(String title, int headerType) {
            this.title = title;
            this.headerType = headerType;
        }

        @Override
        public int getType() {
            return TYPE_HEADER;
        }
    }

    /** Recipe row item. */
    public static class RecipeItem extends ListItem {
        public final SuggestedViewModel.RecipeWithMatch recipe;

        public RecipeItem(SuggestedViewModel.RecipeWithMatch recipe) {
            this.recipe = recipe;
        }

        @Override
        public int getType() {
            return TYPE_RECIPE;
        }
    }

    public SuggestedAdapter(OnRecipeClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<ListItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<ListItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull ListItem oldItem, @NonNull ListItem newItem) {
            if (oldItem.getType() != newItem.getType()) return false;
            if (oldItem instanceof HeaderItem) {
                return ((HeaderItem) oldItem).headerType == ((HeaderItem) newItem).headerType;
            }
            return ((RecipeItem) oldItem).recipe.recipeId == ((RecipeItem) newItem).recipe.recipeId;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ListItem oldItem, @NonNull ListItem newItem) {
            if (oldItem instanceof HeaderItem) {
                HeaderItem a = (HeaderItem) oldItem;
                HeaderItem b = (HeaderItem) newItem;
                return a.title.equals(b.title);
            }
            SuggestedViewModel.RecipeWithMatch a = ((RecipeItem) oldItem).recipe;
            SuggestedViewModel.RecipeWithMatch b = ((RecipeItem) newItem).recipe;
            return a.name.equals(b.name)
                    && a.totalIngredients == b.totalIngredients
                    && a.matchedIngredients == b.matchedIngredients
                    && a.isStrictMatch == b.isStrictMatch
                    && a.missingCount == b.missingCount;
        }
    };

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(ItemSectionHeaderBinding.inflate(inflater, parent, false));
        }
        return new RecipeViewHolder(ItemSuggestedRecipeBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = getItem(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(((HeaderItem) item).title);
        } else if (holder instanceof RecipeViewHolder) {
            ((RecipeViewHolder) holder).bind(((RecipeItem) item).recipe);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final ItemSectionHeaderBinding binding;

        HeaderViewHolder(@NonNull ItemSectionHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String title) {
            binding.tvSectionHeader.setText(title);
        }
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ItemSuggestedRecipeBinding binding;

        RecipeViewHolder(@NonNull ItemSuggestedRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && pos < getCurrentList().size()
                        && getItem(pos) instanceof RecipeItem && clickListener != null) {
                    clickListener.onRecipeClick(((RecipeItem) getItem(pos)).recipe.recipeId);
                }
            });
        }

        void bind(SuggestedViewModel.RecipeWithMatch item) {
            binding.tvRecipeName.setText(item.name);
            binding.tvIngredientCount.setText(String.format(Locale.getDefault(),
                    "%d/%d ingredients", item.matchedIngredients, item.totalIngredients));

            if (item.isStrictMatch) {
                binding.tvMatchIndicator.setText(R.string.match_ready_to_cook);
                binding.tvMatchIndicator.setTextColor(ContextCompat.getColor(
                        binding.tvMatchIndicator.getContext(), R.color.success));
            } else {
                String missing = joinMissing(item.missingIngredients);
                binding.tvMatchIndicator.setText(binding.tvMatchIndicator.getContext().getString(
                        R.string.match_missing_ingredients, item.missingCount, missing));
                binding.tvMatchIndicator.setTextColor(ContextCompat.getColor(
                        binding.tvMatchIndicator.getContext(), R.color.warning));
            }
        }

        /** Joins the missing-ingredient names into a readable list. */
        private String joinMissing(List<String> missingIngredients) {
            if (missingIngredients == null || missingIngredients.isEmpty()) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < missingIngredients.size(); i++) {
                if (i > 0) sb.append(", ");
                // Strip parenthetical explanations like "(not in pantry)" for a cleaner list
                String name = missingIngredients.get(i);
                int paren = name.indexOf('(');
                if (paren > 0) name = name.substring(0, paren).trim();
                sb.append(name);
            }
            return sb.toString();
        }
    }
}
