package com.pam.finalproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DaruratFragment extends Fragment {

    private RecyclerView rvDarurat;
    private View view;
    private FirebaseDatabase database;
    private DatabaseReference SOSReference;
    private SOSAdapter sosAdapter;

    public DaruratFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_darurat, container, false);

        rvDarurat = view.findViewById(R.id.rv_darurat);

        database = FirebaseDatabase.getInstance();
        SOSReference = database.getReference().child("SOS");

        rvDarurat.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<SOS> options
                = new FirebaseRecyclerOptions.Builder<SOS>()
                .setQuery(SOSReference, SOS.class)
                .build();

        sosAdapter = new SOSAdapter(options);

        rvDarurat.setAdapter(sosAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        sosAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        sosAdapter.stopListening();
    }

}