package com.example.notebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class NoteFragment extends Fragment {

    EditText edTitle, edDesc;
    Button button;
    NoteHelper noteHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        edTitle = view.findViewById(R.id.edTitle);
        edDesc = view.findViewById(R.id.edDesc);
        button = view.findViewById(R.id.addButton);
        noteHelper = new NoteHelper(getActivity());  // Use getActivity() for context in fragment

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edTitle.length() > 0 && edDesc.length() > 0) {
                    noteHelper.insertData(edTitle.getText().toString(), edDesc.getText().toString());
                    Toast.makeText(getActivity(), "Successfully Added", Toast.LENGTH_SHORT).show();
                    edDesc.setText("");
                    edTitle.setText("");
                    startActivity(new Intent(getActivity(), MainActivity.class));  // Use getActivity()
                } else {
                    Toast.makeText(getActivity(), "The Edit Box is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;  // Return the inflated view
    }



    // Remove onBackPressed() in fragment, rely on Activity for handling
}





