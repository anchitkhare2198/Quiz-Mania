package com.example.quizmania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Acitivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    EditText email,Password;
    TextView forgotpassword, Newuser;
    Button login, google, facebook;
    private FirebaseAuth firebaseAuth;
    private Dialog loadingDialog;
    private TextView loadingText;
    private long mLastClickTime = 0;

    //GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__acitivity);

        getSupportActionBar().setTitle("Login");

        email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        //google = findViewById(R.id.google);
        //facebook = findViewById(R.id.facebook);
        forgotpassword = findViewById(R.id.forgot_password);
        Newuser = findViewById(R.id.newuser);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        loadingText = loadingDialog.findViewById(R.id.loading_text);


        firebaseAuth = FirebaseAuth.getInstance();

        final Intent i =new Intent(this,Page1.class);

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
//
//        google.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//                startActivityForResult(i,104);
//            }
//
//        });



        if(firebaseAuth.getCurrentUser()!=null)
        {
            startActivity(i);
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(email.getText().toString().isEmpty()){
                    email.setError("Required");
                    return;
                }
                else{
                    email.setError(null);
                }
                if(Password.getText().toString().isEmpty()){
                    Password.setError("Required");
                    return;
                }
                else{
                    Password.setError(null);
                }

                loadingText.setText("Signing In");
                loadingDialog.show();


                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),Password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            loadingDialog.dismiss();
                            checkEmailVerification();
                        }
                        else{
                            loadingDialog.dismiss();
                            Toast.makeText(Login_Acitivity.this,"Incorrect Email or Password",Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Login_Acitivity.this, ForgotPassword.class);
                startActivity(i);
            }
        });

        Newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Login_Acitivity.this, Register.class);
                startActivity(i);
            }
        });
    }

    private void checkEmailVerification()
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        if(emailflag)
        {
            finish();
            Intent i = new Intent(this, Page1.class);
            startActivity(i);
        }

        else
        {
            Toast.makeText(this, " Verify Your Email ", Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
