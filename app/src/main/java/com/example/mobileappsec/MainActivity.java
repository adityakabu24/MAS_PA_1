package com.example.mobileappsec;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText nameField, emailField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        nameField = findViewById(R.id.name);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        Button signupButton = findViewById(R.id.signupButton);
        Button loginButton = findViewById(R.id.loginButton);

        signupButton.setOnClickListener(v -> registerUser());
        loginButton.setOnClickListener(v -> loginUser());
    }

    private void registerUser() {
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUserProfile(user, name);
                    } else {
                        Toast.makeText(MainActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUserProfile(FirebaseUser user, String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Registration successful, welcome " + name + "!", Toast.LENGTH_SHORT).show();
                        clearFieldsAndHideKeyboard();
                        updateUI(user);
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to update profile: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void clearFieldsAndHideKeyboard() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(nameField.getWindowToken(), 0);
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
            String userEmail = user.getEmail();
            Toast.makeText(this, "Welcome, " + userEmail, Toast.LENGTH_LONG).show();

            // Assuming the user enters their name somewhere, pass it to HomeActivity
            String userName = user.getDisplayName(); // Replace this with actual user input
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("USER_NAME", userName); // Pass user's name
            startActivity(intent);
        }
    }
}
