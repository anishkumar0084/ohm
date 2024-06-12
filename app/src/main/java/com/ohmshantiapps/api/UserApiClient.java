package com.ohmshantiapps.api;

import android.app.Activity;
import android.content.Context;

import com.google.gson.JsonObject;
import com.ohmshantiapps.R;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.model.User;
import com.tapadoo.alerter.Alerter;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserApiClient {
    ApiService apiService;

    Context context;

    public UserApiClient() {
        Retrofit retrofit = RetrofitClient.getClient();
        apiService = retrofit.create(ApiService.class);
    }
    public void insertUser(User user) {
        Call<Void> call = apiService.insertUser(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    // User inserted successfully
                } else {
                    // Failed to insert user
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Error handling
            }
        });
    }
    public void insertModelPost(ModelPost modelPost){
        Call<Void> call=apiService.insertModelPost(modelPost);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
//
                } else {
//
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
//

            }
        });



    }


    public void fetchUser(int userId, Callback<User> callback) {
        apiService.fetchUser(userId).enqueue(callback);
    }






}
