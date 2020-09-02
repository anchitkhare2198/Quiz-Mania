package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
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
import java.util.List;
import java.util.Map;

public class LeaderBoard extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference myRef;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<LeaderModel> list1;
    private Dialog loadingDialog;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        firebaseAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("Total_Scores");

        getSupportActionBar().setTitle("Leader Board");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.rv_leaderBoard);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        searchView = findViewById(R.id.search_leaderboard);


        Switch sw = (Switch) findViewById(R.id.subject_switch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled

                    Intent i = new Intent(LeaderBoard.this,Subject_Select_LeaderBoard.class);
                    loadingDialog.show();
                    startActivity(i);
                    finish();
                    loadingDialog.dismiss();

                } else {
                    // The toggle is disabled
                }
            }
        });


        loadingDialog.show();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list1 = new ArrayList<>();
                //List<String> list2 = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    list1.add(dataSnapshot1.getValue(LeaderModel.class));
                    //list2.add(dataSnapshot1.child("totalScore").getValue().toString());
                    Collections.sort(list1,new LeaderBoard.MyComparator());
                }
                //Collections.sort(list1, new LeaderBoard.MyComparator());
                LeaderAdapter adapter = new LeaderAdapter(list1);
                recyclerView.setAdapter(adapter);
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LeaderBoard.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });

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
        ArrayList<LeaderModel> list2 = new ArrayList<>();
        for (LeaderModel object : list1){
            if (object.getFullName().toLowerCase().contains(s.toLowerCase())){
                list2.add(object);
            }
        }
        LeaderAdapter adapter2 = new LeaderAdapter(list2);
        recyclerView.setAdapter(adapter2);
    }

    public class MyComparator implements Comparator<LeaderModel> {
        @Override
        public int compare(LeaderModel p1, LeaderModel p2) {
            return Integer.parseInt(p2.getTotalScore()) - Integer.parseInt(p1.getTotalScore());
        }
    }
}
