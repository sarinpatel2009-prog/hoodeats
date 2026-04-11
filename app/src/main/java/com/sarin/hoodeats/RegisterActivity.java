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
    private String userType = "receiver"; // DEFAULT - ADD THIS LINE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userService = new UserService();

        // GET USER TYPE FROM INTENT - ADD THESE 3 LINES
        if (getIntent().hasExtra("userType")) {
            userType = getIntent().getStringExtra("userType");
        }

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

        // PASS userType TO User CONSTRUCTOR - CHANGE THIS LINE
        User newUser = new User("", name, email, flat, block, contact, userType);

        userService.registerUser(email, password, newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        String errorMessage = "Registration failed";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Register");
                    }
                });
    }
}