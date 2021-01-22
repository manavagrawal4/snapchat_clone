package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class chooseUserActivity extends AppCompatActivity {

    ListView userListView=null;
    ArrayList<String> emails= new ArrayList<String>();
    ArrayList<String> keys= new ArrayList<String>();
    ArrayList<String> names=new ArrayList<>();
    FirebaseAuth mAuth;

    public void sendButton(View view){
        Intent intent= new Intent(chooseUserActivity.this,SnapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);
        setTitle("Send To");
        final Intent intent=getIntent();
        mAuth=FirebaseAuth.getInstance();
        userListView=findViewById(R.id.userListView);
        //final MyListView arrayAdapter=new MyListView(this,names,emails);
        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,names);

        userListView.setAdapter(arrayAdapter);
        userListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);



        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.getKey() != mAuth.getCurrentUser().getUid()) {
                    String email = snapshot.child("email").getValue().toString();
                    String name = snapshot.child("First Name").getValue().toString() + " " + snapshot.child("Last Name").getValue().toString();
                    emails.add(email);
                    names.add(name);
                    keys.add(snapshot.getKey());
                    arrayAdapter.notifyDataSetChanged();
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
        final String[] Name = new String[1];



        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.i(snapshot.child("First Name").getValue().toString()+" "+snapshot.child("Last Name").getValue().toString(),"name from sender");
                Name[0]=snapshot.child("First Name").getValue().toString()+" "+snapshot.child("Last Name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {




                Map map=new HashMap();
                map.put("from", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                map.put("fromName",Name[0]);
                map.put("imageName",intent.getStringExtra("imageName"));
                map.put("imageURL",intent.getStringExtra("imageLink"));
                map.put("message",intent.getStringExtra("message"));


                FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(position)).child("snaps").push().setValue(map);



            }
        });
    }
}