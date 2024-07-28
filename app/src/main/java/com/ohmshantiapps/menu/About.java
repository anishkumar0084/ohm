package com.ohmshantiapps.menu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ohmshantiapps.R;

public class About extends AppCompatActivity {
    private TextView tvPrivacyPolicy, tvTermsConditions, tvCopyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // Initialize TextViews
        tvPrivacyPolicy = findViewById(R.id.tvPrivacyPolicy);
        tvTermsConditions = findViewById(R.id.tvTermsConditions);
        tvCopyright = findViewById(R.id.tvCopyright);
        TextView textView = findViewById(R.id.tvCopyrightContent);
        textView.setText("© 2024 " + getString(R.string.app_name) + ". All rights reserved.");


        // Set click listeners
        tvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Privacy Policy click
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://192.168.207.225/privacypolicy.php"));
                startActivity(browserIntent);
            }
        });

        tvTermsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Terms and Conditions click
                // Handle Terms and Conditions click
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://192.168.207.225/terms_conditions.php"));
                startActivity(browserIntent);
            }
        });

        tvCopyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Copyright click
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://192.168.207.225/copyright.php"));
                startActivity(browserIntent);
            }
        });



    }
}