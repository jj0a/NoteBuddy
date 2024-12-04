package com.example.notebuddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private Context context;
    private List<String> folders;
    private NoteHelper dbHelper;
    private OnFolderClickListener listener;

    // Constructor
    public FolderAdapter(Context context, List<String> folders, NoteHelper dbHelper, OnFolderClickListener listener) {
        this.context = context;
        this.folders = folders;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.folder_item, parent, false); // Inflate folder item layout
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        String folderName = folders.get(position); // Get folder name for the position
        holder.textView.setText(folderName);

        //Once the item is click on the folder it will navigate to other FolderNoteActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FolderNoteActivity.class);
            intent.putExtra("folderName", folderName); // Corrected
            context.startActivity(intent);
        });

        //Delete the folder code here
        Cursor cursor = dbHelper.getReadableDatabase()
                .rawQuery("SELECT id FROM my_folder WHERE folderName = ?", new String[]{folderName});

        if (cursor.moveToFirst()) {
            String folderId = cursor.getString(cursor.getColumnIndex("id"));

            holder.cardView.setOnLongClickListener(view -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Folder")
                        .setMessage("Are you sure you want to delete this folder?")
                        .setIcon(R.drawable.baseline_folder_delete_24)
                        .setPositiveButton("OK", (dialogInterface, i) -> {

                            // Delete folder using its ID
                            dbHelper.deleteFolder(folderId);

                            // Update UI
                            folders.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, folders.size());
                        })
                        .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setIcon(R.drawable.baseline_add_alert_24)
                        .show();

                return true;
            });
        }

        cursor.close();

    }

    @Override
    public int getItemCount() {
        return folders.size(); // Return total number of folders
    }

    // ViewHolder class for folder items
    static class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CardView cardView;


        FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.folderTitle); // Match the TextView ID in folder_item.xml
            cardView = itemView.findViewById(R.id.cardView);

        }
    }

    // Listener interface for folder clicks
    public interface OnFolderClickListener {
        void onFolderClick(int position);
    }
}
