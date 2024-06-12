package com.ohmshantiapps.settings;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.api.UserApiClient;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.model.User;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("Convert2Lambda")
public class UsernameActivity extends AppCompatActivity {

    EditText mUsername;
    private DatabaseReference mDatabase;
    ImageView button,settings;
    ProgressBar progressBar8;
    ApiService userApi;
    UserApiClient userApiClient;
    SharedPref sharedPref;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_username);
        button = findViewById(R.id.menu);
        button = findViewById(R.id.menu);
        mUsername = findViewById(R.id.name);
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar8 = findViewById(R.id.progressBar8);
        progressBar8.setVisibility(View.VISIBLE);

        userApiClient = new UserApiClient();
        userApi = RetrofitClient.getClient().create(ApiService.class);

        SessionManager sessionManager = new SessionManager(this);

        userId = String.valueOf(Integer.parseInt(sessionManager.getUserId()));

        fetchuserprofile();


        button.setOnClickListener(view -> {
            progressBar8.setVisibility(View.VISIBLE);
            final String username = mUsername.getText().toString();
            if (TextUtils.isEmpty(username)) {
                Alerter.create(UsernameActivity.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setText("Enter Username")
                        .show();
                progressBar8.setVisibility(View.GONE);

            } else {
               addUsername(username);
            }
        });


    }
    private void fetchuserprofile(){
        userApiClient.fetchUser(Integer.parseInt(userId), new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user != null) {
                        // Extract and display the user's name and email
                        String userName = user.getUsername();
                        mUsername.setText(userName);
                        progressBar8.setVisibility(View.GONE);



                    } else {
                        // Show a toast message indicating that the user was not found
                    }
                } else {
                    // Show a toast message indicating the failure
                    Toast.makeText(UsernameActivity.this, "Failed to fetch user: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Show a toast message indicating the failure
            }
        });

    }

    private void addUsername(String username) {
        User userUpdateRequest = new User(Integer.parseInt(userId), null, null,username,null,null,null,null,null,null,null,true,null,null);
        Call<Void> call1 = userApi.updateUser(Integer.parseInt(userId), userUpdateRequest);
        call1.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {

                        fetchuserprofile();

                        StyleableToast st = new StyleableToast(UsernameActivity.this, "UserName updated", Toast.LENGTH_LONG);
                        st.setBackgroundColor(Color.parseColor("#001E55"));
                        st.setTextColor(Color.WHITE);
                        st.setIcon(R.drawable.ic_check_wt);
                        st.setMaxAlpha();
                        st.show();
                        progressBar8.setVisibility(View.GONE);

                    });
                } else {
                    progressBar8.setVisibility(View.GONE);

                    Toast.makeText(UsernameActivity.this, "Failed to update username.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar8.setVisibility(View.GONE);

                Toast.makeText(UsernameActivity.this, "Failed to Update username."+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}