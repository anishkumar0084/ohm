package com.ohmshantiapps;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ohmshantiapps.groups.GroupFragment;
import com.ohmshantiapps.notifications.Token;
import com.ohmshantiapps.search.SearchFragment;
import com.ohmshantiapps.shareChat.ChatFragment;
import com.ohmshantiapps.user.HomeFragment;
import com.ohmshantiapps.user.ProfileFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 100;

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    private String userId;
    SharedPref sharedPref;
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(nevigationSelected);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
//        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(userId).setValue(mToken);
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener nevigationSelected =
            item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_search) {
                    selectedFragment = new SearchFragment();
                } else if (itemId == R.id.nav_group) {
                    selectedFragment = new GroupFragment();
                } else if (itemId == R.id.nav_chat) {
                    selectedFragment = new ChatFragment();
                } else if (itemId == R.id.nav_user) {

                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }
                return true;
            };


}



