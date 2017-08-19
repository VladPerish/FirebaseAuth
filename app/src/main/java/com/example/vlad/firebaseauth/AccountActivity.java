package com.example.vlad.firebaseauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends Activity {

    private EditText mAge;
    private EditText mBloodType;
    private Button msubmit;
    private Button mBack;

    private DatabaseReference mDatabase;
    private  String TAG ="NAB";
    private static final String REQUIRED = "Required";



    FirebaseAuth fAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAge=(EditText) findViewById(R.id.ETage);
        mBloodType=(EditText) findViewById(R.id.ETbt);
        msubmit=(Button) findViewById(R.id.btSubmit);
        mBack=(Button) findViewById(R.id.btBack);

        msubmit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            submitData();
        }
    });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }
    private void signOut(){
        fAuth.signOut();
        startActivity(new Intent(AccountActivity.this, MainActivity.class));

    }
    private void submitData(){


        final String age= mAge.getText().toString();
        final String type= mBloodType.getText().toString();

        if (TextUtils.isEmpty(age)) {
            mAge.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(type)) {
            mBloodType.setError(REQUIRED);
            return;
        }

        final String userId =fAuth.getCurrentUser().getUid();
        setEditingEnabled(false);
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (userId == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(AccountActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, age, type);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });

    }
    private void setEditingEnabled(boolean enabled) {
        mAge.setEnabled(enabled);
        mBloodType.setEnabled(enabled);
        if (enabled) {
            msubmit.setVisibility(View.VISIBLE);
        } else {
            msubmit.setVisibility(View.GONE);
        }
    }
    private void writeNewPost(String userId,  String age, String type) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, age, type);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/profiles/" + key, postValues);
        childUpdates.put("/user-profiles/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }





}

