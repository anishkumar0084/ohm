package com.ohmshantiapps.authEmail;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.ohmshantiapps.R;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.menu.Policy;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import papaya.in.sendmail.SendMail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    private EditText mEmail, mName, mPassword;
    private Button mRegisterButton;
    private ProgressBar mProgressBar;
    private TextView mLoadingMessage;
    private ApiService mApiService;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private int randomOTP;

    private static final String EMAIL_ALREADY_EXISTS = "Email already exists. Please log in to our application.";
    private static final String ENTER_EMAIL_NAME_PASSWORD = "Enter email, name, and password";
    private static final String PASSWORD_MIN_LENGTH = "Password should have a minimum of 6 characters";
    private static final String REGISTRATION_FAILED = "Registration failed. Please try again.";
    private static final String OHM_SERVICE_EMAIL = "ohm.serviceapp@gmail.com";
    private static final String EMAIL_PASSWORD = "jlck fogy myxu ospu";
    private static final String OTP_SUBJECT = "OHM app's OTP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
        initFirebase();
        initApiService();
        setListeners();
    }

    private void initViews() {
        mEmail = findViewById(R.id.email);
        mName = findViewById(R.id.name);
        mPassword = findViewById(R.id.password);
        mRegisterButton = findViewById(R.id.button4);
        mProgressBar = findViewById(R.id.progressBar2);
        mLoadingMessage = findViewById(R.id.loading_message);
        TextView textView2 = findViewById(R.id.textView2);
        textView2.setOnClickListener(v -> startActivity(new Intent(SignUp.this, Policy.class)));
        TextView register = findViewById(R.id.textView3);
        register.setOnClickListener(view -> startActivity(new Intent(SignUp.this, SignIn.class)));
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initApiService() {
        mApiService = RetrofitClient.getClient().create(ApiService.class);
    }

    private void setListeners() {
        mRegisterButton.setText("Send OTP");
        mRegisterButton.setOnClickListener(view -> sendOTP());
    }

    private void sendOTP() {
        String email = mEmail.getText().toString().trim();
        String name = mName.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (!validateInput(name, email, password)) return;

        Query emailQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(email);
        emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    showErrorAlert(EMAIL_ALREADY_EXISTS);
                } else {
                    randomOTP = generateOTP();
                    sendOTPEmail(email, randomOTP);
                    showOTPDialog(email, name, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showErrorAlert(databaseError.getMessage());
            }
        });

        mProgressBar.setVisibility(View.VISIBLE);
    }

    private boolean validateInput(String name, String email, String password) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showErrorAlert(ENTER_EMAIL_NAME_PASSWORD);
            return false;
        } else if (password.length() < 6) {
            showErrorAlert(PASSWORD_MIN_LENGTH);
            return false;
        }
        return true;
    }

    private int generateOTP() {
        return new Random().nextInt(900000) + 100000;
    }

    private void sendOTPEmail(String email, int otp) {
        SendMail mail = new SendMail(OHM_SERVICE_EMAIL, EMAIL_PASSWORD, email, OTP_SUBJECT, "Your OTP is -> " + otp);
        mail.execute();
    }

    private void showOTPDialog(String email, String name, String password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_otp, null);
        builder.setView(dialogView);

        OtpView otpView = dialogView.findViewById(R.id.otp_input);
        Button verifyButton = dialogView.findViewById(R.id.verify_button);

        AlertDialog dialog = builder.create();

        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                otpVerification(otp, email, name, password, dialog);
                verifyButton.setOnClickListener(v -> {
                    otpVerification(otp, email, name, password, dialog);

                });

            }
        });



        dialog.show();
    }
    private void otpVerification(String otp,String email, String name, String password, AlertDialog dialog) {
        if (otp.isEmpty()) {
            Toast.makeText(SignUp.this, "Enter OTP", Toast.LENGTH_SHORT).show();
        } else if (!otp.equals(String.valueOf(randomOTP))) {
            Toast.makeText(SignUp.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
        } else {
            dialog.dismiss();
            showLoadingMessage();
            registerWithFirebase(email, name, password);
        }


    }

    private void registerWithFirebase(final String email, final String name, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    saveUserData(firebaseUser.getUid(), name, email);
                }
            } else {
                hideLoadingMessage();
                showErrorAlert(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    private void saveUserData(String userId, String name, String email) {
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("id", userId);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("username", "");
        userData.put("bio", "");
        userData.put("verified", "");
        userData.put("location", "");
        userData.put("status", "online");
        userData.put("typingTo", "noOne");
        userData.put("link", "");
        userData.put("photo", "https://firebasestorage.googleapis.com/v0/b/memespace-34a96.appspot.com/o/avatar.jpg?alt=media&token=8b875027-3fa4-4da4-a4d5-8b661d999472");

        reference.setValue(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                registerWithApi(name, email,mPassword.toString());
            } else {
                hideLoadingMessage();
                showErrorAlert("Failed to save user data.");
            }
        });
    }

    private void registerWithApi(String name, String email,String password) {
        Call<JsonElement> call = mApiService.registerUser(name, email, password);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject.has("user_id")) {
                        String userId = jsonObject.get("user_id").getAsString();

                        if (!TextUtils.isEmpty(userId)) {
                            SessionManager sessionManager = new SessionManager(getApplicationContext());
                            sessionManager.storeUserId(userId);
                            navigateToFinishActivity();
                        } else {
                            showErrorAlert(REGISTRATION_FAILED);
                        }
                    } else {
                        showErrorAlert(REGISTRATION_FAILED);
                        hideLoadingMessage();

                    }
                } else {
                    showErrorAlert(REGISTRATION_FAILED);
                    hideLoadingMessage();

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                hideLoadingMessage();
                showErrorAlert(REGISTRATION_FAILED);
            }
        });
    }

    private void navigateToFinishActivity() {
        Toast.makeText(SignUp.this, "Registration Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignUp.this, Finish.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void showLoadingMessage() {
        mEmail.setEnabled(false);
        mName.setEnabled(false);
        mPassword.setEnabled(false);
        mRegisterButton.setEnabled(false);
        mLoadingMessage.setVisibility(View.VISIBLE);
    }

    private void hideLoadingMessage() {
        mEmail.setEnabled(true);
        mName.setEnabled(true);
        mPassword.setEnabled(true);
        mRegisterButton.setEnabled(true);
        mLoadingMessage.setVisibility(View.GONE);
    }
}
