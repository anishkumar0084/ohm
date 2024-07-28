package com.ohmshantiapps.settings;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.api.UserApiClient;
import com.ohmshantiapps.model.Users;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("Convert2Lambda")
public class BioActivity extends AppCompatActivity {

    EditText mName;
    ImageView settings, menu;
    private DatabaseReference mDatabase;
    ProgressBar progressBar8;
    SharedPref sharedPref;
    ApiService userApi;
    UserApiClient userApiClient;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bio);
        mName = findViewById(R.id.name);
        settings = findViewById(R.id.settings);
        menu = findViewById(R.id.menu);
        progressBar8 = findViewById(R.id.progressBar8);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        userApiClient = new UserApiClient();
        userApi = RetrofitClient.getClient().create(ApiService.class);

        SessionManager sessionManager = new SessionManager(this);

        userId = String.valueOf(Integer.parseInt(sessionManager.getUserId()));

        fetchuserprofile();
        progressBar8.setVisibility(View.VISIBLE);

        menu.setOnClickListener(view -> {
            progressBar8.setVisibility(View.VISIBLE);
            final String name = mName.getText().toString();
           addUsername(name);
        });


    }
    private void fetchuserprofile(){
        userApiClient.fetchUser(Integer.parseInt(userId), new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    Users user = response.body();
                    if (user != null) {
                        // Extract and display the user's name and email
                        String userName = user.getBio();
                        mName.setText(userName);
                        progressBar8.setVisibility(View.GONE);



                    } else {
                        // Show a toast message indicating that the user was not found
                    }
                } else {
                    // Show a toast message indicating the failure
                    Toast.makeText(BioActivity.this, "Failed to fetch user: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                // Show a toast message indicating the failure
            }
        });

    }

    private void addUsername(String name) {
        Users userUpdateRequest = new Users(Integer.parseInt(userId), null, null,null,name,null,null,null,null,null,null,null,null,null,null);
        Call<Void> call1 = userApi.updateUser(Integer.parseInt(userId), userUpdateRequest);
        call1.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {

                        fetchuserprofile();

                        StyleableToast st = new StyleableToast(Objects.requireNonNull(BioActivity.this), "Bio updated", Toast.LENGTH_LONG);
                        st.setBackgroundColor(Color.parseColor("#001E55"));
                        st.setTextColor(Color.WHITE);
                        st.setIcon(R.drawable.ic_check_wt);
                        st.setMaxAlpha();
                        st.show();
                        progressBar8.setVisibility(View.GONE);

                    });
                } else {
                    progressBar8.setVisibility(View.GONE);

                    Toast.makeText(BioActivity.this, "Failed to update Bio.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar8.setVisibility(View.GONE);

                Toast.makeText(BioActivity.this, "Failed to Update Bio."+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}