package com.ohmshantiapps.user;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.Player;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.ExoTrackSelection;
import androidx.media3.ui.PlayerView;

import com.ohmshantiapps.R;
import com.ohmshantiapps.post.PostDetails;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class MediaView extends AppCompatActivity {

    ImageView image;
//    SimpleExoPlayerView vine;
//    SimpleExoPlayer exoPlayer;
    ExoPlayer exoPlayer;
    PlayerView vine;
    Map<String, String> defaultRequestProperties = new HashMap<>();
    String userAgent = "";
    private DefaultTrackSelector trackSelector;
    String type,uri;
    @SuppressWarnings("Convert2Lambda")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media_view);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        uri = intent.getStringExtra("uri");

        image = findViewById(R.id.image);
        vine = findViewById(R.id.videoView);




        if (type.equals("image")){
            image.setVisibility(View.VISIBLE);
           try {
               Picasso.get().load(uri).into(image);
           }catch (Exception ignored){

           }
        }
        if (type.equals("video")){
            image.setVisibility(View.GONE);
            vine.setVisibility(View.VISIBLE);
//
////            Uri vineUri = Uri.parse(uri);

            DataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                    .setUserAgent(userAgent)
                    .setKeepPostFor302Redirects(true)
                    .setAllowCrossProtocolRedirects(true)
                    .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                    .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                    .setDefaultRequestProperties(defaultRequestProperties);
            DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(MediaView.this, httpDataSourceFactory);
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(uri)
                    .setMimeType(MimeTypes.APPLICATION_MP4)
                    .build();

            MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
            ExoTrackSelection.Factory videoSelector = new AdaptiveTrackSelection.Factory();
            trackSelector = new DefaultTrackSelector(MediaView.this, videoSelector);
            exoPlayer = new ExoPlayer.Builder(MediaView.this)
                    .setTrackSelector(trackSelector)
                    .setSeekForwardIncrementMs(10000)
                    .setSeekBackIncrementMs(10000)
                    .build();
            vine.setPlayer(exoPlayer);
            vine.setKeepScreenOn(true);  // keep screen on == consume user battery;
            exoPlayer.setMediaSource(progressiveMediaSource);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);


//
//            try {
//
//                // BandwidthMeter is used for
//                // getting default bandwidth
//                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//
//                // track selector is used to navigate between
//                // video using a default seekbar.
//                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
//
//                // we are adding our track selector to exoplayer.
//                exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
//
//                // we are parsing a video url
//                // and parsing its video uri.
//                Uri videouri = Uri.parse(uri);
//
//                // we are creating a variable for datasource factory
//                // and setting its user agent as 'exoplayer_view'
//                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
//
//                // we are creating a variable for extractor factory
//                // and setting it to default extractor factory.
//                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//
//                // we are creating a media source with above variables
//                // and passing our event handler as null,
//                MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
//
//                // inside our exoplayer view
//                // we are setting our player
//                vine.setPlayer(exoPlayer);
//
//                // we are preparing our exoplayer
//                // with media source.
//                exoPlayer.prepare(mediaSource);
//
//
//                // we are setting our exoplayer
//                // when it is ready.
//                exoPlayer.setPlayWhenReady(true);
//
//            } catch (Exception e) {
//                // below line is used for
//                // handling our errors.
//                Log.e("TAG", "Error : " + e.toString());
//            }
        }

//            vine.setVideoURI(vineUri);
//            vine.start();
//
//            MediaController mediaController = new MediaController(MediaView.this);
//            mediaController.setAnchorView(vine);
//            vine.setMediaController(mediaController);
//
//            //noinspection Convert2Lambda
//            vine.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    mp.setLooping(true);
//                }
//            });




    }
    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.pause();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        exoPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        exoPlayer.setPlayWhenReady(true);
    }

}