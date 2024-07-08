package com.ohmshantiapps.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.adapter.AdapterPost;
import com.ohmshantiapps.adapter.AdapterUsers;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.model.Users;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity {

    private EditText editText;
    private RecyclerView posts_rv;
    private ShimmerRecyclerView users_rv;
    private ShimmerFrameLayout postsg;
    private TextView users, post;
    private RelativeLayout userly, postly;
    private ProgressBar pg;
    private ImageView imageView3;
    private SharedPref sharedPref;

    private AdapterUsers adapterUsers;
    private List<Users> userList;

    private AdapterPost adapterPost;
    private List<ModelPost> postList;
    private ApiService apiInterface;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        postsg = findViewById(R.id.shimmer);
        apiInterface = RetrofitClient.getClient().create(ApiService.class);

        editText = findViewById(R.id.password);
        users_rv = findViewById(R.id.users_rv);
        posts_rv = findViewById(R.id.posts_rv);
        users = findViewById(R.id.users);
        post = findViewById(R.id.post);
        imageView3 = findViewById(R.id.imageView3);
        userly = findViewById(R.id.userly);
        postly = findViewById(R.id.postly);
        pg = findViewById(R.id.pg);

        pg.setVisibility(View.VISIBLE);
        users_rv.showShimmerAdapter();

        Intent intent = getIntent();
        if (intent.hasExtra("hashTag")) {
            String tag = getIntent().getStringExtra("hashTag");
            editText.setText("#" + tag);
        }

        imageView3.setOnClickListener(v -> onBackPressed());

        users.setTextColor(Color.parseColor("#0047ab"));
        posts_rv.setVisibility(View.GONE);

        postly.setOnClickListener(v -> {
            users.setTextColor(Color.parseColor("#161F3D"));
            post.setTextColor(Color.parseColor("#0047ab"));
            users_rv.setVisibility(View.GONE);
            postsg.setVisibility(View.VISIBLE);
            postsg.startShimmer();
            posts_rv.setVisibility(View.VISIBLE);
            getAllPost();  // Call this to ensure data is fetched when switching to Posts view
        });

        userly.setOnClickListener(v -> {
            users.setTextColor(Color.parseColor("#0047ab"));
            post.setTextColor(Color.parseColor("#161F3D"));
            users_rv.setVisibility(View.VISIBLE);
            posts_rv.setVisibility(View.GONE);
            postsg.setVisibility(View.GONE);
            postsg.stopShimmer();  // Ensure shimmer stops when switching to Users view
        });

        users_rv.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        getAllUsers();

        posts_rv.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        getAllPost();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    pg.setVisibility(View.VISIBLE);
                    filterUser(s.toString());
                    filterPost(s.toString());
                } else {
                    getAllUsers();
                    getAllPost();
                }
            }
        });
    }

    private void filterPost(String query) {
        Call<List<ModelPost>> postsCall = apiInterface.getAllPosts();
        postsCall.enqueue(new Callback<List<ModelPost>>() {
            @Override
            public void onResponse(Call<List<ModelPost>> call, Response<List<ModelPost>> response) {
                if (response.isSuccessful()) {
                    postsg.stopShimmer();
                    postsg.setVisibility(View.GONE);
                    List<ModelPost> posts = response.body();
                    if (posts != null && !posts.isEmpty()) {
                        List<ModelPost> filteredPosts = new ArrayList<>();
                        for (ModelPost post : posts) {
                            if ((post.getText() != null && post.getText().toLowerCase().contains(query.toLowerCase())) ||
                                    (post.getName() != null && post.getName().toLowerCase().contains(query.toLowerCase()))) {
                                filteredPosts.add(post);
                            }
                        }
                        postList.clear();
                        postList.addAll(filteredPosts);

                        adapterPost = new AdapterPost(Search.this, postList);
                        posts_rv.setAdapter(adapterPost);
                        pg.setVisibility(View.GONE);
                        adapterPost.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelPost>> call, Throwable t) {
                Toast.makeText(Search.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterUser(String query) {
        Call<List<Users>> call = apiInterface.getUsers();
        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                if (response.isSuccessful()) {
                    users_rv.hideShimmerAdapter();
                    List<Users> userList = response.body();
                    if (userList != null && !userList.isEmpty()) {
                        List<Users> filteredUsers = new ArrayList<>();
                        for (Users user : userList) {
                            if ((user.getName() != null && user.getName().toLowerCase().contains(query.toLowerCase())) ||
                                    (user.getUsername() != null && user.getUsername().toLowerCase().contains(query.toLowerCase()))) {
                                filteredUsers.add(user);
                            }
                        }

                        adapterUsers = new AdapterUsers(Search.this, filteredUsers);
                        users_rv.setAdapter(adapterUsers);
                        pg.setVisibility(View.GONE);
                        adapterUsers.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                Toast.makeText(Search.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllPost() {
        Call<List<ModelPost>> postsCall = apiInterface.getAllPosts();
        postsCall.enqueue(new Callback<List<ModelPost>>() {
            @Override
            public void onResponse(Call<List<ModelPost>> call, Response<List<ModelPost>> response) {
                if (response.isSuccessful()) {
                    postsg.stopShimmer();
                    postsg.setVisibility(View.GONE);
                    List<ModelPost> posts = response.body();
                    if (posts != null && !posts.isEmpty()) {
                        postList.clear();  // Clear previous data
                        postList.addAll(posts);
                        adapterPost = new AdapterPost(Search.this, postList);
                        posts_rv.setAdapter(adapterPost);
                        pg.setVisibility(View.GONE);
                        adapterPost.notifyDataSetChanged();
                    } else {
                        // Handle case when no posts are available
                        Toast.makeText(Search.this, "No posts available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Search.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ModelPost>> call, Throwable t) {
                Toast.makeText(Search.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllUsers() {
        Call<List<Users>> call = apiInterface.getUsers();
        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                if (response.isSuccessful()) {
                    users_rv.hideShimmerAdapter();
                    List<Users> userList = response.body();
                    if (userList != null && !userList.isEmpty()) {
                        adapterUsers = new AdapterUsers(Search.this, userList);
                        users_rv.setAdapter(adapterUsers);
                        pg.setVisibility(View.GONE);
                        adapterUsers.notifyDataSetChanged();
                    } else {
                        // Handle case when no users are available
                        Toast.makeText(Search.this, "No users available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Search.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                Toast.makeText(Search.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
