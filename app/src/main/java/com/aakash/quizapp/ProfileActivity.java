package com.aakash.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aakash.quizapp.Models.UserList;
import com.aakash.quizapp.Utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ProfileActivity extends AppCompatActivity {

    private Button btn;
    private EditText name;
    private EditText email;
    private Spinner spinner;
    private String gender,username;
    private FirebaseAuth firebaseAuth;

    private StorageReference mStorageRef;
    public StorageTask mUploadTask;

    private DatabaseReference rootRef;
    private FloatingActionButton fab;
    private ImageView profile;

    private Uri filepath;
    private Uri image_uri;
    private File actualImage,compressedImage;
    private String currentUser;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btn=findViewById(R.id.btn_submit);
        fab=findViewById(R.id.choose);
        profile=findViewById(R.id.circle_profile);
        name=findViewById(R.id.et_name);
        email=findViewById(R.id.et_profileemail);
        spinner=findViewById(R.id.et_gender);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = this.firebaseAuth.getCurrentUser().getUid();

        this.mStorageRef = FirebaseStorage.getInstance().getReference("Users").child(currentUser);


        rootRef = FirebaseDatabase.getInstance().getReference();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        email.setText(firebaseAuth.getCurrentUser().getEmail());


        ArrayAdapter<CharSequence> adaptergender = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        adaptergender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adaptergender);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username= name.getText().toString();
                if(!username.isEmpty()){
                    uploadFile();
                }
                else{
                    Toast.makeText(ProfileActivity.this, "Enter Your Name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(ProfileActivity.this);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            image_uri = CropImage.getPickImageResultUri(this,data);
            try {
                actualImage = FileUtil.from(this, image_uri);
                compressImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("CheckResult")
    public void compressImage() {
        new Compressor(ProfileActivity.this).compressToFileAsFlowable(actualImage).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<File>() {
            public void accept(File file) {
                compressedImage = file;
                profile.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
                filepath= Uri.fromFile(compressedImage);
                // Toast.makeText(ProfileActivity.this, "this", Toast.LENGTH_SHORT).show();
            }
        }, new Consumer<Throwable>() {
            public void accept(Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(ProfileActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // return compressedImage;
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
    }

    private void uploadFile() {
        if (this.filepath != null) {
            //this.pro.setVisibility(View.VISIBLE);
            progressDialog.show();
            StorageReference storageReference = this.mStorageRef;
            StringBuilder sb = new StringBuilder();
            sb.append(System.currentTimeMillis());
            sb.append(".");
            sb.append(getFileExtension(this.filepath));
            final StorageReference fileReference = storageReference.child(sb.toString());
            this.mUploadTask = fileReference.putFile(this.filepath).addOnSuccessListener((OnSuccessListener) new OnSuccessListener<UploadTask.TaskSnapshot>() {
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            //  ProfileActivity.this.mprogressbar.setProgress(0);
                        }
                    }, 500);
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        public void onSuccess(Uri uri) {
                            Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            gender=spinner.getSelectedItem().toString();
                            UserList userList = new UserList(username, currentUser, gender, uri.toString(),firebaseAuth.getCurrentUser().getEmail());

                            rootRef.child("Users").child(currentUser).setValue(userList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ProfileActivity.this.progressDialog.dismiss();
                                    startActivity(new Intent(ProfileActivity.this,HomeActivity.class));
                                    finish();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception e) {
                    Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    //  ProfileActivity.this.pro.setVisibility(View.GONE);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double bytesTransferred = (double) taskSnapshot.getBytesTransferred();
                    Double.isNaN(bytesTransferred);
                    double d = bytesTransferred * 100.0d;
                    double totalByteCount = (double) taskSnapshot.getTotalByteCount();
                    Double.isNaN(totalByteCount);
                }
            });

        }
        else{
            gender=spinner.getSelectedItem().toString();
            final UserList userList = new UserList(username, currentUser, gender,"null",firebaseAuth.getCurrentUser().getEmail());

            rootRef.child("Status").child(currentUser).setValue(userList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    rootRef.child("Users").child(currentUser).setValue(userList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(ProfileActivity.this,HomeActivity.class));
                            finish();
                        }
                    });
                }
            });

        }
    }
}