package com.example.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Find the Skip TextView and Next Button by ID
        TextView skipText = findViewById(R.id.skip);
        Button nextButton = findViewById(R.id.next);

        // Set an OnClickListener to navigate to MainActivity when Skip is clicked
        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close StartActivity so the user doesn't return when pressing back
            }
        });

        // Set an OnClickListener for the Next button to navigate to Start activity
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Start.class);
                startActivity(intent);
            }
        });
    }
}