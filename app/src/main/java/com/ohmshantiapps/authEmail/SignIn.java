package com.ohmshantiapps.authEmail;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.ohmshantiapps.MainActivity;
import com.ohmshantiapps.R;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignIn extends AppCompatActivity {


    private TextView register, forgot;
    private EditText mEmail, mPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeUI();

        mAuth = FirebaseAuth.getInstance();
        apiService = RetrofitClient.getClient().create(ApiService.class);

        register.setOnClickListener(view -> startActivity(new Intent(SignIn.this, SignUp.class)));
        forgot.setOnClickListener(view -> startActivity(new Intent(SignIn.this, ForgotPassword.class)));

        findViewById(R.id.button3).setOnClickListener(view -> {
            startLoginProcess();
        });
    }

    private void initializeUI() {
        register = findViewById(R.id.register);
        forgot = findViewById(R.id.forgot);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
    }

    private void startLoginProcess() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showErrorAlert("Enter email & password");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        disableUI();

        if (isNetworkAvailable()) {
            loginWithFirebase(email, password);
        } else {
            showErrorAlert("No internet connection. Please check your network settings.");
            progressBar.setVisibility(View.INVISIBLE);
            enableUI();
        }
    }

    private void loginWithFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    loginToServer(email, password);
                } else {
                    showErrorAlert("Email not verified. Please check your email for the verification link.");
                    mAuth.signOut();
                    progressBar.setVisibility(View.INVISIBLE);
                    enableUI();
                }
            } else {
                showErrorAlert("Error: " + Objects.requireNonNull(task.getException()).getMessage());
                progressBar.setVisibility(View.INVISIBLE);
                enableUI();
            }
        });
    }

    private void loginToServer(String email, String password) {
        Call<JsonObject> call = apiService.login(email, password);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                if (response.isSuccessful() && response.body() != null) {

                    JsonObject jsonObject = response.body();
                    if (jsonObject.has("user_id")) {
                        String userId = jsonObject.get("user_id").getAsString();
                        new SessionManager(getApplicationContext()).storeUserId(userId);

                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        showErrorAlert("Users ID not found in response");
                        enableUI();
                    }
                } else {
                    handleServerError(response);
                    enableUI();
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                enableUI();

                if (t instanceof IOException) {
                    showErrorAlert("Network error. Please check your internet connection."+t.getMessage());
                } else {
                    showErrorAlert("Login failed: " + t.getMessage());
                }
            }
        });
    }



    private void handleServerError(@NonNull Response<JsonObject> response) {
        int statusCode = response.code();
        switch (statusCode) {
            case 401:
                showErrorAlert("Invalid email or password. Please try again.");
                break;
            case 403:
                showErrorAlert("You don't have permission to access this resource.");
                break;
            case 404:
                showErrorAlert("The requested resource was not found.");
                break;
            default:
                showErrorAlert("Login failed. Please try again.");
                break;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }



    private void showErrorAlert(String message) {
        Alerter.create(SignIn.this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(10000)
                .setTitleTypeface(Objects.requireNonNull(ResourcesCompat.getFont(this, R.font.bold)))
                .setTextTypeface(Objects.requireNonNull(ResourcesCompat.getFont(this, R.font.med)))
                .enableSwipeToDismiss()
                .setText(message)
                .show();
    }

    private void disableUI() {
        mEmail.setEnabled(false);
        mPassword.setEnabled(false);
        register.setEnabled(false);
        forgot.setEnabled(false);
    }

    private void enableUI() {
        mEmail.setEnabled(true);
        mPassword.setEnabled(true);
        register.setEnabled(true);
        forgot.setEnabled(true);
    }

}
