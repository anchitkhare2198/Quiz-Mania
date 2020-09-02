package com.example.quizmania;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    ProgressBar progressBar;

    //Button SignIn,SignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Quiz Mania");

        progressBar = findViewById(R.id.progress_bar);

        progressBar.isShown();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this,Login_Acitivity.class);
                startActivity(i);
                finish();
            }
        },SPLASH_TIME_OUT);

//        SignIn = findViewById(R.id.SignIn_button);
//        SignUp = findViewById(R.id.SingUp_button);
//
//        SignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this,Login_Acitivity.class);
//                startActivity(i);
//            }
//        });
//
//        SignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this,Register.class);
//                startActivity(i);
//            }
//        });
    }

}
