package com.example.srimanth.moviemagic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final String LIFECYCLE_CALLBACKS_EMAIL_KEY = "emailCallback";
    private static final String LIFECYCLE_CALLBACKS_PASSWORD_KEY = "passwordCallback";
    TextView mErrorTextView;
    private EditText mEditTextEmail, mEditTextPassword;
    private TextInputLayout mLayoutEmail, mLayoutPassword;
    // private Button  mButtonLogin,mButtonSignUp;
    private ProgressBar mProgressBar;


    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLayoutEmail = findViewById(R.id.layoutEmail);
        mLayoutPassword = findViewById(R.id.layoutPassword);

        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextPassword = findViewById(R.id.editTextPassword);

        mErrorTextView = findViewById(R.id.textViewError);

        mProgressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        if (savedInstanceState != null)
        {
            if (savedInstanceState.containsKey(LIFECYCLE_CALLBACKS_EMAIL_KEY))
            {
                String storedEmailString = savedInstanceState.getString(LIFECYCLE_CALLBACKS_EMAIL_KEY);
                mEditTextEmail.setText(storedEmailString);
            }
            if (savedInstanceState.containsKey(LIFECYCLE_CALLBACKS_PASSWORD_KEY))
            {
                String storedPasswordString = savedInstanceState.getString(LIFECYCLE_CALLBACKS_PASSWORD_KEY);
                mEditTextPassword.setText(storedPasswordString);
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        String email = mEditTextEmail.getText().toString();
        String password = mEditTextPassword.getText().toString();
        outState.putString(LIFECYCLE_CALLBACKS_EMAIL_KEY,email);
        outState.putString(LIFECYCLE_CALLBACKS_PASSWORD_KEY,password);

    }

    public void loginClicked(View view) {

        mProgressBar.setVisibility(View.VISIBLE);
        final String email = mEditTextEmail.getText().toString().trim();
        final String password = mEditTextPassword.getText().toString().trim();
        final int clickedButton = view.getId();
        if (clickedButton == R.id.buttonLogin &&
                checkForEmailAndPassword(email, password)) {

                logIn(email, password);

        }
        if (clickedButton == R.id.buttonSignUp &&
                checkForEmailAndPassword(email, password)) {


                //Toast.makeText(LoginActivity.this,"User already exists", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_email)
                        .setTitle("Are tou sure you want to Sign Up ?")
                        .setMessage("By signing up, you agree to the Terms and Conditions of the app.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signUp(email, password);
                            }
                        })
                        .setNegativeButton("No",null)
                        .setNeutralButton("T&C", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(LoginActivity.this, "Terms and conditions", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();

        }
        mProgressBar.setVisibility(View.GONE);
    }


    private void logIn(String email, String password) {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                showError("");
                                //FirebaseUser user = mAuth.getCurrentUser();
                                //move to next activity
                                toMovies();

                            } else {

                                Log.w(TAG, "signInWithEmail:failure", task.getException());

                                firebaseErrors(task);

                                Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }

    private void signUp(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Log.i("SignUp","Sign Up successful");

                            //move to next activity
                            toMovies();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            firebaseErrors(task);

                            Toast.makeText(LoginActivity.this,
                                    "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean checkForEmailAndPassword(String email,String password) {
        if (email.isEmpty())
        {
            mLayoutEmail.setErrorEnabled(true);
            mLayoutEmail.setError("This field is mandatory");

            Log.i("Login","No email found");
            return false;
        }
        if (password.isEmpty())
        {
            mLayoutPassword.setErrorEnabled(true);
            mLayoutPassword.setError("This field is mandatory");

            Log.i("Login","No password found");
            return false;
        }
        if (!isNetworkAvailable())
        {
            showError("Could not connect to the internet");
            return false;
        }
        return true;
    }

    private void showError(String errorMessage) {
        mErrorTextView.setText(errorMessage);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void firebaseErrors(Task<AuthResult> task)
    {


        try {

            throw Objects.requireNonNull(task.getException());

        } catch(FirebaseAuthWeakPasswordException e) {

            showError("The given password should contain a minimum of six characters.");

        } catch(Exception e) {

            Log.e("SignUp", e.getMessage());

        }

        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

        switch (errorCode) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":

                showError("The custom token corresponds to a different audience.");
                break;

            case "ERROR_INVALID_CREDENTIAL":

                showError("The supplied auth credential is malformed or has expired.");
                break;

            case "ERROR_INVALID_EMAIL":

                showError("The email address is badly formatted.");
                mLayoutEmail.setError("The email address is badly formatted.");
                mEditTextEmail.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":

                showError("The password is invalid or the user does not have a password.");
                mEditTextPassword.requestFocus();
                mEditTextPassword.setText("");
                break;

            case "ERROR_USER_MISMATCH":

                showError("The supplied credentials do not correspond to the previously signed in user.");
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":

                showError("This operation is sensitive and requires recent authentication. Log in again before retrying this request.");
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":

                showError("An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.");
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":

                showError("The email address is already in use by another account.");
                mEditTextEmail.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":

                showError("This credential is already associated with a different user account.");
                break;

            case "ERROR_USER_DISABLED":

                showError("The user account has been disabled by an administrator.");
                break;

            case "ERROR_USER_TOKEN_EXPIRED":

                showError("The user\\'s credential is no longer valid. The user must sign in again.");
                break;

            case "ERROR_USER_NOT_FOUND":

                showError("There is no user record corresponding to this identifier. The user may have been deleted.");
                break;

            case "ERROR_INVALID_USER_TOKEN":

                showError("The user\\'s credential is no longer valid. The user must sign in again.");
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":

                showError("This operation is not allowed. You must enable this service in the console.");
                break;

            case "ERROR_WEAK_PASSWORD":

                showError("The given password should contain a minimum of six characters.");
                mEditTextPassword.requestFocus();
                break;



        }
    }

    private void toMovies()
    {
        Intent intent = new Intent(this,MovieActivity.class);
        startActivity(intent);
    }

}
