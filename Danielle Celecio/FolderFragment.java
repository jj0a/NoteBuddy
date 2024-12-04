package com.example.notebuddy;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FolderFragment extends Fragment {

    private NoteHelper dbHelper;
    private RecyclerView recyclerView;
    private List<String> folderNames = new ArrayList<>();
    private FolderAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        dbHelper = new NoteHelper(requireContext()); // Initialize DB Helper

        recyclerView = view.findViewById(R.id.recycler_folder);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize the adapter
        adapter = new FolderAdapter(requireContext(), folderNames, dbHelper, position -> {
            // Handle folder click event
            Toast.makeText(requireContext(), "Clicked on: " + folderNames.get(position), Toast.LENGTH_SHORT).show();

            String selectedFolderName = folderNames.get(position);

            // Create an Intent to start FolderNoteActivity
            Intent intent = new Intent(requireContext(), FolderNoteActivity.class);
            intent.putExtra("folderName", selectedFolderName); // Pass the folder name
            startActivity(intent); // Start the activity
        });
        recyclerView.setAdapter(adapter);

        loadFolders(); // Load folders into the list



        // FloatingActionButton click listener
        FloatingActionButton fab = view.findViewById(R.id.floatingFolder);
        fab.setOnClickListener(v -> showAddFolderDialog());

        return view;
    }

    private void loadFolders() {
        folderNames.clear(); // Clear the list before loading
        Cursor cursor = dbHelper.getAllFolders(); // Fetch all folders
        if (cursor != null) {
            while (cursor.moveToNext()) {
                folderNames.add(cursor.getString(cursor.getColumnIndex("folderName"))); // Assuming "folderName" is the column name
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged(); // Notify adapter about data changes
    }

    /**
     * Show a dialog to add a new folder.
     */
    private void showAddFolderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Folder");
        builder.setIcon(R.drawable.baseline_create_new_folder_24); // Set the icon

        final EditText input = new EditText(requireContext());
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String folderName = input.getText().toString().trim();
            if (!folderName.isEmpty()) {
                if (dbHelper.addFolder(folderName)) { // Add folder to DB
                    folderNames.add(folderName); // Add folder to the list
                    adapter.notifyItemInserted(folderNames.size() - 1); // Notify adapter about insertion
                } else {
                    Toast.makeText(requireContext(), "Failed to add folder. Try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Folder name cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show(); // Show the dialog
    }
}
