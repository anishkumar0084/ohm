package com.ohmshantiapps.menu.wallet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CoinsViewModel extends ViewModel {
    private ApiService apiService;
    private MutableLiveData<Integer> totalCoinsLiveData;

    public CoinsViewModel() {
        Retrofit retrofit = RetrofitClient.getClient();
        apiService = retrofit.create(ApiService.class);
        totalCoinsLiveData = new MutableLiveData<>();
    }

    public LiveData<Integer> getTotalCoins(int userId) {
        fetchTotalCoins(userId);
        return totalCoinsLiveData;
    }

    private void fetchTotalCoins(int userId) {
        Call<TotalCoinsResponse> call = apiService.getTotalCoins(userId);
        call.enqueue(new Callback<TotalCoinsResponse>() {
            @Override
            public void onResponse(Call<TotalCoinsResponse> call, Response<TotalCoinsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TotalCoinsResponse totalCoinsResponse = response.body();
                    try {
                        int totalCoins = Integer.parseInt(totalCoinsResponse.getTotalCoins());
                        totalCoinsLiveData.setValue(totalCoins);
                    } catch (NumberFormatException e) {
                        totalCoinsLiveData.setValue(null);
                    }
                } else {
                    totalCoinsLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<TotalCoinsResponse> call, Throwable t) {
                totalCoinsLiveData.setValue(null);
            }
        });
    }

}
