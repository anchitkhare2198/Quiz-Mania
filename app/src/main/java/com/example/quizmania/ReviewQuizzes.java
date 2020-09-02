package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ReviewQuizzes extends AppCompatActivity {

    EditText review;
    Button submit;
    FirebaseAuth firebaseAuth;
    DatabaseReference myRef2;
    Dialog loadingDialog;
    TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_quizzes);

        firebaseAuth = FirebaseAuth.getInstance();
        myRef2 = FirebaseDatabase.getInstance().getReference("Users");

        review = findViewById(R.id.review);
        submit = findViewById(R.id.submit_review);

        getSupportActionBar().setTitle("Review");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        loadingText = loadingDialog.findViewById(R.id.loading_text);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingText.setText("Submitting Response..");
                loadingDialog.show();
                if (review.getText().toString().isEmpty()){
                    review.setError("Required");
                    return;
                }else{
                    review.setError(null);
                }

                myRef2.child(firebaseAuth.getUid()).child("User_Details").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String newcate = dataSnapshot.child("fullname").getValue().toString();
                        DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference();

                        final Map<String,Object> map = new HashMap<>();
                        map.put("fullName",newcate);
                        map.put("review",review.getText().toString());

                        myRef3.child("User_Reviews").child(firebaseAuth.getUid()).setValue(map);
                        Toast.makeText(ReviewQuizzes.this,"Response Submitted Successfully!!",Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                        finish();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                        Toast.makeText(ReviewQuizzes.this,"Failed to submit Response",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
