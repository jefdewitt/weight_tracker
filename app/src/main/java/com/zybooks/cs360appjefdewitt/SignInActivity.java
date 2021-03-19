package com.zybooks.cs360appjefdewitt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SignInActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "Account ID";
    Intent intent;
    private EditText usernameText;
    private EditText passwordText;
    private View signInButton;
    private View signUpButton;
    private WeightEntryDatabase mWeightEntryDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Clear db/start from scratch
//        Context context = getApplicationContext();
//        context.deleteDatabase("weights.db");

        mWeightEntryDb = WeightEntryDatabase.getInstance(getApplicationContext());

        usernameText = findViewById(R.id.username_input);
        passwordText = findViewById(R.id.password_input);
        signInButton = findViewById(R.id.button_sign_in);
        signUpButton = findViewById(R.id.button_sign_up);
        signInButton.setEnabled(false);
        signInButton.setAlpha(.66f);

        /**
         * Learned this technique via
         * https://stackoverflow.com/questions/22680106/how-to-disable-button-if-edittext-is-empty
         */
        passwordText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if (s.toString().equals("")) {
                    // If password EditText is empty keep button disabled
                    signInButton.setEnabled(false);
                } else {
                    // If password && username EditTexts both have values
                    if (!usernameText.getText().toString().equals("")) {
                        signInButton.setEnabled(true);
                        signInButton.setAlpha(1f);
                    }
                }
            }

            // Auto generated stub
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            // Auto generated stub
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /**
         * Check for existing account
         */
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence username = usernameText.getText();
                CharSequence password = passwordText.getText();

                Account account = mWeightEntryDb.getAccount(username, password);
                if (account.getUsername() == null) {
                    showUsernameNotFoundMessage();
                } else {
                    sendAccountInfoMessage(account.getId());
                }
            }
        });

        /**
         * Sign up for new account
         */
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence username = usernameText.getText();
                CharSequence password = passwordText.getText();

                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {

                    boolean usernameExists = mWeightEntryDb.usernameCheck(username);

                    if (usernameExists) {
                        usernameAlreadyExists();
                    } else {
                        long accountId = mWeightEntryDb.addAccount(username, password);
                            sendAccountInfoMessage(accountId);
                    }
                }
            }
        });
    }

    /**
     * If username doesn't exist, instruct user what to sign up
     */
    private void showUsernameNotFoundMessage() {
        new AlertDialog.Builder(SignInActivity.this)
                .setTitle("Username not found")
                .setMessage("Please try again or sign up for a new account")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    /**
     * If username already exists on Sign Up click, instruct user what to sign up
     */
    private void usernameAlreadyExists() {
        new AlertDialog.Builder(SignInActivity.this)
                .setTitle("Username already exists")
                .setMessage("Please try again")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }



    /** Called when the user taps the 'Sign In' button */
    private void sendAccountInfoMessage(long accountId) {
        clearFields();
        intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, String.valueOf(accountId));
        startActivity(intent);
    }

    private void clearFields() {
        usernameText.setText("");
        passwordText.setText("");
    }
}