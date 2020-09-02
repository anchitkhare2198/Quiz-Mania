package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.Index;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Questions extends AppCompatActivity {

    public static final String FILE_NAME = "Quiz_Mania";
    public static final String KEY_NAME = "Questions";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    FirebaseAuth firebaseAuth;
    private TextView questions;
    private TextView quesno;
    private TextView timer;
    private FloatingActionButton bookmark;
    private LinearLayout optionscontainer;
    private Button next;
    private int count = 0;
    private int count2 = 0;
    private List<QuestionModel> list;
    private List<QuestionModel> list2;
    private int position= 0;
    private int Score = 0;
    private String setId;
    private String idall;



    private Dialog loadingDialog;
    private List<BookmarkModel> bookmarkslist;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private int matchedQuestionPosition;
    private String categoryName;
    public static final long COUNTDOWN = 30000;
    private ColorStateList textColorDefaultCd;
    private Button optionA,optionB,optionC,optionD;
    private long mLastClickTime = 0;

    private CountDownTimer countDownTimer;
    private long Timeleft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        setId = getIntent().getStringExtra("setId");
        categoryName = getIntent().getStringExtra("category");

        getSupportActionBar().setTitle(categoryName+" Questions");

        questions = findViewById(R.id.questions);
        quesno = findViewById(R.id.score);
        bookmark = findViewById(R.id.bookmark_btn);
        optionscontainer = findViewById(R.id.option_buttons);
        next = findViewById(R.id.next);


        optionA = findViewById(R.id.option1);
        optionB = findViewById(R.id.option2);
        optionC = findViewById(R.id.option3);
        optionD = findViewById(R.id.option4);

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                storebookmarks();
            }
        });



        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);



        list = new ArrayList<>();
        loadingDialog.show();
        myRef.child("Sets").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    if (dataSnapshot1.child("optionC").exists() && dataSnapshot1.child("optionD").exists()){
                        String id = dataSnapshot1.getKey();
                        String question = dataSnapshot1.child("question").getValue().toString();
                        String a = dataSnapshot1.child("optionA").getValue().toString();
                        String b = dataSnapshot1.child("optionB").getValue().toString();
                        String c = dataSnapshot1.child("optionC").getValue().toString();
                        String d = dataSnapshot1.child("optionD").getValue().toString();
                        String correctAnswer = dataSnapshot1.child("correctAnswer").getValue().toString();
                        list.add(new QuestionModel(id, question, a, b, c, d, correctAnswer, setId));

                    }else{
                        String id = dataSnapshot1.getKey();
                        String question = dataSnapshot1.child("question").getValue().toString();
                        String a = dataSnapshot1.child("optionA").getValue().toString();
                        String b = dataSnapshot1.child("optionB").getValue().toString();
                        String correctAnswer = dataSnapshot1.child("correctAnswer").getValue().toString();
                        list.add(new QuestionModel(id, question, a, b, correctAnswer, setId));

                    }
                }

                if(list.size() > 0)
                {

                    for (int i=0;i<4;i++)
                    {
//                        Timeleft = COUNTDOWN;
//                        startCountDown();
                        optionscontainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkAnswer((Button) v);
                            }
                        });
                    }
                    playAnime(questions, 0, list.get(position).getQuestion());
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            next.setEnabled(false);
                            next.setAlpha(0.7f);
                            enableoption(true);
                            position++;
                            if (position == list.size()){
                                //score
                                Intent i = new Intent(Questions.this,Score.class);
                                i.putExtra("score",Score);
                                i.putExtra("total",list.size());
                                i.putExtra("category",categoryName);
                                i.putExtra("setId",setId);
                                startActivity(i);
                                finish();
                                return;
                            }
                            count = 0;
                            playAnime(questions, 0, list.get(position).getQuestion());
                        }
                    });
                }
                else if (list.size() == 0)
                {
                    finish();
                    Toast.makeText(Questions.this,"No Questions Available",Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Questions.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //    private void startCountDown(){
//        timer = new CountDownTimer(Timeleft,1000){
//
//            @Override
//            public void onTick(long millisUntilFinished) {
//                Timeleft = millisUntilFinished;
//                updateCountDown();
//
//            }
//
//            @Override
//            public void onFinish() {
//                Timeleft = 0;
//                updateCountDown();
//            }
//        }.start();
//    }
//
//    private void updateCountDown(){
//
//        int min = (int) (Timeleft/1000)/60;
//        int sec = (int) (Timeleft/1000) % 60;
//
//        String time = String.format(Locale.getDefault(),"%02d:%02d",min,sec);
//        timer.setText(time);
//
//        if(Timeleft < 10000){
//            timer.setTextColor(Color.RED);
//        }
//        else{
//            timer.setTextColor(textColorDefaultCd);
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        //storebookmarks();
    }

    private void playAnime(final View view, final int value, final String data)
    {

//        for (int i=0;i<4;i++)
//        {
//            optionscontainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#10242E")));
//        }


        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                bookmark.setClickable(false);

                if(value == 0 && count < 4)
                {
                    String option = "";
                    if(count == 0){
                        option = list.get(position).getA();
                    }
                    if(count == 1){
                        option = list.get(position).getB();
                    }
                    if(count == 2){
                        option = list.get(position).getC();
                        if (option == null || option.isEmpty()){
                            optionC.setVisibility(View.INVISIBLE);
                        }else{
                            optionC.setVisibility(View.VISIBLE);
                        }
                    }
                    if(count == 3){
                        option = list.get(position).getD();
                        System.out.println(option);
                        if (option == null || option.isEmpty()){
                            optionD.setVisibility(View.INVISIBLE);
                        }else{
                            optionD.setVisibility(View.VISIBLE);
                        }
                    }
                    playAnime(optionscontainer.getChildAt(count),0, option);
                    count++;
                }

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bookmark.setClickable(true);

                if(value == 0 )
                {
                    try{
                        ((TextView)view).setText(data);
                        quesno.setText(position+1+"/"+list.size());
                        getbookmarks();
                        //bookmark.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                        //bookmark.setClickable(true);
//                        if(modelmatch()){
//                            bookmark.setImageDrawable(getDrawable(R.drawable.bookmark2));
//                        }else{
//                            bookmark.setImageDrawable(getDrawable(R.drawable.bookmark_border));
//                        }
                    }catch (ClassCastException ex){
                        ((Button)view).setText(data);
                    }
                    view.setTag(data);
                    playAnime(view,1, data);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void checkAnswer(Button selected)
    {
        enableoption(false);
        next.setEnabled(true);
        next.setAlpha(1);
        if(selected.getText().toString().equals(list.get(position).getAnswer())){
            Score++;
            selected.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }else{
            selected.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
            Button correctoption = (Button) optionscontainer.findViewWithTag(list.get(position).getAnswer());
            correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }
    }

    private void enableoption(boolean enable)
    {

        for (int i=0;i<4;i++)
        {
            optionscontainer.getChildAt(i).setEnabled(enable);
            if(enable){
                optionscontainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#10242E")));
            }
        }

    }

//    private void getbookmarks(){
//        String json = preferences.getString(KEY_NAME,"");
//        Type type = new TypeToken<List<BookmarkModel>>(){}.getType();
//
//        bookmarkslist = gson.fromJson(json,type);
//
//        if(bookmarkslist == null)
//        {
//            bookmarkslist = new ArrayList<>();
//        }
//
//    }

//    private boolean modelmatch(){
//        final boolean[] matched = {false};
//        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Users");
//        myRef2.child(firebaseAuth.getUid()).child("Bookmarks").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    QuestionModel model = null;
//                    BookmarkModel model2 = null;
//                    if (model.getQuestion().equals(model2.getQuestion())
//                    && model.getAnswer().equals(model2.getAnswer())){
//                        matched[0] = true;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(Questions.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
//            }
//        });
//        return matched[0];
//
//    }


//    private boolean modelmatch(){
//        boolean matched = false;
//        int i = 0;
//        for(BookmarkModel model : bookmarkslist){
//
//            if(model.getQuestion().equals(list.get(position).getQuestion())
//            && model.getAnswer().equals(list.get(position).getAnswer())){
//                matched = true;
//                matchedQuestionPosition = i;
//            }
//            i++;
//        }
//        return matched;
//
//    }

    private void getbookmarks(){

        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Users");
        bookmarkslist = new ArrayList<>();
        myRef2.child(firebaseAuth.getUid()).child("Bookmarks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String ques = null;
                    String ans = null;
                    Map<String,String> map = new HashMap<String, String>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        ques = dataSnapshot1.child("question").getValue().toString();
                        ans  = dataSnapshot1.child("answer").getValue().toString();

                        map.put(ques,ans);
                    }
                    for (Map.Entry<String,String> entry : map.entrySet()){
                        String a = entry.getKey();
                        String b = entry.getValue();
//                        System.out.println("Question = " + entry.getKey());
//                        System.out.println("Answer = " + entry.getValue());
                        if (a.equals(list.get(position).getQuestion()) && b.equals(list.get(position).getAnswer())){
                            bookmark.setImageDrawable(getDrawable(R.drawable.bookmark2));
                            bookmark.setClickable(false);
                            break;
                        }else{
                            bookmark.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                            bookmark.setClickable(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Questions.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storebookmarks() {
        final DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Users");
        final Map<String, Object> map = new HashMap<>();
        int i = 0;
        String ques = list.get(position).getQuestion();
        String ans = list.get(position).getAnswer();
        String id1 = UUID.randomUUID().toString();
        for (QuestionModel model : list) {
            map.put("question", ques);
            map.put("answer", ans);
            BookmarkModel bookmarkModel = new BookmarkModel(ques, ans, id1);
            map.get(bookmarkModel);
            myRef2.child(firebaseAuth.getUid()).child("Bookmarks").child(id1).setValue(map);
            bookmark.setImageDrawable(getDrawable(R.drawable.bookmark2));
            bookmark.setClickable(false);
            matchedQuestionPosition = i;
            i++;
        }

    }


//    private void storebookmarks(){
//        String json = gson.toJson(bookmarkslist);
//        editor.putString(KEY_NAME,json);
//        editor.commit();
//    }
}
