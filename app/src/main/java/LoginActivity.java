
package com.example.hostel_application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        updateFields();

        roleGroup.setOnCheckedChangeListener((group, checkedId) ->
                updateFields()
        );

        loginButton.setOnClickListener(v -> login());
    }

    private boolean isWardenSelected() {
        return roleGroup.getCheckedRadioButtonId() == R.id.wardenRadio;
    }

    private void updateFields() {
        boolean warden = isWardenSelected();

        sapInput.setVisibility(warden
                ? android.view.View.GONE
                : android.view.View.VISIBLE);

        wardenEmailInput.setVisibility(warden
                ? android.view.View.VISIBLE
                : android.view.View.GONE);

        sapInput.setError(null);
        wardenEmailInput.setError(null);
        passwordInput.setError(null);
    }

    private void login() {
        String name = nameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        boolean warden = isWardenSelected();

        if (name.isEmpty()) {
            nameInput.setError("Required!");
            nameInput.requestFocus();
            return;
        }

        if (warden) {
            String email = wardenEmailInput.getText()
                    .toString().trim().toLowerCase();

            if (email.isEmpty()) {
                wardenEmailInput.setError("Email is required!");
                wardenEmailInput.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                passwordInput.setError("Required!");
                passwordInput.requestFocus();
                return;
            }

            if (!password.equals("svkm123")) {
                passwordInput.setError("Incorrect password!");
                passwordInput.requestFocus();
                return;
            }

            boolean authorized = db.isAuthorizedWarden(email);

            // Support the previous seeded test email too.
            if (!authorized && email.equals("warden@local")) {
                authorized = db.isAuthorizedWarden("warden@hostel.local");
            }

            if (!authorized) {
                wardenEmailInput.setError("Warden email is not authorized!");
                wardenEmailInput.requestFocus();
                return;
            }

            saveLogin("warden", name, email);
            openDashboard();
            return;
        }

        String sapId = sapInput.getText().toString().trim();

        if (sapId.isEmpty()) {
            sapInput.setError("Required!");
            sapInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Required!");
            passwordInput.requestFocus();
            return;
        }

        if (!sapId.matches("\\d{11}")) {
            sapInput.setError("Enter an 11-digit SAP ID!");
            sapInput.requestFocus();
            return;
        }

        if (!password.equals("svkm123")) {
            passwordInput.setError("Incorrect password!");
            passwordInput.requestFocus();
            return;
        }

        // Student account authorization will be enforced
        // through the admin-managed student database.
        saveLogin("student", name, sapId);
        openDashboard();
    }

    private void saveLogin(String role, String name, String identifier) {
        SharedPreferences preferences =
                getSharedPreferences("HostelPrefs", MODE_PRIVATE);

        preferences.edit()
                .putString("role", role)
                .putString("name", name)
                .putString("sapId", role.equals("student") ? identifier : "")
                .putString("wardenEmail", role.equals("warden") ? identifier : "")
                .apply();
    }

    private void openDashboard() {
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
