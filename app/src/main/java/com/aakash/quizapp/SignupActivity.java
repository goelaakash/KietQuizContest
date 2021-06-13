package com.aakash.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aakash.quizapp.Utils.Internet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText emailid;
    private EditText password;
    private ProgressBar pro;
    private Button register;
    private EditText repassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailid = (EditText) findViewById(R.id.et_emailid);
        password = (EditText) findViewById(R.id.et_passwordid);
        repassword = (EditText) findViewById(R.id.et_repasswordid);
        register = (Button) findViewById(R.id.btn_register);
        pro = (ProgressBar) findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();


        this.register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (Internet.haveNetwork(SignupActivity.this)) {
                    String email = emailid.getText().toString().trim();
                    String passwordid = password.getText().toString().trim();
                    String confirmpass = repassword.getText().toString().trim();


                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(passwordid)) {
                        Toast.makeText(getApplicationContext(), "Enter Your Password!", Toast.LENGTH_SHORT).show();
                    } else if (passwordid.length() < 6) {
                        Toast.makeText(getApplicationContext(), "Password Length should be greater than 6", Toast.LENGTH_SHORT).show();
                    } else if (!isEmailValid(email)) {
                        Toast.makeText(getApplicationContext(), "Your Email Id is Invalid.", Toast.LENGTH_SHORT).show();
                    } else if(!passwordid.equals(confirmpass)){
                        Toast.makeText(getApplicationContext(), "Your Password don't Match with Confirm Password.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        pro.setVisibility(View.VISIBLE);
                        auth.createUserWithEmailAndPassword(email, passwordid).addOnCompleteListener((Activity) SignupActivity.this, (OnCompleteListener<AuthResult>) new OnCompleteListener<AuthResult>() {
                            public void onComplete(Task<AuthResult> task) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("createUserWithEmail:onComplete: ");
                                sb.append(task.isSuccessful());
                                Toast.makeText(SignupActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                                pro.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Either there is some error or Email already exist", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                else {
                                    sendemail();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void sendemail() {
        FirebaseUser user = this.auth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(SignupActivity.this, "Registeration Successsful. We have send you an email please verify it..", Toast.LENGTH_LONG).show();
                        auth.signOut();
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        finish();
                    }
                }
            });
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignupActivity.this,MainActivity.class));
        finish();

    }
}