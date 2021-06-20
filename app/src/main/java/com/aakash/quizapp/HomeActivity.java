package com.aakash.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.aakash.quizapp.Models.Questions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {

    Button b1,b2;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.feed_toolbar);
        setSupportActionBar(toolbar);

        b1=findViewById(R.id.btn_quiz);
        b2=findViewById(R.id.btn_bookmark);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,CategoryActivity.class));
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,BookmarksActivity.class));
            }
        });

//        Questions questions = new Questions("Q10. What will the result of len variable after execution of the following statements?\n" +
//                "1.\tint len;  \n" +
//                "2.\tchar str1[] = {\"39 march road\"};  \n" +
//                "3.\tlen = strlen(str1);  \n"
//,"A. 11","B. 12","C. 13","D. 14","C","10");
//
//
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Quiz");
//        reference.child("CLanguage").child("10").setValue(questions);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater()
                .inflate(R.menu.navigation, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected( final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.findfriend:
                startActivity(new Intent(HomeActivity.this,EditProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(HomeActivity.this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

}