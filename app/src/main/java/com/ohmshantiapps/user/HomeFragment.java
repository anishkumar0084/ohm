package com.ohmshantiapps.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ohmshantiapps.Chats.MainChat;
import com.ohmshantiapps.R;
import com.ohmshantiapps.adapter.AdapterPost;
import com.ohmshantiapps.adapter.AdapterStory;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.api.UserApiClient;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.model.ModelStory;
import com.ohmshantiapps.model.User;
import com.ohmshantiapps.model.Users;
import com.ohmshantiapps.post.Post;
import com.ohmshantiapps.search.Search;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("NullableProblems")
public class HomeFragment extends Fragment {

    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    List<ModelPost>postList;
    AdapterPost adapterPost;
    ProgressBar pb;
    ImageView imageView4,imageView3;

    private AdapterStory story;
    private List<ModelStory> storyList;

    private int userId;
    CircleImageView circular;
    ConstraintLayout post;
    private UserApiClient userApiClient;

    private static final int TOTAL_ITEMS_TO_LOAD = 7;
    private int mCurrenPage = 1;
    ApiService apiService;
    FirebaseDatabase database;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_home, container, false);

      recyclerView = view.findViewById(R.id.postView);
      shimmerFrameLayout = view.findViewById(R.id.shimmer);
        NestedScrollView contentView = view.findViewById(R.id.cv);

      shimmerFrameLayout.startShimmer();
        database = FirebaseDatabase.getInstance();

        RecyclerView storyView = view.findViewById(R.id.storyView);
        storyView.setHasFixedSize(true);
        circular= view.findViewById(R.id.circular);
        post= view.findViewById(R.id.post);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        storyView.setLayoutManager(linearLayoutManager);
        storyList = new ArrayList<>();
        story = new AdapterStory(getContext(), storyList);
        storyView.setAdapter(story);
        imageView3 = view.findViewById(R.id.imageView3);
        imageView3.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Search.class);
            startActivity(intent);
        });
//      pb = view.findViewById(R.id.pb);
        mAuth = FirebaseAuth.getInstance();
        imageView4 = view.findViewById(R.id.imageView4);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        postList= new ArrayList<>();

        post.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Post.class);
            startActivity(intent);
        });
         apiService = RetrofitClient.getClient().create(ApiService.class);


        imageView4.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainChat.class);
            startActivity(intent);
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                mCurrenPage++;
//                    checkFollowing();
                }
            }
        });



        SessionManager sessionManager = new SessionManager(requireContext());

         userId = Integer.parseInt(sessionManager.getUserId());
        userApiClient = new UserApiClient();




        fetchUserDetails(userId);
        fetchFollowingUsers(userId);






      return view;
    }
    private void fetchUserDetails(int userId) {

        userApiClient.fetchUser(userId, new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    Users user = response.body();
                    if (user != null) {

                        String photoUrl = user.getPhoto();
                        String userIds=user.getUserid();

                        User userk = new User(userIds, user.getName(), user.getEmail(), user.getPhoto());

                        database.getReference()
                                .child("users")
                                .child(userIds)
                                .setValue(userk)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });


                        if (userIds.isEmpty()){

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String userid = Objects.requireNonNull(firebaseUser).getUid();
                            apiService = RetrofitClient.getClient().create(ApiService.class);



                            Users userUpdateRequest = new Users(Integer.parseInt(String.valueOf(userId)), null, null,null,null,null,null,null,null,null,null,true,null,userid);

                            Call<Void> call1 =apiService.updateUser(Integer.parseInt(String.valueOf(userId)), userUpdateRequest);
                            call1.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {

                                    } else {
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                }
                            });

                        }else {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("photo", "" + photoUrl);
                            hashMap.put("bio", "" + userId);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                            reference.child(Objects.requireNonNull(userIds)).updateChildren(hashMap)
                                    .addOnSuccessListener(aVoid -> {

                                    })
                                    .addOnFailureListener(e -> {

                                    });
                        }
                        try {
                            Glide.with(getActivity())
                                    .load(photoUrl)
                                    .apply(new RequestOptions()
                                            .error(R.drawable.avatar))  // Set the error placeholder
                                    .into(circular);
                        } catch (Exception e) {
                            Glide.with(getActivity())
                                    .load(R.drawable.avatar)
                                    .into(circular);
                        }

                    } else {
                        showFetchError();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch user: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                showError(t);
            }
        });
    }



    private void showFetchError() {
        Alerter.create(getActivity())
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(10000)
                .enableSwipeToDismiss()
                .setText("Failed to fetch user profile")
                .show();
    }

    private void showError(Throwable t) {
        Alerter.create(getActivity())
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(10000)
                .enableSwipeToDismiss()
                .setText("Failed to fetch user profile: " + t.getMessage())
                .show();
    }




//    private void checkSFollowing(){
//        followingSList = new ArrayList<>();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
//                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
//                .child("Following");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                followingSList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    followingSList.add(snapshot.getKey());
//                }
//                readStory();
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    private void fetchFollowingUsers(int followerId) {
        Call<List<Integer>> followingUsersCall = apiService.getFollowingUsers("getFollowingUsers", followerId);
        followingUsersCall.enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()) {
                    List<Integer> followingUserIds = response.body();
                    assert followingUserIds != null;
                    followingUserIds.add(userId);
                    if (followingUserIds != null && !followingUserIds.isEmpty()) {
                        fetchPostsFromFollowingUsers(followingUserIds);
                    } else {
                        // Handle case where user is not following anyone
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void fetchPostsFromFollowingUsers(List<Integer> followingUserIds) {
        recyclerView.setVisibility(View.VISIBLE);
        for (Integer userId : followingUserIds) {
            // Fetch posts by user ID
            Call<List<ModelPost>> postsCall = apiService.getPostsByUserId("getPostsByUserId", userId);
            postsCall.enqueue(new Callback<List<ModelPost>>() {
                @Override
                public void onResponse(Call<List<ModelPost>> call, Response<List<ModelPost>> response) {
                    if (response.isSuccessful()) {
                        List<ModelPost> posts = response.body();
                        if (posts != null && !posts.isEmpty()) {
                            postList.addAll(posts);
                            adapterPost = new AdapterPost(getActivity(), postList);
                           recyclerView.setAdapter(adapterPost);
                            adapterPost.notifyDataSetChanged();


                            // Handle received posts
                        } else {
                            // Handle case where no posts are available
                        }
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);

                    } else {
                        // Handle unsuccessful response
                    }
                }

                @Override
                public void onFailure(Call<List<ModelPost>> call, Throwable t) {
                    // Handle failure
                }
            });
        }
    }


//    private void readStory(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                long timecurrent = System.currentTimeMillis();
//                storyList.clear();
//                storyList.add(new ModelStory("",0,0,"", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()));
//                for (String id : followingSList){
//                    int countStory = 0;
//                    ModelStory modelStory = null;
//                    for (DataSnapshot snapshot1 : snapshot.child(id).getChildren()){
//                        modelStory = snapshot1.getValue(ModelStory.class);
//                        if (timecurrent > Objects.requireNonNull(modelStory).getTimestart() && timecurrent < modelStory.getTimeend()){
//                            countStory++;
//                        }
//                    }
//                    if (countStory > 0){
//                        storyList.add(modelStory);
//                    }
//                }
//                story.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

}
