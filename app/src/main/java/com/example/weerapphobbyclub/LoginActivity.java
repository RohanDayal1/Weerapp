package com.example.weerapphobbyclub;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private HashMap<String, String> userCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#522549"));
        }

        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.loginButton);

        userCredentials = RegistrationActivity.getUserCredentials();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vul alle velden in", Toast.LENGTH_SHORT).show();
                } else if (!userCredentials.containsKey(username) || !userCredentials.get(username).equals(password)) {
                    Toast.makeText(LoginActivity.this, "Onjuiste gebruikersnaam of wachtwoord", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, InfoActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
