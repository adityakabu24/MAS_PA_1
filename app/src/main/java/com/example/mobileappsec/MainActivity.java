package com.example.mobileappsec;

import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // firebase auth instance
    private EditText emailField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.email);
        Button signupButton = findViewById(R.id.signupButton);
        passwordField = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);
        signupButton.setOnClickListener(v -> registerUser());
        loginButton.setOnClickListener(v -> loginUser());
    }

    // user registration method
    private void registerUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Registration successful, welcome!", Toast.LENGTH_SHORT).show();
                        // Hide the keyboard and clear input fields
                        clearFieldsAndHideKeyboard();
                        updateUI(user);
                    } else {
                        Toast.makeText(MainActivity.this, "Registration failed, try again: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        updateUI(null);
                    }
                });
    }

    // Method to hide the keyboard and clear the input fields
    private void clearFieldsAndHideKeyboard() {
        // Clear input fields
        emailField.setText("");
        passwordField.setText("");

        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(emailField.getWindowToken(), 0);
        }
    }

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        updateUI(user);
                    } else {
                        Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(this, "Welcome, " + user.getEmail(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please login or sign up", Toast.LENGTH_LONG).show();
        }
    }
}
