package com.example.notebuddy;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<NoteModel> arrayList = new ArrayList<>();
    NoteHelper noteHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        recyclerView = view.findViewById(R.id.recycler_favorites);
        noteHelper = new NoteHelper(getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch and display favorite notes
        loadFavoriteNotes();

        return view;
    }

    private void loadFavoriteNotes() {
        Cursor cursor = null;
        try {
            cursor = noteHelper.getFavoriteNotes();

            // Clear the arrayList before populating it
            arrayList.clear();

            // Check if cursor has data and populate arrayList
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    arrayList.add(new NoteModel(
                            cursor.getString(1), // title
                            cursor.getString(2), // description
                            cursor.getInt(0)     // id
                    ));
                } while (cursor.moveToNext());
            }

            FavoriteAdapter adapter = new FavoriteAdapter(getActivity(), arrayList, noteHelper);
            recyclerView.setAdapter(adapter);

            // Notify adapter to refresh the view
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed to load favorites", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            // Always close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}



