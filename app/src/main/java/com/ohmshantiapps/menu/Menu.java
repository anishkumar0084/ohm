package com.ohmshantiapps.menu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.user.MyFollowing;
import com.ohmshantiapps.welcome.IntroLast;

import java.util.Objects;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class Menu extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String baseUrl = "http://68.183.245.154/user/";
    private SharedPreferences sharedPreferences;
    FirebaseUser firebaseUser;
    ConstraintLayout logout,save,followers,following,invite,policy,delete,email,password;
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
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        aSwitch = findViewById(R.id.mySwitch);
        policy = findViewById(R.id.policy);
        delete = findViewById(R.id.delete);
        imageView3 = findViewById(R.id.imageView3);
        imageView3.setOnClickListener(v -> onBackPressed());
        invite = findViewById(R.id.invite);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        followers.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, MyFollowing.class);
            intent.putExtra("title", "followers");
            startActivity(intent);
        });
        apiService = RetrofitClient.getClient().create(ApiService.class);
        policy.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Policy.class);
            startActivity(intent);
        });
        following.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, MyFollowing.class);
            intent.putExtra("title", "following");
            startActivity(intent);
        });
        invite.setOnClickListener(v -> {
            String shareBody = "Myfriends - Friends Social Network" + " Download now on play store \nhttps://play.google.com/store/apps/details?id=com.ohm.shantiapp";
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/*");
            intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
            intent.putExtra(Intent.EXTRA_TEXT,shareBody);
            startActivity(Intent.createChooser(intent, "Share Via"));
        });
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
        save.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Saved.class);
            startActivity(intent);
        });
        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
            builder.setTitle("Delete Account");
            builder.setMessage("Do you want to delete your account?");
            builder.setPositiveButton("Delete", (dialog, which) -> {
                // Show a dialog to enter the user's password for re-authentication
                showReAuthenticationDialog();
            }).setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

//       DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                 mEmail = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
//                 if (mEmail.isEmpty()){
//                     email.setVisibility(View.GONE);
//                     password.setVisibility(View.GONE);
//                 }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Alerter.create(Menu.this)
//                        .setTitle("Error")
//                        .setIcon(R.drawable.ic_error)
//                        .setBackgroundColorRes(R.color.colorPrimary)
//                        .setDuration(10000)
//                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                        .enableSwipeToDismiss()
//                        .setText(databaseError.getMessage())
//                        .show();
//
//            }
//        });

        password.setVisibility(View.GONE);

        email.setOnClickListener(v -> {

           shareProfileLink();
        });
        password.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, ChangePassword.class);
            startActivity(intent);
        });





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
            Toast.makeText(Menu.this, "User not logged in or already deleted.", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(Menu.this, IntroLast.class);
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
}
