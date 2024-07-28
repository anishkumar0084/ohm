package com.ohmshantiapps.menu.wallet;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

public class UpdateCoinsWorker extends Worker {
    private ApiService apiService;

    public UpdateCoinsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Retrofit retrofit = RetrofitClient.getClient();
        apiService = retrofit.create(ApiService.class);
    }

    @NonNull
    @Override
    public Result doWork() {
        Call<String> call = apiService.updateCoins();

        try {
            Response<String> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                return Result.success();
            } else {
                return Result.failure();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
