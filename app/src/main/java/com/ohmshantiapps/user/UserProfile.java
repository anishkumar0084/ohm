package com.ohmshantiapps.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.adapter.AdapterPost;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.FollowersFollowingResponse;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.api.UserApiClient;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.model.ModelUser;
import com.ohmshantiapps.model.Users;
import com.ohmshantiapps.notifications.Data;
import com.ohmshantiapps.notifications.Sender;
import com.ohmshantiapps.notifications.Token;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

@SuppressWarnings("ALL")
public class UserProfile extends AppCompatActivity {


    TextView mUsername, mName,noFollowers,noFollowing,noPost;
    CircleImageView circularImageView;
    TextView  bio, link, location;
    String receiver_id = "";

    RelativeLayout bio_layout, web_layout,location_layout,followingly,followersly;
    ProgressBar pb;
    ConstraintLayout constraintLayout;
    RecyclerView recyclerView;
    Button message;
    List<ModelPost> postList;
    AdapterPost adapterPost;
    String uid;
    private String userId;
    Button follow,following;
    ImageView imageView3;
     String hisUid,userIdk;
    SharedPref sharedPref;
    @SuppressWarnings("unused")
    private RequestQueue requestQueue;
    private boolean notify = false;

    private static final int TOTAL_ITEMS_TO_LOAD = 7;
    private int mCurrenPage = 1;
    UserApiClient userApiClient;
    ApiService apiService;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        recyclerView = findViewById(R.id.postView);
        context = this;
        apiService = RetrofitClient.getClient().create(ApiService.class);

         hisUid = getIntent().getStringExtra("hisUid");

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        //view
        mUsername = findViewById(R.id.textView10);
        message = findViewById(R.id.message);
        imageView3 = findViewById(R.id.imageView3);
        mName = findViewById(R.id.textView9);
        followersly = findViewById(R.id.followersly);
        followingly = findViewById(R.id.followingly);
        noPost = findViewById(R.id.noPost);
        noFollowing = findViewById(R.id.noFollowing);
        circularImageView = findViewById(R.id.circularImageView);
        noFollowers = findViewById(R.id.noFollowers);
        pb = findViewById(R.id.pb);
        following = findViewById(R.id.following);
        pb.setVisibility(View.VISIBLE);
        constraintLayout = findViewById(R.id.constraintLayout);
        follow = findViewById(R.id.follow);
        //view
        bio = findViewById(R.id.bio);
        link = findViewById(R.id.link);
        location = findViewById(R.id.location);
        bio_layout = findViewById(R.id.bio_layout);
        web_layout = findViewById(R.id.web_layout);
        location_layout = findViewById(R.id.location_layout);
        NestedScrollView cv = findViewById(R.id.cv);



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mCurrenPage++;
//                    loadPost();
                }
            }
        });

        imageView3.setOnClickListener(v -> onBackPressed());

        userApiClient = new UserApiClient();

        SessionManager sessionManager = new SessionManager(UserProfile.this);
        userId =sessionManager.getUserId();

        int is=Integer.parseInt(hisUid);
        message.setOnClickListener(v -> {


        });









        userApiClient.fetchUser(is, new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, retrofit2.Response<Users> response) {
                if (response.isSuccessful()) {
                    Users user = response.body();
                    if (user != null) {
                        // Extract and display the user's name and email
                        String userName = user.getName();
                        String userEmail = user.getEmail();
                        String photoUrl = user.getPhoto();
                        String username = user.getUsername();
                        String bi = user.getBio();
                        String lik = user.getLink();
                        String loc = user.getLocation();



                        mName.setText(userName);
                        mUsername.setText(username);
                        bio.setText(bi);
                         link.setText(lik);
                          location.setText(loc);
                        followingly.setOnClickListener(v -> {
                        Intent intent = new Intent(UserProfile.this, FollowersList.class);
                        intent.putExtra("id", hisUid);
                        intent.putExtra("title", "following");
                        startActivity(intent);
                    });

                    followersly.setOnClickListener(v -> {
                        Intent intent = new Intent(UserProfile.this, FollowersList.class);
                        intent.putExtra("id", hisUid);
                        intent.putExtra("title", "followers");
                        startActivity(intent);
                    });

                        try {
                            Glide.with(UserProfile.this)
                                    .load(photoUrl)
                                    .apply(new RequestOptions()
                                            .error(R.drawable.avatar))  // Set the error placeholder
                                    .into(circularImageView);
                        } catch (Exception e) {
                            Glide.with(UserProfile.this)
                                    .load(R.drawable.avatar)
                                    .into(circularImageView);
                        }

                    String ed_text = bio.getText().toString().trim();
                    if (ed_text.length() > 0) {
                        bio_layout.setVisibility(View.VISIBLE);

                    } else {
                        bio_layout.setVisibility(View.GONE);
                    }
                    String ed_link = link.getText().toString().trim();

                    if (ed_link.length() > 0) {
                        web_layout.setVisibility(View.VISIBLE);

                    } else {
                        web_layout.setVisibility(View.GONE);
                    }

                    String ed_location = location.getText().toString().trim();

                    if (ed_location.length() > 0) {
                        location_layout.setVisibility(View.VISIBLE);

                    } else {
                        location_layout.setVisibility(View.GONE);
                    }

                    pb.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);


                        // Show a toast message with the user's name
//                        Toast.makeText(requireContext(), "Users Name: " + userName, Toast.LENGTH_SHORT).show();
                    } else {
                        // Show a toast message indicating that the user was not found
//                        showFetchError();
                    }
                } else {
                    // Show a toast message indicating the failure
                    Toast.makeText(UserProfile.this, "Failed to fetch user: ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {

                showError(t);

            }
        });



        follow.setOnClickListener(v -> {
            followUser( Integer.parseInt(userId),is);
                follow.setVisibility(View.GONE);
                following.setVisibility(View.VISIBLE);
            addToHisNotification(""+userIdk);
            notify = true;

            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users").child(userIdk);
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ModelUser user = snapshot.getValue(ModelUser.class);
                    if (notify){
                        sendNotification(userIdk, Objects.requireNonNull(user).getName(), "Started following you");

                    }
                    notify = false;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    StyleableToast st = new StyleableToast(Objects.requireNonNull(Objects.requireNonNull(getApplicationContext())), error.getMessage(), Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#001E55"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_error);
                    st.setMaxAlpha();
                    st.show();
                }
            });
            getFollowers();

        });
        following.setOnClickListener(v -> {

            Call<Void> call = apiService.deleteRelationship(Integer.parseInt(userId),is);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        following.setVisibility(View.GONE);
                        follow.setVisibility(View.VISIBLE);

                        getFollowers();


                    } else {
                        // Handle unsuccessful response
                        Toast.makeText(UserProfile.this, "Oops! An error occurred.", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {
                    Toast.makeText(UserProfile.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();


                }
            });



        });


        isFollowing();
        getFollowers();
        postList= new ArrayList<>();
        loadPost();
    }



    @Override
    protected void onStart() {
        super.onStart();
        isFollowing();
        if (hisUid.equals(userId)){
            FragmentManager fragmentManager = getSupportFragmentManager();
            ProfileFragment profileFragment = new ProfileFragment();
            fragmentManager.beginTransaction().replace(R.id.container,profileFragment).commit();
        }
    }
    private void followUser(int followerId, int followingId) {
        Call<Void> call = apiService.followUser(followerId, followingId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle successful follow response
                    Toast.makeText(UserProfile.this, "Users followed successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(UserProfile.this, "Oops! An error occurred.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Toast.makeText(UserProfile.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();


            }
        });

    }
    private void showError(Throwable t) {
        Alerter.create(this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(10000)
                .enableSwipeToDismiss()
                .setText("Failed to fetch user profile: " + t.getMessage())
                .show();
    }

    private void addToHisNotification(String hisUid){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", "");
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", "Started following you");
        hashMap.put("sUid", userId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {

                }).addOnFailureListener(e -> {

                });

    }
    private void isFollowing() {
        // Get the current user's UID (replace with your actual user ID retrieval method)

        // Make the API call using Retrofit
        Call<Boolean> call = apiService.checkFollowing(userId, hisUid);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, retrofit2.Response<Boolean> response) {
                if (response.isSuccessful()) {
                    boolean isFollowing = response.body();
                    if (isFollowing) {
                        follow.setVisibility(View.GONE);
                        following.setVisibility(View.VISIBLE);
                    } else {
                        follow.setVisibility(View.VISIBLE);
                        following.setVisibility(View.GONE);
                    }
                } else {
                    // Handle unsuccessful response
                }

            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {

            }
        });


    }

    private void loadPost() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        Call<List<ModelPost>> postsCall = apiService.getPostsByUserId("getPostsByUserId", Integer.parseInt(hisUid));
        postsCall.enqueue(new Callback<List<ModelPost>>() {
            @Override
            public void onResponse(Call<List<ModelPost>> call, retrofit2.Response<List<ModelPost>> response) {
                if (response.isSuccessful()) {
                    List<ModelPost> posts = response.body();
                    if (posts != null && !posts.isEmpty()) {
                        postList.addAll(posts);
                        adapterPost = new AdapterPost(UserProfile.this, postList);
                        recyclerView.setAdapter(adapterPost);
                        adapterPost.notifyDataSetChanged();
                        noPost.setText(String.valueOf(postList.size()));





                        // Handle received posts
                    } else {

                        // Handle case where no posts are available
                    }
                } else {

                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<ModelPost>> call, Throwable t) {

                Toast.makeText(UserProfile.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Handle failure
            }
        });
    }
    @SuppressWarnings("SameParameterValue")
    private void sendNotification(final String hisId, final String name, final String message) {
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);

                    // Ensure the request queue is initialized
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                    // Create the data payload to send
                    Data data = new Data(userId, name + " : " + message, "New Message", hisId, R.drawable.logo);

                    // Create the sender object with data and token
                    Sender sender = new Sender(data, token.getToken());

                    // Convert sender to JSONObject
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));

                        // Create the request to FCM
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send",
                                jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSON_RESPONSE", "onResponse" + response.toString());
                                // Notification sent successfully
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onErrorResponse" + error.toString());
                                // Error occurred
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                // Set headers for the request
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAADu5rTxA:APA91bEvZnB9PdnPsCSZGXOakuCmEoyrhraMXdOTrXbxsolCRdVwRqe_XLf8cFZnngoEtn0xDWqbVs1gv2KUFtJ02VBwatkKSpLY1cev-uj_jEWJcydOrIvYi-Ph4NBot_FG4fNt5G8f");
                                return headers;
                            }
                        };

                        // Add the request to the RequestQueue
                        requestQueue.add(jsonObjectRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void  getFollowers(){

        Call<FollowersFollowingResponse> call = apiService.getFollowersFollowing(Integer.parseInt(hisUid));
        // Pass user ID
        call.enqueue(new Callback<FollowersFollowingResponse>() {
            @Override
            public void onResponse(Call<FollowersFollowingResponse> call, retrofit2.Response<FollowersFollowingResponse> response) {

                if (response.isSuccessful()) {
                    FollowersFollowingResponse data = response.body();
                    if (data != null) {
                        int followers = data.getNumFollowers();
                        int following = data.getNumFollowing();
                        noFollowers.setText(""+followers);
                        noFollowing.setText(""+following);
                        // Handle successful response
                        // Use the followers and following data as needed
                    }
                } else {
                    // Handle unsuccessful response
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



}

