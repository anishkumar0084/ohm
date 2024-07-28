package com.ohmshantiapps.menu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.api.UserApiClient;
import com.ohmshantiapps.menu.wallet.CoinUtils;
import com.ohmshantiapps.menu.wallet.CoinsViewModel;
import com.ohmshantiapps.model.Users;
import com.ohmshantiapps.settings.BioActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reward extends AppCompatActivity {
    private SharedPref sharedPref;
    private TextView coins, minim;
    private CoinsViewModel coinsViewModel;
    private int mini, totalCoin;
    private ApiService userApi;
    private UserApiClient userApiClient;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        userApi = RetrofitClient.getClient().create(ApiService.class);
        userApiClient = new UserApiClient();

        coins = findViewById(R.id.tv_coin_balance_value);
        minim = findViewById(R.id.tv_min_withdrawal);
        SessionManager sessionManager = new SessionManager(this);
        int userId = Integer.parseInt(sessionManager.getUserId());

        fetch(userId);

        coinsViewModel = new ViewModelProvider(this).get(CoinsViewModel.class);
        coinsViewModel.getTotalCoins(userId).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer totalCoins) {
                if (totalCoins != null) {
                    totalCoin = totalCoins;
                    update(userId, totalCoins);
                } else {
                    coins.setText("0");
                }
            }
        });

        // Initialize FirebaseRemoteConfig
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.fetchAndActivate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                FirebaseRemoteConfigValue configValue = mFirebaseRemoteConfig.getValue("coins");

                // Convert the value to an int
                try {
                    mini = Integer.parseInt(configValue.asString());
                    minim.setText("Minimum Withdrawal: " + mini + "\n" + "1000 Coins = $1.");
                } catch (NumberFormatException e) {
                    mini = 0; // Default value if parsing fails
                    minim.setText("Minimum Withdrawal: Not set");
                }
            }
        });

        // Set up withdrawal method UI
        RadioGroup rgWithdrawalMethod = findViewById(R.id.rg_withdrawal_method);
        EditText etPaypalEmail = findViewById(R.id.et_paypal_email);
        RadioButton rbPayPal = findViewById(R.id.rb_paypal);
        EditText etUpiNumber = findViewById(R.id.et_upi_number);
        Button btnWithdraw = findViewById(R.id.btn_withdraw);

        rbPayPal.setChecked(true);
        etUpiNumber.setVisibility(View.GONE);
        etPaypalEmail.setVisibility(View.VISIBLE);

        rgWithdrawalMethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_paypal) {
                    etPaypalEmail.setVisibility(View.VISIBLE);
                    etUpiNumber.setVisibility(View.GONE);
                } else if (checkedId == R.id.rb_upi) {
                    etPaypalEmail.setVisibility(View.GONE);
                    etUpiNumber.setVisibility(View.VISIBLE);
                }
            }
        });

        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check withdrawal time
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

                if (currentHour >= 10 && currentHour <= 23) {
                    String withdrawalMethod = rbPayPal.isChecked() ? "PayPal" : "UPI";
                    String details = withdrawalMethod.equals("PayPal") ? etPaypalEmail.getText().toString() : etUpiNumber.getText().toString();

                    // Validate details
                    if (details.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter valid details.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (totalCoin >= mini) {
                        // Create withdrawal request
                        String userIdStr = sessionManager.getUserId();
                        int amount = totalCoin; // Replace with the amount you want to withdraw
                        String status = "Pending";
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                        WithdrawalRequest withdrawalRequest = new WithdrawalRequest(userIdStr, userName, amount, status, time, details);
                        performWithdraw(withdrawalRequest, Integer.parseInt(userIdStr));
                    } else {
                        Toast.makeText(getApplicationContext(), "Insufficient coins for withdrawal.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Withdrawals are only available from 10 AM to 11 PM.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void performWithdraw(WithdrawalRequest request, int userId) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.withdrawCoins(request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Withdrawal successful
                    update(userId, 0);
                    fetch(userId);
                    Toast.makeText(getApplicationContext(), "Withdrawal successful.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Withdrawal failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void update(int userId, int totalCoin) {
        Users userUpdateRequest = new Users(userId, null, null, null, null, null, null, null, null, null, null, null, null, null, String.valueOf(totalCoin));
        Call<Void> call1 = userApi.updateUser(userId, userUpdateRequest);
        call1.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        // Update UI if needed
                    });
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Show a toast message indicating the failure
            }
        });
    }

    private void fetch(int userId) {
        userApiClient.fetchUser(userId, new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    Users user = response.body();
                    if (user != null) {
                        userName = user.getName();
                        // Extract and display the user's name and email
                        String userName = user.getCoin();
                        String formattedCoins = CoinUtils.formatCoins(Integer.parseInt(userName));
                        coins.setText(formattedCoins);
                    } else {
                        Toast.makeText(getApplicationContext(), "User not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to fetch user details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
