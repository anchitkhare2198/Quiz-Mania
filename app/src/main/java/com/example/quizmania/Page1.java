package com.example.quizmania;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Page1 extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Dialog loadingDialog;
    private TextView loadingText;
    private long mLastClickTime = 0;

    private Button quiz,bookmarks,marks,leaderboard, review_quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1);

        quiz = findViewById(R.id.takequiz);
        bookmarks = findViewById(R.id.bookmark);
        marks = findViewById(R.id.marks);
        leaderboard = findViewById(R.id.leaderboard);
        review_quiz = findViewById(R.id.review_quizzes_btn);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        loadingText = loadingDialog.findViewById(R.id.loading_text);

        firebaseAuth = FirebaseAuth.getInstance();

        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Page1.this,Directory_Display.class);
                startActivity(i);
            }
        });

        bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Page1.this,Bookmark.class);
                startActivity(i);
            }
        });

        marks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i =new Intent(Page1.this,PreviousScore.class);
                startActivity(i);
            }
        });

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i =new Intent(Page1.this,LeaderBoard.class);
                startActivity(i);
            }
        });

        review_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i =new Intent(Page1.this,ReviewQuizzes.class);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.LogoutMenu)
        {
            new AlertDialog.Builder(Page1.this, R.style.Theme_AppCompat_Light_Dialog).setTitle("Logout")
                    .setMessage("Are you sure you want to Logout from this session ?")
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingDialog.show();
                            loadingText.setText("Signing Out");
                            firebaseAuth.signOut();
                            Intent i = new Intent(Page1.this, Login_Acitivity.class);
                            loadingDialog.dismiss();
                            startActivity(i);
                            finish();

                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        loadingDialog.dismiss();

        if (item.getItemId() == R.id.Refresh){
            finish();
            startActivity(getIntent());
        }

        if (item.getItemId() == R.id.UploadVideo){
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return super.onOptionsItemSelected(item);
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(Page1.this,VideoActivity.class);
            loadingDialog.show();
            startActivity(i);
        }

        if (item.getItemId() == R.id.UploadCV){
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return super.onOptionsItemSelected(item);
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(Page1.this,UploadCv.class);
            loadingDialog.show();
            startActivity(i);
        }

        if (item.getItemId() == R.id.contactUs){
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return super.onOptionsItemSelected(item);
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(Page1.this,AboutUS.class);
            loadingDialog.show();
            startActivity(i);
        }

        if (item.getItemId() == R.id.update_profile){
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return super.onOptionsItemSelected(item);
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(Page1.this,Profile_Activity.class);
            loadingDialog.show();
            startActivity(i);
        }

        if (item.getItemId() == R.id.FAQ){
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return super.onOptionsItemSelected(item);
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(Page1.this,FAQs.class);
            loadingDialog.show();
            startActivity(i);
        }

        loadingDialog.dismiss();

        return super.onOptionsItemSelected(item);
    }
}
