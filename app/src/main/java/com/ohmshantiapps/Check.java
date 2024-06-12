package com.ohmshantiapps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ohmshantiapps.R;
import com.ohmshantiapps.welcome.IntroLast;

public class Check extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final String PREF_NAME = "MyPrefs";
    private static final String USER_ID_KEY = "userId";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Check if the user has a valid user ID
        String userId = getUserId();
        if (!TextUtils.isEmpty(userId)) {
            redirectToMainActivity();
        } else {
            redirectToIntroLastActivity();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            Intent intent = new Intent(Check.this, IntroLast.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(Check.this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private String getUserId() {
        // Retrieve the user ID from SharedPreferences
        return sharedPreferences.getString(USER_ID_KEY, "");
    }

    private void redirectToMainActivity() {
        // Redirect the user to MainActivity
        Intent intent = new Intent(Check.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish Check activity to prevent user from coming back using back button
    }

    private void redirectToIntroLastActivity() {
        // Redirect the user to IntroLast activity for login or registration
        Intent intent = new Intent(Check.this, IntroLast.class);
        startActivity(intent);
        finish(); // Finish Check activity to prevent user from coming back using back button
    }

    private void logout() {
        // Clear the user ID from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_ID_KEY);
        editor.apply();

        // Redirect the user to IntroLast activity after logout
        redirectToIntroLastActivity();
    }

    // Add this method to invoke logout from other parts of your application
    public void performLogout() {
        logout();
    }
}
