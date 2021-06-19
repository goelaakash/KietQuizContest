package com.aakash.quizapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class EditProfileActivity extends AppCompatActivity {

    private Button btn;
    private EditText name;
    private EditText email;
    private Spinner spinner;
    private String gender,username;
    private FirebaseAuth firebaseAuth;
    private static final int PERMISSION_CODE = 1000;

    private StorageReference mStorageRef;
    public StorageTask mUploadTask;
    private Uri image_uri;

    private DatabaseReference rootRef;
    private FloatingActionButton fab;
    private ImageView profile;

    private Uri filepath;
    private File actualImage,compressedImage;
    private String currentUser;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        btn=findViewById(R.id.btn_submit);
        fab=findViewById(R.id.choose);
        profile=findViewById(R.id.top_profile);
        name=findViewById(R.id.et_name);
        email=findViewById(R.id.et_profileemail);
        spinner=findViewById(R.id.et_gender);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = this.firebaseAuth.getCurrentUser().getUid();

        this.mStorageRef = FirebaseStorage.getInstance().getReference("Users").child(currentUser);

        retrievedata();


        rootRef = FirebaseDatabase.getInstance().getReference();
        progressDialog=new ProgressDialog(EditProfileActivity.this);
        progressDialog.setMessage("Uploading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        email.setText(firebaseAuth.getCurrentUser().getEmail());


        ArrayAdapter<CharSequence> adaptergender = ArrayAdapter.createFromResource(EditProfileActivity.this,
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
                    Toast.makeText(EditProfileActivity.this, "Enter Your Name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            }
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                @SuppressLint("IntentReset")
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, " New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
                image_uri = EditProfileActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);

                Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {getIntent,cameraIntent});

                startActivityForResult(chooserIntent, 1);
            }
        });

    }

    private void retrievedata() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserList userList=dataSnapshot.getValue(UserList.class);
                if(userList.getName()!=null){
                    name.setText(userList.getName());
                }
                if(!userList.getImage().equals("null")){
                    if(EditProfileActivity.this!=null) {
                        Picasso.with(EditProfileActivity.this).load(userList.getImage()).fit()
                                .centerCrop()
                                .placeholder(R.drawable.ic_menu_camera).into(profile);
                    }

                }
                if(userList.getGender()!=null){
                    if(userList.getGender().equals("Male")){
                        spinner.setSelection(0);
                    }
                    if(userList.getGender().equals("Female")){
                        spinner.setSelection(1);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1 ) {
            if(data == null){
                filepath=image_uri;
                try {
                    actualImage = FileUtil.from(EditProfileActivity.this, filepath);
                    compressImage();
                } catch (IOException e) {
                    Toast.makeText(EditProfileActivity.this, "Failed to read picture!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else {
                filepath = data.getData();
                try {
                    actualImage = FileUtil.from(EditProfileActivity.this, data.getData());
                    compressImage();
                } catch (IOException e) {
                    Toast.makeText(EditProfileActivity.this, "Failed to read picture data!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    public void compressImage() {
        new Compressor(EditProfileActivity.this).compressToFileAsFlowable(actualImage).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<File>() {
            public void accept(File file) {
                compressedImage = file;
                profile.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
                filepath = Uri.fromFile(compressedImage);
                // Toast.makeText(ProfileActivity.this, "this", Toast.LENGTH_SHORT).show();
            }
        }, new Consumer<Throwable>() {
            public void accept(Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(EditProfileActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // return compressedImage;
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(EditProfileActivity.this.getContentResolver().getType(uri));
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
                            Toast.makeText(EditProfileActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            gender=spinner.getSelectedItem().toString();
                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("gender",gender);
                            hashMap.put("image",uri.toString());
                            hashMap.put("name",username);
                            hashMap.put("emailid",firebaseAuth.getCurrentUser().getEmail());
                            hashMap.put("userID",currentUser);
                            rootRef.child("Users").child(currentUser).setValue(hashMap);
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Details Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception e) {
                    Toast.makeText(EditProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double bytesTransferred = (double) taskSnapshot.getBytesTransferred();
                    Double.isNaN(bytesTransferred);
                    double d = bytesTransferred * 100.0d;
                    double totalByteCount = (double) taskSnapshot.getTotalByteCount();
                    Double.isNaN(totalByteCount);
                    //  ProfileActivity.this.mprogressbar.setProgress((int) (d / totalByteCount));
                }
            });

        }
        else{
            gender=spinner.getSelectedItem().toString();
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("gender",gender);
            hashMap.put("name",username);
            hashMap.put("userID",currentUser);
            hashMap.put("emailid",firebaseAuth.getCurrentUser().getEmail());
            rootRef.child("Users").child(currentUser).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(EditProfileActivity.this, "Details Updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                else {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
