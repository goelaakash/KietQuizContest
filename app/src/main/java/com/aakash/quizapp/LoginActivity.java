package com.aakash.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aakash.quizapp.Utils.Internet;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    public EditText emailid;
    FirebaseAuth mAuth;
    public EditText passwordid;
    ProgressBar pro;
    private Boolean emailAddressChecker;
    private Button signin;
    private TextView reset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.emailid = findViewById(R.id.et_emailid);
        this.passwordid = findViewById(R.id.et_passwordid);
        this.signin = findViewById(R.id.phonelogin);
        this.pro = findViewById(R.id.pro_login);
        reset=findViewById(R.id.resetpassword);

        this.mAuth = FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailid.getText().toString();
                if(!email.isEmpty() && isEmailValid(email)){
                    pro.setVisibility(View.VISIBLE);
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "We have mailed you instructions to reset your password!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                            }
                            pro.setVisibility(View.GONE);
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this, "Enter the Email Address!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.signin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (Internet.haveNetwork(LoginActivity.this)) {
                    String email = emailid.getText().toString().trim();
                    String password = passwordid.getText().toString().trim();
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    } else if (password.length() < 6) {
                        Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    } else {
                        pro.setVisibility(View.VISIBLE);
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            public void onComplete(Task<AuthResult> task) {
                                pro.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                else{
                                   // startActivity(new Intent(LoginActivity.this,CheckVerificationActivity.class));
                                    emailverification();
                                }

                            }
                        });
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void emailverification() {
        this.emailAddressChecker = Boolean.valueOf(this.mAuth.getCurrentUser().isEmailVerified());
        if (this.emailAddressChecker) {
            startActivity(new Intent(LoginActivity.this, CheckVerificationActivity.class));
            finish();
            return;
        } else {
            Toast.makeText(this, "Your Email is not verified.", Toast.LENGTH_SHORT).show();
            sendemail();
        }
    }



    private void sendemail() {
        FirebaseUser user = this.mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "We have send you an email please verify it..", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }
}