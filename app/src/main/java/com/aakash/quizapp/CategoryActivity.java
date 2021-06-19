package com.aakash.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CategoryActivity extends AppCompatActivity {

    CardView aptitude,ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        aptitude = findViewById(R.id.card_apti);
        ds = findViewById(R.id.card_ds);

        aptitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent =  new Intent(CategoryActivity.this,QuizActivity.class);
                intent.putExtra("type","Aptitude");
               startActivity(intent);
               finish();
            }
        });

        ds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(CategoryActivity.this,QuizActivity.class);
                intent.putExtra("type","ds");
                startActivity(intent);
                finish();
            }
        });

    }
}