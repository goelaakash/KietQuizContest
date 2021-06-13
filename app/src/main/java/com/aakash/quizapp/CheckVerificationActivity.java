package com.aakash.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.aakash.quizapp.Models.UserList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckVerificationActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    public FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    int x=0,y=0;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_verification);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (x == 0) {
                    x=1;
                    UserList userList = dataSnapshot.getValue(UserList.class);
                    String value = dataSnapshot.child("name").getValue(String.class);

                    if (value == null) {
                        //databaseReference.removeEventListener(valueEventListener);
                        progressDialog.dismiss();
                        finish();
                        startActivity(new Intent(CheckVerificationActivity.this, ProfileActivity.class));
                    }
                    else{
                       // databaseReference.removeEventListener(valueEventListener);
                        progressDialog.dismiss();
                        finish();
                        startActivity(new Intent(CheckVerificationActivity.this, HomeActivity.class));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    protected void onResume(){
        super.onResume();
        y=0;
        x=0;
    }
}