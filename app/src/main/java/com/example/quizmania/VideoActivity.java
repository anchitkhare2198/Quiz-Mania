package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class VideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 108;

    private Button chooseVideo, submitVideo;
    private EditText videoName;
    private VideoView videoView;
    private Uri videoUri;
    MediaController mediaController;
    private MediaController mediaControls;
    private int position = 0;
    private StorageReference storageReference;
    private DatabaseReference myRef;
    private Dialog loadingDialog;
    private TextView loadingText;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoName = findViewById(R.id.videoName);
        videoView = findViewById(R.id.videoView);
        chooseVideo = findViewById(R.id.videoChoose);
        submitVideo = findViewById(R.id.videoSubmit);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        loadingText = loadingDialog.findViewById(R.id.loading_text);

        mediaController = new MediaController(this);

        getSupportActionBar().setTitle("Upload Video");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference("Videos");
        myRef = FirebaseDatabase.getInstance().getReference("Videos");

        loadingDialog.show();
        myRef.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //loadingDialog.show();
                    retrieveVideo();
                }else {
                    loadingDialog.dismiss();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
                Toast.makeText(VideoActivity.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
            }
        });

        //retrieveVideo();

        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.start();

        chooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                chooseVideoMethod();
                videoName.setEnabled(true);
            }
        });

        submitVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(videoName.getText().toString().isEmpty()){
                    videoName.setError("Required");
                    return;
                }
                uploadVideo();
            }
        });
    }

    private void retrieveVideo(){
        loadingDialog.show();
        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Videos");
        final FirebaseAuth firebaseAuth2 = FirebaseAuth.getInstance();

        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                        String id = dataSnapshot1.getKey().toString();
                        if (id.equals(firebaseAuth2.getUid())){
                            String video = dataSnapshot1.child("videoName").getValue().toString();
                            String url = dataSnapshot1.child("videoUrl").getValue().toString();
                            videoName.setText(video);
                            videoName.setEnabled(false);
                            if (mediaControls == null)
                            {
                                mediaControls = new MediaController(VideoActivity.this);
                            }
                            try
                            {
                                // set the media controller in the VideoView
                                videoView.setMediaController(mediaControls);

                                // set the uri of the video to be played
                                videoView.setVideoURI(Uri.parse(url));
                                loadingDialog.dismiss();

                            } catch (Exception e)
                            {
                                loadingDialog.dismiss();
                                Log.e("Error", e.getMessage());
                                e.printStackTrace();
                            }
                            videoView.requestFocus();
                            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                            {
                                public void onPrepared(MediaPlayer mediaPlayer)
                                {
                                    // if we have a position on savedInstanceState, the video
                                    // playback should start from here
                                    videoView.seekTo(position);
                                    if (position == 0)
                                    {
                                        videoView.start();
                                    } else
                                    {
                                        // if we come from a resumed activity, video playback will
                                        // be paused
                                        videoView.pause();
                                    }
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
                Toast.makeText(VideoActivity.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void retrieveVideo(){
//
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Users");
//        loadingDialog.show();
//
//        myRef2.child(firebaseAuth.getUid()).child("User_Details").child("Video_Details").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    String uri = null;
//                    String video = null;
//                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
//                        for (DataSnapshot dataSnapshot2 :dataSnapshot1.getChildren()){
//                            uri = dataSnapshot2.child("videoUrl").getValue().toString();
//                            video = dataSnapshot2.child("videoName").getValue().toString();
//                            //videoView.setVideoURI(Uri.parse(uri));
//                            //videoName.setText(video);
//                            System.out.println(uri);
//                            System.out.println(video);
//
////                            for (DataSnapshot dataSnapshot3 :dataSnapshot2.getChildren()){
////                                String uri = dataSnapshot3.child("videoUrl").getValue().toString();
////                                String video = dataSnapshot3.child("videoName").getValue().toString();
////                                System.out.println(uri);
////                                System.out.println(video);
////                                //videoView.setVideoURI(Uri.parse(uri));
////                                videoName.setText(video);
////                            }
//                        }
//                    }
//
//                    loadingDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                loadingDialog.dismiss();
//                Toast.makeText(VideoActivity.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }

    private void uploadVideo() {

        if (videoUri != null){

            loadingText.setText("Uploading..");
            loadingDialog.show();

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    final DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Users");
                    final DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("Videos");

                    final StorageReference reference = storageReference.child(firebaseAuth.getUid()+"."+getFileExt(videoUri));


                    if (videoName.getText().toString().isEmpty()){
                        loadingDialog.dismiss();
                        videoName.setError("Required");
                        return;
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        loadingDialog.dismiss();
                                        finish();
                                        Toast.makeText(VideoActivity.this,"Video Successfully Uploaded!!",Toast.LENGTH_SHORT).show();

                                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uri.isComplete());
                                        Uri url1 = uri.getResult();
                                        final String url = String.valueOf(url1);

                                        Member member = new Member(videoName.getText().toString().trim(),
                                                url);
                                        myRef3.child(firebaseAuth.getUid()).setValue(member);

                                        myRef2.child(firebaseAuth.getUid()).child("User_Details").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()){
                                                    String newcate = dataSnapshot.child("fullname").getValue().toString();
                                                    myRef3.child(firebaseAuth.getUid()).child("fullName").setValue(newcate);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Toast.makeText(VideoActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingDialog.dismiss();
                                        Toast.makeText(VideoActivity.this,"Video Not Uploaded!!",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        return;

//                        reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                loadingDialog.dismiss();
//                                finish();
//                                Toast.makeText(VideoActivity.this,"Video Successfully Uploaded!!",Toast.LENGTH_SHORT).show();
//
//                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
//                                while (!uri.isComplete());
//                                Uri url1 = uri.getResult();
//                                final String url = String.valueOf(url1);
//
//                                Member member = new Member(videoName.getText().toString().trim(),
//                                        url);
//                                myRef3.child(firebaseAuth.getUid()).setValue(member);
//
//                                myRef2.child(firebaseAuth.getUid()).child("User_Details").addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        if (dataSnapshot.exists()){
//                                            String newcate = dataSnapshot.child("fullname").getValue().toString();
//                                            myRef3.child(firebaseAuth.getUid()).child("fullName").setValue(newcate);
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        Toast.makeText(VideoActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                loadingDialog.dismiss();
//                                Toast.makeText(VideoActivity.this,"Video Not Uploaded!!",Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    }

                }
            });
        }
        else{
            loadingDialog.dismiss();
            Toast.makeText(VideoActivity.this,"No video Selected!!",Toast.LENGTH_SHORT).show();
            videoName.setEnabled(false);
        }
    }

    private void chooseVideoMethod() {
        Intent i = new Intent();
        i.setType("video/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            videoUri = data.getData();

            videoView.setVideoURI(videoUri);
        }
    }

    private String getFileExt(Uri videoUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
