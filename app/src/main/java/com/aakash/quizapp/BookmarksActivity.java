package com.aakash.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.aakash.quizapp.Adapters.BookmarkAdapter;
import com.aakash.quizapp.Models.Questions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookmarkAdapter bookmarkAdapter;
    private List<Questions> userslist;

    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        recyclerView=findViewById(R.id.recycler_book);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(BookmarksActivity.this));

        firebaseAuth = FirebaseAuth.getInstance();

        userslist=new ArrayList<>();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading You Quiz");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        reference = FirebaseDatabase.getInstance().getReference("Bookmarks").child(firebaseAuth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userslist.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String category = snapshot.getKey();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        Questions questions = snapshot1.getValue(Questions.class);
                        questions.setCategory(category);
                        userslist.add(questions);
                        progressDialog.dismiss();
                    }
                }

                bookmarkAdapter = new BookmarkAdapter(BookmarksActivity.this, userslist);
                recyclerView.setAdapter(bookmarkAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}