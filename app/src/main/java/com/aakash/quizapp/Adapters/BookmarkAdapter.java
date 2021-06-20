package com.aakash.quizapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.aakash.quizapp.Models.Questions;
import com.aakash.quizapp.QuizActivity;
import com.aakash.quizapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ImageViewHolder>{

    public Context mContext1;
    private List<Questions> mUploads;
    private FirebaseUser firebaseUser;


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView ques,opt1,opt2,opt3,opt4,type,answer;
        public ImageView img;

        public ImageViewHolder(View itemView) {
            super(itemView);
            this.ques = (TextView) itemView.findViewById(R.id.tv_question);
            this.opt1 = (TextView) itemView.findViewById(R.id.tv_option1);
            this.opt2 = (TextView) itemView.findViewById(R.id.tv_option2);
            this.opt3 = (TextView) itemView.findViewById(R.id.tv_option3);
            this.opt4 = (TextView) itemView.findViewById(R.id.tv_option4);
            this.type = (TextView) itemView.findViewById(R.id.tv_category);
            this.answer = (TextView) itemView.findViewById(R.id.tv_anser);

            this.img = (ImageView) itemView.findViewById(R.id.img_delete);


        }
    }

    public BookmarkAdapter(Context context, List<Questions> uploads) {
        this.mContext1 = context;
        this.mUploads = uploads;
    }

    public BookmarkAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookmarkAdapter.ImageViewHolder(LayoutInflater.from(this.mContext1).inflate(R.layout.bookmark_item, parent, false));
    }

    public void onBindViewHolder(final BookmarkAdapter.ImageViewHolder holder, int position) {
        final Questions userList = (Questions) this.mUploads.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.ques.setText(userList.getQuest());
        holder.type.setText(userList.getCategory());
        holder.opt1.setText(userList.getOption1());
        holder.opt2.setText(userList.getOption2());
        holder.opt3.setText(userList.getOption3());
        holder.opt4.setText(userList.getOption4());
        holder.answer.setText("Correct Answer:- " + userList.getAnswer());

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext1);
                builder.setMessage("Are you Sure you Want to UnBookMark this Question");
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

                        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("Bookmarks");
                        reference.child(firebaseUser.getUid()).child(userList.getCategory()).child(userList.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(mContext1, "Bookmark Removed", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext1, "Bookmark Failed to Remove", Toast.LENGTH_LONG).show();
                            }
                        });
                        //Toast.makeText(MainActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


    }

    public int getItemCount() {
        return this.mUploads.size();
    }



}
