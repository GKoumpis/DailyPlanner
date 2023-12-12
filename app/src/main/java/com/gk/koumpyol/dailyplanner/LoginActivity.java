package com.gk.koumpyol.dailyplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText uName, pWord, confirmPWord;
    Button login;
    CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uName = findViewById(R.id.username);
        pWord = findViewById(R.id.pword);
        confirmPWord = findViewById(R.id.confirmPword);
        login = findViewById(R.id.entry);
        rememberMe = findViewById(R.id.rememberMe);

        boolean remMeStatus = isRememberMeEnabled();

        if (remMeStatus)
        {
            rememberMe.setChecked(true);
            autoLogin();
            return;
        }

        boolean firstUse = isFirstUse();

        if (firstUse)
        {
            confirmPWord.setVisibility(View.VISIBLE);
            login.setText("Sign up");

            login.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String username = uName.getText().toString();
                    String password = pWord.getText().toString();
                    String passWordConfirm = confirmPWord.getText().toString();

                    boolean remMe = rememberMe.isChecked();

                    if (password.equals(passWordConfirm) && !(username.isEmpty()))
                    {
                        saveCredentials(username, password);

                        if (remMe)
                        {
                            saveRememberMeStatus(true);
                        }
                        else
                        {
                            saveRememberMeStatus(false);
                        }

                        Intent enterAppIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(enterAppIntent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "The passwords don't match, or username is empty.", Toast.LENGTH_SHORT).show();
                    }

                    // Check if Remember Me is enabled
                }
            });
        }
        else
        {
            confirmPWord.setVisibility(View.GONE);
            login.setText("Log In");

            login.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String username = uName.getText().toString();
                    String password = pWord.getText().toString();

                    boolean remMe = rememberMe.isChecked();
                    boolean isLoginSuccessful = checkCredentials(username, password);

                    if (isLoginSuccessful)
                    {
                        if (remMe)
                        {
                            saveRememberMeStatus(true);
                        }
                        else
                        {
                            saveRememberMeStatus(false);
                        }

                        Intent enterAppIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(enterAppIntent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Wrong username or password.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public boolean isFirstUse()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", MODE_PRIVATE);

        return (!sharedPreferences.contains("username") || !sharedPreferences.contains("password"));
    }

    private void saveCredentials(String username, String password)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    private boolean checkCredentials(String username, String password)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", MODE_PRIVATE);

        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");

        return (username.equals(savedUsername) && password.equals(savedPassword));
    }

    private void saveRememberMeStatus(boolean rememberMe)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("remember_me", rememberMe);
        editor.apply();
    }

    private boolean isRememberMeEnabled()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", MODE_PRIVATE);

        boolean enabled = sharedPreferences.getBoolean("remember_me" ,false);

        return enabled;
    }

    private void autoLogin()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");

        boolean loginSuccessful = checkCredentials(savedUsername, savedPassword);

        if (loginSuccessful)
        {
            Intent enterAppIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(enterAppIntent);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Outdated credentials, sorry!", Toast.LENGTH_SHORT).show();
        }
    }
}