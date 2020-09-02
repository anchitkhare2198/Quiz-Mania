package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText name, username, email, password, cnfpassword;
    Button register;
    private FirebaseAuth firebaseAuth;
    String s1, s2, s3, s4, s5;
    private Dialog loadingDialog;
    private TextView loadingText;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("Register");

        name = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        cnfpassword = findViewById(R.id.cnfpassword);
        register = findViewById(R.id.signin_btn);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        loadingText = loadingDialog.findViewById(R.id.loading_text);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                s1 = name.getText().toString().trim();
                s2 = username.getText().toString().trim();
                s3 = email.getText().toString().trim();
                s4 = password.getText().toString().trim();
                s5 = cnfpassword.getText().toString().trim();

                if (name.getText().toString().isEmpty()||username.getText().toString().isEmpty()
                     ||email.getText().toString().isEmpty()||password.getText().toString().isEmpty()|| cnfpassword.getText().toString().isEmpty()) {

                    name.setError("Required");
                    username.setError("Required");
                    email.setError("Required");
                    password.setError("Required");
                    cnfpassword.setError("Required");
                    return;

                } else {
                    if (s4.equals(s5)) {
                        loadingText.setText("Signing Up");
                        loadingDialog.show();
                        firebaseAuth.createUserWithEmailAndPassword(s3, s4).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                if (task.isSuccessful()) {
                                    sendEmailVerification();
                                }
                                else {
                                    Toast.makeText(Register.this, " Registration UnSuccessful ", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), " Passwords Don't Match ", Toast.LENGTH_LONG).show();
                    }

                }


            }
        });


    }

    private void sendEmailVerification() {
        final FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        sendUserData();
                        Toast.makeText(Register.this, " Registered Successfully, Verification Email sent ", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        loadingDialog.dismiss();
                        Intent i = new Intent(Register.this, Login_Acitivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(Register.this, " Mail Not Verified ", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference();
        UserProfile userProfile = new UserProfile(s1,s2,"null");
        myRef.child("Users").child(firebaseAuth.getUid()).child("User_Details").setValue(userProfile);
    }



}


