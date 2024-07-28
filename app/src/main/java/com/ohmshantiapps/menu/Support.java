package com.ohmshantiapps.menu;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ohmshantiapps.R;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SupportResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Support extends AppCompatActivity {

    private EditText nameEditText, emailEditText, descriptionEditText;
    private Spinner problemTypeSpinner;
    private Button submitButton;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        problemTypeSpinner = findViewById(R.id.problemTypeSpinner);
        submitButton = findViewById(R.id.submitButton);
        apiService=RetrofitClient.getClient().create(ApiService.class);


        // Set up Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.problem_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        problemTypeSpinner.setAdapter(adapter);

        // Set up submit button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    submitSupportRequest();
                }
            }
        });
    }

    private boolean validateFields() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String problemType = problemTypeSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            emailEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(problemType) || problemType.equals("Select problem type")) {
            TextView errorText = (TextView) problemTypeSpinner.getSelectedView();
            errorText.setError("Problem type is required");
            problemTypeSpinner.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(description)) {
            descriptionEditText.setError("Description is required");
            descriptionEditText.requestFocus();
            return false;
        }

        if (description.split("\\s+").length > 500) {
            descriptionEditText.setError("Description should not exceed 500 words");
            descriptionEditText.requestFocus();
            return false;
        }

        return true;
    }
    private void submitSupportRequest() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String problemType = problemTypeSpinner.getSelectedItem().toString();
        String description = descriptionEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || problemType.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<SupportResponse> call = apiService.submitSupportRequest(name, email, problemType, description);
        call.enqueue(new Callback<SupportResponse>() {
            @Override
            public void onResponse(Call<SupportResponse> call, Response<SupportResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Support.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Support.this, "Failed to submit request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SupportResponse> call, Throwable t) {
                Toast.makeText(Support.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}