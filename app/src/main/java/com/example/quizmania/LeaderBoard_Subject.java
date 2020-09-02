package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderBoard_Subject extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<SubjectLeaderBoardModel> list;
    private Dialog loadingDialog;
    SearchView searchView;
    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board__subject);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Subject_Scores");

        getSupportActionBar().setTitle("Subject Leader Board");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoryName = getIntent().getStringExtra("title");

        searchView = findViewById(R.id.search_leaderboard_Subject);

        recyclerView = findViewById(R.id.rv_leaderBoard_Subject);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        loadingDialog.show();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){
                        String newcate = dataSnapshot2.getKey();
                        if (newcate.equals(categoryName)){
                            list.add(dataSnapshot2.getValue(SubjectLeaderBoardModel.class));

                            Collections.sort(list, new LeaderBoard_Subject.MyComparator());
                        }
                    }
                    SubjectLeaderBoardAdapter adapter = new SubjectLeaderBoardAdapter(list);
                    recyclerView.setAdapter(adapter);
                    loadingDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LeaderBoard_Subject.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
        ArrayList<SubjectLeaderBoardModel> list2 = new ArrayList<>();
        for (SubjectLeaderBoardModel object : list){
            if (object.getFullName().toLowerCase().contains(s.toLowerCase())){
                list2.add(object);
            }
        }
        SubjectLeaderBoardAdapter adapter2 = new SubjectLeaderBoardAdapter(list2);
        recyclerView.setAdapter(adapter2);
    }

    public class MyComparator implements Comparator<SubjectLeaderBoardModel> {
        @Override
        public int compare(SubjectLeaderBoardModel p1, SubjectLeaderBoardModel p2) {
            return p2.getFinalScore().compareTo(p1.getFinalScore());
        }
    }
}
