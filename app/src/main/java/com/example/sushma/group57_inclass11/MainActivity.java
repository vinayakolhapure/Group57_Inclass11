package com.example.sushma.group57_inclass11;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText email, password;
    Button login, signup;
    private FirebaseAuth mAuth;
    String email1, pwd1;
    User user;
    public static String TAG = "Main Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPwd);
        login = (Button) findViewById(R.id.buttonLogin);
        signup = (Button) findViewById(R.id.buttonSignUp);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
//            Log.d("Firebase user",firebaseUser.getPhotoUrl()+"");
            Log.d(TAG, "In firebase user");
            Intent chatIntent = new Intent(this, ChatActivity.class);
            startActivity(chatIntent);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email1 = email.getText().toString();
                pwd1 = password.getText().toString();
                if (!validateForm()) {
                    return;
                }
                Log.d(TAG, "In login");
                mAuth.signInWithEmailAndPassword(email1,pwd1)
                        .addOnCompleteListener(MainActivity.this,new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
                                    startActivity(chatIntent);
                                } else {
                                    Toast.makeText(getBaseContext(), "Authentication Failed",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = this.email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            this.email.setError("Required.");
            valid = false;
        } else {
            this.email.setError(null);
        }

        String password = this.password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            this.password.setError("Required.");
            valid = false;
        } else {
            this.password.setError(null);
        }

        return valid;
    }
}
