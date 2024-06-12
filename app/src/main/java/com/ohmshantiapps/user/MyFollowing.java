package com.ohmshantiapps.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.adapter.AdapterChatUsers;
import com.ohmshantiapps.adapter.AdapterPost;
import com.ohmshantiapps.adapter.AdapterUsers;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.FollowersFollowingResponse;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.model.ModelUser;
import com.ohmshantiapps.model.User;
import com.ohmshantiapps.search.ProfileSearch;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFollowing extends AppCompatActivity {

    String title;
    List<String> idList;
    RecyclerView recyclerView;
    private String userId;
    AdapterUsers adapterUsers;
    TextView textView11;
    ProgressBar pg;
    List<ModelUser> userList;
    ImageView imageView3;
    ApiService userApi;
    SharedPref sharedPref;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_following);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        imageView3 = findViewById(R.id.imageView3);
        pg = findViewById(R.id.pg);
        pg.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.users);
        textView11 = findViewById(R.id.textView11);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        idList = new ArrayList<>();
        userApi = RetrofitClient.getClient().create(ApiService.class);


        imageView3.setOnClickListener(v -> onBackPressed());

        SessionManager sessionManager = new SessionManager(this);

        userId = String.valueOf(Integer.parseInt(sessionManager.getUserId()));

        showUsers();






    }


    private void showUsers(){
        Call<FollowersFollowingResponse> call = userApi.getFollowersFollowing(Integer.parseInt(userId));

        call.enqueue(new Callback<FollowersFollowingResponse>() {
            @Override
            public void onResponse(Call<FollowersFollowingResponse> call, Response<FollowersFollowingResponse> response) {
                if (response.isSuccessful()) {
                    FollowersFollowingResponse data = response.body();
                    if (data != null) {

                        List<Integer> followersList = data.getFollowers();
                        List<Integer> followingList = data.getFollowing();

                        Toast.makeText(MyFollowing.this, ""+followersList, Toast.LENGTH_SHORT).show();
                        Toast.makeText(MyFollowing.this, ""+followingList, Toast.LENGTH_SHORT).show();


                        switch (title){
                            case "following":
                                getAllUsers(followingList);
                                textView11.setText("Following");
                                break;
                            case "followers":
                                getAllUsers(followersList);
                                textView11.setText("Followers");

                                break;
                        }





                    }
                } else {
                    showToast("Failed to fetch data");
                }
            }

            @Override
            public void onFailure(Call<FollowersFollowingResponse> call, Throwable throwable) {
                showToast("Network error: " + throwable.getMessage());
            }
        });


    }
    private void showToast(String message) {
        Alerter.create(this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(10000)
                .enableSwipeToDismiss()
                .setText("" + message)
                .show();
    }
    private void getAllUsers(List<Integer> targetIds) {
        Call<List<User>> call = userApi.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> allUsers = response.body();
                    if (allUsers != null) {
                        // Filter the users based on targetIds
                        List<User> filteredUsers = new ArrayList<>();
                        for (User user : allUsers) {
                            if (targetIds.contains(user.getId())) {
                                filteredUsers.add(user);
                            }
                        }
                        // Update RecyclerView with filtered users
                        adapterUsers = new AdapterUsers(MyFollowing.this, filteredUsers);
                        recyclerView.setAdapter(adapterUsers);
                        pg.setVisibility(View.GONE);
                    } else {
                        // Handle the case where the user list is null
                        showToast("User list is empty");
                    }
                } else {
                    // Handle unsuccessful response
                    showToast("Failed to fetch data");
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Handle network error
                showToast("Network error: " + t.getMessage());
            }
        });
    }
}