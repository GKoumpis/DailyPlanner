package com.gk.koumpyol.dailyplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangeCredentialsActivity extends AppCompatActivity {

    EditText exUsername, exPassword, newUsername, newPassword, confNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_credentials);

        exUsername = findViewById(R.id.exUsername);
        exPassword = findViewById(R.id.exPWord);
        newUsername = findViewById(R.id.newUsername);
        newPassword = findViewById(R.id.newPWord);
        confNewPassword = findViewById(R.id.confirmNewPWord);
    }

    public void saveChanges(View view)
    {
        String oldUsername = exUsername.getText().toString();
        String oldPassword = exPassword.getText().toString();
        String username = newUsername.getText().toString();
        String password = newPassword.getText().toString();
        String passWordConfirm = confNewPassword.getText().toString();

        boolean isOldCredsCorrect = checkExistingCreds(oldUsername, oldPassword);

        if (isOldCredsCorrect && password.equals(passWordConfirm) && !(username.isEmpty()))
        {
            saveCredentials(username, password);
            resetRememberMe();
            finish();
        }
        else
        {
            if (isOldCredsCorrect)
            {
                Toast.makeText(getApplicationContext(), "Wrong existing username or password", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "The new passwords don't match, or new username is empty.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveCredentials(String username, String password)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    private boolean checkExistingCreds(String name, String pWord)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", MODE_PRIVATE);

        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");

        return (name.equals(savedUsername) && pWord.equals(savedPassword));
    }

    private void resetRememberMe()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("remember_me", false);
        editor.apply();
    }
}