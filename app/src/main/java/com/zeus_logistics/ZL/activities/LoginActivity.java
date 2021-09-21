package com.zeus_logistics.ZL.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.zeus_logistics.ZL.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText mEditTextEmail;
    private TextInputEditText mEditTextPass;
    private String mEmail;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_design);

        mAuth = FirebaseAuth.getInstance();
        // If the objects getCurrentUser method is not null
        // then user is already signed in

        // Quicker and worse way of checking whether there is a user signed in.
        // If Activity will be paused and them resumed with onResume(), then there will be no
        // onCreate() call, and there will be no check. So if user is not signed in at that moment
        if(mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        mEditTextEmail =  findViewById(R.id.email);
        mEditTextPass =  findViewById(R.id.password);
        Button mButtonLogin = (Button) findViewById(R.id.loginButton);
        TextView mTextViewNotYet = (TextView) findViewById(R.id.toSignUp);
        TextView mTextViewForgot = (TextView) findViewById(R.id.forgotPassword);
        mProgressDialog = new ProgressDialog(this);

        // Signing in Button's listener.
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        // If user doesn't have an account, go to RegisterActivity
        mTextViewNotYet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        // If user forgot his password, send a default e-mail with reset instructions.
        mTextViewForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });
    }

    private void showRecoverPasswordDialog() {
        //Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Recover Password");

        //set layout linear layout
        LinearLayout linearLayout = new LinearLayout(this);
        //Views to set in dialog
        EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        //set minimum text width
        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        //Button Recover Password
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Input email
                String email = emailEt.getText().toString().trim();
                //begin recovery
                beginRecovery(email);
            }
        });
        //Button cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dismiss dialog
                dialogInterface.dismiss();
            }
        });
        //show dialog
        builder.create().show();
    }

    private void beginRecovery(String email) {
        //show progress dialog
        mProgressDialog.setMessage("Sending email...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        // Show Toast based on whether password reset email was sent or not
                        if(task.isSuccessful()) {
//                            Toast.makeText(LoginActivity.this, "Email Sent" ,Toast.LENGTH_SHORT).show();
                            Toast toast = Toast.makeText(LoginActivity.this, "Email Sent", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
//                            Toast.makeText(LoginActivity.this, "Failed, retry.." ,Toast.LENGTH_SHORT).show();
                            Toast toast = Toast.makeText(LoginActivity.this, "Failed, retry..", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
                //get and show proper error message
                Toast.makeText(LoginActivity.this, ""+e.getMessage() ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void userLogin() {
        mEmail = mEditTextEmail.getText().toString().trim();
        String mPass = mEditTextPass.getText().toString().trim();
        if(TextUtils.isEmpty(mEmail)){
//            Toast.makeText(this,getString(R.string.login_hint_email), Toast.LENGTH_SHORT).show();
            Toast toast = Toast.makeText(LoginActivity.this, R.string.login_hint_email, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        if(TextUtils.isEmpty(mPass)){
//            Toast.makeText(this,getString(R.string.login_hint_password),Toast.LENGTH_SHORT).show();
            Toast toast = Toast.makeText(LoginActivity.this, R.string.login_hint_password, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        mProgressDialog.setMessage(getString(R.string.login_progressbar_login));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        //Signing the user in
        mAuth.signInWithEmailAndPassword(mEmail, mPass)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if(task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast toast = Toast.makeText(LoginActivity.this, "Failed... Retry", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });

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
                LoginActivity.super.onBackPressed();
            }
        });
        //Button cancel
        builder.setNegativeButton("No", null);
        //show dialog
        builder.create().show();
    }
}
