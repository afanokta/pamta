package com.pam.finalproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddPengaduanActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivBack;
    private EditText etNama, etNoHP, etAlamat, etNamaSaksi, etNoHPSaksi, etAlamatSaksi,
            etKronologiKejadian;
    private String nama, noHP, alamat, namaSaksi, noHPSaksi, alamatSaksi, kronologiKejadian,
            kategoriKejahatan, urlBukti, tanggal, jam;
    private Spinner spKategoriKejahatan;
    private TextView btnBuktiKejahatan;
    private AppCompatButton btnSubmit;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference pengaduanReference;
    private FirebaseStorage storage;
    private StorageReference buktiKejahatanReference;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private Uri filePath;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pengaduan);

        ivBack = findViewById(R.id.iv_back);
        etNama = findViewById(R.id.et_nama);
        etNoHP = findViewById(R.id.et_noHP);
        etAlamat = findViewById(R.id.et_alamat);
        etNamaSaksi = findViewById(R.id.et_nama_saksi);
        etNoHPSaksi = findViewById(R.id.et_noHP_saksi);
        etAlamatSaksi = findViewById(R.id.et_alamat_saksi);
        etKronologiKejadian = findViewById(R.id.et_kronologi_kejadian);
        spKategoriKejahatan = findViewById(R.id.sp_kategori_kejahatan);
        btnBuktiKejahatan = findViewById(R.id.btn_bukti_kejahatan);
        btnSubmit = findViewById(R.id.btn_submit);
        progressBar = findViewById(R.id.progressBar);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(R.array.kategori_kejahatan));
        spKategoriKejahatan.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        pengaduanReference = database.getReference().child("Pengaduan").child(firebaseUser.getUid());

        storage = FirebaseStorage.getInstance();
        buktiKejahatanReference = storage.getReference().child("Bukti Pengaduan/" + firebaseUser.getUid() + "/" + UUID.randomUUID());

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            filePath = data.getData();
                            String displayName = null;
                            String uriString = filePath.toString();
                            File myFile = new File(uriString);
                            if (uriString.startsWith("content://")) {
                                Cursor cursor = null;
                                try {
                                    cursor = getContentResolver().query(filePath, null, null, null, null);
                                    if (cursor != null && cursor.moveToFirst()) {
                                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    }
                                } finally {
                                    cursor.close();
                                }
                            } else if (uriString.startsWith("file://")) {
                                displayName = myFile.getName();
                            }
                            Toast.makeText(AddPengaduanActivity.this, "Berkas " +
                                            displayName + " berhasil dipilih",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        ivBack.setOnClickListener(this);
        btnBuktiKejahatan.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_bukti_kejahatan:
                selectImage();
                break;
            case R.id.btn_submit:
                addPengaduan();
                break;
        }
    }

    private void selectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageIntent.setType("image/*");
        someActivityResultLauncher.launch(imageIntent);
    }

    private void addPengaduan() {
        if (!validateForm()) {
            return;
        }

        showLoading(true);

        nama = etNama.getText().toString();
        noHP = etNoHP.getText().toString();
        alamat = etAlamat.getText().toString();
        namaSaksi = etNamaSaksi.getText().toString();
        noHPSaksi = etNoHPSaksi.getText().toString();
        alamatSaksi = etAlamatSaksi.getText().toString();
        kronologiKejadian = etKronologiKejadian.getText().toString();

        kategoriKejahatan = spKategoriKejahatan.getSelectedItem().toString();

        Date date = new Date();
        tanggal = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date);
        jam = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);

        buktiKejahatanReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                buktiKejahatanReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        urlBukti = uri.toString();
                        Pengaduan pengaduan = new Pengaduan(nama, noHP, alamat, namaSaksi, noHPSaksi,
                                alamatSaksi, kategoriKejahatan, kronologiKejadian, urlBukti, tanggal, jam);
                        pengaduanReference.push().setValue(pengaduan).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                showLoading(false);
                                Toast.makeText(AddPengaduanActivity.this, "Pengaduan Berhasil Diunggah", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(etNama.getText().toString())) {
            etNama.setError("Required");
            result = false;
        } else {
            etNama.setError(null);
        }
        if (TextUtils.isEmpty(etNoHP.getText().toString())) {
            etNoHP.setError("Required");
            result = false;
        } else {
            etNoHP.setError(null);
        }
        if (TextUtils.isEmpty(etAlamat.getText().toString())) {
            etAlamat.setError("Required");
            result = false;
        } else {
            etAlamat.setError(null);
        }
        if (TextUtils.isEmpty(etNamaSaksi.getText().toString())) {
            etNamaSaksi.setError("Required");
            result = false;
        } else {
            etNamaSaksi.setError(null);
        }
        if (TextUtils.isEmpty(etNoHPSaksi.getText().toString())) {
            etNoHPSaksi.setError("Required");
            result = false;
        } else {
            etNoHPSaksi.setError(null);
        }
        if (TextUtils.isEmpty(etAlamatSaksi.getText().toString())) {
            etAlamatSaksi.setError("Required");
            result = false;
        } else {
            etAlamatSaksi.setError(null);
        }
        if (TextUtils.isEmpty(etKronologiKejadian.getText().toString())) {
            etKronologiKejadian.setError("Required");
            result = false;
        } else {
            etKronologiKejadian.setError(null);
        }
        if (filePath == null) {
            result = false;
            Toast.makeText(this, "Masukkan gambar produk!", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private void showLoading(Boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

}