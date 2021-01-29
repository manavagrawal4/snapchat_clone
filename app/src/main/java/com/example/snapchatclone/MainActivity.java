package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 120;
    TextView email;
    TextView password;
    FirebaseAuth mAuth;
    private SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    private String TAG = "MainActivity";


    public void signUpButton(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }


    public void signClicked(View view) {
        //check if we can log in
       if(email.getText().toString().equals("")||password.getText().toString().equals("")) {
           Toast.makeText(this, "Invalid email/password", Toast.LENGTH_SHORT).show();

       }else{

           mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                   .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful()) {
                               // Sign in success, update UI with the signed-in user's information
                               logIn();
                           } else {

                               Toast.makeText(MainActivity.this, "Invalid email/password", Toast.LENGTH_SHORT).show();
                           }

                       }
                   });
       }

    }
    public void resetPass(View view){
        Intent intent=new Intent(this,PasswordResetActivity.class);
        startActivity(intent);
    }


    //for google sign in button
    public void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }



    //for login
    public void logIn() {
        //move to another activity
        Intent goToSnap = new Intent(this, SnapHomeActivity.class);
        startActivity(goToSnap);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Snapchat");
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        signInButton = findViewById(R.id.sign_in_button);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();



        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });


        /*if (mAuth.getCurrentUser() != null) {
            logIn();

        }
         */

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN&&resultCode== Activity.RESULT_OK) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            FirebaseGoogleAuth(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            FirebaseGoogleAuth(null);

        }
    }

    private  void FirebaseGoogleAuth(GoogleSignInAccount account){
        AuthCredential authCredential= GoogleAuthProvider.getCredential((account.getIdToken()),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Map map=new HashMap<>();
                    GoogleSignInAccount account1=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                    if(account1!=null){
                        map.put("email",account1.getEmail());
                        String[] Name= account1.getDisplayName().split(" ");
                        map.put("First Name",Name[0]);
                        map.put("Last Name",Name[1]);
                        FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).updateChildren(map);
                    }
                    //Toast.makeText(MainActivity.this, "signup", Toast.LENGTH_SHORT).show();
                    logIn();

                }else {
                    Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}