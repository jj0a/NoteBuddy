package com.example.notebuddy;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Note1Fragment extends Fragment {

    //Initializations
    FloatingActionButton floatingId;                        // A floating action button, which will used for adding a new note.
    RecyclerView recyclerView;                              // A RecyclerView to display our list of notes
    ArrayList<NoteModel> arrayList = new ArrayList<>();     // Declares a variable named arrayList of type ArrayList that holds objects of type NoteModel.
    ArrayList<NoteModel> filteredList = new ArrayList<>();  // Stores filtered notes based on the search query.
    SearchView searchView;                                  // A search bar to allow us to search or filter notes
    NoteAdapter adapter;                                    // A NoteAdapter to bind note data to the RecyclerView
    NoteHelper noteHelper;                                  // A NoteHelper instance for our database operations

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note1, container, false);  //Ginagamit ito upang i-convert (o i-inflate) ang XML layout file (sa mga actual na View objects.


        //Declaring the Id name to each of the initialized view
        floatingId = view.findViewById(R.id.floatingId);
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);

        noteHelper = new NoteHelper(getActivity());                            // magagamit mo ang NoteHelper sa fragment dahil sa getActivity()

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));  //Configures the RecyclerView to use a vertical linear layout.

        // Fetch data from the database and load it into the arrayList
        loadNotesFromDatabase();

        // Set initial filtered list to the full notes list
        filteredList.addAll(arrayList);

        // Initialize and set the adapter
        adapter = new NoteAdapter(getActivity(), filteredList);
        recyclerView.setAdapter(adapter);

        // Floating action button click to add a new note
        floatingId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the AddNotesFragment using FragmentTransaction
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new NoteFragment())  // Replace the fragment
                        .addToBackStack(null)  // Optional: Adds this transaction to the back stack
                        .commit();  // Commit the transaction
            }
        });

        // Set up the search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the notes based on the search query
                filterNotes(newText);
                return true;
            }
        });

        return view;
    }

    // Fetch notes from the database
    private void loadNotesFromDatabase() {
        Cursor cursor = null;
        try {
            cursor = noteHelper.showData(); // Retrieve notes from the database
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Add notes to arrayList
                    NoteModel note = new NoteModel(cursor.getString(1), cursor.getString(2), cursor.getInt(0));
                    arrayList.add(note);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close(); // Always close the cursor to prevent memory leaks
            }
        }
    }

    // Filter the notes based on the search query
    private void filterNotes(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(arrayList); // Show all notes if query is empty
        } else {
            // Filter the notes based on title or description containing the query
            for (NoteModel note : arrayList) {
                if (note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        note.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(note);
                }
            }
        }
        // Notify the adapter about the data change
        adapter.notifyDataSetChanged();
    }
}
