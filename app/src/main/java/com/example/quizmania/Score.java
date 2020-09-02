package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Score extends AppCompatActivity {


    private TextView scored,total, name, ct;
    private Button done,share;
    private FirebaseAuth firebaseAuth;
    private String setId, categoryName;
    private List<ScoreModel> list;
    File imagePath;
    private long mLastClickTime = 0;
    Handler handler;
    Runnable runnable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scored = findViewById(R.id.marks);
        total = findViewById(R.id.total);
        done = findViewById(R.id.done);
        share = findViewById(R.id.share);

        name = findViewById(R.id.name);
        ct = findViewById(R.id.categoryName);

        firebaseAuth = FirebaseAuth.getInstance();

        setId = getIntent().getStringExtra("setId");
        categoryName = getIntent().getStringExtra("category");

        //System.out.println(categoryName);

        scored.setText(String.valueOf(getIntent().getIntExtra("score",0)));
        total.setText(String.valueOf(getIntent().getIntExtra("total",0)));

        ct.setText(categoryName);

        getSupportActionBar().setTitle("Score");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sendUserData();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Score.this,PreviousScore.class);
                i.putExtra("categoryName",categoryName);
                finish();
            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (ActivityCompat.checkSelfPermission(Score.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    Bitmap bitmap = takeScreenshot();
                    saveBitmap(bitmap);
                    shareIt();

                }else{
                    ActivityCompat.requestPermissions(Score.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},105);
                }
//                Bitmap bitmap = takeScreenshot();
//                saveBitmap(bitmap);
//                shareIt();
            }
        });

//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("plain/text");
//                i.putExtra(Intent.EXTRA_SUBJECT,"My Score");
//                i.putExtra(Intent.EXTRA_TEXT, body);
//                startActivity(Intent.createChooser(i,"Share via"));
//            }
//        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 105) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Bitmap bitmap = takeScreenshot();
                saveBitmap(bitmap);
                shareIt();

            } else {
                Toast.makeText(this, "Please Grant Permission.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void sendUserData(){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = firebaseDatabase.getReference("Users");

        final String id = UUID.randomUUID().toString();
        final String a = scored.getText().toString().trim();
        final String b = total.getText().toString().trim();

        DateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        final String stringdate = dt.format(date);

        DateFormat dt2 = new SimpleDateFormat("HH:mm");
        long date2 = new Date().getTime();
        final String time = dt2.format(date2);

        final Map<String,Object> map = new HashMap<>();
        map.put("categoryName",categoryName);
        map.put("setId",setId);
        map.put("score",a);
        map.put("total",b);
        map.put("date",stringdate);
        map.put("time",time);

        myRef.child(firebaseAuth.getUid()).child("Scores").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("setId").getValue().toString().equals(setId)){
                        Toast.makeText(Score.this,"Your Score has already been noted!!",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    ScoreModel userprofile = new ScoreModel(categoryName,setId,a,b,id,stringdate,time);
                    map.get(userprofile);
                    myRef.child(firebaseAuth.getUid()).child("Scores").child(setId).setValue(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Score.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
            }
        });

        sendTotal();
        sendSubjectScore();

    }

    private void sendSubjectScore(){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = firebaseDatabase.getReference("Users");
        final DatabaseReference myRef2 = firebaseDatabase.getReference();

        myRef.child(firebaseAuth.getUid()).child("User_Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String newcate = dataSnapshot.child("fullname").getValue().toString();
                    myRef2.child("Subject_Scores").child(firebaseAuth.getUid()).child(categoryName).child("fullName").setValue(newcate);
                    name.setText(newcate);
                    myRef.child(firebaseAuth.getUid()).child("Scores").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){

                                int sum = 0;
                                int sum2 = 0;
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                    if (dataSnapshot1.child("categoryName").getValue().toString().equals(categoryName)){

                                        Map<String, Object> map4 = (Map<String, Object>) dataSnapshot1.getValue();
                                        Object score1 = map4.get("score");
                                        int Value = Integer.parseInt(String.valueOf(score1));
                                        sum = sum + Value;
                                        String newscore = String.valueOf(sum);
                                        myRef2.child("Subject_Scores").child(firebaseAuth.getUid()).child(categoryName).child("finalScore").setValue(newscore);

                                        Map<String, Object> map3 = (Map<String, Object>) dataSnapshot1.getValue();
                                        Object total1 = map3.get("total");
                                        int Value2 = Integer.parseInt(String.valueOf(total1));
                                        sum2 = sum2 + Value2;
                                        String newtotal = String.valueOf(sum2);
                                        myRef2.child("Subject_Scores").child(firebaseAuth.getUid()).child(categoryName).child("finalTotal").setValue(newtotal);
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Score.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Score.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        myRef.child(firebaseAuth.getUid()).child("Scores").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//
//                    int sum = 0;
//                    int sum2 = 0;
//                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
//                        if (dataSnapshot1.child("categoryName").getValue().toString().equals(categoryName)){
//
//                            Map<String, Object> map4 = (Map<String, Object>) dataSnapshot1.getValue();
//                            Object score1 = map4.get("score");
//                            int Value = Integer.parseInt(String.valueOf(score1));
//                            sum = sum + Value;
//                            String newscore = String.valueOf(sum);
//                            myRef2.child("Subject_Scores").child(firebaseAuth.getUid()).child(categoryName).child("finalScore").setValue(newscore);
//
//                            Map<String, Object> map3 = (Map<String, Object>) dataSnapshot1.getValue();
//                            Object total1 = map3.get("total");
//                            int Value2 = Integer.parseInt(String.valueOf(total1));
//                            sum2 = sum2 + Value2;
//                            String newtotal = String.valueOf(sum2);
//                            myRef2.child("Subject_Scores").child(firebaseAuth.getUid()).child(categoryName).child("finalTotal").setValue(newtotal);
//                        }
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(Score.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }


    private void sendTotal() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef2 = firebaseDatabase.getReference("Users");

        final DatabaseReference myRef3 = firebaseDatabase.getReference();

        myRef2.child(firebaseAuth.getUid()).child("User_Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String newcate = dataSnapshot.child("fullname").getValue().toString();
                    myRef3.child("Total_Scores").child(firebaseAuth.getUid()).child("fullName").setValue(newcate);

                    myRef2.child(firebaseAuth.getUid()).child("Scores").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                int sum = 0;
                                int sum2 = 0;
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    Map<String, Object> map4 = (Map<String, Object>) dataSnapshot1.getValue();
                                    Object score1 = map4.get("score");
                                    int Value = Integer.parseInt(String.valueOf(score1));
                                    sum = sum + Value;
                                    String newscore = String.valueOf(sum);

                                    myRef3.child("Total_Scores").child(firebaseAuth.getUid()).child("totalScore").setValue(newscore);


                                    Map<String, Object> map3 = (Map<String, Object>) dataSnapshot1.getValue();
                                    Object total1 = map3.get("total");
                                    int Value2 = Integer.parseInt(String.valueOf(total1));
                                    sum2 = sum2 + Value2;
                                    String newtotal = String.valueOf(sum2);

                                    myRef3.child("Total_Scores").child(firebaseAuth.getUid()).child("grandTotal").setValue(newtotal);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Score.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Score.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        myRef2.child(firebaseAuth.getUid()).child("Scores").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    int sum = 0;
//                    int sum2 = 0;
//                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//
//                        Map<String, Object> map4 = (Map<String, Object>) dataSnapshot1.getValue();
//                        Object score1 = map4.get("score");
//                        int Value = Integer.parseInt(String.valueOf(score1));
//                        sum = sum + Value;
//                        String newscore = String.valueOf(sum);
//
//                        myRef3.child("Total_Scores").child(firebaseAuth.getUid()).child("totalScore").setValue(newscore);
//
//
//                        Map<String, Object> map3 = (Map<String, Object>) dataSnapshot1.getValue();
//                        Object total1 = map3.get("total");
//                        int Value2 = Integer.parseInt(String.valueOf(total1));
//                        sum2 = sum2 + Value2;
//                        String newtotal = String.valueOf(sum2);
//
//                        myRef3.child("Total_Scores").child(firebaseAuth.getUid()).child("grandTotal").setValue(newtotal);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(Score.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    private void saveBitmap(Bitmap bitmap) {
        imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png"); ////File imagePath
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private void shareIt() {
        //Uri uri = Uri.fromFile(imagePath);

        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", imagePath);


        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        String shareBody = "My score's screen shot";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My score");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
