package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.quizmania.Questions.FILE_NAME;
import static com.example.quizmania.Questions.KEY_NAME;

public class Bookmark extends AppCompatActivity {

    private List<BookmarkModel> list;
    BookmarkAdapter adapter;
    private RecyclerView recyclerView;
    DatabaseReference myRef;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        getSupportActionBar().setTitle("Bookmarks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.rv_bookmark);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("Users");
        list = new ArrayList<>();
        adapter = new BookmarkAdapter(list, new BookmarkAdapter.DeleteListener() {
            @Override
            public void onDelete(final String key, final int position) {
                new AlertDialog.Builder(Bookmark.this,R.style.Theme_AppCompat_Light_Dialog).setTitle("Delete Question")
                        .setMessage("Are you sure you want to delete this question ?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                myRef.child(firebaseAuth.getUid()).child("Bookmarks").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            list.remove(position);
                                        }
                                        finish();
                                        startActivity(getIntent());
                                        adapter.notifyItemRemoved(position);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        loadingDialog.show();
        myRef.child(firebaseAuth.getUid()).child("Bookmarks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                        list.add(new BookmarkModel(dataSnapshot1.child("question").getValue().toString(),
                                dataSnapshot1.child("answer").getValue().toString(),
                                dataSnapshot1.getKey()));
                    }
                    loadingDialog.dismiss();
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }else{
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
                Toast.makeText(Bookmark.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
