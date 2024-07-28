package com.ohmshantiapps.menu;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ohmshantiapps.Chats.MainChat;
import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.authEmail.ForgotPassword;
import com.ohmshantiapps.authEmail.SignIn;
import com.ohmshantiapps.user.MyFollowing;
import com.ohmshantiapps.welcome.IntroLast;

import java.util.Objects;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class Menu extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String baseUrl = "http://68.183.245.154/user/";
    private SharedPreferences sharedPreferences;
    FirebaseUser firebaseUser;
    ConstraintLayout logout,save,Reward,about,Support,Feedback,email,password;
    Switch aSwitch;
    SharedPref sharedPref;
    private static final String PREF_NAME = "MyPrefs";
    private static final String USER_ID_KEY = "userId";

    ImageView imageView3;
    ApiService apiService;
    String profileUrl;
    String mEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
           setTheme(R.style.DarkTheme);
       }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        logout = findViewById(R.id.logout);
        Support = findViewById(R.id.support);
        Feedback = findViewById(R.id.feedback);
        Reward = findViewById(R.id.Reward);
        about = findViewById(R.id.about);
        aSwitch = findViewById(R.id.mySwitch);
        imageView3 = findViewById(R.id.imageView3);
        imageView3.setOnClickListener(v -> onBackPressed());
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        if (sharedPref.loadNightModeState()){
            aSwitch.setChecked(true);
        }
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
              sharedPref.setNightModeState(true);
            }else {
                sharedPref.setNightModeState(false);
            }
            restartApp();
        });
        save = findViewById(R.id.save);
        logout.setOnClickListener(v -> {
            mAuth.signOut();
            logout();
            sharedPref.setNightModeState(false);
            checkUserStatus();
        });

        Feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openPlayStoreReview();

            }
        });

//        delete.setOnClickListener(v -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
//            builder.setTitle("Delete Account");
//            builder.setMessage("Do you want to delete your account?");
//            builder.setPositiveButton("Delete", (dialog, which) -> {
//                // Show a dialog to enter the user's password for re-authentication
//                showReAuthenticationDialog();
//            }).setNegativeButton("No", (dialog, which) -> dialog.dismiss());
//            builder.show();
//        });


      setupButtonClickListeners();





    }
    private void shareProfileLink() {
        SessionManager sessionManager = new SessionManager(this);

        String  userId =sessionManager.getUserId();

        String shareableLink = baseUrl + userId;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this profile: " + shareableLink);
        startActivity(Intent.createChooser(shareIntent, "Share Profile via"));
    }

    private void showReAuthenticationDialog() {
        // Create an AlertDialog to enter the user's password
        AlertDialog.Builder reAuthBuilder = new AlertDialog.Builder(Menu.this);
        reAuthBuilder.setTitle("Re-authenticate");
        reAuthBuilder.setMessage("Please enter your password to proceed.");

        // Set up the input
        final EditText input = new EditText(Menu.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        reAuthBuilder.setView(input);

        // Set up the buttons
        reAuthBuilder.setPositiveButton("OK", (dialog, which) -> {
            String password = input.getText().toString();
            reAuthenticateUser(password);
        });
        reAuthBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        reAuthBuilder.show();
    }

    private void reAuthenticateUser(String password) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
            firebaseUser.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        // Re-authentication successful, proceed with account deletion
                        firebaseUser.delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    // Redirect to IntroLast activity
                                    Intent intent = new Intent(Menu.this, IntroLast.class);
                                    sharedPref.setNightModeState(false); // Assuming sharedPref is defined and initialized
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    Toast.makeText(Menu.this, "Failed to delete account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Handle re-authentication failure
                        Toast.makeText(Menu.this, "Re-authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Handle case where firebaseUser is null
            Toast.makeText(Menu.this, "Users not logged in or already deleted.", Toast.LENGTH_SHORT).show();
        }
    }


    private void restartApp() {
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        Objects.requireNonNull(i).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void checkUserStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser == null) {
            Intent intent = new Intent(Menu.this, SignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_ID_KEY);
        editor.apply();

    }
    private void setupButtonClickListeners() {
        setButtonClickListener(Support, Support.class);
        setButtonClickListener(about, About.class);
        setButtonClickListener(Reward, Reward.class);
        setButtonClickListener(email, AccountStatus.class);
        setButtonClickListener(password, ForgotPassword.class);
        setButtonClickListener(save, Saved.class);
    }

    private void setButtonClickListener(View button, final Class<?> targetActivity) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Menu.this, targetActivity);
                startActivity(intent);
            }
        });
    }
    private void openPlayStoreReview() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

}
