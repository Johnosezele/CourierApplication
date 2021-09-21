package com.zeus_logistics.ZL.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeus_logistics.ZL.R;
import com.zeus_logistics.ZL.items.User;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextInputEditText mEditTextEmail;
    private TextInputEditText mEditTextPassword;
    private TextInputEditText mEditTextPhone;
    private TextInputEditText mEditTextName;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_design_signup);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    // User is signed in
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                } else {
                    // User is signed out
                }
            }
        };

        mEditTextEmail =  findViewById(R.id.userEmail);
        mEditTextPassword =  findViewById(R.id.userPassword);
        mEditTextPhone =  findViewById(R.id.userPhone);
        mEditTextName =  findViewById(R.id.username);
        TextView mTextViewSignIn = (TextView) findViewById(R.id.toSignIn);
        Button mSignUpButton = (Button) findViewById(R.id.newUserReg);
        mProgressDialog = new ProgressDialog(this);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        mTextViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    private void registerUser() {
        final String mEmail = mEditTextEmail.getText().toString().trim();
        final String mPhone = mEditTextPhone.getText().toString().trim();
        final String mName = mEditTextName.getText().toString().trim();
        String mPass = mEditTextPassword.getText().toString().trim();
        if(TextUtils.isEmpty(mEmail)){
            Toast.makeText(this,getString(R.string.login_hint_email), Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(mPass)){
            Toast.makeText(this,getString(R.string.login_hint_password),Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(mPhone) || mPhone.length()<9){
            Toast.makeText(this,getString(R.string.hint_phone),Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mName)){
            Toast.makeText(this,getString(R.string.hint_name),Toast.LENGTH_SHORT).show();
            return;
        }
        // If the email and password are not empty
        // display progress dialog
        mProgressDialog.setMessage(getString(R.string.login_progressbar_register));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        // Create a new User
        mAuth.createUserWithEmailAndPassword(mEmail, mPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                            mDatabaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User mUser = new User();
                                            mUser.setName(mName);
                                            mUser.setEmail(mEmail);
                                            mUser.setRole("customer");
                                            mUser.setPhone(mPhone);
                                            mUser.setUid(mAuth.getCurrentUser().getUid());
                                            mDatabaseReference.child("users").child(
                                                    mAuth.getCurrentUser().getUid())
                                                    .setValue(mUser);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        mProgressDialog.dismiss();
                        if(!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                    mEditTextPassword.setError(getString(R.string.login_weak_password));
                                    mEditTextPassword.requestFocus();
                            } catch(Exception e) {
                                if(e.toString().contains("WEAK_PASSWORD")) {
                                    mEditTextPassword.setError(getString(R.string.login_weak_password));
                                    mEditTextPassword.requestFocus();
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseAuthWeakPasswordException) {
                            mEditTextPassword.setError(getString(R.string.login_weak_password));
                            mEditTextPassword.requestFocus();
                        } else if(e instanceof FirebaseAuthInvalidCredentialsException) {
                            mEditTextEmail.setError(getString(R.string.login_bad_email));
                            mEditTextEmail.requestFocus();
                        } else if(e instanceof FirebaseAuthUserCollisionException) {
                            mEditTextEmail.setError(getString(R.string.login_user_exists));
                            mEditTextEmail.requestFocus();
                        }
                        mProgressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    private void showExitDialog() {
        //Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Exit");
        builder.setMessage("Are You Sure You Want To Exit");

        //Button Recover Password
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RegisterActivity.super.onBackPressed();
            }
        });
        //Button cancel
        builder.setNegativeButton("No", null);
        //show dialog
        builder.create().show();
    }
}
