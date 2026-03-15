package com.sarin.hoodeats;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.sarin.hoodeats.models.User;
import com.sarin.hoodeats.services.UserService;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etFlat, etBlock, etContact;
    private Button btnRegister;
    private TextView tvLogin;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userService = new UserService();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etFlat = findViewById(R.id.etFlat);
        etBlock = findViewById(R.id.etBlock);
        etContact = findViewById(R.id.etContact);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String flat = etFlat.getText().toString().trim();
        String block = etBlock.getText().toString().trim();
        String contact = etContact.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                flat.isEmpty() || block.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Creating account...");

        User newUser = new User("", name, email, flat, block, contact);

        userService.registerUser(email, password, newUser)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Register");
                });
    }
}