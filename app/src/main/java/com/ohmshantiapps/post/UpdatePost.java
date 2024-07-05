package com.ohmshantiapps.post;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;

import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;

import androidx.media3.ui.PlayerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.ModelPostRequest;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.api.UserApiClient;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.model.Users;
import com.tapadoo.alerter.Alerter;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class UpdatePost extends AppCompatActivity {
    SharedPref sharedPref;
    private static final int PICK_VIDEO_REQUEST = 1;

    ImageView meme,cancel;
    PlayerView vines;

    private ExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private GestureDetector gestureDetector;
    private FrameLayout playerContainer;
    ApiService userApi;
    ConstraintLayout add_meme,add_vines,update_remove,remove;
    Button update_it, update_vine;
    EditText text;
    TextView mName,type;
    CircleImageView circleImageView3;
    ProgressBar pd;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String name, dp, id;
    String editText, editMeme, editVine;


    String editPostId;

    private Uri image_uri, video_uri;
    MediaController mediaController;
    private UserApiClient userApiClient;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);

        meme = findViewById(R.id.meme);
        add_meme = findViewById(R.id.constraintLayout3);
        update_remove = findViewById(R.id.update_remove);
        remove = findViewById(R.id.remove);
        text = findViewById(R.id.post_text);
        update_it = findViewById(R.id.post_it);
        update_vine = findViewById(R.id.post_vine);
        type = findViewById(R.id.username);
        mName = findViewById(R.id.name);
        circleImageView3 = findViewById(R.id.circleImageView3);
        pd = findViewById(R.id.pb);
        cancel = findViewById(R.id.imageView);
        cancel.setOnClickListener(v -> onBackPressed());
        add_vines = findViewById(R.id.vines_lt);
        vines = findViewById(R.id.vines);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(vines);
        MediaController ctrl = new MediaController(UpdatePost.this);
        ctrl.setVisibility(View.GONE);

        userApi = RetrofitClient.getClient().create(ApiService.class);

        userApiClient = new UserApiClient();
        SessionManager sessionManager = new SessionManager(this);

        int userId = Integer.parseInt(sessionManager.getUserId());

        userApiClient.fetchUser(userId, new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    Users user = response.body();
                    if (user != null) {
                        // Extract and display the user's name and email
                        name = user.getName();
                        dp = user.getPhoto();
                        mName.setText(name);

                        Glide.with(UpdatePost.this)
                                .load(dp)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .error(R.drawable.avatar)  // Add an error image
                                .into(circleImageView3);


                    }



                } else {
                    // Show a toast message indicating the failure
                    Toast.makeText(UpdatePost.this, "Failed to fetch user: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {

                Toast.makeText(UpdatePost.this, "Failed to fetch user: ", Toast.LENGTH_SHORT).show();
            }
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                int screenWidth = vines.getWidth();
                float x = e.getX();
                if (x < screenWidth / 2) {
                    // Double-tap on the left side: rewind
                    if (player != null) {
                        player.seekTo(Math.max(player.getCurrentPosition() - 10000, 0));
                    }
                } else {
                    // Double-tap on the right side: forward
                    if (player != null) {
                        player.seekTo(player.getCurrentPosition() + 10000);
                    }
                }
                return true;
            }
        });

        vines.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));


        Intent intent = getIntent();
        String isUpdateKey = ""+intent.getStringExtra("key");
        editPostId = ""+intent.getStringExtra("editPostId");

        if (isUpdateKey.equals("editPost")){
            loadPostData(editPostId);

        }

        remove.setVisibility(View.GONE);
        add_meme.setVisibility(View.GONE);
        add_vines.setVisibility(View.GONE);
        update_vine.setVisibility(View.GONE);
        update_it.setOnClickListener(v -> {
            String mText = text.getText().toString().trim();

            if (TextUtils.isEmpty(mText)) {
                Alerter.create(UpdatePost.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Objects.requireNonNull(ResourcesCompat.getFont(UpdatePost.this, R.font.bold)))
                        .setTextTypeface(Objects.requireNonNull(ResourcesCompat.getFont(UpdatePost.this, R.font.med)))
                        .enableSwipeToDismiss()
                        .setText("Enter caption")
                        .show();
                return;
            }
//
            else {
                updateData(mText);
                pd.setVisibility(View.VISIBLE);
            }

        });


        update_remove.setVisibility(View.GONE);

    }




    private void updateData(String mText) {

        userApi = RetrofitClient.getClient().create(ApiService.class);
        ModelPost modelPost = new ModelPost(null,null,null,null,null,editPostId,null,mText,null,null,null,null);
        Call<Void> call2 = userApi.updatePostByPid(Long.parseLong(editPostId),modelPost);
        call2.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()){

                    text.setText("");
                    vines.setVisibility(View.GONE);
                    update_vine.setVisibility(View.GONE);
                    update_it.setVisibility(View.VISIBLE);
                    type.setText("Text");
                    pd.setVisibility(View.GONE);
                    update_remove.setVisibility(View.GONE);
                    update_remove.setVisibility(View.GONE);
                    onBackPressed();
                    Alerter.create(UpdatePost.this)
                            .setTitle("Successfull")
                            .setIcon(R.drawable.ic_check_wt)
                            .setBackgroundColorRes(R.color.colorPrimaryDark)
                            .setDuration(10000)
                            .enableSwipeToDismiss()
                            .setTitleTypeface(Objects.requireNonNull(ResourcesCompat.getFont(UpdatePost.this, R.font.bold)))
                            .setTextTypeface(Objects.requireNonNull(ResourcesCompat.getFont(UpdatePost.this, R.font.med)))
                            .setText("Post Updated")
                            .show();




                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {

                pd.setVisibility(View.GONE);
                Alerter.create(UpdatePost.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_check_wt)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setTitleTypeface(Objects.requireNonNull(ResourcesCompat.getFont(UpdatePost.this, R.font.bold)))
                        .setTextTypeface(Objects.requireNonNull(ResourcesCompat.getFont(UpdatePost.this, R.font.med)))
                        .setText(throwable.getMessage())
                        .show();

            }
        });

    }

    private void loadPostData(String editPostId) {

        ModelPostRequest request = new ModelPostRequest("getPostsByPid", editPostId);

        // Call API to get post by pId
        Call<ModelPost[]> call = userApi.getPostByPid(request);

        call.enqueue(new Callback<ModelPost[]>() {
            @Override
            public void onResponse(Call<ModelPost[]> call, Response<ModelPost[]> response) {
                if (response.isSuccessful() && response.body() != null && response.body().length > 0) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedPostId", editPostId);
                    setResult(Activity.RESULT_OK, resultIntent);

                    ModelPost post = response.body()[0];
                    editText = post.getText();
                    editMeme = post.getMeme();
                    editVine = post.getVine();
                    update_remove.setVisibility(View.GONE);
                    update_it.setVisibility(View.VISIBLE);
                    update_vine.setVisibility(View.GONE);
                    text.setText(editText);
                    if (!editMeme.equals("noImage")) {
                        try {
                            Glide.with(UpdatePost.this)
                                    .load(editMeme)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(meme);
                            meme.setVisibility(View.VISIBLE);
                            vines.setVisibility(View.GONE);
                            update_it.setVisibility(View.VISIBLE);
                            update_vine.setVisibility(View.GONE);
                            update_remove.setVisibility(View.VISIBLE);
                        } catch (Exception ignored) {

                        }
                    }
                    if (!editVine.equals("noVideo")) {
                        try {
//                            vines.setVideoPath(editVine);
                            initializePlayer(editVine);
                            vines.setVisibility(View.VISIBLE);
                            meme.setVisibility(View.GONE);
                            update_it.setVisibility(View.VISIBLE);
                            update_remove.setVisibility(View.INVISIBLE);
                            update_vine.setVisibility(View.INVISIBLE);
                        } catch (Exception ignored) {

                        }
                    }

                } else {
                    Toast.makeText(UpdatePost.this, "Failed to get post details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelPost[]> call, Throwable t) {
                pd.setVisibility(View.GONE);
                Alerter.create(UpdatePost.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_check_wt)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText(t.getMessage())
                        .show();
                Toast.makeText(UpdatePost.this, "Network error: ", Toast.LENGTH_SHORT).show();
            }
        });


    }




    private void initializePlayer(String url) {
        player = new ExoPlayer.Builder(this).build();
        vines.setPlayer(player);

        Uri uri = Uri.parse(url);
        MediaSource mediaSource = buildMediaSource(uri);

        player.setMediaSource(mediaSource);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Toast.makeText(UpdatePost.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        player.prepare();
    }

    private MediaSource buildMediaSource(Uri uri) {
        DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent("Ohm.app.main");

        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(uri)
                .build();

        if (uri.toString().contains(".m3u8")) {
            return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
        } else if (uri.toString().contains(".mpd")) {
            return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
        } else {
            return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
        }
    }



    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
                releasePlayer();

    }

    @Override
    protected void onStop() {
        super.onStop();
            releasePlayer();


    }

}