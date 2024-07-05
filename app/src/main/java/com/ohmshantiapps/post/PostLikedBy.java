package com.ohmshantiapps.post;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.adapter.AdapterUsers;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.LikeResponse;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.model.Users;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostLikedBy extends AppCompatActivity {
    SharedPref sharedPref;
    String postId;
    private RecyclerView recyclerView;
    private List<Users> userList;
    ProgressBar pg;
    private AdapterUsers adapterUsers;
    ImageView imageView3;

    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_liked_by);
        Intent intent = getIntent();
        imageView3 = findViewById(R.id.imageView3);
        pg = findViewById(R.id.pg);
        imageView3.setOnClickListener(v -> onBackPressed());
        postId = intent.getStringExtra("postId");
        recyclerView = findViewById(R.id.users);
       userList = new ArrayList<>();

        apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<LikeResponse> call = apiService.getLikes(postId, "get_likes");

        call.enqueue(new retrofit2.Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LikeResponse likeResponse = response.body();
                    List<String> userIds = likeResponse.getUserIds();
                    getUsers(userIds);






                } else {
                    Log.e(TAG, "Failed to get likes. Response code: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable throwable) {

            }
        });

    }

    private void getUsers(List<String> hisUid) {
        String ids = TextUtils.join(",", hisUid);

        Call<List<Users>> call=apiService.getUsersByIds(ids);

        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                if (response.isSuccessful()) {
                    List<Users> users = response.body();
                    adapterUsers = new AdapterUsers(PostLikedBy.this, users);
                    recyclerView.setAdapter(adapterUsers);
                    pg.setVisibility(View.GONE);

                } else {
                    Toast.makeText(PostLikedBy.this, "No users found", Toast.LENGTH_SHORT).show();
                    pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable throwable) {
                Toast.makeText(PostLikedBy.this, "Failed to fetch users: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });





    }
}