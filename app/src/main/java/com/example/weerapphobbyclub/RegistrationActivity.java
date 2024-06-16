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

public class RegistrationActivity extends AppCompatActivity {

    private EditText etRegisterUsername;
    private EditText etRegisterPassword;
    private Button btnRegister;
    private static HashMap<String, String> userCredentials = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#522549"));
        }

        etRegisterUsername = findViewById(R.id.registerUsername);
        etRegisterPassword = findViewById(R.id.registerPassword);
        btnRegister = findViewById(R.id.registerButton);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etRegisterUsername.getText().toString();
                String password = etRegisterPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Vul alle velden in", Toast.LENGTH_SHORT).show();
                } else if (userCredentials.containsKey(username)) {
                    Toast.makeText(RegistrationActivity.this, "Gebruikersnaam is al geregistreerd", Toast.LENGTH_SHORT).show();
                } else {
                    userCredentials.put(username, password);
                    Toast.makeText(RegistrationActivity.this, "Registratie succesvol", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public static HashMap<String, String> getUserCredentials() {
        return userCredentials;
    }
}
