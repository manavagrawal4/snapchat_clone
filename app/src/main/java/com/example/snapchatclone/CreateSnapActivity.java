package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CreateSnapActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE =100 ;
    ImageView imageView=null;
    TextView messageTextView=null;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_create_snap);
        setTitle("Create Snap");
        imageView=findViewById(R.id.imageView);
        messageTextView=findViewById(R.id.messageBar);
        progressBar=findViewById(R.id.progressBar);
        Intent intent=getIntent();
        Uri myUri = Uri.parse(intent.getStringExtra("image"));
        String key=intent.getStringExtra("key");
        if(key.equals("gallery")) {

            try {
                imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), myUri));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(key.equals("camera")) {
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeFile(intent.getStringExtra("image"));
            imageView.setImageBitmap(bitmap);
        }

    }

    String imageName,imageLink="";

    //for uploading the image in firebase
    public void nextClicked(View view){
        // Get the data from an ImageView as bytes

        if(imageView.getDrawable()!=null){
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] data = baos.toByteArray();
        imageName=UUID.randomUUID().toString()+".jpg";
        progressBar.setVisibility(View.VISIBLE);



        //for progress bar
        final UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                int currentprogress = (int) progress;
                Log.i("percentage",Integer.toString(currentprogress));
                progressBar.setProgress(currentprogress);
            }
        });

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(CreateSnapActivity.this, "upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                FirebaseStorage.getInstance().getReference().child("images").child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i("imageLink",uri.toString());
                        imageLink=uri.toString();
                        Log.i("linkInString",imageLink);
                        //Toast.makeText(CreateSnapActivity.this, "uploaded successfully", Toast.LENGTH_SHORT).show();
                        Intent chooseUser=new Intent(CreateSnapActivity.this,chooseUserActivity.class);
                        Log.i("imageLink just before puttting",imageLink);
                        chooseUser.putExtra("imageName",imageName);
                        chooseUser.putExtra("imageLink",imageLink);
                        chooseUser.putExtra("message",messageTextView.getText().toString());
                        startActivity(chooseUser);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("upload LinkFailed","sorry");
                    }
                });


            }
        });
        }else {
            Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
        }


    }
}