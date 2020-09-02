package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
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

public class Subject_Select_LeaderBoard extends AppCompatActivity {

    SearchView searchView;
    private Dialog loadingDialog;
    RecyclerView recyclerView;
    List<CategoryModel> list;

    String DirectoryName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject__select__leader_board);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.rv_SubjectleaderBoard);
        searchView = findViewById(R.id.search_Subjectleaderboard);

        DirectoryName = Categories.DirectoryName;

        getSupportActionBar().setTitle("Select Subject");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);


        Switch sw = (Switch) findViewById(R.id.leader_switch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled



                } else {
                    // The toggle is disabled
                    Intent i = new Intent(Subject_Select_LeaderBoard.this,LeaderBoard.class);
                    loadingDialog.show();
                    startActivity(i);
                    finish();
                    loadingDialog.dismiss();

                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        final SubjectLeaderAdapter adapter = new SubjectLeaderAdapter(list);
        recyclerView.setAdapter(adapter);

        loadingDialog.show();

        myRef.child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){
                        List<String> sets = new ArrayList<>();
                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.child("sets").getChildren()){
                            sets.add(dataSnapshot3.getKey());
                        }

                        list.add(new CategoryModel(dataSnapshot2.child("name").getValue().toString(),
                                sets,
                                dataSnapshot2.child("url").getValue().toString(),
                                dataSnapshot2.getKey()));

                        Collections.sort(list, new Subject_Select_LeaderBoard.MyComparator());
                    }

                }
                adapter.notifyDataSetChanged();
                loadingDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Subject_Select_LeaderBoard.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();

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


//        myRef.child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
//                {
//                    List<String> sets = new ArrayList<>();
//                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("sets").getChildren()){
//                        sets.add(dataSnapshot2.getKey());
//                    }
//
//                    list.add(new CategoryModel(dataSnapshot1.child("name").getValue().toString(),
//                            sets,
//                            dataSnapshot1.child("url").getValue().toString(),
//                            dataSnapshot1.getKey()));
//
//                    Collections.sort(list, new Subject_Select_LeaderBoard.MyComparator());
//                }
//                adapter.notifyDataSetChanged();
//                loadingDialog.dismiss();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                Toast.makeText(Subject_Select_LeaderBoard.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                loadingDialog.dismiss();
//                finish();
//
//            }
//        });
    }

    private void search(String s){
        ArrayList<CategoryModel> list2 = new ArrayList<>();
        for (CategoryModel object : list){
            if (object.getName().toLowerCase().contains(s.toLowerCase())){
                list2.add(object);
            }
        }
        SubjectLeaderAdapter adapter2 = new SubjectLeaderAdapter(list2);
        recyclerView.setAdapter(adapter2);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public class MyComparator implements Comparator<CategoryModel> {
        @Override
        public int compare(CategoryModel p1, CategoryModel p2) {
            return p1.getName().compareTo(p2.getName());
        }
    }
}
