package com.ohmshantiapps.settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.ohmshantiapps.Adpref;
import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.api.ApiService;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout name_layout, username_layout, bio_layout, web_layout, location_layout;
    ImageView edit, settings, menu;
    CircleImageView profile_image;
    ProgressBar pb;
    ConstraintLayout constraintLayout3,delete;
    BottomSheetDialog bottomSheetDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;
    String dbImage;
    private Uri image_uri;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    SharedPref sharedPref;
    UserApiClient userApiClient;
    ApiService userApi;
    String photoUrl;
    TextView name,username,bio,link,location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);
        link = findViewById(R.id.link);
        location = findViewById(R.id.location);

        userApiClient = new UserApiClient();
        userApi = RetrofitClient.getClient().create(ApiService.class);

        SessionManager sessionManager = new SessionManager(this);

        userId = String.valueOf(Integer.parseInt(sessionManager.getUserId()));
        name_layout = findViewById(R.id.name_layout);
        username_layout = findViewById(R.id.username_layout);
        bio_layout = findViewById(R.id.bio_layout);
        web_layout = findViewById(R.id.web_layout);
        location_layout = findViewById(R.id.location_layout);
        edit = findViewById(R.id.edit);
        menu = findViewById(R.id.imageView4);
        settings = findViewById(R.id.imageView3);
        profile_image = findViewById(R.id.profile_image);
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        createBottomSheetDialog();

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Adpref adpref;
        adpref = new Adpref(this);
        if (adpref.loadAdsModeState()){
            mAdView.setVisibility(View.VISIBLE);

        }
        fetchuserprofile();





        settings.setOnClickListener(view -> onBackPressed());

        name_layout.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfile.this, NameActivity.class);
            startActivity(intent);
        });

        username_layout.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfile.this, UsernameActivity.class);
            startActivity(intent);
        });

        bio_layout.setOnClickListener(view -> {
           Intent intent = new Intent(EditProfile.this, BioActivity.class);
           startActivity(intent);
        });

        web_layout.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfile.this, LinkActivity.class);
            startActivity(intent);
        });
        location_layout.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfile.this, LocationActivity.class);
            startActivity(intent);
        });

        edit.setOnClickListener(v -> bottomSheetDialog.show());

        menu.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);


            if (image_uri == null) {
                Toast.makeText(EditProfile.this, "Please select an image.", Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
                return;
            }

            File imageFile = new File(getRealPathFromImageURI(image_uri));



            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", imageFile.getName(),
                            RequestBody.create(MediaType.parse("image/*"), imageFile))

                    .build();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://68.183.245.154/img.php") // Replace with your actual image upload URL
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        pb.setVisibility(View.GONE);
                        Toast.makeText(EditProfile.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                    });
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

                                if (photoUrl!=null){
                                    deleteImage(photoUrl);

                                }

                                Users userUpdateRequest = new Users(Integer.parseInt(userId), null, null,null,null,null,null,imageUrl,null,null,null,null,null,null,null);
                                ModelPost modelPost = new ModelPost(imageUrl,userId,null,null,null,null,null,null,null,null,null,null,null);
                                Call<Void> call2 = userApi.updateModelPost(Integer.parseInt(userId),modelPost);
                                call2.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable throwable) {

                                    }
                                });


                                // Make the API call
                                Call<Void> call1 = userApi.updateUser(Integer.parseInt(userId), userUpdateRequest);
                                call1.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            runOnUiThread(() -> {
                                                menu.setVisibility(View.INVISIBLE);
                                                pb.setVisibility(View.GONE);

                                                Alerter.create(EditProfile.this)
                                                        .setTitle("Successful")
                                                        .setIcon(R.drawable.ic_check_wt)
                                                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                                                        .setDuration(10000)
                                                        .enableSwipeToDismiss()
                                                        .setText("Image Uploaded")
                                                        .show();
                                            });
                                        } else {
                                            Toast.makeText(EditProfile.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(EditProfile.this, "Failed to upload image."+t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });





                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(EditProfile.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                                });
                                menu.setVisibility(View.INVISIBLE);
                                pb.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> {
                                Toast.makeText(EditProfile.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(EditProfile.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                        });
                        menu.setVisibility(View.INVISIBLE);
                                pb.setVisibility(View.GONE);
                    }
                }
            });


        });

    }
    @Override
    public void finish() {
        Intent data = new Intent();
        // You can add extra data to the intent if needed
        setResult(RESULT_OK, data);
        super.finish();
    }
    private void fetchuserprofile(){
        userApiClient.fetchUser(Integer.parseInt(userId), new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    Users user = response.body();
                    if (user != null) {
                        String userName = user.getName();
                        String userEmail = user.getEmail();
                        photoUrl = user.getPhoto();
                        name.setText(""+userName);
                        username.setText(""+user.getUsername());
                        bio.setText(user.getBio());
                        link.setText(""+user.getLink());
                        location.setText(""+user.getLocation());

                        try {
                            Glide.with(EditProfile.this)
                                    .load(photoUrl)
                                    .apply(new RequestOptions()
                                            .error(R.drawable.avatar))  // Set the error placeholder
                                    .into(profile_image);
                            pb.setVisibility(View.GONE);

                        } catch (Exception e) {
                            Glide.with(EditProfile.this)
                                    .load(R.drawable.avatar)
                                    .into(profile_image);
                            pb.setVisibility(View.GONE);

                        }


                    } else {
                        // Show a toast message indicating that the user was not found
                        showFetchError();
                    }
                } else {
                    // Show a toast message indicating the failure
                    Toast.makeText(EditProfile.this, "Failed to fetch user: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                // Show a toast message indicating the failure
                showError(t);
            }
        });

    }
    private void deleteImage(String imageUrl) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("oldImageUrl", imageUrl)
                .build();

        Request request = new Request.Builder()
                .url("http://68.183.245.154/img.php") // Replace with your actual delete URL
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    pb.setVisibility(View.GONE);
                    Toast.makeText(EditProfile.this, "Failed to delete image.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                final String responseBody = response.body().string();
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            String status = jsonResponse.getString("status");
                            if ("success".equals(status)) {



                            } else {
                                Toast.makeText(EditProfile.this, "Failed to delete image.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditProfile.this, "Failed to delete image.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditProfile.this, "Failed to delete image.", Toast.LENGTH_SHORT).show();
                    }
                    pb.setVisibility(View.GONE);
                });
            }
        });
    }

    private String getRealPathFromImageURI(Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
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

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //Handel Permission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Alerter.create(EditProfile.this)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        bottomSheetDialog.cancel();
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            image_uri = Objects.requireNonNull(data).getData();
            Picasso.get().load(image_uri).into(profile_image);
            menu.setVisibility(View.VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createBottomSheetDialog(){
        if (bottomSheetDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.edit_bottom_sheet, null);
            constraintLayout3 = view.findViewById(R.id.constraintLayout3);
            delete = view.findViewById(R.id.delete);
            constraintLayout3.setOnClickListener(this);
            delete.setOnClickListener(this);
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }
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

            deleteImage(photoUrl);

            Users userUpdateRequest = new Users(Integer.parseInt(userId), null, null,null,null,null,null,"no",null,null,null,null,null,null,null);
            ModelPost modelPost = new ModelPost("no",userId,null,null,null,null,null,null,null,null,null,null,null);
            Call<Void> call2 = userApi.updateModelPost(Integer.parseInt(userId),modelPost);
            call2.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()){
                        fetchuserprofile();
                    }


                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {


                }
            });


            // Make the API call
            Call<Void> call1 = userApi.updateUser(Integer.parseInt(userId), userUpdateRequest);
            call1.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            menu.setVisibility(View.INVISIBLE);
                            pb.setVisibility(View.GONE);
                            fetchuserprofile();

                            Alerter.create(EditProfile.this)
                                    .setTitle("Successful")
                                    .setIcon(R.drawable.ic_check_wt)
                                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                                    .setDuration(10000)
                                    .enableSwipeToDismiss()
                                    .setText("Image Uploaded")
                                    .show();
                        });
                    } else {
                        Toast.makeText(EditProfile.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(EditProfile.this, "Failed to upload image."+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



            fetchuserprofile();
            Alerter.create(EditProfile.this)
                    .setTitle("Profile")
                    .setIcon(R.drawable.ic_error)
                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                    .setDuration(10000)
                    .enableSwipeToDismiss()
                   .setTitleTypeface(Objects.requireNonNull(ResourcesCompat.getFont(this, R.font.bold)))
                    .setTextTypeface(Objects.requireNonNull(ResourcesCompat.getFont(this, R.font.med)))
                    .setText("Image Delete Sucessfull")
                    .show();


        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Fetch the user profile whenever this activity is resumed
        fetchuserprofile();
    }

}
