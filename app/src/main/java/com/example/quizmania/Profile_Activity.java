package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Activity extends AppCompatActivity {

    private Uri image;
    private String downloadUrl;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myRef;

    private CircleImageView add_Image,display_image;
    private EditText fullname,username;
    private Button update,delete;
    List<UserProfile> list1;
    private Dialog loadingDialog;
    private TextView loadingText,ImpText;
    private long mLastClickTime = 0;

    String displayImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);

        firebaseAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("Users");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        loadingText = loadingDialog.findViewById(R.id.loading_text);

        add_Image = findViewById(R.id.profile_picture);
        fullname = findViewById(R.id.editName);
        username = findViewById(R.id.editUserName);
        update = findViewById(R.id.Update);
        delete = findViewById(R.id.Delete_account);

        ImpText = findViewById(R.id.ImpText);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog.show();
        myRef.child(firebaseAuth.getUid()).child("User_Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    fullname.setText(userProfile.getFullname());
                    username.setText(userProfile.getUsername());
                    String data = dataSnapshot.child("profile_url").getValue().toString();

                    if (data.equals("null") == true){
                        loadingDialog.dismiss();
                        //Glide.with(Profile_Activity.this).load(add_Image).into(add_Image);
                        //Glide.with(Profile_Activity.this).load(add_Image).into(add_Image);
                    }
                    else {
                        Glide.with(Profile_Activity.this).load(data).into(add_Image);
                        ImpText.setText("Tap above to change your profile picture");
                        loadingDialog.dismiss();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
            }
        });

        add_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery,103);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                AlertDialog.Builder dialog = new AlertDialog.Builder(Profile_Activity.this);
                dialog.setTitle("Are you sure ?");
                dialog.setMessage("Deleting account will erase all your data.");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadingDialog.show();

                        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference();

                        myRef2.child("Total_Scores").child(firebaseAuth.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Profile_Activity.this,"Total Score Database Deleted Successfully!!",Toast.LENGTH_SHORT).show();
                                }
//                                else {
//                                    Toast.makeText(Profile_Activity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                                }
                            }
                        });

                        myRef2.child("Subject_Scores").child(firebaseAuth.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Profile_Activity.this,"Subject Score Database Deleted Successfully!!",Toast.LENGTH_SHORT).show();
                                }
//                                else{
//                                    Toast.makeText(Profile_Activity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                                }
                            }
                        });
//
//
                        myRef.child(firebaseAuth.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Profile_Activity.this,"Database Deleted Successfully!!",Toast.LENGTH_SHORT).show();

                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                    storageReference.child("Profile_Pictures").child(firebaseAuth.getUid() + ".jpeg").delete();
                                    //Toast.makeText(Profile_Activity.this,"Storage Deleted Successfully!!",Toast.LENGTH_SHORT).show();


                                }
//                                else {
//                                    Toast.makeText(Profile_Activity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                                }
                            }
                        });

                        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    Toast.makeText(Profile_Activity.this,"Account Deleted Successfully!!",Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(Profile_Activity.this,Login_Acitivity.class);
                                    startActivity(i);
                                    loadingText.setText("Deleting User...");
                                    loadingDialog.dismiss();
                                    finish();
                                }
                                else{
                                    Toast.makeText(Profile_Activity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });


        add_Image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(Profile_Activity.this, R.style.Theme_AppCompat_Light_Dialog).setTitle("Delete Profile Picture")
                        .setMessage("Are you sure you want to delete this profile picture?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingText.setText("Deleting");
                                loadingDialog.show();
                                myRef.child(firebaseAuth.getUid()).child("User_Details").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child("profile_url").getValue() != "null"){

                                            myRef.child(firebaseAuth.getUid()).child("User_Details").child("profile_url").setValue("null");
                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                            storageReference.child("Profile_Pictures").child(firebaseAuth.getUid() + ".jpeg").delete();


                                        }
                                        finish();
                                        startActivity(getIntent());
                                        Toast.makeText(Profile_Activity.this,"Profile Picture Deleted",Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return false;

            }
        });



        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (image == null){
                    Toast.makeText(Profile_Activity.this,"Please Select your Image",Toast.LENGTH_SHORT).show();

                    return;
                }
                uploadData();



            }



        });


        //addImage();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 103){
            if (resultCode == RESULT_OK){
                image = data.getData();
                add_Image.setImageURI(image);
            }
        }

    }

    private void uploadData(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        //final StorageReference imageReference = storageReference.child("Profile_Pictures").child(image.getLastPathSegment());

        final StorageReference imageReference = storageReference.child("Profile_Pictures").child(firebaseAuth.getUid() + ".jpeg");

        UploadTask uploadTask = imageReference.putFile(image);

        loadingText.setText("Updating...");
        loadingDialog.show();

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadUrl =  task.getResult().toString();
                            Toast.makeText(Profile_Activity.this,"Profile Updated Successfully!!",Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                            uploadfullInfo();
                            //uploadCategoryName();
                        }
                        else{
                            loadingDialog.dismiss();
                            Toast.makeText(Profile_Activity.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(Profile_Activity.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadfullInfo(){

        firebaseAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("Users");

//        String name = fullname.getText().toString();
//        String userName = username.getText().toString();
//
//        final Map<String,Object> map = new HashMap<>();
//        map.put("fullname",name);
//        map.put("username",userName);
//        map.put("Profile_url",downloadUrl);
//
//        UserProfile userprofile = new UserProfile();
//        map.get(userprofile);
//        myRef.child(firebaseAuth.getUid()).setValue(map);

        myRef.child(firebaseAuth.getUid()).child("User_Details").child("profile_url").setValue(downloadUrl);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
