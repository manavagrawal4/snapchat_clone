package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SnapHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

     private DrawerLayout drawer;
    FirebaseAuth mAuth;
    ArrayList<DataSnapshot> snaps=new ArrayList<>();
    private ArrayList<CustomItem> customList;
    private RecyclerView mRecyclerView;
    TextView noSnapTextView,headerName,headerEmail;
    private CustomAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    GoogleSignInClient mGoogleSignInClient;
    String currentPhotoPath;
    boolean isOpen=false;
    private static final int REQUEST_IMAGE_CAPTURE =100 ;


    //for naming photo that is clicked
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //for camera activity
    public void getCameraPhoto(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(SnapHomeActivity.this, "com.example.snapchatclone.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //for selecting image
    public void getPhoto(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    //for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
        if(requestCode==2){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCameraPhoto();
            }

        }

    }


    public void buildRecyclerView(){
        mRecyclerView=findViewById(R.id.snapActivityRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager =new LinearLayoutManager(this);
        mAdapter=new CustomAdapter(customList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                DataSnapshot snapshot=snaps.get(position);
                Intent intent=new Intent(SnapHomeActivity.this,SnapViewActivity.class);
                intent.putExtra("imageName",snapshot.child("imageName").getValue().toString());
                intent.putExtra("imageURL",snapshot.child("imageURL").getValue().toString());
                intent.putExtra("message",snapshot.child("message").getValue().toString());
                intent.putExtra("snapKey",snapshot.getKey());
                startActivity(intent);

            }

        });

    }

    public void googleSignIn(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    public void fab(){
        final FloatingActionButton floatingActionButton1=findViewById(R.id.floatingActionButton1);
        final FloatingActionButton floatingActionButton2=findViewById(R.id.floatingActionButton2);
        final Animation fabOpen= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        final Animation fabClose=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        final Animation fabRC=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        final Animation fabRAC=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);
        final ExtendedFloatingActionButton fab = findViewById(R.id.floating_action_button);
        final TextView cameraTextView=findViewById(R.id.cameraTextView);
        final TextView galleryTextView=findViewById(R.id.galleryTextView);

        fab.extend();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen){
                    fab.extend();
                    floatingActionButton1.startAnimation(fabClose);
                    floatingActionButton2.startAnimation(fabClose);
                    cameraTextView.startAnimation(fabClose);
                    galleryTextView.startAnimation(fabClose);
                    //fab.startAnimation(fabRC);
                    floatingActionButton1.setClickable(false);
                    floatingActionButton2.setClickable(false);
                    isOpen=false;

                }
                else {

                    fab.shrink();
                    cameraTextView.startAnimation(fabOpen);
                    galleryTextView.startAnimation(fabOpen);
                    floatingActionButton1.startAnimation(fabOpen);
                    floatingActionButton2.startAnimation(fabOpen);
                    //fab.startAnimation(fabRAC);
                    floatingActionButton1.setClickable(true);
                    floatingActionButton2.setClickable(true);
                    isOpen=true ;
                }
            }
        });

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }else{
                    getPhoto();
                }
            }
        });

        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
                } else {

                    getCameraPhoto();

                }

            }
        });
    }
    public void emptyView(){
        //for empty view
        if(customList.size()==0){
            noSnapTextView.setVisibility(View.VISIBLE);
        }
        else {
            noSnapTextView.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_home);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer=findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        mAuth = FirebaseAuth.getInstance();
        setTitle("Snaps");
        googleSignIn();
        customList=new ArrayList<>();
        noSnapTextView=findViewById(R.id.noSnapTextView);
        headerEmail=headerView.findViewById(R.id.headerEmail);
        headerName=headerView.findViewById(R.id.headerName);
        headerEmail.setText(mAuth.getCurrentUser().getEmail());
        buildRecyclerView();
       FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
                 headerName.setText(snapshot.child("First Name").getValue().toString() + " " + snapshot.child("Last Name").getValue().toString());

       }

       @Override
       public void onCancelled(@NonNull DatabaseError error) {

            }
        });

                fab();
        emptyView();





        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                snaps.add(snapshot);
                customList.add(new CustomItem(snapshot.child("fromName").getValue().toString(),snapshot.child("from").getValue().toString()));
                mAdapter.notifyDataSetChanged();
                emptyView();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                for (int index = 0;index<snaps.size();++index) {
                    if (snaps.get(index).getKey().equals(snapshot.getKey())==true){
                        snaps.remove(index);
                        customList.remove(index);
                        index++;
                    }
                    mAdapter.notifyDataSetChanged();
                    emptyView();

                }
            }


            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK&&data!=null){
            Uri selectedImage=data.getData();

            try {
                Intent intent=new Intent(this,CreateSnapActivity.class);
                intent.putExtra("image",selectedImage.toString());
                intent.putExtra("key","gallery");
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
        {
            try {


                Intent intent=new Intent(this,CreateSnapActivity.class);
                intent.putExtra("image",currentPhotoPath);
                intent.putExtra("key","camera");
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_logout:
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}