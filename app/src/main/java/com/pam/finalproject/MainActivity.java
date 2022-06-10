package com.pam.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView tvTitle;
    private FrameLayout frameLayout;
    private BottomNavigationView navBottom;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTitle = findViewById(R.id.tv_title);
        frameLayout = findViewById(R.id.frame_layout);
        navBottom = findViewById(R.id.nav_bottom);

        mAuth = FirebaseAuth.getInstance();

        tvTitle.setText("Beranda");
        Fragment fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, fragment.getClass().getSimpleName()).commit();

        navBottom.setOnItemSelectedListener(onItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    public void goToProfil(){
        tvTitle.setText("Profil");
        ProfilFragment fragment = new ProfilFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, fragment.getClass().getSimpleName()).commit();
        navBottom.getMenu().findItem(R.id.nav_profil).setChecked(true);
    }

    private final NavigationBarView.OnItemSelectedListener onItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.nav_beranda:
                    tvTitle.setText("Beranda");
                    fragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, fragment.getClass().getSimpleName()).commit();
                    return true;
                case R.id.nav_darurat:
                    tvTitle.setText("Butuh Bantuanmu!");
                    fragment = new DaruratFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, fragment.getClass().getSimpleName()).commit();
                    return true;
                case R.id.nav_pengaduan:
                    tvTitle.setText("Daftar Pengaduan");
                    fragment = new PengaduanFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, fragment.getClass().getSimpleName()).commit();
                    return true;
                case R.id.nav_profil:
                    tvTitle.setText("Profil");
                    fragment = new ProfilFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, fragment.getClass().getSimpleName()).commit();
                    return true;
            }
            return false;
        }
    };
}