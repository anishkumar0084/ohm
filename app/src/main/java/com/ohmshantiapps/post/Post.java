package com.ohmshantiapps.post;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import okhttp3.RequestBody;
import okhttp3.MultipartBody;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.CountingRequestBody;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.api.UserApiClient;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.model.Users;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class Post extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;
    ImageView meme,cancel;
    VideoView vines;
    ConstraintLayout add_meme,add_vines,remove_lt;
    Button post, post_vine;
    EditText text;
    TextView mName,type;
    CircleImageView circleImageView3;
    ProgressBar pd;
    ApiService apiService;
    String name, dp, id,mText, imageUrl;
    private Uri image_uri, video_uri;
    MediaController mediaController;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private UserApiClient userApiClient;


    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);






        meme = findViewById(R.id.meme);
        add_meme = findViewById(R.id.constraintLayout3);
        post = findViewById(R.id.post_it);
        post_vine = findViewById(R.id.post_vine);
        text = findViewById(R.id.post_text);
        type = findViewById(R.id.username);
        mName = findViewById(R.id.name);
        circleImageView3 = findViewById(R.id.circleImageView3);
        pd = findViewById(R.id.pb);
        cancel = findViewById(R.id.imageView);
        cancel.setOnClickListener(v -> onBackPressed());
        add_vines = findViewById(R.id.vines_lt);
        remove_lt = findViewById(R.id.remove_lt);
        vines = findViewById(R.id.vines);
        mediaController = new MediaController(this);
        vines.setMediaController(mediaController);
        mediaController.setAnchorView(vines);
        MediaController ctrl = new MediaController(Post.this);
        ctrl.setVisibility(View.GONE);
        vines.setMediaController(ctrl);
        vines.start();
        vines.setOnPreparedListener(mp -> mp.setLooping(true));
        remove_lt.setOnClickListener(v -> {

            meme.setImageURI(null);
            image_uri = null;
             post.setVisibility(View.VISIBLE);
            vines.setVisibility(View.GONE);
             post_vine.setVisibility(View.GONE);
            remove_lt.setVisibility(View.GONE);
            type.setText("Text");
        });





        add_vines.setOnClickListener(v -> {
//            Check Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO)
                        == PackageManager.PERMISSION_DENIED){
                    String[] permissions = {Manifest.permission.READ_MEDIA_VIDEO};
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else {
                    chooseVideo();
                }
            }
            else {
                chooseVideo();
            }

        });
        apiService = RetrofitClient.getClient().create(ApiService.class);



        // Initialize your UserApiClient
        userApiClient = new UserApiClient();
        SessionManager sessionManager = new SessionManager(this);

        int userId = Integer.parseInt(sessionManager.getUserId()); // Example user ID
        id=String.valueOf(userId);







        userApiClient.fetchUser(userId, new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    Users user = response.body();
                    if (user != null) {
                        // Extract and display the user's name and email
                        name = user.getName();
                        String userEmail = user.getEmail();
                        dp = user.getPhoto();
                        mName.setText(name);

                        if (dp==null){

                            dp="uuuu";


                        }



                        try {
                            Picasso.get().load(dp).placeholder(R.drawable.avatar).into(circleImageView3);

                            post.setOnClickListener(v -> {

                                mText = text.getText().toString().trim();

                                if (TextUtils.isEmpty(mText)) {
                                    Alerter.create(Post.this)
                                            .setTitle("Error")
                                            .setIcon(R.drawable.ic_error)
                                            .setBackgroundColorRes(R.color.colorPrimary)
                                            .setDuration(10000)
//                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                            .enableSwipeToDismiss()
                                            .setText("Enter caption")
                                            .show();
                                    return;
                                }
                                if (image_uri == null){
                                    pd.setVisibility(View.VISIBLE);
                                  uploadData(mText, "noImage",dp,name);
                                }
                                else {






                                    uploadData(mText, String.valueOf(image_uri),dp,name);

                                    pd.setVisibility(View.VISIBLE);



                                }

                            });
                            post_vine.setOnClickListener(v -> {
                                pd.setVisibility(View.VISIBLE);
                                String mText = text.getText().toString().trim();

                                if (TextUtils.isEmpty(mText)) {
                                    Alerter.create(Post.this)
                                            .setTitle("Error")
                                            .setIcon(R.drawable.ic_error)
                                            .setBackgroundColorRes(R.color.colorPrimary)
                                            .setDuration(10000)
//                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                            .enableSwipeToDismiss()
                                            .setText("Enter caption")
                                            .show();
                                }else {
//                                    uploadVine(mText, String.valueOf(video_uri));
                                    uploadVideo(String.valueOf(video_uri),dp,name,mText);
                                }

                            });


                        }
                                catch (Exception e ){
                           Picasso.get().load(R.drawable.avatar).into(circleImageView3);
                                }


                    } else {
                        showFetchError();
                    }
                } else {
                    // Show a toast message indicating the failure
                    Toast.makeText(Post.this, "Failed to fetch user: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                // Show a toast message indicating the failure
                showError(t);
            }
        });



        add_meme.setOnClickListener(v -> {

            type.setText("Meme");
            //Check Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                        == PackageManager.PERMISSION_DENIED){
                    String[] permissions = {Manifest.permission.READ_MEDIA_IMAGES};
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else {
                    pickImageFromGallery();
                }
            }
            else {
                pickImageFromGallery();
            }
        });


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type!=null){
          if ("text/plain".equals(type)){
              sendText(intent);
          }
        else if (type.startsWith("image")){
                sendImage(intent);
            }else if (type.startsWith("video")){
                sendVideo(intent);
            }
        }








    }


    private void showFetchError() {
        Alerter.create(this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(10000)
                .enableSwipeToDismiss()
                .setText("Failed to fetch user profile")
                .show();
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



    private void sendVideo(Intent intent) {
        Uri videoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (videoUri != null){
            video_uri = videoUri;
            vines.setVideoURI(video_uri);
            vines.setVisibility(View.VISIBLE);
            remove_lt.setVisibility(View.VISIBLE);
            post_vine.setVisibility(View.VISIBLE);
            post.setVisibility(View.GONE);
        }
    }

    private void sendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null){
            image_uri = imageUri;
            meme.setImageURI(image_uri);
            meme.setVisibility(View.VISIBLE);
            remove_lt.setVisibility(View.VISIBLE);

        }
    }

    private void sendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText!=null){
            text.setText(sharedText);
        }
    }







    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        android.database.Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }

    private void uploadData(String mText, String uri, String dp, String name) {
        if (!uri.equals("noImage")) {
            if (image_uri == null) {
                Log.e("Upload", "No image selected.");
                return;
            }

            File file = new File(getRealPathFromURI(image_uri));

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", file.getName(),
                            RequestBody.create(MediaType.parse("image/*"), file))
                    .build();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://68.183.245.154//img.php") // Replace "YOUR_BASE_URL" with your actual base URL
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                    // Handle failure...
                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                    final String responseBody = response.body().string();
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            String status = jsonResponse.getString("status");
                            if ("success".equals(status)) {
                                String imageUrl = jsonResponse.getString("imageUrl");

                                String timeStamp = String.valueOf(System.currentTimeMillis());
                                ModelPost modelPost = new ModelPost(dp, id, imageUrl, name, null, timeStamp, timeStamp, mText, "noVideo", "Image", null, null);
                                userApiClient.insertModelPost(modelPost);
                                runOnUiThread(() -> {
                                    text.setText("");
                                    meme.setImageURI(null);
                                    image_uri = null;
                                    remove_lt.setVisibility(View.GONE);
                                    pd.setVisibility(View.GONE);
                                    type.setText("Text");
                                    Alerter.create(Post.this)
                                            .setTitle("Successful")
                                            .setIcon(R.drawable.ic_check_wt)
                                            .setBackgroundColorRes(R.color.colorPrimaryDark)
                                            .setDuration(10000)
                                            .enableSwipeToDismiss()
                                            .setText("Post Uploaded")
                                            .show();
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(Post.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        } else {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            ModelPost modelPost = new ModelPost(dp, id, "noImage", name, "1", timeStamp, timeStamp, mText, "noVideo", "Text", "1", "1");
            userApiClient.insertModelPost(modelPost);
            runOnUiThread(() -> {
                text.setText("");
                meme.setImageURI(null);
                image_uri = null;
                remove_lt.setVisibility(View.GONE);
                pd.setVisibility(View.GONE);
                type.setText("Text");
                Alerter.create(Post.this)
                        .setTitle("Successful")
                        .setIcon(R.drawable.ic_check_wt)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setText("Post Uploaded")
                        .show();
            });

        }
    }


    private String getRealPathFromVideoURI(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }



    private void uploadVideo(String videoUri, String dp, String name, String mText) {
        if (videoUri.equals("noVideo")) {
            Log.e("Upload Video", "No video selected.");
            return;
        }

        File videoFile = new File(getRealPathFromVideoURI(Uri.parse(videoUri)));
        long fileSize = videoFile.length();

        OkHttpClient client = new OkHttpClient();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "upload_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Video Upload", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Video Upload")
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.ic_share)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true);

        notificationManager.notify(1, builder.build());

        RequestBody videoRequestBody = RequestBody.create(MediaType.parse("video/*"), videoFile);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("video", videoFile.getName(), new CountingRequestBody(videoRequestBody, (bytesWritten, contentLength) -> {
                    int progress = (int) (bytesWritten * 100 / contentLength);
                    builder.setProgress(100, progress, false);
                    builder.setContentText(progress + "% uploaded");
                    notificationManager.notify(1, builder.build());
                }))
                .build();

        Request request = new Request.Builder()
                .url("http://68.183.245.154/upload.php") // Replace with your actual video upload URL
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    builder.setContentText("Upload failed")
                            .setProgress(0, 0, false)
                            .setOngoing(false);
                    notificationManager.notify(1, builder.build());
                    Toast.makeText(Post.this, "Failed to upload video.", Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String status = jsonResponse.getString("status");
                        if ("success".equals(status)) {
                            String videoUrl = jsonResponse.getString("videoUrl");

                            String timeStamp = String.valueOf(System.currentTimeMillis());
                            ModelPost modelPost = new ModelPost(dp, id, "noImage", name, "1", timeStamp, timeStamp, mText, videoUrl, "Video", "1", "1");
                            userApiClient.insertModelPost(modelPost);
                            runOnUiThread(() -> {
                                text.setText("");
                                vines.setVideoURI(null);
                                video_uri = null;
                                vines.setVisibility(View.GONE);
                                post_vine.setVisibility(View.GONE);
                                post.setVisibility(View.VISIBLE);
                                type.setText("Text");
                                pd.setVisibility(View.GONE);
                                remove_lt.setVisibility(View.GONE);
                                Alerter.create(Post.this)
                                        .setTitle("Successful")
                                        .setIcon(R.drawable.ic_check_wt)
                                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                                        .setDuration(10000)
                                        .enableSwipeToDismiss()
                                        .setText("Video Uploaded")
                                        .show();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(Post.this, "Failed to upload video.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(Post.this, "Failed to upload video.", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        builder.setContentText("Upload failed")
                                .setProgress(0, 0, false)
                                .setOngoing(false);
                        notificationManager.notify(1, builder.build());
                        Toast.makeText(Post.this, "Failed to upload video.", Toast.LENGTH_SHORT).show();
                    });
                }
                response.close();
            }
        });
    }





    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Alerter.create(Post.this)
                        .setTitle("Successful")
                        .setIcon(R.drawable.ic_check_wt)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
//                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText("Storage permission Allowed")
                        .show();
            } else {
                Alerter.create(Post.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
//                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText("Storage permission is required")
                        .show();
            }
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE   && data != null && data.getData() != null){
            image_uri = data.getData();
            meme.setImageURI(image_uri);
            post_vine.setVisibility(View.GONE);
            post.setVisibility(View.VISIBLE);
            meme.setVisibility(View.VISIBLE);
            vines.setVisibility(View.GONE);
            post_vine.setVisibility(View.GONE);
            remove_lt.setVisibility(View.VISIBLE);
            type.setText("Image");
        }if (image_uri == null){
            meme.setVisibility(View.GONE);
            post.setVisibility(View.VISIBLE);
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            video_uri = data.getData();
            vines.setVideoURI(video_uri);
            post.setVisibility(View.GONE);
            remove_lt.setVisibility(View.VISIBLE);
            vines.setVisibility(View.VISIBLE);
            post_vine.setVisibility(View.VISIBLE);
            meme.setVisibility(View.GONE);
            type.setText("Video");
        }
        if (video_uri == null){
            vines.setVisibility(View.GONE);
        }
    }

    private String getfileExt(Uri video_uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(video_uri));
    }

}