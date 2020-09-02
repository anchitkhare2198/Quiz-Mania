package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
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

public class Directory_Display extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference myRef;
    List<DirectoryModel> list;

    private RecyclerView recyclerView;
    public Dialog loadingDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory__display);

        firebaseAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        getSupportActionBar().setTitle("Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        recyclerView = findViewById(R.id.rv_Directory_Display);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();

        loadingDialog.show();

        final DirectoryAdapter adapter = new DirectoryAdapter(list);
        recyclerView.setAdapter(adapter);

        myRef.child("Subject_Dir").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    List<String> sets = new ArrayList<>();
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("sets").getChildren()){
                        sets.add(dataSnapshot2.getKey());
                    }

                    list.add(new DirectoryModel(dataSnapshot1.child("Dir_Name").getValue().toString(),
                            sets,dataSnapshot1.getKey()));

                    Collections.sort(list, new Directory_Display.MyComparator());
                }
                adapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Directory_Display.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });

//        myRef.child("Subject_Dir").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
//                    list.add(new DirectoryModel(dataSnapshot1.child("Dir_Name").getValue().toString()));
//                }
//                adapter.notifyDataSetChanged();
//                loadingDialog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(Directory_Display.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
//                loadingDialog.dismiss();
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public class MyComparator implements Comparator<DirectoryModel> {
        @Override
        public int compare(DirectoryModel p1, DirectoryModel p2) {
            return p1.getDir_Name().compareTo(p2.getDir_Name());
        }
    }

}
