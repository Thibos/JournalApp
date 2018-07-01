package com.example.tmahlangu3.journalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private Button createAccBtn;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        firstName = (EditText)findViewById(R.id.firstNameAct);
        lastName = (EditText)findViewById(R.id.lastNameAct);
        email =(EditText)findViewById(R.id.emailAct);
        password = (EditText)findViewById(R.id.passwordAct);
        createAccBtn = (Button)findViewById(R.id.createAccBtnAct);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MJournalUsers");
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        final String fName= firstName.getText().toString().trim();
        final String lName = lastName.getText().toString().trim();
        final String pwd = password.getText().toString().trim();
       final String emailAddress = email.getText().toString().trim();
        if(!TextUtils.isEmpty(fName) && !TextUtils.isEmpty(lName) && !TextUtils.isEmpty(pwd) &&
                !TextUtils.isEmpty(emailAddress)){
            mProgress.setMessage("Creating Account..");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(emailAddress,pwd)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if(authResult !=null){
                                String userId = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentDBUser = mDatabaseReference.child(userId);
                                currentDBUser.child("firstname").setValue(fName);
                                currentDBUser.child("lastname").setValue(lName);
                                currentDBUser.child("image").setValue("none");
                                mProgress.dismiss();
                                //open user postList Activity
                                Intent intent = new Intent(CreateAccountActivity.this,NotesListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    });

        }

    }
}
