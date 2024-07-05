package com.ohmshantiapps.api;

import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.model.Users;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserApiClient {
    ApiService apiService;


    public UserApiClient() {
        Retrofit retrofit = RetrofitClient.getClient();
        apiService = retrofit.create(ApiService.class);
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


    public void fetchUser(int userId, Callback<Users> callback) {
        apiService.fetchUser(userId).enqueue(callback);
    }






}
