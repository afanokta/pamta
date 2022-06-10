package com.pam.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ProfilFragment";
    private CircleImageView profileImage;
    private AppCompatButton btnEditFoto, btnUpdate;
    private EditText etFirstName, etLastName, etNoHP, etGender, etBirthday;
    private CountryCodePicker ccp;
    private View view;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private Uri filePath;
    private String firstName, lastName, noHP, jenisKelamin, tanggalLahir,
            newFirstName, newLastName, newNoHP, newJenisKelamin, newTanggalLahir;
    private ProgressBar progressBar;

    public ProfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profil, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        btnEditFoto = view.findViewById(R.id.btn_edit_foto);
        btnUpdate = view.findViewById(R.id.btn_update);
        etFirstName = view.findViewById(R.id.et_first_name);
        etLastName = view.findViewById(R.id.et_last_name);
        etNoHP = view.findViewById(R.id.et_noHP);
        etGender = view.findViewById(R.id.et_gender);
        etBirthday = view.findViewById(R.id.et_birthday);
        ccp = view.findViewById(R.id.ccp);
        progressBar = view.findViewById(R.id.progressBar);

        ccp.registerCarrierNumberEditText(etNoHP);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        userReference = database.getReference().child("User").child(firebaseUser.getUid());

        showLoading(true);

        if (firebaseUser.getPhotoUrl() != null) {
            Uri photoUrl = firebaseUser.getPhotoUrl();
            if (!photoUrl.equals("") | !photoUrl.equals(null)) {
                Glide.with(this)
                        .load(photoUrl)
                        .into(profileImage);
            }
        }

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null){
                    firstName = user.getFirstName();
                    lastName = user.getLastName();
                    noHP = user.getNoTelp();
                    jenisKelamin = user.getJenisKelamin();
                    tanggalLahir = user.getTanggalLahir();

                    etFirstName.setText(firstName);
                    etLastName.setText(lastName);
                    ccp.setFullNumber(noHP);
                    etGender.setText(jenisKelamin);
                    etBirthday.setText(tanggalLahir);

                    showLoading(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
            }
        });

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            filePath = data.getData();

                            showLoading(true);

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(filePath)
                                    .build();

                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showLoading(false);
                                                Log.d(TAG, "User profile updated.");
                                                Glide.with(getActivity())
                                                        .load(filePath)
                                                        .into(profileImage);
                                            }
                                        }
                                    });
                        }
                    }
                });

        showLoading(false);

        btnEditFoto.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_edit_foto:
                getImage();
                break;
            case R.id.btn_update:
                updateData();
                break;
        }
    }

    private void updateData() {
        if (!validateForm()){
            return;
        }

        showLoading(true);

        newFirstName = etFirstName.getText().toString();
        newLastName = etLastName.getText().toString();
        newNoHP = ccp.getFullNumberWithPlus();
        newJenisKelamin = etGender.getText().toString();
        newTanggalLahir = etBirthday.getText().toString();

        User user = new User(newFirstName, newLastName, newNoHP, newJenisKelamin, newTanggalLahir);
        userReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showLoading(false);
                Toast.makeText(getActivity(), "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(etFirstName.getText().toString())) {
            etFirstName.setError("Required");
            result = false;
        } else {
            etFirstName.setError(null);
        }
        if (TextUtils.isEmpty(etNoHP.getText().toString())) {
            etNoHP.setError("Required");
            result = false;
        } else {
            etNoHP.setError(null);
        }
        return result;
    }

    private void getImage() {
        Intent intent_image = new Intent(Intent.ACTION_GET_CONTENT);
        intent_image.setType("image/*");
        someActivityResultLauncher.launch(intent_image);
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