package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText email;
    Button Send;
    private FirebaseAuth firebaseAuth;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.email);
        Send = findViewById(R.id.send);
        firebaseAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("Forgot Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                String userEmail = email.getText().toString().trim();

                if (userEmail.equals(""))
                {
                    Toast.makeText(ForgotPassword.this, " Please Enter a Registered Email ID ", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                Toast.makeText(ForgotPassword.this, " Password Reset Email Sent ", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent i = new Intent(ForgotPassword.this, Login_Acitivity.class);
                                startActivity(i);

                            }
                            else
                            {
                                Toast.makeText(ForgotPassword.this, " Email ID not Registered Please Register ", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });

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
}
