package com.pam.finalproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PengaduanFragment extends Fragment {

    private RecyclerView rvPengaduan;
    private FloatingActionButton fabAdd;
    private View view;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference pengaduanReference;
    private PengaduanAdapter pengaduanAdapter;

    public PengaduanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pengaduan, container, false);

        rvPengaduan = view.findViewById(R.id.rv_pengaduan);
        fabAdd = view.findViewById(R.id.fab_add);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        pengaduanReference = database.getReference().child("Pengaduan").child(firebaseUser.getUid());

        rvPengaduan.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<Pengaduan> options
                = new FirebaseRecyclerOptions.Builder<Pengaduan>()
                .setQuery(pengaduanReference, Pengaduan.class)
                .build();

        pengaduanAdapter = new PengaduanAdapter(options);

        rvPengaduan.setAdapter(pengaduanAdapter);

        fabAdd.setOnClickListener(view -> {
            Intent addPengaduanIntent = new Intent(getActivity(), AddPengaduanActivity.class);
            startActivity(addPengaduanIntent);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        pengaduanAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        pengaduanAdapter.stopListening();
    }
}