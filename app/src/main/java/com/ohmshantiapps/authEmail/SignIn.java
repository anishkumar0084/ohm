package com.ohmshantiapps.authEmail;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SignIn extends AppCompatActivity {
    private static final String BASE_URL = "http://68.183.245.154/";

    private TextView register, forgot;
    private EditText mEmail, mPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeUI();

        register.setOnClickListener(view -> startActivity(new Intent(SignIn.this, SignUp.class)));
        forgot.setOnClickListener(view -> startActivity(new Intent(SignIn.this, ForgotPassword.class)));

        findViewById(R.id.button3).setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                showErrorAlert("Enter email & password");
                progressBar.setVisibility(View.INVISIBLE);
            } else if (isNetworkAvailable()) {
                loginWithFirebase(email, password);
            } else {
                showErrorAlert("No internet connection. Please check your network settings.");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initializeUI() {
        register = findViewById(R.id.register);
        forgot = findViewById(R.id.forgot);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
    }

    private void loginWithFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignIn.this, task -> {
            if (task.isSuccessful()) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        showAlert("Successful login");
                        loginToServer(email, password);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showErrorAlert("Error: " + databaseError.getMessage());
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            } else {
                showErrorAlert("Error: " + Objects.requireNonNull(task.getException()).getMessage());
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void loginToServer(String email, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<String> call = apiService.login(email, password);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        String userId = jsonObject.getString("user_id");
                        new SessionManager(getApplicationContext()).storeUserId(userId);

                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        showErrorAlert("Error parsing server response");
                    }
                } else {
                    handleServerError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                if (t instanceof IOException) {
                    showErrorAlert("Network error. Please check your internet connection.");
                } else {
                    showErrorAlert("Login failed: " + t.getMessage());
                }
            }
        });
    }

    private void handleServerError(@NonNull Response<String> response) {
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

    private void showAlert(String message) {
        Alerter.create(SignIn.this)
                .setTitle("Response")
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(10000)
                .setTitleTypeface(Objects.requireNonNull(ResourcesCompat.getFont(SignIn.this, R.font.bold)))
                .setTextTypeface(Objects.requireNonNull(ResourcesCompat.getFont(SignIn.this, R.font.med)))
                .enableSwipeToDismiss()
                .setText(message)
                .show();
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
}
