package com.example.notebuddy;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FolderNoteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FolderNoteAdapter adapter;
    private List<NoteModel> folderNotes = new ArrayList<>();
    private NoteHelper dbHelper;
    private String folderName;
    private TextView folderNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foldernote);

        folderNameTextView = findViewById(R.id.folderNameTextView); // Reference to the TextView

        // Initialize variables
        recyclerView = findViewById(R.id.folderSongRecyclerView);
        dbHelper = new NoteHelper(this);
        folderName = getIntent().getStringExtra("folderName"); // Get folder name from Intent

        // Set the folder name in the TextView
        if (folderName != null) {
            folderNameTextView.setText(folderName); // Set the folder name to TextView
        }

        int folderId = getFolderId();

        if (folderId != -1) {
            Log.d("FolderNoteActivity", "Using folder ID: " + folderId);
            loadNotes();

            // Pass the folderId to the adapter
            adapter = new FolderNoteAdapter(this, (ArrayList<NoteModel>) folderNotes, dbHelper, folderId);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        } else {
            Log.e("FolderNoteActivity", "Invalid folder ID. Cannot proceed.");
            Toast.makeText(this, "Folder not found.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        }

    }

    // Load the notes in the folder
    private void loadNotes() {
        int folderId = getFolderId();
        if (folderId != -1) {
            Log.d("FolderNoteActivity:", "Folder ID found: " + folderId);
            Cursor cursor = dbHelper.getNotesByFolderId(folderId);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String description = cursor.getString(cursor.getColumnIndex("description"));

                    // Create a NoteModel object and add it to the list
                    NoteModel note = new NoteModel(title, description, id);
                    folderNotes.add(note);

                    Log.d("FolderNoteActivity:", "Loaded note: " + title);
                } while (cursor.moveToNext());
                cursor.close();
            } else {
                Log.d("FolderNoteActivity:", "No notes found for folder ID: " + folderId);
                Toast.makeText(this, "No notes found for this folder.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("FolderNoteActivity:", "Folder not found for name: " + folderName);
            Toast.makeText(this, "Folder not found. Please select a valid folder.", Toast.LENGTH_SHORT).show();
        }
    }

    // Get the folder ID based on the folder name
    private int getFolderId() {
        Cursor cursor = dbHelper.getAllFolders();
        int id = -1;
        while (cursor.moveToNext()) {
            String dbFolderName = cursor.getString(cursor.getColumnIndex("folderName"));
            if (dbFolderName.equals(folderName)) {
                id = cursor.getInt(cursor.getColumnIndex("id"));
                Log.d("FolderNoteActivity", "Found folder ID: " + id);
                break;
            }
        }
        cursor.close();
        return id;
    }
}
