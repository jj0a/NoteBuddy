package com.example.notebuddy;

import android.app.AlertDialog;
import android.content.Context;
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

public class FolderNoteAdapter extends RecyclerView.Adapter<FolderNoteAdapter.ViewHolder> {

    Context context;
    ArrayList<NoteModel> arrayList = new ArrayList<>();
    NoteHelper noteHelper;  // Add this field
    int folderId; // Add this field


    public FolderNoteAdapter(Context context, ArrayList<NoteModel> arrayList, NoteHelper noteHelper, int folderId) {
        this.context = context;
        this.arrayList = arrayList;
        this.noteHelper = noteHelper;
        this.folderId = folderId; // Set the folderId

    }

    @NonNull
    @Override
    public FolderNoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);


        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull FolderNoteAdapter.ViewHolder holder, int position) {


        holder.tvTitle.setText(arrayList.get(position).getTitle());
        holder.tvDesc.setText(arrayList.get(position).getDescription());


        // Handle removing notes from a specific folder
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Remove note from folder")
                    .setMessage("Are you sure you want to remove this note from this folder?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Remove from database
                        boolean removed = noteHelper.removeNoteFromFolder(arrayList.get(position).getId(), folderId);

                        if (removed) {
                            // Update the list and notify the adapter
                            arrayList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, arrayList.size());
                            Toast.makeText(context, "Note removed from folder", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to remove note from folder", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context, UpdateActivity.class);

                intent.putExtra("title", arrayList.get(position).getTitle());
                intent.putExtra("description", arrayList.get(position).getDescription());
                intent.putExtra("id", arrayList.get(position).getId());
                context.startActivity(intent);


            }
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