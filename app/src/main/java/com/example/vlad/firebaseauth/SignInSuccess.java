package com.example.vlad.firebaseauth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class SignInSuccess extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private Button mSignOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_success);
        fAuth = FirebaseAuth.getInstance();
        mSignOutBtn= (Button) findViewById(R.id.btnSignOut);
        mSignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });;

    }
    private void signOut() {

        fAuth.signOut();
        startActivity(new Intent(SignInSuccess.this
                ,MainActivity.class));
        finish();

    }

}
