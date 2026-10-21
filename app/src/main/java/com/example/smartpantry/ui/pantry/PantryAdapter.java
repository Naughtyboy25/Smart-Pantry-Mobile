package com.example.smartpantry.ui.pantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.data.entity.PantryItem;
import com.example.smartpantry.databinding.ItemPantryBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * RecyclerView Adapter for Pantry List screen.
 * Uses ListAdapter with DiffUtil for efficient updates.
 * Binds pantry item data to item_pantry.xml layout via ViewBinding.
 */
public class PantryAdapter extends ListAdapter<PantryItem, PantryAdapter.PantryViewHolder> {

    private final OnItemClickListener clickListener;
    private final OnItemDeleteListener deleteListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(PantryItem item);
    }

    public PantryAdapter(OnItemClickListener clickListener, OnItemDeleteListener deleteListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    private static final DiffUtil.ItemCallback<PantryItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<PantryItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull PantryItem oldItem, @NonNull PantryItem newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull PantryItem oldItem, @NonNull PantryItem newItem) {
            return oldItem.ingredientName.equals(newItem.ingredientName)
                    && oldItem.quantity == newItem.quantity
                    && oldItem.unit.equals(newItem.unit)
                    && ((oldItem.expiryDate == null && newItem.expiryDate == null)
                        || (oldItem.expiryDate != null && oldItem.expiryDate.equals(newItem.expiryDate)));
        }
    };

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPantryBinding binding = ItemPantryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PantryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class PantryViewHolder extends RecyclerView.ViewHolder {
        private final ItemPantryBinding binding;

        PantryViewHolder(@NonNull ItemPantryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onItemClick(getItem(pos));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && deleteListener != null) {
                    deleteListener.onItemDelete(getItem(pos));
                    return true;
                }
                return false;
            });
        }

        void bind(PantryItem item) {
            binding.tvItemName.setText(item.ingredientName);
            binding.tvQuantityUnit.setText(String.format(Locale.getDefault(), "%.1f %s", item.quantity, item.unit));

            if (item.expiryDate != null) {
                binding.tvExpiry.setText("Expires: " + dateFormat.format(new Date(item.expiryDate)));
                binding.tvExpiry.setVisibility(View.VISIBLE);
            } else {
                binding.tvExpiry.setVisibility(View.GONE);
            }
        }
    }
}