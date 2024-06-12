package com.ohmshantiapps.post;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.Player;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.ExoTrackSelection;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.adapter.AdapterComments;
import com.ohmshantiapps.api.ApiResponse;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.ModelPostRequest;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.groups.ShareGroupActivity;
import com.ohmshantiapps.model.ModelComments;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.search.Search;
import com.ohmshantiapps.shareChat.ShareActivity;
import com.ohmshantiapps.user.MediaView;
import com.ohmshantiapps.user.UserProfile;
import com.ohmshantiapps.welcome.GetTimeAgo;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class PostDetails extends AppCompatActivity implements View.OnClickListener {
    SharedPref sharedPref;
    private static final int PICK_VIDEO_REQUEST = 1;
    private DefaultTrackSelector trackSelector;
    ImageView back, more, meme, send, add, like_img, ic_eye, attach;
    RelativeLayout like, comment, share, view_ly, video_share;
    RecyclerView recyclerView;
    CircleImageView circleImageView3;
    TextView name, type, text, likeNo, commentNo, views;
    EditText textBox;
    ConstraintLayout constraintLayout9, viewlt;
    private String userId, myName, myDp;
    private DatabaseReference mDatabase;
    Map<String, String> defaultRequestProperties = new HashMap<>();
    String userAgent = "";
    String hisId, hisName, postId, pLikes, hisDp, hisMeme, hisVine, hisTime, hisText, hisTypes;
    boolean mProcessComment = false;
    boolean mProcessCLike = false;
    List<ModelComments> commentsList;
    AdapterComments adapterComments;
    ProgressBar load;
    //Share
    ConstraintLayout chatshare, appshare, groupShare;
    BottomSheetDialog bottomDialog;
    //Share
    ConstraintLayout chatvid, appvid;
    BottomSheetDialog bottom;
    //Add
    ConstraintLayout constraintLayout3, delete;
    BottomSheetDialog bottomSheetDialog;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    ExoPlayer exoPlayer;
    PlayerView vine;
    ApiService userApi;
    BigInteger bignumber;
    String Id;
//    SimpleExoPlayer exoPlayer;


    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        userApi = RetrofitClient.getClient().create(ApiService.class);


        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        Id = intent.getStringExtra("postIds");

        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        textBox = findViewById(R.id.textBox);
        name = findViewById(R.id.name);
        ic_eye = findViewById(R.id.eye);
        attach = findViewById(R.id.imageView11);
        load = findViewById(R.id.load);
        views = findViewById(R.id.views);

        likeNo = findViewById(R.id.likeNo);
        like_img = findViewById(R.id.like_img);
        type = findViewById(R.id.username);
        view_ly = findViewById(R.id.view_ly);
        commentNo = findViewById(R.id.commentNo);


        vine = findViewById(R.id.videoView);

        text = findViewById(R.id.textView2);
        viewlt = findViewById(R.id.viewlt);
        constraintLayout9 = findViewById(R.id.constraintLayout9);
        circleImageView3 = findViewById(R.id.circleImageView3);
        recyclerView = findViewById(R.id.recyclerView);
        like = findViewById(R.id.relativeLayout);
        comment = findViewById(R.id.relativeLayout6);
        share = findViewById(R.id.meme_share);
        video_share = findViewById(R.id.vine_share);
        back = findViewById(R.id.imageView3);
        more = findViewById(R.id.more);
        meme = findViewById(R.id.imageView2);
        send = findViewById(R.id.imageView10);
        add = findViewById(R.id.imageView11);
        recyclerView.smoothScrollToPosition(0);
        recyclerView.setFocusable(false);


        HashTagHelper mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.colorPrimary), hashTag -> {
            Intent intent1 = new Intent(PostDetails.this, Search.class);
            intent1.putExtra("hashTag", hashTag);
            startActivity(intent1);
        });

        mTextHashTagHelper.handle(text);

        attach.setOnClickListener(v -> bottomSheetDialog.show());

        share.setOnClickListener(v -> bottomDialog.show());

        more.setOnClickListener(v -> showMoreOptions());
        back.setOnClickListener(v -> onBackPressed());

        loadPostInfo();
//        loadUserInfo();
//        setLikes();
//        setViews();
        loadComments();
//        createBottomSheetDialog();
//        createBottomDialog();
//        BottomDialog();
//        noLike();
        video_share.setOnClickListener(v -> bottom.show());

        send.setOnClickListener(v -> postComment());
        like.setOnClickListener(v -> likePost());

    }

    private void setViews() {
        DatabaseReference viewRef = FirebaseDatabase.getInstance().getReference().child("Views");
        viewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).hasChild(userId)) {

                    ic_eye.setImageResource(R.drawable.ic_eyed);

                } else {
                    ic_eye.setImageResource(R.drawable.ic_eye);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        commentsList = new ArrayList<>();

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<ModelComments>> call = apiService.getCommentsByPostId(postId);

        call.enqueue(new Callback<List<ModelComments>>() {
            @Override
            public void onResponse(Call<List<ModelComments>> call, Response<List<ModelComments>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commentsList.clear();
                    commentsList.addAll(response.body());
                    adapterComments = new AdapterComments(getApplicationContext(), commentsList);
                    recyclerView.setAdapter(adapterComments);
                } else {
                    // Handle unsuccessful fetch
                    Log.e("LoadComments", "Failed to load comments");
                    Toast.makeText(PostDetails.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ModelComments>> call, Throwable t) {
                // Handle failure
                Log.e("LoadComments", "Error loading comments: " + t.getMessage());
                Toast.makeText(PostDetails.this, "Error loading com"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void loadComments() {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(layoutManager);
//        commentsList = new ArrayList<>();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                commentsList.clear();
//                for(DataSnapshot ds: dataSnapshot.getChildren()){
//                    ModelComments modelComments = ds.getValue(ModelComments.class);
//                    commentsList.add(modelComments);
//                    adapterComments = new AdapterComments(getApplicationContext(), commentsList);
//                    recyclerView.setAdapter(adapterComments);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void createBottomSheetDialog() {
        if (bottomSheetDialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.add_bottom_sheet, null);
            constraintLayout3 = view.findViewById(R.id.constraintLayout3);
            delete = view.findViewById(R.id.delete);
            constraintLayout3.setOnClickListener(this);
            delete.setOnClickListener(this);
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }
    }

    private void createBottomDialog() {
        if (bottomDialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.share_bottom_sheet, null);
            chatshare = view.findViewById(R.id.chatshare);
            appshare = view.findViewById(R.id.appshare);
            groupShare = view.findViewById(R.id.groupShare);
            chatshare.setOnClickListener(this);
            groupShare.setOnClickListener(this);
            appshare.setOnClickListener(this);
            bottomDialog = new BottomSheetDialog(this);
            bottomDialog.setContentView(view);
        }
    }

    private void BottomDialog() {
        if (bottom == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.share_vid_bottom_sheet, null);
            chatvid = view.findViewById(R.id.chatvid);
            appvid = view.findViewById(R.id.appvid);
            chatvid.setOnClickListener(this);
            appvid.setOnClickListener(this);
            bottom = new BottomSheetDialog(this);
            bottom.setContentView(view);
        }
    }

    private void showMoreOptions() {
        PopupMenu popupMenu = new PopupMenu(this, more, Gravity.END);
        if (Id.equals(userId)) {
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
        }
        popupMenu.getMenu().add(Menu.NONE, 2, 0, "Liked By");
        if (!meme.equals("noImage") || !vine.equals("noVideo")) {
            popupMenu.getMenu().add(Menu.NONE, 3, 0, "Fullscreen");
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == 0) {
                beginDelete();
            } else if (id == 1) {
                Intent intent = new Intent(PostDetails.this, UpdatePost.class);
                intent.putExtra("key", "editPost");
                intent.putExtra("editPostId", postId);
                startActivity(intent);
            } else if (id == 2) {
                Intent intent = new Intent(PostDetails.this, PostLikedBy.class);
                intent.putExtra("postId", postId);
                startActivity(intent);
            } else if (id == 3) {
                if (!hisVine.equals("noVideo")) {
                    Intent intent = new Intent(getApplicationContext(), MediaView.class);
                    intent.putExtra("type", "video");
                    intent.putExtra("uri", hisVine);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (!hisMeme.equals("noImage")) {
                    Intent intent = new Intent(getApplicationContext(), MediaView.class);
                    intent.putExtra("type", "image");
                    intent.putExtra("uri", hisMeme);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }

            return false;
        });
        popupMenu.show();
    }

    private void beginDelete() {
        if (vine.equals("noVideo") && meme.equals("noImage")) {
            deleteWithoutBoth();
        } else if (vine.equals("noVideo")) {
            deleteWithoutVine();
        } else if (meme.equals("noImage")) {
            deleteWithoutMeme();
        }
    }

    private void deleteWithoutMeme() {
        StorageReference vidRef = FirebaseStorage.getInstance().getReferenceFromUrl(hisVine);
        vidRef.delete().addOnSuccessListener(aVoid -> {

            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }).addOnFailureListener(e -> {

        });
    }

    private void deleteWithoutVine() {


        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(hisMeme);
        picRef.delete().addOnSuccessListener(aVoid -> {

            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }).addOnFailureListener(e -> {

        });

    }

    private void deleteWithoutBoth() {
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void noLike() {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String numOfLikes = String.valueOf((int) snapshot.getChildrenCount());
                if (numOfLikes.equals("0")) {
                    likeNo.setText("Like");

                } else {
                    likeNo.setText(snapshot.getChildrenCount() + "");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setLikes() {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).hasChild(userId)) {

                    like_img.setImageResource(R.drawable.ic_liked);

                } else {
                    like_img.setImageResource(R.drawable.ic_like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likePost() {

        mProcessCLike = true;
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessCLike) {
                    if (dataSnapshot.child(postId).hasChild(userId)) {
                        likeRef.child(postId).child(userId).removeValue();
                        mProcessCLike = false;
                    } else {
                        likeRef.child(postId).child(userId).setValue("Liked");
                        mProcessCLike = false;
                        addToHisNotification("" + hisId, "" + postId, "Liked your post");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void postComment() {
        String comment = textBox.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            // Handle empty comment
            return;
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        ModelComments comments = new ModelComments(timeStamp, comment, hisDp, userId, hisName, timeStamp, "0", postId, "text", null);

        Call<ApiResponse> call = apiService.postComment(comments);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response: " + response.body().getMessage());
                    textBox.setText("");
//                    updateCommentCount();
                    Toast.makeText(PostDetails.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Failed to post comment: " + response.message());
                    Toast.makeText(PostDetails.this, "Failed to post comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(PostDetails.this,  t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



//    private void postComment() {
//        String comment = textBox.getText().toString().trim();
//        if (TextUtils.isEmpty(comment)){
//
//        }else {
//            String timeStamp = String.valueOf(System.currentTimeMillis());
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
//
//            HashMap<String, Object> hashMap = new HashMap<>();
//            hashMap.put("cId", timeStamp);
//            hashMap.put("comment", comment);
//            hashMap.put("timestamp", timeStamp);
//            hashMap.put("id", userId);
//            hashMap.put("pLikes", "0");
//            hashMap.put("type", "text");
//            hashMap.put("pId", postId);
//            hashMap.put("dp", myDp);
//            hashMap.put("mane", myName);
//
//            ref.child(timeStamp).setValue(hashMap)
//                    .addOnSuccessListener(aVoid -> {
//                        textBox.setText("");
//                        updateCommentCount();
//                    }).addOnFailureListener(e -> {
//
//                    });
//        }
//    }

    private void addToHisNotification(String hisUid, String pId, String notification){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", pId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", notification);
        hashMap.put("sUid", userId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {

                }).addOnFailureListener(e -> {

                });

    }
    private void updateCommentCount() {
        mProcessComment = true;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessComment){
                    String comments = ""+dataSnapshot.child("pComments").getValue();
                    int newCommentCal = Integer.parseInt(comments)+1;
                    ref.child("pComments").setValue(""+newCommentCal);
                    mProcessComment = false;
                    addToHisNotification(""+hisId,""+postId,"Commented on your post");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void loadUserInfo() {
        Query query = FirebaseDatabase.getInstance().getReference("Users");
        query.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    myName = ""+ds.child("name").getValue();
                    myDp = ""+ds.child("photo").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPostInfo(){

        ModelPostRequest request = new ModelPostRequest("getPostsByPid", postId);

        // Call API to get post by pId
        Call<ModelPost[]> call = userApi.getPostByPid(request);

        call.enqueue(new Callback<ModelPost[]>() {
            @Override
            public void onResponse(Call<ModelPost[]> call, Response<ModelPost[]> response) {
                if (response.isSuccessful() && response.body() != null && response.body().length > 0) {
                    ModelPost post = response.body()[0];
                    // Assuming there's only one post with the given pId
                    hisText = post.getText();
                    hisMeme = post.getMeme();
                    hisVine = post.getVine();
                    hisDp = post.getDp();
                    hisTime = post.getpTime();
                    hisTypes = post.getType();
                    String hisViews = post.getpViews();
                    hisName = post.getName();
                    hisId = post.getId();
                    pLikes = post.getpLikes();
                    String comment = post.getpComments();

                    // Update UI with post data
                    views.setText(hisViews);
                    text.setText(hisText);
                    name.setText(hisName);

                    // Example of time conversion (replace with your actual implementation)
                    if (hisTime != null && !hisTime.isEmpty()) {
                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        long lastTime = Long.parseLong(hisTime);
                        String lastSeenTime = getTimeAgo.getTimeAgo(lastTime);

                        type.setText(hisTypes +  " - " +lastSeenTime );


                        // Handle your time display here
                    }
                    // Load image using Picasso
                    try {
                        Picasso.get().load(hisDp).placeholder(R.drawable.avatar).into(circleImageView3);
                    } catch (Exception ignored) {}

                    // Handle image and video visibility
                    if (hisMeme.equals("noImage") &&  hisVine.equals("noVideo")) {
                        load.setVisibility(View.GONE);
                    }

                    // Handle video playback
                    if (!hisVine.equals("noVideo")) {
                        // Your video handling logic here
                    }

                    // Handle comment visibility
                    if (comment.equals("0")) {
                        commentNo.setText("Comment");
                    } else {
                        // Handle other cases
                    }

                    // Handle text visibility
                    String ed_text = text.getText().toString().trim();
                    constraintLayout9.setVisibility(ed_text.length() > 0 ? View.VISIBLE : View.GONE);

                    // Handle click listeners
                    name.setOnClickListener(v -> {
                        Intent intent = new Intent(PostDetails.this, UserProfile.class);
                        intent.putExtra("hisUid", hisId);
                        startActivity(intent);
                    });

                    circleImageView3.setOnClickListener(v -> {
                        Intent intent = new Intent(PostDetails.this, UserProfile.class);
                        intent.putExtra("hisUid", hisId);
                        startActivity(intent);
                    });

                    // Load post image using Picasso
                    if (!hisMeme.equals("noImage")) {
                        try {
                            Picasso.get().load(hisMeme).into(meme);
                        } catch (Exception ignored) {}
                    }
                    //Post Vine
                    if (hisVine.equals("noVideo")){
                        vine.setVisibility(View.GONE);
                        view_ly.setVisibility(View.GONE);
                        video_share.setVisibility(View.GONE);

                    }else {

                        DataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                                .setUserAgent(userAgent)
                                .setKeepPostFor302Redirects(true)
                                .setAllowCrossProtocolRedirects(true)
                                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                                .setDefaultRequestProperties(defaultRequestProperties);
                        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(PostDetails.this, httpDataSourceFactory);
                        MediaItem mediaItem = new MediaItem.Builder()
                                .setUri(hisVine)
                                .setMimeType(MimeTypes.APPLICATION_MP4)
                                .build();

                        MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
                        ExoTrackSelection.Factory videoSelector = new AdaptiveTrackSelection.Factory();
                        trackSelector = new DefaultTrackSelector(PostDetails.this, videoSelector);
                        exoPlayer = new ExoPlayer.Builder(PostDetails.this)
                                .setTrackSelector(trackSelector)
                                .setSeekForwardIncrementMs(10000)
                                .setSeekBackIncrementMs(10000)
                                .build();
                        vine.setPlayer(exoPlayer);
                        vine.setKeepScreenOn(true);  // keep screen on == consume user battery;
                        exoPlayer.setMediaSource(progressiveMediaSource);
                        exoPlayer.prepare();
                        exoPlayer.setPlayWhenReady(true);
                    }



                    // Handle other post details here as needed
                } else {
                    Toast.makeText(PostDetails.this, "Failed to get post details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelPost[]> call, Throwable t) {
                Toast.makeText(PostDetails.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }














//    private void loadPostInfo() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
//        Query query = ref.orderByChild("pId").equalTo(postId);
//        query.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot ds : dataSnapshot.getChildren()){
//                    hisText = ""+ds.child("text").getValue();
//                    hisMeme = ""+ds.child("meme").getValue();
//                    hisVine = ""+ds.child("vine").getValue();
//                    hisDp = ""+ds.child("dp").getValue();
//                    hisTime = ""+ds.child("pTime").getValue();
//                    hisTypes = ""+ds.child("type").getValue();
//                    String hisViews = ""+ds.child("pViews").getValue();
//                    hisName = ""+ds.child("name").getValue();
//                    hisId = ""+ds.child("id").getValue();
//                    pLikes = ""+ds.child("pLikes").getValue();
//                    String comment = ""+ds.child("pComments").getValue();
//
//                    GetTimeAgo getTimeAgo = new GetTimeAgo();
//                    long lastTime = Long.parseLong(hisTime);
//                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
//
//                    views.setText(hisViews);
//                    text.setText(hisText);
//                    //DP
//                    try {
//                        Picasso.get().load(hisDp).placeholder(R.drawable.avatar).into(circleImageView3);
//                    }catch (Exception ignored){
//
//                    }
//
//                    if (hisMeme.equals("noImage") &&  hisVine.equals("noVideo")){
//                        load.setVisibility(View.GONE);
//                    }
//
//                    //hisTime
//                    type.setText(hisTypes +  " - " +lastSeenTime );
//                    name.setText(hisName);
//                    if (comment.equals("0")){
//                        commentNo.setText("Comment");
//
//                    }else {
//                        commentNo.setText(comment);
//                    }
//
//                    String ed_text = text.getText().toString().trim();
//                    if(ed_text.length() > 0)
//                    {
//                        constraintLayout9.setVisibility(View.VISIBLE);
//
//                    }
//                    else
//                    {
//                        constraintLayout9.setVisibility(View.GONE);
//                    }
//
//                    name.setOnClickListener(v -> {
//                        Intent intent = new Intent(PostDetails.this, UserProfile.class);
//                        intent.putExtra("hisUid", hisId);
//                        startActivity(intent);
//                    });
//                    circleImageView3.setOnClickListener(v -> {
//                        Intent intent = new Intent(PostDetails.this, UserProfile.class);
//                        intent.putExtra("hisUid", hisId);
//                        startActivity(intent);
//                    });
//
//                    //Post Image
//                    if (hisMeme.equals("noImage")){
//                        meme.setVisibility(View.GONE);
//                    }else {
//                        try {
//                            Picasso.get().load(hisMeme).into(meme);
//                        }catch (Exception ignored){
//
//                        }
//                    }
//
//
//                    //Post Vine
//                    if (hisVine.equals("noVideo")){
//                        vine.setVisibility(View.GONE);
//                        view_ly.setVisibility(View.GONE);
//                  video_share.setVisibility(View.GONE);
//
//                    }else {
//
//                        DataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
//                                .setUserAgent(userAgent)
//                                .setKeepPostFor302Redirects(true)
//                                .setAllowCrossProtocolRedirects(true)
//                                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
//                                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
//                                .setDefaultRequestProperties(defaultRequestProperties);
//                        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(PostDetails.this, httpDataSourceFactory);
//                        MediaItem mediaItem = new MediaItem.Builder()
//                                .setUri(hisVine)
//                                .setMimeType(MimeTypes.APPLICATION_MP4)
//                                .build();
//
//                        MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
//                        ExoTrackSelection.Factory videoSelector = new AdaptiveTrackSelection.Factory();
//                        trackSelector = new DefaultTrackSelector(PostDetails.this, videoSelector);
//                        exoPlayer = new ExoPlayer.Builder(PostDetails.this)
//                                .setTrackSelector(trackSelector)
//                                .setSeekForwardIncrementMs(10000)
//                                .setSeekBackIncrementMs(10000)
//                                .build();
//                        vine.setPlayer(exoPlayer);
//                        vine.setKeepScreenOn(true);  // keep screen on == consume user battery;
//                        exoPlayer.setMediaSource(progressiveMediaSource);
//                        exoPlayer.prepare();
//                        exoPlayer.setPlayWhenReady(true);
//
//
//
//                    }
//
//
//
//
////                        Uri vineUri = Uri.parse(hisVine);
////                     vine.setVideoURI(vineUri);
////
////                     vine.start();
////
////                        MediaController mediaController = new MediaController(PostDetails.this);
////                        mediaController.setAnchorView(vine);
////                        vine.setMediaController(mediaController);
////
////                        vine.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
////                            @Override
////                            public void onPrepared(MediaPlayer mp) {
////                                mp.setLooping(true);
////                            }
////                        });
//
//
//
//
//                }
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void shareImageAndText(String text, Bitmap bitmap) {
        Uri uri = saveImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(this.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdir();
            File file = new File(imageFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.ohmshantiapps.fileprovider", file);

        } catch (Exception e) {
            StyleableToast st = new StyleableToast(this, e.getMessage(), Toast.LENGTH_LONG);
            st.setBackgroundColor(Color.parseColor("#001E55"));
            st.setTextColor(Color.WHITE);
            st.setIcon(R.drawable.ic_check_wt);
            st.setMaxAlpha();
            st.show();
        }
        return uri;
    }

    private void shareTextOnly(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        intent.putExtra(Intent.EXTRA_TEXT, text);
      startActivity(Intent.createChooser(intent, "Share Via"));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.constraintLayout3) {
            // Check Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                        == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_MEDIA_IMAGES};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    pickImageFromGallery();
                }
            } else {
                pickImageFromGallery();
            }
        } else if (id == R.id.delete) {
            // Check Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO)
                        == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_MEDIA_VIDEO};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    chooseVideo();
                }
            } else {
                chooseVideo();
            }
        } else if (id == R.id.chatshare || id == R.id.chatvid) {
            Intent intent = new Intent(PostDetails.this, ShareActivity.class);
            intent.putExtra("postId", postId);
            startActivity(intent);
        } else if (id == R.id.appshare) {
            String shareText = text.getText().toString().trim();
            BitmapDrawable bitmapDrawable = (BitmapDrawable) meme.getDrawable();
            if (bitmapDrawable == null) {
                shareTextOnly(shareText);
            } else {
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageAndText(shareText, bitmap);
            }
        } else if (id == R.id.appvid) {
            String shareBody = hisText;
            String shareUrl = hisVine;
            Intent intent2 = new Intent(Intent.ACTION_SEND);
            intent2.setType("text/*");
            intent2.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
            intent2.putExtra(Intent.EXTRA_TEXT,shareBody+" Link: "+shareUrl);
            startActivity(Intent.createChooser(intent2, "Share Via"));
        } else if (id == R.id.groupShare) {
            Intent intent4 = new Intent(PostDetails.this, ShareGroupActivity.class);
            intent4.putExtra("postId", postId);
            startActivity(intent4);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Alerter.create(PostDetails.this)
                        .setTitle("Successful")
                        .setIcon(R.drawable.ic_check_wt)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText("Storage permission Allowed")
                        .show();
            } else {
                Alerter.create(PostDetails.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText("Storage permission is required")
                        .show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        bottomSheetDialog.cancel();
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Uri image_uri = Objects.requireNonNull(data).getData();
            sendImage(image_uri);
            bottomSheetDialog.cancel();
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri video_uri = data.getData();
            sendVideo(video_uri);
            bottomSheetDialog.cancel();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendImage(Uri image_uri) {
        String timeStamp = ""+System.currentTimeMillis();
        String filenameAndPath = "ChatImages/"+"post_"+System.currentTimeMillis();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
        ref.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("cId", timeStamp);
                hashMap.put("comment", downloadUri);
                hashMap.put("timestamp", timeStamp);
                hashMap.put("id", userId);
                hashMap.put("pLikes", "0");
                hashMap.put("type", "image");
                hashMap.put("pId", postId);
                hashMap.put("dp", myDp);
                hashMap.put("mane", myName);

                ref1.child(timeStamp).setValue(hashMap)
                        .addOnSuccessListener(aVoid -> {
                            textBox.setText("");
                            updateCommentCount();
                        }).addOnFailureListener(e -> {

                });


            }
        }).addOnFailureListener(e -> {

        });
    }

    private void sendVideo(Uri video_uri) {
        String timeStamp = ""+System.currentTimeMillis();
        String filenameAndPath = "ChatImages/"+"post_"+System.currentTimeMillis();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
        ref.putFile(video_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("cId", timeStamp);
                hashMap.put("comment", downloadUri);
                hashMap.put("timestamp", timeStamp);
                hashMap.put("id", userId);
                hashMap.put("pLikes", "0");
                hashMap.put("type", "video");
                hashMap.put("pId", postId);
                hashMap.put("dp", myDp);
                hashMap.put("mane", myName);

                ref1.child(timeStamp).setValue(hashMap)
                        .addOnSuccessListener(aVoid -> {
                            textBox.setText("");
                            updateCommentCount();
                        }).addOnFailureListener(e -> {

                });


            }
        }).addOnFailureListener(e -> {

        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }
    private String getfileExt(Uri video_uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(video_uri));
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(!hisVine.equals("noVideo")){
            exoPlayer.pause();


        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        if(!hisVine.equals("noVideo")){
            exoPlayer.pause();


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        exoPlayer.setPlayWhenReady(true);
    }


}