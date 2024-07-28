package com.ohmshantiapps.Chats;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ohmshantiapps.Adpref;
import com.ohmshantiapps.Chats.Adapters.TopStatusAdapter;
import com.ohmshantiapps.Chats.Adapters.UsersAdapter;
import com.ohmshantiapps.R;
import com.ohmshantiapps.adapter.AdapterUsers;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.FollowersFollowingResponse;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.databinding.ActivityMainChatBinding;
import com.ohmshantiapps.model.Status;
import com.ohmshantiapps.model.User;
import com.ohmshantiapps.model.UserStatus;
import com.ohmshantiapps.model.Users;
import com.ohmshantiapps.user.MyFollowing;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainChat extends AppCompatActivity {

    private ActivityMainChatBinding binding;
    FirebaseDatabase database;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    TopStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    AlertDialog progressDialog;
    User user;
    ApiService userApi;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar.toolbar);
        userApi = RetrofitClient.getClient().create(ApiService.class);

        SessionManager sessionManager = new SessionManager(this);

        userId = String.valueOf(Integer.parseInt(sessionManager.getUserId()));

        showUsers();

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView mAdView =findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageUri = data.getData();
                            Toast.makeText(MainChat.this, "Image Selected: " + imageUri, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Adpref adpref;
        adpref = new Adpref(this);
        if (adpref.loadAdsModeState()){
            mAdView.setVisibility(View.VISIBLE);

        }
        FirebaseRemoteConfig mFirebaseRemoteConfig =FirebaseRemoteConfig.getInstance();


        mFirebaseRemoteConfig.fetchAndActivate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

                String backgroundImage = mFirebaseRemoteConfig.getString("backgroundImage");
//                Glide.with(MainChat.this)
//                        .load(backgroundImage)
//                        .into(binding.back);

                /* Toolbar Color */
                String toolbarColor = mFirebaseRemoteConfig.getString("toolbarColor");
                String toolBarImage = mFirebaseRemoteConfig.getString("toolbarImage");
                boolean isToolBarImageEnabled = mFirebaseRemoteConfig.getBoolean("toolBarImageEnabled");



                if(isToolBarImageEnabled) {
                    Glide.with(MainChat.this)
                            .load(toolBarImage)
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull @NotNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                                    getSupportActionBar()
                                            .setBackgroundDrawable(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                                }
                            });
                } else {
//                    getSupportActionBar()
//                            .setBackgroundDrawable
//                                    (new ColorDrawable(Color.parseColor(toolbarColor)));
                }

            }
        });

        database = FirebaseDatabase.getInstance();

        FirebaseMessaging.getInstance()
                .getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("token", token);
                        database.getReference()
                                .child("users")
                                .child(FirebaseAuth.getInstance().getUid())
                                .updateChildren(map);
                    }
                });





        users = new ArrayList<>();
        userStatuses = new ArrayList<>();

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        usersAdapter = new UsersAdapter(this, users);
        statusAdapter = new TopStatusAdapter(this, userStatuses);
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusList.setLayoutManager(layoutManager);
        binding.statusList.setAdapter(statusAdapter);

        binding.recyclerView.setAdapter(usersAdapter);

        binding.recyclerView.showShimmerAdapter();
        binding.statusList.showShimmerAdapter();

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    if(!user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                        users.add(user);
                }
                binding.recyclerView.hideShimmerAdapter();
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







    }
    private void fetchUserStories(List<String> userIds) {
        // Get the current user ID
        String currentUserId = getCurrentUserId();

        if (currentUserId == null) {
            showToast("Current user not found");
            return;
        }

        // Include the current user's ID in the userIds list if not already included
        if (!userIds.contains(currentUserId)) {
            userIds.add(currentUserId);
        }
        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.statusList.hideShimmerAdapter(); // Dismiss shimmer adapter first

                if (snapshot.exists() && snapshot.hasChildren()) {
                    userStatuses.clear();
                    for (String userId : userIds) {
                        if (snapshot.hasChild(userId)) {
                            DataSnapshot storySnapshot = snapshot.child(userId);
                            UserStatus status = new UserStatus();
                            status.setName(storySnapshot.child("name").getValue(String.class));
                            status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                            status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));

                            ArrayList<Status> statuses = new ArrayList<>();

                            for (DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()) {
                                Status sampleStatus = statusSnapshot.getValue(Status.class);
                                statuses.add(sampleStatus);
                            }

                            status.setStatuses(statuses);
                            userStatuses.add(status);
                        }
                    }
                    statusAdapter.notifyDataSetChanged();
                } else {
                    userStatuses.clear();
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.statusList.hideShimmerAdapter();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if(data.getData() != null) {
                showProgressDialog();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserStatus userStatus = new UserStatus();
                                    userStatus.setName(user.getName());
                                    userStatus.setProfileImage(user.getProfileImage());
                                    userStatus.setLastUpdated(date.getTime());

                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("name", userStatus.getName());
                                    obj.put("profileImage", userStatus.getProfileImage());
                                    obj.put("lastUpdated", userStatus.getLastUpdated());

                                    String imageUrl = uri.toString();
                                    Status status = new Status(imageUrl, userStatus.getLastUpdated());

                                    database.getReference()
                                            .child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    database.getReference().child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("statuses")
                                            .push()
                                            .setValue(status);

//                                    fetchUserStories(Collections.singletonList(FirebaseAuth.getInstance().getUid()));

                                    dismissProgressDialog();

                                }
                            });
                        }
                    }
                });
            }
        }
    }
    private void showProgressDialog() {
        if (progressDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.progress_dialog, null));
            builder.setCancelable(false);
            progressDialog = builder.create();
        }
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.invite) {
            Toast.makeText(MainChat.this, "Search clicked.", Toast.LENGTH_SHORT).show();
                        return true;
//        } else if (id == R.id.search) {
//            Toast.makeText(MainChat.this, "Search clicked.", Toast.LENGTH_SHORT).show();
//            return true;
        } else
            if (id == R.id.settings) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            pickImageLauncher.launch(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);
        return super.onCreateOptionsMenu(menu);
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

                        getAllUsers(followersList);

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

        Call<List<Users>> call = userApi.getUsers();
        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                if (response.isSuccessful()) {
                    List<Users> allUsers = response.body();
                    if (allUsers != null) {
                        // Filter the users based on targetIds
                        List<Users> filteredUsers = new ArrayList<>();
                        for (Users user : allUsers) {
                            if (targetIds.contains(user.getId())) {
                                filteredUsers.add(user);
                            }
                        }

                        // Extract user IDs from the filtered users
                        List<String> filteredUserIds = new ArrayList<>();
                        for (Users user : filteredUsers) {
                            filteredUserIds.add(user.getUserid()); // Assuming getUserId() returns the Firebase User ID
                        }

                        // Fetch user stories for the filtered user IDs
                        fetchUserStories(filteredUserIds);

                    } else {
                        // Handle the case where the user list is null
                        showToast("Users list is empty");
                    }
                } else {
                    // Handle unsuccessful response
                    showToast("Failed to fetch data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                // Handle network error
                showToast("Network error: " + t.getMessage());
            }
        });
    }
    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }


}


