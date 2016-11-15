package com.example.sushma.group57_inclass11;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class SignupActivity extends AppCompatActivity {

    private EditText etFname, etLname, etEmail, etPassword, etRepeat;
    private Button cancelButton, signupButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    //private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etFname = (EditText) findViewById(R.id.etFname);
        etLname = (EditText) findViewById(R.id.etLname);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etRepeat = (EditText) findViewById(R.id.etRepeatPass);

        cancelButton = (Button) findViewById(R.id.buttonCancel);
        signupButton = (Button) findViewById(R.id.buttonSignup);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUp();
            }
        });

    }

    private void onSignUp() {

        final String fname = etFname.getText().toString();
        final String lname = etLname.getText().toString();
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        String repeat = etRepeat.getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d("demo","Signup Successfull");
                    User user = new User();
                    user.setFirstName(fname);
                    user.setLastName(lname);
                    user.setPassword(password);
                    user.setEmail(email);
                    final String uid = mAuth.getCurrentUser().getUid();

                    mDatabase.child("users").child(uid).child("firstName").setValue(user.getFirstName());
                    mDatabase.child("users").child(uid).child("lastName").setValue(user.getLastName());
                    mDatabase.child("users").child(uid).child("password").setValue(user.getPassword());
                    mDatabase.child("users").child(uid).child("email").setValue(user.getEmail());

                    Intent intent = new Intent(SignupActivity.this,MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(SignupActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
