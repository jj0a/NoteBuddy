package com.example.notebuddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    Context context;
    ArrayList<NoteModel> arrayList;
    NoteHelper noteHelper;  // Add this field

    public FavoriteAdapter(Context context, ArrayList<NoteModel> arrayList, NoteHelper noteHelper) {
        this.context = context;
        this.arrayList = arrayList;
        this.noteHelper = noteHelper;  // Initialize the noteHelper
    }

    @NonNull
    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        holder.tvTitle.setText(arrayList.get(position).getTitle());
        holder.tvDesc.setText(arrayList.get(position).getDescription());

        // Handle unfavoriting (removing from favorites)
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Remove Favorite")
                    .setMessage("Are you sure you want to remove this note from favorite?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Remove from database
                        boolean removed = noteHelper.removeFromFavorites(arrayList.get(position).getId());

                        if (removed) {
                            // Update the list and notify the adapter
                            arrayList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, arrayList.size());
                            Toast.makeText(context, "Note removed from favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to remove favorite", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setIcon(R.drawable.baseline_add_alert_24)
                    .show();

            return true;
        });

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateActivity.class);
            intent.putExtra("title", arrayList.get(position).getTitle());
            intent.putExtra("description", arrayList.get(position).getDescription());
            intent.putExtra("id", arrayList.get(position).getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDesc;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
