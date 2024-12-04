package com.example.notebuddy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class UpdateActivity extends AppCompatActivity {


    EditText updateTitle,updateDesc;
    Button updateBtn;
    ImageButton addNotesFolder;
    ImageButton addNotesFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);
        updateBtn = findViewById(R.id.updateBtn);
        updateTitle = findViewById(R.id.updateTitle);
        updateDesc = findViewById(R.id.updateDesc);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("description");
        int id = getIntent().getIntExtra("id",0);
        updateTitle.setText(title);
        updateDesc.setText(desc);

        String sId = String.valueOf(id);


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (updateTitle.length()>0&&updateDesc.length()>0){

                    NoteHelper noteHelper = new NoteHelper(UpdateActivity.this);
                    noteHelper.updateData(updateTitle.getText().toString(),updateDesc.getText().toString(),sId);
                    Toast.makeText(UpdateActivity.this,"The Data is Successfully Updated",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateActivity.this,MainActivity.class));

                }else {

                    Toast.makeText(UpdateActivity.this,"The Edit Box is empty",Toast.LENGTH_SHORT).show();

                }

            }
        });



        addNotesFolder = findViewById(R.id.addNotesToFolder);
        addNotesFolder.setOnClickListener(v -> {
            showAddToFolderDialog(id, title, desc);
        });


        addNotesFavorite = findViewById(R.id.addNotesToFavorites);
        addNotesFavorite.setOnClickListener(v -> {
            Log.d("UpdateActivity", "Favorites button clicked!");
            showAddToFavoritesDialog(id, title, desc);
        });


    }

    private void showAddToFolderDialog(int noteId,  String title, String description) {
        Log.d("UpdateActivity", "showAddToFavoritesDialog executed for noteId: " + noteId);

        NoteHelper dbHelper = new NoteHelper(this);




        // Get all folders as a list of strings
        List<String> folders = dbHelper.getAllFoldersAsList();

        if (folders.isEmpty()) {
            Toast.makeText(this, "No folders available. Please create a folder first.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Folder");
        builder.setIcon(R.drawable.baseline_note_add_24);

        // Convert the folder list to a string array for the AlertDialog
        String[] folderArray = folders.toArray(new String[0]);

        builder.setItems(folderArray, (dialog, which) -> {
            String selectedFolder = folderArray[which];  // Get the selected folder name

            // Call the helper method with updated parameters
            boolean success = dbHelper.addNotesToFolder(noteId, selectedFolder, title, description);

            if (success) {
                Toast.makeText(this, "Note added to " + selectedFolder, Toast.LENGTH_SHORT).show();
                Log.d("NoteHelper", "Note with ID " + noteId + ", " + title + " and " + description + " was successfully added to folder: " + selectedFolder);

            } else {
                Toast.makeText(this, "Notes " + title + " already exist in the folder.", Toast.LENGTH_SHORT).show();
                Log.d("NoteHelper", "Failed to add note with ID " + noteId + ", " + title + " and " + description + " to folder: " + selectedFolder);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void showAddToFavoritesDialog(int noteId, String title, String description) {
        NoteHelper dbHelper = new NoteHelper(this);

        if (dbHelper.isNoteFavorite(noteId)) {
            Toast.makeText(this, "Note is already in favorites", Toast.LENGTH_SHORT).show();
            Log.d("NoteHelper", "Note with ID " + noteId + " is already in favorites.");
            return; // Exit early to avoid showing the dialog
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add to Favorites?");
        builder.setIcon(R.drawable.baseline_favorite_24);
        builder.setMessage("Do you want to add the note \"" + title + "\" to your favorites?");

        // Positive Button for adding the note to favorites
        builder.setPositiveButton("Yes", (dialog, which) -> {
            boolean success = dbHelper.addNotesToFavorite(noteId);

            if (success) {
                Toast.makeText(this, "Note added to favorites", Toast.LENGTH_SHORT).show();
                Log.d("NoteHelper", "Note with ID " + noteId + ", title \"" + title + "\" and description \"" + description + "\" was successfully added to favorites.");
            } else {
                Toast.makeText(this, "Failed to add note to favorites.", Toast.LENGTH_SHORT).show();
                Log.d("NoteHelper", "Failed to add note with ID " + noteId + ", title \"" + title + "\" and description \"" + description + "\" to favorites.");
            }
        });

        // Negative Button for Cancel
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss(); // Dismiss the dialog
        });


        builder.show();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(UpdateActivity.this, MainActivity.class));
        super.onBackPressed();


    }
}

