
package com.example.hostel_application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WardenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warden);

        SharedPreferences prefs =
                getSharedPreferences("HostelConnectPrefs", MODE_PRIVATE);

        if (!"warden".equalsIgnoreCase(prefs.getString("role", "student"))) {
            Toast.makeText(this, "Warden access required",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String name = prefs.getString("name", "Warden");
        TextView welcome = findViewById(R.id.wardenWelcome);
        welcome.setText("Hello, " + name);

        findViewById(R.id.wardenComplaintsButton).setOnClickListener(v ->
                startActivity(new Intent(this, WardenComplaintsActivity.class)));

        findViewById(R.id.wardenMessButton).setOnClickListener(v ->
                openSection("Mess Menu"));

        findViewById(R.id.wardenPestButton).setOnClickListener(v ->
                openSection("Pest Control"));

        findViewById(R.id.wardenLaundryButton).setOnClickListener(v ->
                openSection("Laundry"));

        findViewById(R.id.wardenHomeButton).setOnClickListener(v -> {
            Toast.makeText(this, "You are on the Warden Home page",
                    Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.wardenAboutButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("section", "About Us");
            startActivity(intent);
        });
    }

    private void openSection(String section) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("section", section);
        startActivity(intent);
    }
}

