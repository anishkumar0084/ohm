package com.ohmshantiapps.authEmail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import com.ohmshantiapps.R;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.authEmail.SignIn;
import com.tapadoo.alerter.Alerter;

import org.json.JSONObject;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    private TextView signIn, signUp;
    private EditText mEmail;
    private Button button;
    private ProgressBar progressBar4;
    private ApiService apiService;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        progressBar4 = findViewById(R.id.progressBar4);
        signIn = findViewById(R.id.textView5);
        signUp = findViewById(R.id.textView6);
        mEmail = findViewById(R.id.email);
        button = findViewById(R.id.button5);

        mAuth = FirebaseAuth.getInstance();
        apiService = RetrofitClient.getClient().create(ApiService.class);

        signIn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignIn.class);
            startActivity(intent);
        });

        signUp.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignUp.class);
            startActivity(intent);
        });

        button.setOnClickListener(view -> {
            String email = mEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                showErrorAlert("Enter email");
                return;
            }

            progressBar4.setVisibility(View.VISIBLE);

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showResetLinkSentMessage();
                } else {
                    String errorMessage = task.getException().getMessage();
                    handlePasswordResetError(errorMessage);
                }

                progressBar4.setVisibility(View.INVISIBLE);
            });
        });
    }

    private void showResetLinkSentMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password Reset");
        builder.setMessage("A password reset link has been sent to your email. Please reset your password through the link before updating it here.");

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            promptUserForNewPassword();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void handlePasswordResetError(String errorMessage) {
        String errorTitle;
        switch (errorMessage) {
            case "There is no user record corresponding to this identifier. The user may have been deleted.":
                errorTitle = "User Not Found";
                break;
            case "We have blocked all requests from this device due to unusual activity. Try again later.":
                errorTitle = "Blocked Device";
                break;
            case "The email address is badly formatted.":
                errorTitle = "Invalid Email Format";
                break;
            default:
                errorTitle = "Password Reset Error";
                break;
        }

        Alerter.create(ForgotPassword.this)
                .setTitle(errorTitle)
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(10000)
                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                .enableSwipeToDismiss()
                .setText(errorMessage)
                .show();
    }

    private void promptUserForNewPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password Reset");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = input.getText().toString();

                SessionManager sessionManager = new SessionManager(getApplicationContext());
                int userId = Integer.parseInt(sessionManager.getUserId());

                updatePasswordOnServer(userId, newPassword);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updatePasswordOnServer(int userId, String newPassword) {
        Call<ResponseBody> call = apiService.updatePassword(userId, newPassword);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        Log.d("Password Update", message);

                        if ("success".equals(status)) {
                            Toast.makeText(getApplicationContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                            navigateToSignIn();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to update password: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("Password Update", "Failed to update password on server.");
                    Toast.makeText(getApplicationContext(), "Failed to update password on server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Password Update", "Request failed: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(getApplicationContext(), SignIn.class);
        startActivity(intent);
        finish();
    }

    private void showErrorAlert(String message) {
        Alerter.create(ForgotPassword.this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(10000)
                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                .enableSwipeToDismiss()
                .setText(message)
                .show();
    }
}
