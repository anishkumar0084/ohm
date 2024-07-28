package com.ohmshantiapps.menu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.adapter.AdapterPost;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.ModelPostRequest;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.post.UpdatePost;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Saved extends AppCompatActivity {

    RecyclerView recyclerView;
     String userId;
    TextView textView11;
    ProgressBar pg;
    ImageView imageView3;
    AdapterPost adapterPost;
    List<ModelPost> postList;
    SharedPref sharedPref;
    ApiService userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        recyclerView = findViewById(R.id.users);
        imageView3 = findViewById(R.id.imageView3);
        textView11 = findViewById(R.id.textView11);
        pg = findViewById(R.id.pg);
        pg.setVisibility(View.VISIBLE);
        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
        imageView3.setOnClickListener(v -> onBackPressed());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        adapterPost = new AdapterPost(this, postList);
        recyclerView.setAdapter(adapterPost);
        userApi = RetrofitClient.getClient().create(ApiService.class);
        mySaved();

    }

    private void mySaved() {
        Call<List<String>> call =userApi.getSavedPosts("get_saved_posts", userId);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> savedPosts = response.body();
                    loadPostData((ArrayList<String>) savedPosts);

                } else {
                    Toast.makeText(Saved.this, "Failed to get saved posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(Saved.this, "Network error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void loadPostData(ArrayList<String> postIds) {
        for (String postId : postIds) {
            ModelPostRequest request = new ModelPostRequest("getPostsByPid", postId);

            // Call API to get post by pId
            Call<ModelPost[]> call = userApi.getPostByPid(request);

            call.enqueue(new Callback<ModelPost[]>() {
                @Override
                public void onResponse(Call<ModelPost[]> call, Response<ModelPost[]> response) {
                    if (response.isSuccessful()) {
                        ModelPost[] posts = response.body();
                        if (posts != null && posts.length > 0) {
                            for (ModelPost post : posts) {
                                postList.add(post);
                            }
                            adapterPost.notifyDataSetChanged();
                            pg.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(Saved.this, "No posts found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Saved.this, "Failed to get posts: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ModelPost[]> call, Throwable t) {
                    Alerter.create(Saved.this)
                            .setTitle("Error")
                            .setIcon(R.drawable.ic_check_wt)
                            .setBackgroundColorRes(R.color.colorPrimaryDark)
                            .setDuration(10000)
                            .enableSwipeToDismiss()
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                            .setText("Network error: " + t.getMessage())
                            .show();
                    Toast.makeText(Saved.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


}