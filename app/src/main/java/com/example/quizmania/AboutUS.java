package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutUS extends AppCompatActivity {

    private TextView text1,text2;
    Spanned Text1, Text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        getSupportActionBar().setTitle("About Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        text1 = findViewById(R.id.text1);
        text1.setClickable(true);
        text2 = findViewById(R.id.text2);
        text2.setClickable(true);

        Text1 = Html.fromHtml("<a href = 'www.enable-careers.com'>www.enable-careers.com</a>");
        Text2 = Html.fromHtml("<a href = 'jobs@enable-careers.com'>jobs@enable-careers.com</a>");

        text1.setMovementMethod(LinkMovementMethod.getInstance());
        text1.setText(Text1);

        text2.setMovementMethod(LinkMovementMethod.getInstance());
        text2.setText(Text2);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
