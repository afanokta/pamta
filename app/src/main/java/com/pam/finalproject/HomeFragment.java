package com.pam.finalproject;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements View.OnClickListener, LocationListener {

    private static final String TAG = "HomeFragment";
    private View view;
    private CircleImageView profileImage;
    private TextView tvName, tvUsername;
    private ImageView ivEdit, ivSOS;
    private User user;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference userReference, SOSReference;
    private AlertDialog dialog;
    private LocationManager locationManager;
    private String firstName, lastName, name = "", fullAddress, noTelp;
    private Double latitude, longitude;
    private ProgressBar progressBar;
    private ActivityResultLauncher<String> mPermissionResult;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        profileImage = view.findViewById(R.id.profile_image);
        tvName = view.findViewById(R.id.tv_name);
        tvUsername = view.findViewById(R.id.tv_username);
        ivEdit = view.findViewById(R.id.iv_edit);
        ivSOS = view.findViewById(R.id.iv_sos);
        progressBar = view.findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        mPermissionResult = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            SOS();
                        } else {
                            Toast.makeText(getActivity(), "No permission.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        showLoading(true);

        database = FirebaseDatabase.getInstance();
        if (firebaseUser != null){
            userReference = database.getReference().child("User").child(firebaseUser.getUid());

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    if (user == null) {
                        dialog.show();
                    } else {
                        if (user.getFirstName() == null || user.getNoTelp() == null) {
                            dialog.show();
                        }
                        if (user.getFirstName() != null) {
                            firstName = user.getFirstName();
                            name += firstName;
                        }
                        if (user.getLastName() != null) {
                            lastName = user.getLastName();
                            name += " " + lastName;
                        }
                        tvName.setText(name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, error.getMessage());
                }
            });

            tvUsername.setText("@" + firebaseUser.getDisplayName());

            if (firebaseUser.getPhotoUrl() != null) {
                Uri photoUrl = firebaseUser.getPhotoUrl();
                if (!photoUrl.equals("") | !photoUrl.equals(null)) {
                    Glide.with(this)
                            .load(photoUrl)
                            .into(profileImage);
                }
            }
        }
        SOSReference = database.getReference().child("SOS");

        makeAlertDialog();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        showLoading(false);

        ivEdit.setOnClickListener(this);
        ivSOS.setOnClickListener(this);

        return view;
    }

    private void makeAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Peringatan!");

        builder.setMessage("Demi kenyamanan bersama, dimohon untuk melengkapi nama dan nomor telepon " +
                "terlebih dahulu sebelum menggunakan aplikasi.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ((MainActivity) getActivity()).goToProfil();
            }
        });

        dialog = builder.create();
        dialog.setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_edit:
                ((MainActivity) getActivity()).goToProfil();
                break;
            case R.id.iv_sos:
                SOS();
                break;
        }
    }

    private void SOS(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mPermissionResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            return;
        }
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:110"));
        startActivity(dialIntent);
        showLoading(true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Geocoder gCoder = new Geocoder(getActivity());
        List<Address> addresses = null;
        try {
            addresses = gCoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();

            fullAddress = address + " " + city + " " + country;
        }
        String noTelp = user.getNoTelp();
        SOS sos = new SOS(name, fullAddress, noTelp);
        SOSReference.push().setValue(sos).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                locationManager.removeUpdates(HomeFragment.this);
                showLoading(false);
                Toast.makeText(getActivity(), "Permintaan Bantuan telah dikirimkan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(Boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            progressBar.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}