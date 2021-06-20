package com.aakash.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CategoryActivity extends AppCompatActivity {

    CardView aptitude,ds,va,clang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        aptitude = findViewById(R.id.card_apti);
        ds = findViewById(R.id.card_ds);
        va = findViewById(R.id.card_verbal);
        clang = findViewById(R.id.card_clang);

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
                intent.putExtra("type","DataStructure");
                startActivity(intent);
                finish();
            }
        });

        va.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(CategoryActivity.this,QuizActivity.class);
                intent.putExtra("type","VerbalAbility");
                startActivity(intent);
                finish();
            }
        });

        clang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(CategoryActivity.this,QuizActivity.class);
                intent.putExtra("type","CLanguage");
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed(){
        finish();
    }
}