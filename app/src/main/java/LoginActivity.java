
package com.example.hostel_application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText nameInput, sapInput, wardenEmailInput, passwordInput;
    private RadioGroup roleGroup;
    private Button loginButton;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameInput = findViewById(R.id.nameInput);
        sapInput = findViewById(R.id.sapInput);
        wardenEmailInput = findViewById(R.id.wardenEmailInput);
        passwordInput = findViewById(R.id.passwordInput);
        roleGroup = findViewById(R.id.roleGroup);
        loginButton = findViewById(R.id.loginButton);

        db = new DBHelper(this);

        roleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.studentRadio) {
                sapInput.setVisibility(View.VISIBLE);
                wardenEmailInput.setVisibility(View.GONE);
            } else if (checkedId == R.id.wardenRadio) {
                sapInput.setVisibility(View.GONE);
                wardenEmailInput.setVisibility(View.VISIBLE);
            }
        });

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String name = nameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        int selectedRole = roleGroup.getCheckedRadioButtonId();

        if (name.isEmpty()) {
            nameInput.setError("Name is required!");
            nameInput.requestFocus();
            return;
        }

        if (selectedRole == -1) {
            Toast.makeText(this, "Please select Student or Warden",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password is required!");
            passwordInput.requestFocus();
            return;
        }

        if (!password.equals("svkm123")) {
            passwordInput.setError("Incorrect password!");
            passwordInput.requestFocus();
            return;
        }

        String role;
        String sapId = "";
        String email = "";

        if (selectedRole == R.id.studentRadio) {
            role = "student";
            sapId = sapInput.getText().toString().trim();

            if (sapId.isEmpty()) {
                sapInput.setError("SAP ID is required!");
                sapInput.requestFocus();
                return;
            }

            if (!sapId.matches("\\d{11}")) {
                sapInput.setError("Enter a valid 11-digit SAP ID!");
                sapInput.requestFocus();
                return;
            }

        } else if (selectedRole == R.id.wardenRadio) {
            role = "warden";
            email = wardenEmailInput.getText()
                    .toString().trim().toLowerCase();

            if (email.isEmpty()) {
                wardenEmailInput.setError("Warden email is required!");
                wardenEmailInput.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                wardenEmailInput.setError("Enter a valid email!");
                wardenEmailInput.requestFocus();
                return;
            }

            boolean authorized = db.isAuthorizedWarden(email);

            // Compatibility with the previously seeded warden account
            if (!authorized && email.equals("warden@local")) {
                authorized = db.isAuthorizedWarden("warden@hostel.local");
                if (authorized) {
                    email = "warden@hostel.local";
                }
            }

            if (!authorized) {
                wardenEmailInput.setError("This warden is not authorized!");
                wardenEmailInput.requestFocus();
                return;
            }

        } else {
            return;
        }

        SharedPreferences preferences =
                getSharedPreferences("HostelConnectPrefs", MODE_PRIVATE);

        preferences.edit()
                .putString("role", role)
                .putString("name", name)
                .putString("sapId", sapId)
                .putString("wardenEmail", email)
                .apply();

        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
