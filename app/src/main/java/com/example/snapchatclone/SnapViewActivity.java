package com.example.snapchatclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SnapViewActivity extends AppCompatActivity {


    TextView messageTextView;
    ImageView snapImageView;
    Intent intent;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_snap_view);

        messageTextView=findViewById(R.id.messageTextView);
        snapImageView=findViewById(R.id.snapImageView);
        intent=getIntent();
        mAuth = FirebaseAuth.getInstance();

        messageTextView.setText(intent.getStringExtra("message"));
        downloadImage();

    }

    public void downloadImage() {


        downloadTask task = new downloadTask();
        Bitmap myImage;
        try {
            myImage = task.execute(intent.getStringExtra("imageURL")).get();
            snapImageView.setImageBitmap(myImage);
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    public class downloadTask extends AsyncTask<String ,Integer, Bitmap>
    {




        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url;
            try {
                url=new URL(urls[0]);
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in=urlConnection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(in);
                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }




        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").child(intent.getStringExtra("snapKey")).removeValue();
        finish();
        //FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName")).delete();
    }
}