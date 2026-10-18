package com.example.hostel_application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText sapInput = findViewById(R.id.sapInput);
        EditText emailInput = findViewById(R.id.wardenEmailInput);
        Button studentButton = findViewById(R.id.studentLoginButton);
        Button wardenButton = findViewById(R.id.wardenLoginButton);

        DBHelper db = new DBHelper(this);

        studentButton.setOnClickListener(v -> {
            String sap = sapInput.getText().toString().trim();

            if (!sap.matches("\\d{11}")) {
                sapInput.setError("Enter your 11-digit SAP ID");
                return;
            }

            getSharedPreferences("HostelPrefs", MODE_PRIVATE)
                    .edit()
                    .putString("role", "student")
                    .putString("sapId", sap)
                    .apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        wardenButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Enter a valid email");
                return;
            }

            if (!db.isAuthorizedWarden(email)) {
                Toast.makeText(this, "Warden email not authorized",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            getSharedPreferences("HostelPrefs", MODE_PRIVATE)
                    .edit()
                    .putString("role", "warden")
                    .putString("sapId", "")
                    .apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}