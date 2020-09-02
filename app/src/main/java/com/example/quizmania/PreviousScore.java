package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class PreviousScore extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference myRef;
    FirebaseAuth firebaseAuth;
    ArrayList<PreviousScoreModel> list;
    private Dialog loadingDialog;
    private TextView TotalMarks,outof;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_score);

        getSupportActionBar().setTitle("Previous Scores");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        myRef = FirebaseDatabase.getInstance().getReference("Users");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        recyclerView = findViewById(R.id.rv_previousScore);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        TotalMarks = findViewById(R.id.totalmarks);
        outof = findViewById(R.id.previousoutofmarks);

        searchView = findViewById(R.id.search_previousScore);

        //categoryName = getIntent().getStringExtra("categoryName");

    }

    @Override
    protected void onStart() {
        super.onStart();


        loadingDialog.show();
        if (myRef != null){

            myRef.child(firebaseAuth.getUid()).child("Scores").addValueEventListener(new ValueEventListener() {
                int sum = 0;
                int sum1 = 0;
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list = new ArrayList<>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        list.add(dataSnapshot1.getValue(PreviousScoreModel.class));

                        Map<String,Object> map = (Map<String, Object>) dataSnapshot1.getValue();
                        Object score = map.get("score");
                        int Value = Integer.parseInt(String.valueOf(score));
                        sum = sum + Value;

                        TotalMarks.setText(String.valueOf(sum));

                        Map<String,Object> map2 = (Map<String, Object>) dataSnapshot1.getValue();
                        Object total = map2.get("total");
                        int Value2 = Integer.parseInt(String.valueOf(total));
                        sum1 = sum1 + Value2;

                        outof.setText(String.valueOf(sum1));

                        Collections.sort(list, new PreviousScore.MyComparator());

                    }
                    PreviousScoreAdapter adapter = new PreviousScoreAdapter(list);
                    recyclerView.setAdapter(adapter);
                    loadingDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(PreviousScore.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    finish();
                }
            });
        }

        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void search(String s){
        ArrayList<PreviousScoreModel> list2 = new ArrayList<>();
        for (PreviousScoreModel object : list){
            if (object.getCategoryName().toLowerCase().contains(s.toLowerCase())){
                list2.add(object);
            }
        }
        PreviousScoreAdapter adapter2 = new PreviousScoreAdapter(list2);
        recyclerView.setAdapter(adapter2);
    }


    public class MyComparator implements Comparator<PreviousScoreModel> {
        @Override
        public int compare(PreviousScoreModel p1, PreviousScoreModel p2) {
            return p1.getScore().compareTo(p2.getScore());
        }
    }
}
