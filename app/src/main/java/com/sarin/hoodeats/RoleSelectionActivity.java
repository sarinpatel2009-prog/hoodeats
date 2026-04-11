package com.sarin.hoodeats;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectionActivity extends AppCompatActivity {

    private Button btnDonor, btnReceiver;
    private String selectedRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        btnDonor = findViewById(R.id.btnDonor);
        btnReceiver = findViewById(R.id.btnReceiver);

        btnDonor.setOnClickListener(v -> {
            selectedRole = "donor";
            proceedToRegistration();
        });

        btnReceiver.setOnClickListener(v -> {
            selectedRole = "receiver";
            proceedToRegistration();
        });
    }

    private void proceedToRegistration() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("userType", selectedRole);
        startActivity(intent);
        finish();
    }
}
