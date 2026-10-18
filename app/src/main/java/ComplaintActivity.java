
package com.example.hostel_application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ComplaintActivity extends AppCompatActivity {

    private DBHelper db;
    private Spinner categorySpinner;
    private EditText roomNumberInput, descriptionInput;
    private TextView studentInfo;
    private Button submitButton, homeButton, aboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        db = new DBHelper(this);

        categorySpinner = findViewById(R.id.complaintTypeSpinner);
        roomNumberInput = findViewById(R.id.roomNumberInput);
        descriptionInput = findViewById(R.id.complaintDescription);
        studentInfo = findViewById(R.id.studentInfo);
        submitButton = findViewById(R.id.submitComplaintButton);
        homeButton = findViewById(R.id.homeButton);
        aboutButton = findViewById(R.id.aboutButton);

        SharedPreferences prefs = getSharedPreferences(
                "HostelConnectPrefs", MODE_PRIVATE);

        String name = prefs.getString("name", "");
        String sapId = prefs.getString("sapId", "");
        String role = prefs.getString("role", "student");

        if (!"student".equalsIgnoreCase(role)) {
            Toast.makeText(this,
                    "This form is for students only.",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        studentInfo.setText(
                "Student: " + name + "\nSAP ID: " + sapId);

        String[] categories = {
                "Select complaint type",
                "Electricity",
                "Carpentry",
                "Plumbing"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);

        submitButton.setOnClickListener(v -> submitComplaint());

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ComplaintActivity.this, MainActivity.class);
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        aboutButton.setOnClickListener(v ->
                startActivity(new Intent(
                        ComplaintActivity.this, ProfileActivity.class)
                        .putExtra("section", "About Us")));
    }

    private void submitComplaint() {

        SharedPreferences prefs = getSharedPreferences(
                "HostelConnectPrefs", MODE_PRIVATE);

        String name = prefs.getString("name", "");
        String sapId = prefs.getString("sapId", "");

        String room = roomNumberInput.getText()
                .toString().trim();

        String description = descriptionInput.getText()
                .toString().trim();

        String category = categorySpinner
                .getSelectedItem().toString();

        if (name.isEmpty() || sapId.isEmpty()) {
            Toast.makeText(this,
                    "Please log in again.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (categorySpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this,
                    "Please select a complaint type.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (room.isEmpty()) {
            roomNumberInput.setError("Room number is required!");
            roomNumberInput.requestFocus();
            return;
        }

        if (!room.matches("\\d+")) {
            roomNumberInput.setError(
                    "Room number must contain digits only!");
            roomNumberInput.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            descriptionInput.setError(
                    "Complaint description is required!");
            descriptionInput.requestFocus();
            return;
        }

        long result = db.addComplaint(
                name, sapId, room, category, description);

        if (result != -1) {
            Toast.makeText(this,
                    "Complaint submitted successfully!",
                    Toast.LENGTH_LONG).show();

            roomNumberInput.setText("");
            descriptionInput.setText("");
            categorySpinner.setSelection(0);
        } else {
            Toast.makeText(this,
                    "Failed to submit complaint. Try again.",
                    Toast.LENGTH_LONG).show();
        }
    }
}

