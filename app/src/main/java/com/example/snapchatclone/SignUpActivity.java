package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    TextView editTextEmail,editTextPassword,editTextFullName,editTextLastName;
    public void logIn(){
        //move to another activity
        Intent goToSnap=new Intent(this,SnapHomeActivity.class);
        startActivity(goToSnap);


    }

    public void signUpClicked(View view){

        if(editTextEmail.getText().toString().equals("")||editTextFullName.getText().toString().equals("")||editTextLastName.getText().toString().equals("")||editTextPassword.getText().toString().equals(""))
        {
            Toast.makeText(this, "blank field", Toast.LENGTH_SHORT).show();
        }
        else {


            // SignUp
            mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Map map = new HashMap<>();
                                map.put("email", editTextEmail.getText().toString());
                                map.put("First Name", editTextFullName.getText().toString());
                                map.put("Last Name", editTextLastName.getText().toString());
                                FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).setValue(map);
                                Toast.makeText(SignUpActivity.this, "signup", Toast.LENGTH_SHORT).show();
                                logIn();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignUpActivity.this, "signup failed/Incorrect email", Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail=findViewById(R.id.editTextEmail);
        editTextFullName=findViewById(R.id.editTextFullName);
        editTextLastName=findViewById(R.id.editTextLastName);
        editTextPassword=findViewById(R.id.editTextPassword);




    }
}