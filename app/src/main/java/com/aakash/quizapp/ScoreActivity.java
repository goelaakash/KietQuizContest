package com.aakash.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    Button b1;
    String score;
    TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        t1= findViewById(R.id.tv_score);
        b1=findViewById(R.id.btn_finish);

        score = getIntent().getStringExtra("Score");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        t1.setText(score+"/10");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}