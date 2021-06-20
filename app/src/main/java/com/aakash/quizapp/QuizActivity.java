package com.aakash.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aakash.quizapp.Models.Questions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    String type;
    TextView quest,option1,option2,option3,option4;
    CardView que,opt1,opt2,opt3,opt4;

    private List<Questions> questions;
    ProgressDialog progressDialog;
    Button next,bookmark;

    int i =0;
    int score=0;
    String ans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        type = getIntent().getStringExtra("type");
        quest = findViewById(R.id.tv_quest);
        option1 = findViewById(R.id.tv_option1);
        option2 = findViewById(R.id.tv_option2);
        option3 = findViewById(R.id.tv_option3);
        option4 = findViewById(R.id.tv_option4);

        que = findViewById(R.id.card_quest);
        opt1 = findViewById(R.id.card_option1);
        opt2 = findViewById(R.id.card_option2);
        opt3 = findViewById(R.id.card_option3);
        opt4 = findViewById(R.id.card_option4);

        next = findViewById(R.id.btn_next);
        bookmark =findViewById(R.id.btn_book);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading You Quiz");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        questions =new ArrayList<>();
        loaddata();

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("Bookmarks");
                reference.child(firebaseAuth.getUid()).child(type).child(questions.get(i).getId()).setValue(questions.get(i)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(QuizActivity.this, "Bookmark Added", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuizActivity.this, "Bookmark Failed to Add", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        opt1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if(ans.equals("A")){
                    opt1.setBackgroundResource(R.color.green);
                    score++;
                }
                else{
                    opt1.setBackgroundResource(R.color.red);
                    if(ans.equals("B")){
                        opt2.setBackgroundResource(R.color.green);
                    }
                    else if(ans.equals("C")){
                        opt3.setBackgroundResource(R.color.green);
                    }
                    else{
                        opt4.setBackgroundResource(R.color.green);
                    }
                }

                opt1.setEnabled(false);
                opt2.setEnabled(false);
                opt3.setEnabled(false);
                opt4.setEnabled(false);
            }
        });

        opt2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if(ans.equals("B")){
                    opt2.setBackgroundResource(R.color.green);
                    score++;
                }
                else{
                    opt2.setBackgroundResource(R.color.red);
                    if(ans.equals("A")){
                        opt1.setBackgroundResource(R.color.green);
                    }
                    else if(ans.equals("C")){
                        opt3.setBackgroundResource(R.color.green);
                    }
                    else{
                        opt4.setBackgroundResource(R.color.green);
                    }
                }
                opt1.setEnabled(false);
                opt2.setEnabled(false);
                opt3.setEnabled(false);
                opt4.setEnabled(false);
            }
        });

        opt3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if(ans.equals("C")){
                    opt3.setBackgroundResource(R.color.green);
                    score++;
                }
                else{
                    opt3.setBackgroundResource(R.color.red);
                    if(ans.equals("A")){
                        opt1.setBackgroundResource(R.color.green);
                    }
                    else if(ans.equals("B")){
                        opt2.setBackgroundResource(R.color.green);
                    }
                    else{
                        opt4.setBackgroundResource(R.color.green);
                    }
                }
                opt1.setEnabled(false);
                opt2.setEnabled(false);
                opt3.setEnabled(false);
                opt4.setEnabled(false);
            }
        });

        opt4.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if(ans.equals("D")){
                    opt4.setBackgroundResource(R.color.green);
                    score++;
                }
                else{
                    opt4.setBackgroundResource(R.color.red);
                    if(ans.equals("A")){
                        opt1.setBackgroundResource(R.color.green);
                    }
                    else if(ans.equals("B")){
                        opt2.setBackgroundResource(R.color.green);
                    }
                    else{
                        opt3.setBackgroundResource(R.color.green);
                    }
                }
                opt1.setEnabled(false);
                opt2.setEnabled(false);
                opt3.setEnabled(false);
                opt4.setEnabled(false);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i<9) {
                    i++;
                    opt1.setEnabled(true);
                    opt2.setEnabled(true);
                    opt3.setEnabled(true);
                    opt4.setEnabled(true);
                    setques();
                }
                else{
                    Intent intent = new Intent(QuizActivity.this,ScoreActivity.class);
                    intent.putExtra("Score",String.valueOf(score));
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    public void loaddata(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Quiz");
        reference.child(type).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Questions q = dataSnapshot.getValue(Questions.class);
                    questions.add(q);
                }
                progressDialog.dismiss();
                setques();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setques(){
        opt1.setBackgroundResource(R.color.white);
        opt2.setBackgroundResource(R.color.white);
        opt3.setBackgroundResource(R.color.white);
        opt4.setBackgroundResource(R.color.white);

        quest.setText(questions.get(i).getQuest());
        option1.setText(questions.get(i).getOption1());
        option2.setText(questions.get(i).getOption2());
        option3.setText(questions.get(i).getOption3());
        option4.setText(questions.get(i).getOption4());
        ans = questions.get(i).getAnswer();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        builder.setMessage("Are you Sure you Want to leave the Quiz");
        builder.setCancelable(true);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                //Toast.makeText(MainActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}