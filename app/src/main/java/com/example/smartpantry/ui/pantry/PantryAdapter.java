package com.example.smartpantry.ui.pantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.data.entity.PantryItem;

/**
 * RecyclerView Adapter for Pantry List screen.
 * Uses ListAdapter with DiffUtil for efficient updates.
 * Binds pantry item data to item_pantry.xml layout.
 */
public class PantryAdapter extends ListAdapter<PantryItem, PantryAdapter.PantryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(PantryItem item);
    }

    public PantryAdapter(OnItemClickListener clickListener, OnItemDeleteListener deleteListener) {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<PantryItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<PantryItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull PantryItem oldItem, @NonNull PantryItem newItem) {
            return false; // TODO: Implement ID comparison
        }

        @Override
        public boolean areContentsTheSame(@NonNull PantryItem oldItem, @NonNull PantryItem newItem) {
            return false; // TODO: Implement content comparison
        }
    };

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null; // TODO: Inflate item_pantry.xml
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        // TODO: Bind item data to ViewHolder
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}