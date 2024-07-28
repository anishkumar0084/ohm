package com.ohmshantiapps.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ohmshantiapps.R;
import com.ohmshantiapps.adapter.AdapterPost;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.FollowersFollowingResponse;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.api.UserApiClient;
import com.ohmshantiapps.menu.Menu;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.model.Users;
import com.ohmshantiapps.post.Post;
import com.ohmshantiapps.settings.EditProfile;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("NullableProblems")
public class ProfileFragment extends Fragment {

    //xml
    ImageView imageView3,imageView4;
    CircleImageView circularImageView, circular;
    RelativeLayout followingly,followersly;
    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;

    //Theme
    RelativeLayout containers,edittext_bg;
    ConstraintLayout header,constraintLayout,post;
    ProgressBar pb;
    RelativeLayout bio_layout, web_layout,location_layout;
    TextView  bio, link, location,post_meme;
    TextView mUsername, mName,noFollowers,noFollowing,noPost;
    ImageView imageView12,bio_img,web_img,location_img;

    private static final int TOTAL_ITEMS_TO_LOAD = 7;
    private int mCurrenPage = 1;
    UserApiClient userApiClient;


    ApiService userApi;

    int userIdd;
    private ActivityResultLauncher<Intent> editProfileLauncher;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageView12 = view.findViewById(R.id.imageView12);
        bio_img = view.findViewById(R.id.bio_img);
        web_img = view.findViewById(R.id.web_img);
        location_img = view.findViewById(R.id.location_img);
        post_meme = view.findViewById(R.id.post_meme);
        mUsername = view.findViewById(R.id.textView10);
        mName = view.findViewById(R.id.textView9);
        imageView3 = view.findViewById(R.id.imageView3);
        noFollowers = view.findViewById(R.id.noFollowers);
        noFollowing = view.findViewById(R.id.noFollowing);
        noPost = view.findViewById(R.id.noPost);
        imageView4 = view.findViewById(R.id.imageView4);
        followersly = view.findViewById(R.id.followersly);
        followingly = view.findViewById(R.id.followingly);
        circularImageView = view.findViewById(R.id.circularImageView);
        pb = view.findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        constraintLayout = view.findViewById(R.id.constraintLayout);
        containers = view.findViewById(R.id.container);
        edittext_bg = view.findViewById(R.id.edittext_bg);
        circular = view.findViewById(R.id.circular);
        header = view.findViewById(R.id.header);
        //view
        bio = view.findViewById(R.id.bio);
        link = view.findViewById(R.id.link);
        location = view.findViewById(R.id.location);
        bio_layout = view.findViewById(R.id.bio_layout);
        web_layout = view.findViewById(R.id.web_layout);
        location_layout = view.findViewById(R.id.location_layout);
        post = view.findViewById(R.id.post);
        recyclerView = view.findViewById(R.id.postView);


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


        post.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Post.class);
            startActivity(intent);
        });

        imageView4.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Menu.class);
            startActivity(intent);
        });

        imageView3.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            editProfileLauncher.launch(intent);
        });
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Refresh the profile fragment
                        refreshProfile();
                    }
                });

        // Initialize ApiService
        userApi = RetrofitClient.getClient().create(ApiService.class);

        userApiClient = new UserApiClient();
        SessionManager sessionManager = new SessionManager(requireContext());

         userIdd = Integer.parseInt(sessionManager.getUserId());

         fetchProfile();

        postList= new ArrayList<>();
        loadPost();

        followingly.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MyFollowing.class);
            intent.putExtra("title", "following");
            startActivity(intent);
        });

        followersly.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MyFollowing.class);
            intent.putExtra("title", "followers");
            startActivity(intent);
        });

        getFollowersFollowing(Integer.parseInt(String.valueOf(userIdd)));

        return view;
    }

    private void refreshProfile() {
        fetchProfile();

    }
    private void fetchProfile(){
        userApiClient.fetchUser(userIdd, new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    Users user = response.body();
                    if (user != null) {
                        String dbBio = user.getBio();
                        String dbLink = user.getLink();
                        String photoUrl = user.getPhoto();
                        String dbLocation=user.getLocation();
                        String name=user.getName();
                        String username=user.getUsername();
                        mName.setText(name);
                        mUsername.setText(username);


                        bio.setText(dbBio);
                        link.setText(dbLink);
                        location.setText(dbLocation);
                        if (photoUrl == null) {
                          circularImageView.setImageResource(R.drawable.avatar);
                        }else {

                            try {
                                Glide.with(requireActivity())
                                        .load(photoUrl)
                                        .apply(new RequestOptions()
                                                .error(R.drawable.avatar)
                                        )  // Set the error placeholder
                                        .into(circularImageView);
                            } catch (Exception e) {
                                Glide.with(requireActivity())
                                        .load(R.drawable.avatar)
                                        .into(circularImageView);
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
                        }
                        String ed_text = bio.getText().toString().trim();
                        if(ed_text.length() > 0)
                        {
                            bio_layout.setVisibility(View.VISIBLE);

                        }
                        else
                        {
                            bio_layout.setVisibility(View.GONE);
                        }
                        String ed_link = link.getText().toString().trim();

                        if(ed_link.length() > 0)
                        {
                            web_layout.setVisibility(View.VISIBLE);

                        }
                        else
                        {
                            web_layout.setVisibility(View.GONE);
                        }

                        String ed_location = location.getText().toString().trim();

                        if(ed_location.length() > 0)
                        {
                            location_layout.setVisibility(View.VISIBLE);

                        }
                        else
                        {
                            location_layout.setVisibility(View.GONE);
                        }

                        pb.setVisibility(View.GONE);
                        constraintLayout.setVisibility(View.VISIBLE);
                        post.setVisibility(View.VISIBLE);

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



    public void getFollowersFollowing(int userId) {
        Call<FollowersFollowingResponse> call = userApi.getFollowersFollowing(userId);

        call.enqueue(new Callback<FollowersFollowingResponse>() {
            @Override
            public void onResponse(Call<FollowersFollowingResponse> call, Response<FollowersFollowingResponse> response) {
                if (response.isSuccessful()) {
                    FollowersFollowingResponse data = response.body();
                    if (data != null) {
                        int followers = data.getNumFollowers();
                        int following = data.getNumFollowing();
                        List<Integer> followersList = data.getFollowers();
                        List<Integer> followingList = data.getFollowing();

                        // Update UI or handle data as needed
                        // For example, update TextViews
                        noFollowers.setText(String.valueOf(followers));
                        noFollowing.setText(String.valueOf(following));
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
        Alerter.create(getActivity())
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(10000)
                .enableSwipeToDismiss()
                .setText("" + message)
                .show();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loadPost() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // Fetch posts by user ID
        Call<List<ModelPost>> postsCall = userApi.getPostsByUserId("getPostsByUserId", userIdd);
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

                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Handle failure
            }
        });
    }
}
