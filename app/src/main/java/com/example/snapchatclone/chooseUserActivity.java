package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class chooseUserActivity extends AppCompatActivity {

    ArrayList<String> emails = new ArrayList<String>();
    ArrayList<String> keys = new ArrayList<String>();
    ArrayList<String> names = new ArrayList<>();
    FirebaseAuth mAuth;
    Intent intent;
    final String[] Name = new String[1];
    private ArrayList<CustomItemSend> mModelList;
    private RecyclerView mRecyclerView;
    private CustomAdapterSend mAdapter;
    private RecyclerView.LayoutManager manager;

    public void sendButton(View view) {
        for (int i = 0; i < CustomAdapterSend.getSelected().size(); i++){

            Map map=new HashMap();
            map.put("from", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            map.put("fromName",Name[0]);
            map.put("imageName",intent.getStringExtra("imageName"));
            map.put("imageURL",intent.getStringExtra("imageLink"));
            map.put("message",intent.getStringExtra("message"));


            FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(CustomAdapterSend.getSelected().get(i).getPosition())).child("snaps").push().setValue(map);
        }

        Intent intent = new Intent(chooseUserActivity.this, SnapHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
/*
    public void getSelected(View view){
        if (CustomAdapterSend.getSelected().size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < CustomAdapterSend.getSelected().size(); i++) {
                stringBuilder.append(CustomAdapterSend.getSelected().get(i).getText());
                stringBuilder.append("\n");
            }
            Toast.makeText(this, stringBuilder.toString().trim(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No selection", Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);
        setTitle("Send To");
        intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        ///new added
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewChoose);
        mModelList=new ArrayList<>();
        mAdapter = new CustomAdapterSend(mModelList);
        manager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);


        //new ended


        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.getKey() != mAuth.getCurrentUser().getUid()) {
                    String email = snapshot.child("email").getValue().toString();
                    String name = snapshot.child("First Name").getValue().toString() + " " + snapshot.child("Last Name").getValue().toString();
                    emails.add(email);
                    names.add(name);
                    keys.add(snapshot.getKey());
                    mModelList.add(new CustomItemSend(name));
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Log.i(snapshot.child("First Name").getValue().toString() + " " + snapshot.child("Last Name").getValue().toString(), "name from sender");
                Name[0] = snapshot.child("First Name").getValue().toString() + " " + snapshot.child("Last Name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




       /*
                Map map=new HashMap();
                map.put("from", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                map.put("fromName",Name[0]);
                map.put("imageName",intent.getStringExtra("imageName"));
                map.put("imageURL",intent.getStringExtra("imageLink"));
                map.put("message",intent.getStringExtra("message"));


                FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(position)).child("snaps").push().setValue(map);

    */
    }
}