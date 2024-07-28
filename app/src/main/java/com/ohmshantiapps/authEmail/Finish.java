package com.ohmshantiapps.authEmail;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.ohmshantiapps.MainActivity;
import com.ohmshantiapps.R;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.tapadoo.alerter.Alerter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Finish extends AppCompatActivity {
    EditText mUsername;
    ProgressBar progressBar3;
    Button button;

    private ApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        button = findViewById(R.id.button2);
        mUsername = findViewById(R.id.username);
        progressBar3 = findViewById(R.id.progressBar3);

        SessionManager sessionManager = new SessionManager(getApplicationContext());


        mApiService = RetrofitClient.getClient().create(ApiService.class);

        button.setOnClickListener(view -> {
            progressBar3.setVisibility(View.VISIBLE);
            final String username = mUsername.getText().toString();
            if (TextUtils.isEmpty(username)) {
                Alerter.create(Finish.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setText("Enter Username")
                        .show();
                progressBar3.setVisibility(View.INVISIBLE);
            } else {
                // Get the user ID passed from the previous activity
                String userId = sessionManager.getUserId();


                if (userId != null && !userId.isEmpty()) {
                    // Pass the username and user ID to the method to insert username
                    insertUsername(username, userId);
                } else {
                    Alerter.create(Finish.this)
                            .setTitle("Error")
                            .setIcon(R.drawable.ic_error)
                            .setBackgroundColorRes(R.color.colorPrimary)
                            .setDuration(10000)
                            .enableSwipeToDismiss()
                            .setText("Users ID not found")
                            .show();
                    progressBar3.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void insertUsername(String username, String userId) {
        Call<JsonObject> call = mApiService.updateUsername(username, userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseObject = response.body();
                    String status = responseObject.get("status").getAsString();
                    if (status.equals("success")) {
                        // Username updated successfully, proceed to MainActivity
                        Intent intent = new Intent(Finish.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Handle failure to update username
                        String message = responseObject.get("message").getAsString();
                        showError(message);
                    }
                } else {
                    showError("Failed to update username");
                }
                progressBar3.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                // Handle network or other errors
                showError("Failed to update username: " + t.getMessage());
                progressBar3.setVisibility(View.INVISIBLE);
            }

            private void showError(String message) {
                Alerter.create(Finish.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setText(message)
                        .show();
            }
        });
    }

}
