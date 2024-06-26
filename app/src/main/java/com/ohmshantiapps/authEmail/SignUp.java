package com.ohmshantiapps.authEmail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ohmshantiapps.R;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.menu.Policy;
import com.ohmshantiapps.model.User;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SignUp extends AppCompatActivity {

    private EditText mEmail, mName, mPassword;
    private Button mRegisterButton;
    private ProgressBar mProgressBar;
    private ApiService mApiService;
    private FirebaseAuth mAuth;
    TextView textView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        mEmail = findViewById(R.id.email);
        mName = findViewById(R.id.name);
        mAuth = FirebaseAuth.getInstance();
        mPassword = findViewById(R.id.password);
        mRegisterButton = findViewById(R.id.button4);
        mProgressBar = findViewById(R.id.progressBar2);
        TextView register = findViewById(R.id.textView3);
        textView2 = findViewById(R.id.textView2);

        // Initialize Retrofit
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://68.183.245.154/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        mApiService = retrofit.create(ApiService.class);

        mRegisterButton.setOnClickListener(view -> registerUser());
        textView2.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, Policy.class);
            startActivity(intent);
        });
        register.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String email = mEmail.getText().toString().trim();
        String name = mName.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        mProgressBar.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showErrorAlert("Enter email, name, and password");
            mProgressBar.setVisibility(View.INVISIBLE);
            return;
        }

        if (password.length() < 6) {
            showErrorAlert("Password should have minimum 6 characters");
            mProgressBar.setVisibility(View.INVISIBLE);
            return;
        }

        mUser(name,email,password);



    }
    private void mUser(String name,String email,String password){

        // Make registration request
        Call<JsonElement> call = mApiService.registerUser(name, email, password);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    // Registration successful
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject.has("user_id") ) {
                        String userId = jsonObject.get("user_id").getAsString();

                        if (!TextUtils.isEmpty(userId)) {
                            // Store token locally (e.g., SharedPreferences)
                            SessionManager sessionManager = new SessionManager(getApplicationContext());
                            sessionManager.storeUserId(userId);

                            Toast.makeText(SignUp.this,"Register Successful",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUp.this, Finish.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            mProgressBar.setVisibility(View.INVISIBLE);




                        } else {
                            showErrorAlert("Registration failed. Please try again.");
                        }
                    } else {
                        showErrorAlert("Registration failed. Please try again.");
                    }
                } else {
                    // Registration failed
                    showErrorAlert("Registration failed. Please try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                mProgressBar.setVisibility(View.INVISIBLE);
                // Handle network error
                showErrorAlert("Registration failed. Please try again.");
            }
        });


    }
    private void showErrorAlert(String message) {
        Alerter.create(SignUp.this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(10000)
                .enableSwipeToDismiss()
                .setText(message)
                .show();
    }

}
