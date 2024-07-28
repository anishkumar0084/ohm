package com.ohmshantiapps.menu;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ohmshantiapps.R;

public class AccountStatus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_status);
        TextView accountStatus = findViewById(R.id.accountStatus);
        String status = "Account Status: Good";

        SpannableString spannable = new SpannableString(status);
        int start = status.indexOf("Good");
        int end = start + "Good".length();

        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#13ED1C")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        accountStatus.setText(spannable);



    }
}