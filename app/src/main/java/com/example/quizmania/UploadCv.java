package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadCv extends AppCompatActivity {

    private Button choose_CV,Submit;
    private TextView notification;
    Uri pdfUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    DatabaseReference myRef2;
    ProgressDialog progressDialog;
    private Dialog loadingDialog;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_cv);

        getSupportActionBar().setTitle("Upload Resume");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        //storageReference = FirebaseStorage.getInstance().getReference();
        myRef2 = FirebaseDatabase.getInstance().getReference("Resumes");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        choose_CV = findViewById(R.id.Choose_CV);
        Submit = findViewById(R.id.submit_CV);
        notification = findViewById(R.id.noFile);

        loadingDialog.show();
        myRef2.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    retrievePdf();
                }else{
                    loadingDialog.dismiss();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
                Toast.makeText(UploadCv.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
            }
        });
//        loadingDialog.show();
//        myRef2.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    loadingDialog.show();
//                    retrievePdf();
//                }else{
//                    loadingDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                loadingDialog.dismiss();
//                Toast.makeText(UploadCv.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
//            }
//        });

        choose_CV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (ContextCompat.checkSelfPermission(UploadCv.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    selectPDF();
                }else{
                    ActivityCompat.requestPermissions(UploadCv.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},109);
                }
            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (pdfUri!=null){
                    upoadPDF(pdfUri);
                }else{
                    Toast.makeText(UploadCv.this,"Please select a file.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void retrievePdf(){
        DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("Resumes");
        final FirebaseAuth firebaseAuth2 = FirebaseAuth.getInstance();

        myRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                        String id = dataSnapshot1.getKey().toString();
                        if (id.equals(firebaseAuth2.getUid())){
                            final String url = dataSnapshot1.child("resume").getValue().toString();
                            final String name = dataSnapshot1.child("fullName").getValue().toString();
                            notification.setText("View Your Resume.");
                            loadingDialog.dismiss();
                            notification.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                                        return;
                                    }
                                    mLastClickTime = SystemClock.elapsedRealtime();
                                    Intent i = new Intent(UploadCv.this,PdfView.class);
                                    i.putExtra("Url",url);
                                    i.putExtra("Name",name);
                                    startActivity(i);
                                }
                            });
                        }
                    }
                }else {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
                Toast.makeText(UploadCv.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void upoadPDF(final Uri pdfUri){

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File...");
        progressDialog.setProgress(0);
        progressDialog.show();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                firebaseAuth = FirebaseAuth.getInstance();
                final StorageReference storageReference = storage.getReference("Resumes");
                //StorageReference reference = storageReference.child("Resumes"+firebaseAuth.getUid()+".pdf");

                myRef2 = FirebaseDatabase.getInstance().getReference("Resumes");
                final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myRef.child(firebaseAuth.getUid()).child("User_Details").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    String newcate = dataSnapshot.child("fullname").getValue().toString();
                                    myRef2.child(firebaseAuth.getUid()).child("fullName").setValue(newcate);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(UploadCv.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                        storageReference.child(firebaseAuth.getUid()).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uri.isComplete());
                                Uri url1 = uri.getResult();
                                String url = String.valueOf(url1);
                                myRef2.child(firebaseAuth.getUid()).child("resume").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Toast.makeText(UploadCv.this,"Resume uploaded successfully!!",Toast.LENGTH_SHORT).show();
                                            //retrievePdf();
                                            finish();
                                        }else{
                                            progressDialog.dismiss();
                                            Toast.makeText(UploadCv.this,"Failed to upload file!!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(UploadCv.this,"Failed to upload file!!",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                progressDialog.setProgress(currentProgress);
                            }
                        });
                    }
                });

//                myRef.child(firebaseAuth.getUid()).child("User_Details").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()){
//                            String newcate = dataSnapshot.child("fullname").getValue().toString();
//                            myRef2.child(firebaseAuth.getUid()).child("fullName").setValue(newcate);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(UploadCv.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
//                storageReference.child(firebaseAuth.getUid()).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
//                        while (!uri.isComplete());
//                        Uri url1 = uri.getResult();
//                        String url = String.valueOf(url1);
//                        myRef2.child(firebaseAuth.getUid()).child("resume").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()){
//                                    progressDialog.dismiss();
//                                    Toast.makeText(UploadCv.this,"Resume uploaded successfully!!",Toast.LENGTH_SHORT).show();
//                                    //retrievePdf();
//                                    finish();
//                                }else{
//                                    progressDialog.dismiss();
//                                    Toast.makeText(UploadCv.this,"Failed to upload file!!",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(UploadCv.this,"Failed to upload file!!",Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                        int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
//                        progressDialog.setProgress(currentProgress);
//                    }
//                });
            }
        });

//        firebaseAuth = FirebaseAuth.getInstance();
//        final StorageReference storageReference = storage.getReference("Resumes");
//        //StorageReference reference = storageReference.child("Resumes"+firebaseAuth.getUid()+".pdf");
//
//        myRef2 = FirebaseDatabase.getInstance().getReference("Resumes");
//        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
//
//        myRef.child(firebaseAuth.getUid()).child("User_Details").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    String newcate = dataSnapshot.child("fullname").getValue().toString();
//                    myRef2.child(firebaseAuth.getUid()).child("fullName").setValue(newcate);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(UploadCv.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//        storageReference.child(firebaseAuth.getUid()).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
//            while (!uri.isComplete());
//            Uri url1 = uri.getResult();
//                String url = String.valueOf(url1);
//                myRef2.child(firebaseAuth.getUid()).child("resume").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()){
//                            progressDialog.dismiss();
//                            Toast.makeText(UploadCv.this,"Resume uploaded successfully!!",Toast.LENGTH_SHORT).show();
//                            //retrievePdf();
//                            finish();
//                        }else{
//                            progressDialog.dismiss();
//                            Toast.makeText(UploadCv.this,"Failed to upload file!!",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                progressDialog.dismiss();
//                Toast.makeText(UploadCv.this,"Failed to upload file!!",Toast.LENGTH_SHORT).show();
//            }
//        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
//                progressDialog.setProgress(currentProgress);
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 109 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
            selectPDF();
        }else{
            Toast.makeText(this, "Please Grant Permission.", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPDF() {
        Intent i = new Intent();
        i.setType("application/pdf");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            notification.setText("File : "+ data.getData().getLastPathSegment());
        } else {
            Toast.makeText(this, "Please Select a File.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
