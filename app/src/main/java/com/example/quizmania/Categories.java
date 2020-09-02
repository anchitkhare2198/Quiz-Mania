package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Categories extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private Dialog loadingDialog;

    private RecyclerView recyclerView;
    public static List<CategoryModel> list;
    public static String DirectoryName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        //androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        //Toolbar toolbar = findViewById(R.id.toolbar);
//        Toolbar toolbar = findViewById(R.id.toolbar);
        DirectoryName = getIntent().getStringExtra("title");
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(DirectoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        recyclerView = findViewById(R.id.rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);


        list = new ArrayList<>();
        final CategoryAdapter adapter = new CategoryAdapter(list);
        recyclerView.setAdapter(adapter);

        loadingDialog.show();
        myRef.child("Subjects").child(DirectoryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    List<String> sets = new ArrayList<>();
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("sets").getChildren()){
                        sets.add(dataSnapshot2.getKey());
                    }

                    list.add(new CategoryModel(dataSnapshot1.child("name").getValue().toString(),
                            sets,
                            dataSnapshot1.child("url").getValue().toString(),
                            dataSnapshot1.getKey()));

                    Collections.sort(list, new MyComparator());
                }
                adapter.notifyDataSetChanged();
                loadingDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Categories.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();

            }
        });

    }

//    private void setSupportActionBar(Toolbar toolbar) {
//    }

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
