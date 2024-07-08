package com.ohmshantiapps.adapter;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
//import com.google.android.ads.nativetemplates.TemplateView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.ohmshantiapps.Adpref;
import com.ohmshantiapps.R;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.CommentCountResponse;
import com.ohmshantiapps.api.DeleteRequestBody;
import com.ohmshantiapps.api.DeleteResponse;
import com.ohmshantiapps.api.LikeResponse;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.api.SessionManager;
import com.ohmshantiapps.api.UniqueLinkResponse;
import com.ohmshantiapps.groups.ShareGroupActivity;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.post.PostDetails;
import com.ohmshantiapps.post.PostLikedBy;
import com.ohmshantiapps.post.UpdatePost;
import com.ohmshantiapps.search.Search;
import com.ohmshantiapps.shareChat.ShareActivity;
import com.ohmshantiapps.user.MediaView;
import com.ohmshantiapps.user.UserProfile;
import com.ohmshantiapps.welcome.GetTimeAgo;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {

    Context context;
    List<ModelPost> postList;
    private boolean isLiked = false;

    private String userId;
    private final DatabaseReference postsRef1;
    boolean mProcessLike = false;
    boolean mProcessView = false;

    ApiService apiService;

    public AdapterPost(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        postsRef1 = FirebaseDatabase.getInstance().getReference().child("Posts");
         apiService = RetrofitClient.getClient().create(ApiService.class);

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }




    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String dp = postList.get(position).getDp();
        String id = postList.get(position).getId();
        String meme = postList.get(position).getMeme();
        String name = postList.get(position).getName();
        String pId = String.valueOf(postList.get(position).getpId());
        String pTime = postList.get(position).getpTime();
        String text = postList.get(position).getText();
        String vine = postList.get(position).getVine();
        String type = postList.get(position).getType();
        String pViews = postList.get(position).getpViews();
        String comment = postList.get(position).getpComments();

        SessionManager sessionManager = new SessionManager(context);

        userId =sessionManager.getUserId();


        //Time
        GetTimeAgo getTimeAgo = new GetTimeAgo();
        long lastTime = Long.parseLong(pTime);
        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);

        //Set Data
        holder.pName.setText(name);
        holder.pText.setText(text);
        holder.pType.setText(type);

        if (pViews == null || pViews.isEmpty()) {
            holder.views.setText("0");
        } else {
            try {
                long views = Long.parseLong(pViews);
                String formattedViews = formatViews(views);
                holder.views.setText(formattedViews);
            } catch (NumberFormatException e) {
                holder.views.setText("0");
            }
        }




        setLikes(holder, pId);
        setViews(holder, pId);
        String ed_text = holder.pText.getText().toString().trim();
        if (ed_text.length() > 0) {
            holder.constraintLayout9.setVisibility(View.VISIBLE);

        } else {
            holder.constraintLayout9.setVisibility(View.GONE);
        }



        HashTagHelper mTextHashTagHelper = HashTagHelper.Creator.create(context.getResources().getColor(R.color.colorPrimary), hashTag -> {
            Intent intent1 = new Intent(context, Search.class);
            intent1.putExtra("hashTag",hashTag);
           context.startActivity(intent1);
        });

        mTextHashTagHelper.handle(holder.pText);

        //More
        //Time

        holder.like.setOnClickListener(v -> {
            mProcessLike = true;
            String postId = String.valueOf(postList.get(position).getpId());
                likePost(postId, userId,holder,position);
                mProcessLike = false;
//
        });

        holder.comment.setOnClickListener(v -> {
            if (vine.equals("noVideo")) {
                Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("postId", pId);
                intent.putExtra("postIds", id);
                context.startActivity(intent);
            } else {

                int pViews1 = Integer.parseInt(postList.get(position).getpViews());
                mProcessView = true;
                String postId = String.valueOf(postList.get(position).getId());


                        if (mProcessView) {

                            if (postId.equals(userId)) {
                                mProcessView = false;
                                Intent intent = new Intent(context, PostDetails.class);
                                intent.putExtra("postId", pId);
                                intent.putExtra("postIds", postId);
                                context.startActivity(intent);
                            } else {
                                mProcessView = false;
                                Intent intent = new Intent(context, PostDetails.class);
                                intent.putExtra("postId", pId);
                                intent.putExtra("postIds", "0");
                                context.startActivity(intent);

//                                addToHisNotification(""+id,""+pId,"Viewed  your post");
                            }
                        }
                    }




        });
        //Share
        holder.share.setOnClickListener(v -> shareMoreOptions(holder.share,holder, pId));

        //Click
        holder.pName.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("hisUid", id);
            context.startActivity(intent);
        });
        holder.pDp.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("hisUid", id);
            context.startActivity(intent);
        });

        holder.more.setOnClickListener(v -> showMoreOptions(holder.more, id, userId, pId, meme, vine,position));



        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context)
                .load(dp)
                .apply(requestOptions)
                .into(holder.pDp);


        //Post Image
        if (meme.equals("noImage")) {
            holder.pMeme.setVisibility(View.GONE);
        } else {

            RequestOptions requestOptions1 = new RequestOptions()
                    .placeholder(R.drawable.avatar)  // Placeholder with loading animation
                    .diskCacheStrategy(DiskCacheStrategy.ALL);  // Cache strategy for better performance

            Glide.with(context)
                    .load(meme)
                    .apply(requestOptions1)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            holder.pMeme.setImageDrawable(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // This method is called when the load is cancelled or the target is cleared
                            holder.pMeme.setImageDrawable(placeholder);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            // If the load fails, we can still show the placeholder (loading animation)
                            holder.pMeme.setImageDrawable(requestOptions.getPlaceholderDrawable());
                        }
                    });

//

        }

        if (meme.equals("noImage") &&  vine.equals("noVideo")){
            holder.load.setVisibility(View.GONE);
        }



        Uri uri = Uri.parse(vine);
        //Post Vine
        if (vine.equals("noVideo")) {
            holder.video_share.setVisibility(View.GONE);
            holder.pVine.setVisibility(View.GONE);
        } else {
            try {
                Glide.with(context).asBitmap().centerCrop().load(uri).into(holder.pVine);
                holder.pause.setVisibility(View.VISIBLE);
                holder.video_share.setVisibility(View.VISIBLE);
                holder.share.setVisibility(View.GONE);
                holder.view_ly.setVisibility(View.VISIBLE);
            } catch (Exception ignored) {

            }
        }


        holder.video_share.setOnClickListener(v -> vidshareMoreOptions(holder.video_share, pId,vine,text));




        Adpref adpref;
        adpref = new Adpref(context);
        if (adpref.loadAdsModeState()){
            if (!vine.equals("noVideo")){
//                holder.ad.setVisibility(View.VISIBLE);
            }
        }



        holder.viewlt.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (vine.equals("noVideo")) {
                    Intent intent = new Intent(context, PostDetails.class);
                    intent.putExtra("postId", pId);
                    intent.putExtra("postIds", id);
                    context.startActivity(intent);
                } else {

                    int pViews = Integer.parseInt(postList.get(position).getpViews());
                    mProcessView = true;
                    String postId = String.valueOf(postList.get(position).getpId());

                    if (postId.equals(userId)) {
                        mProcessView = false;
                        Intent intent = new Intent(context, PostDetails.class);
                        intent.putExtra("postId", pId);
                        intent.putExtra("postIds", postId);
                        context.startActivity(intent);
                    } else {
                        mProcessView = false;
                        Intent intent = new Intent(context, PostDetails.class);
                        intent.putExtra("postId", pId);
                        intent.putExtra("postIds", "0");
                        context.startActivity(intent);
//                        addToHisNotification(""+id,""+pId,"Viewed  your post");
                    }


                }
            }

            @Override
            public void onDoubleClick(View view) {
                mProcessLike = true;
                String postId = String.valueOf(postList.get(position).getpId());

                    likePost(postId, userId,holder,position);
                    mProcessLike = false;

//                addToHisNotification(""+id,""+pId,"Liked your post");
//
            }
        }));

        holder.constraintLayout9.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (vine.equals("noVideo")) {
                    Intent intent = new Intent(context, PostDetails.class);
                    intent.putExtra("postId", pId);
                    context.startActivity(intent);
                } else {

                    int pViews = Integer.parseInt(postList.get(position).getpViews());
                    mProcessView = true;
                    String postId = String.valueOf(postList.get(position).getpId());

                            if (mProcessView) {
                                if (postId.equals(id)) {
                                    mProcessView = false;
                                    Intent intent = new Intent(context, PostDetails.class);
                                    intent.putExtra("postId", pId);
                                    intent.putExtra("postIds", id);

                                    context.startActivity(intent);
                                } else {
                                    postsRef1.child(postId).child("pViews").setValue("" + (pViews + 1));
                                    mProcessView = false;
                                    Intent intent = new Intent(context, PostDetails.class);
                                    intent.putExtra("postId", pId);
                                    context.startActivity(intent);
                                    intent.putExtra("postIds", "0");
//                                    addToHisNotification(""+id,""+pId,"Viewed  your post");
                                }
                            }



                }
            }

            @Override
            public void onDoubleClick(View view) {
                mProcessLike = true;
                String postId = String.valueOf(postList.get(position).getpId());

                    likePost(postId, userId,holder,position);
                    mProcessLike = false;
            }
        }));

        noLike(position,holder);
        noComment(position,holder);

    }

    private void geneartelink(String pId) {
        Call<UniqueLinkResponse> call = apiService.generateUniqueLink(Long.parseLong(pId));
        call.enqueue(new Callback<UniqueLinkResponse>() {
            @Override
            public void onResponse(Call<UniqueLinkResponse> call, Response<UniqueLinkResponse> response) {
                if (response.isSuccessful()){
                    Toast.makeText(context, "Link Generated", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Failed to generate link", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UniqueLinkResponse> call, Throwable throwable) {
                Toast.makeText(context, "Failed to generate link"+throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void noComment(int position,MyHolder holder){
        String postId = String.valueOf(postList.get(position).getpId());

        Call<CommentCountResponse> call=apiService.getCommentCount(postId,"true");
        call.enqueue(new retrofit2.Callback<CommentCountResponse>() {
            @Override
            public void onResponse(Call<CommentCountResponse> call, Response<CommentCountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int commentCount = response.body().getCommentCount();

                        if (commentCount==0) {
                            holder.commentNo.setText("Comment");

                        } else {

                            holder.commentNo.setText(formatViews(commentCount));
                        }



                } else {
                }
            }

            @Override
            public void onFailure(Call<CommentCountResponse> call, Throwable throwable) {

            }
        });




    }

    private void noLike(int position, MyHolder holder) {
        String postId = String.valueOf(postList.get(position).getpId());

        Call<LikeResponse> call = apiService.getLikes(postId, "get_likes");

        call.enqueue(new retrofit2.Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LikeResponse likeResponse = response.body();
                    List<String> userIds = likeResponse.getUserIds();
                    String likes= String.valueOf(likeResponse.getLikeCount());


                    if (likes.equals("0")||likes==null) {
                      holder.likeNo.setText("Like");

                   }else {
                        holder.likeNo.setText(formatViews(Long.parseLong(likes))+"");
                   }


                } else {
                    Log.e(TAG, "Failed to get likes. Response code: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable throwable) {

            }
        });
    }

    private void vidshareMoreOptions(RelativeLayout video_share, String pId, String vine, String text) {
        PopupMenu popupMenu = new PopupMenu(context, video_share, Gravity.END);

        popupMenu.getMenu().add(Menu.NONE,0,0, "In chats");
        popupMenu.getMenu().add(Menu.NONE,1,0, "To apps");
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id==0){

                Intent intent = new Intent(context, ShareActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);

            }else if (id==1){

                Intent intent2 = new Intent(Intent.ACTION_SEND);
                intent2.setType("text/*");
                intent2.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
                intent2.putExtra(Intent.EXTRA_TEXT, text +" Link: "+ vine);
                context.startActivity(Intent.createChooser(intent2, "Share Via"));

            }

            return false;
        });
        popupMenu.show();
    }


    private void shareMoreOptions(RelativeLayout share, MyHolder holder, String pId) {
        PopupMenu popupMenu = new PopupMenu(context, share, Gravity.END);


        popupMenu.getMenu().add(Menu.NONE,0,0, "In chats");
        popupMenu.getMenu().add(Menu.NONE,1,0, "In groups");
        popupMenu.getMenu().add(Menu.NONE,2,0, "To apps");
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id==0){
                Intent intent = new Intent(context, ShareActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }else if (id==1){
//                Intent intent = new Intent(context, ShareGroupActivity.class);
//                intent.putExtra("postId", pId);
//                context.startActivity(intent);
                geneartelink(pId);
            }
            else if (id==2){
                String shareText = holder.pText.getText().toString().trim();
                BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.pMeme.getDrawable();
                if (bitmapDrawable == null) {
                    shareTextOnly(shareText);
                } else {




                    Call<UniqueLinkResponse> call = apiService.getUniqueLink(Long.parseLong(pId));
                    call.enqueue(new Callback<UniqueLinkResponse>() {
                        @Override
                        public void onResponse(Call<UniqueLinkResponse> call, Response<UniqueLinkResponse> response) {
                            if (response.isSuccessful()){
                            String uniqueLink = response.body().getUniqueLink();
                            shareImageAndText(uniqueLink);

                            }else {
                                Toast.makeText(context, "Failed to get link", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UniqueLinkResponse> call, Throwable throwable) {
                            Toast.makeText(context, "Failed to get link"+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }

            return false;
        });
        popupMenu.show();
    }


    private void shareImageAndText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(shareIntent, "Share Post Link"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdir();
            File file = new File(imageFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.ohmshantiapps.fileprovider", file);

        } catch (Exception e) {
            StyleableToast st = new StyleableToast(context, e.getMessage(), Toast.LENGTH_LONG);
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
        context.startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private void setViews(MyHolder holder, String postKey) {
//        viewRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child(postKey).hasChild(userId)){
//
//                    holder.eye.setImageResource(R.drawable.ic_eyed);
//
//                }else {
//                    holder.eye.setImageResource(R.drawable.ic_eye);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }

    private void setLikes(MyHolder holder, String postKey) {
        checkLikeStatus(postKey,userId,holder);
    }
    private void checkLikeStatus(String postId, String userId, MyHolder holder) {
        Call<LikeResponse> call = apiService.checkLikeStatus(postId, userId, "check");

        call.enqueue(new retrofit2.Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                if (response.isSuccessful()) {
                    LikeResponse likeResponse = response.body();
                    if (likeResponse != null) {
                        String status = likeResponse.getStatus();
                        if ("liked".equals(status)) {
                            isLiked = true;
                        } else if ("unliked".equals(status)) {
                            isLiked = false;
                        }
                        updateLikeButtonState(holder);
                    } else {
                        Toast.makeText(context, "Null response received", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to get like status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable throwable) {
                Toast.makeText(context, "Network error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





}

    private void likePost(String postId, String userId, MyHolder holder,int position) {
        Call<LikeResponse> call = apiService.toggleLike(postId, userId, "toggle");

        call.enqueue(new retrofit2.Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                if (response.isSuccessful()) {
                    LikeResponse likeResponse = response.body();
                    if (likeResponse != null) {
                        String status = likeResponse.getStatus();
                        if ("liked".equals(status)) {
                            isLiked = true;
                            noLike(position,holder);
                            Toast.makeText(context, "Post liked successfully", Toast.LENGTH_SHORT).show();
                        } else if ("unliked".equals(status)) {
                            isLiked = false;
                            noLike(position,holder);

                                Toast.makeText(context, "Post unliked successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Unexpected response", Toast.LENGTH_SHORT).show();
                        }
                        updateLikeButtonState(holder);
                    } else {
                        Toast.makeText(context, "Null response received", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to toggle like status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable throwable) {
                Toast.makeText(context, "Network error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }










    private void updateLikeButtonState(MyHolder holder) {

        if (isLiked) {

            holder.like_img.setImageResource(R.drawable.ic_liked);
//
//
        } else {
            holder.like_img.setImageResource(R.drawable.ic_like);
//
        }
    }



    private void showMoreOptions(ImageView more, String id, String userId, String pId, String meme, String vine,int position) {


        final boolean[] isSaved = {checkIfPostIsSaved(pId)};

        PopupMenu popupMenu = new PopupMenu(context, more, Gravity.END);


        if (id.equals(userId)){
            popupMenu.getMenu().add(Menu.NONE,0,0, "Delete");
            popupMenu.getMenu().add(Menu.NONE,1,0, "Edit");
        }
        popupMenu.getMenu().add(Menu.NONE, 2, 0, isSaved[0] ? "Unsave" : "Save");
        popupMenu.getMenu().add(Menu.NONE, 3,0,"Details");
        popupMenu.getMenu().add(Menu.NONE, 4,0,"Liked By");
        if (!meme.equals("noImage")){
//            popupMenu.getMenu().add(Menu.NONE, 5,0,"Download");
        }
        if (!vine.equals("noVideo")){
//            popupMenu.getMenu().add(Menu.NONE, 6,0,"Download");
        }
        if(!meme.equals("noImage") || !vine.equals("noVideo")){
            popupMenu.getMenu().add(Menu.NONE,7,0, "Fullscreen");
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            int id1 = item.getItemId();
            if (id1 ==0){

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Delete Post")
                        .setMessage("Are you sure you want to delete this post?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            // Users confirmed to delete the post
                            beginDelete(pId, meme, vine, position);
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            // Users cancelled the deletion
                            dialog.dismiss();
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }else if (id1 ==1){
                Intent intent = new Intent(context, UpdatePost.class);
                intent.putExtra("key","editPost");
                intent.putExtra("editPostId", pId);
                context.startActivity(intent);
            }
            else if (id1 ==2){
                String firebaseAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference saveRef = FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseAuth).child(pId);
                if (isSaved[0]) {
                    // Unsave the post
                    saveRef.removeValue().addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Post Unsaved", Toast.LENGTH_SHORT).show();
                        isSaved[0] = false;
                        item.setTitle("Save");
                    }).addOnFailureListener(e -> {
                        // Handle failure
                    });
                } else {
                    // Save the post
                    saveRef.setValue(true).addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Post Saved", Toast.LENGTH_SHORT).show();
                        isSaved[0] = true;
                        item.setTitle("Unsave"); // Update menu item title to "Unsave"
                    }).addOnFailureListener(e -> {
                        // Handle failure
                    });
                }


            }
            else if (id1 ==3){
                Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
            else if (id1 ==4){
                Intent intent = new Intent(context, PostLikedBy.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
            else if (id1 ==5){
                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(meme);
                picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String url = uri.toString();
                    downloadFile(context, "Image", ".png", DIRECTORY_DOWNLOADS, url);

                }).addOnFailureListener(e -> {

                });
            }
            else if (id1 ==6){
                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(vine);
                picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String url = uri.toString();
                    downloadFile(context, "Video", ".mp4", DIRECTORY_DOWNLOADS, url);

                }).addOnFailureListener(e -> {

                });
            }else if (id1 ==7){
                if(!vine.equals("noVideo")){
                    Intent intent = new Intent(context, MediaView.class);
                    intent.putExtra("type","video");
                    intent.putExtra("uri",vine);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   context.startActivity(intent);
                }else
                if(!meme.equals("noImage")){
                    Intent intent = new Intent(context, MediaView.class);
                    intent.putExtra("type","image");
                    intent.putExtra("uri",meme);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
            return false;
        });
        popupMenu.show();


    }

    private void beginDelete(String pId, String meme, String vine,int position) {

        if (vine.equals("noVideo") && meme.equals("noImage")){

            deletk(pId,position);

        }else if (vine.equals("noVideo")){
            deleteImage(meme,pId,position);

        }else if (meme.equals("noImage")){
            deleteVideo(vine,pId,position);

        }

    }
    private boolean checkIfPostIsSaved(String pId) {
        String firebaseAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference saveRef = FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseAuth).child(pId);
        final boolean[] isSaved = {false};
        saveRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isSaved[0] = snapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
        return isSaved[0];
    }

    private void deleteVideo(String videoUrl, String pId,int position) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("oldVideoUrl", videoUrl)
                .add("action", "deleteVideo")
                .build();

        Request request = new Request.Builder()
                .url("http://68.183.245.154/upload.php") // Replace with your actual delete URL
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Toast.makeText(context, "Failed to delete video.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                final String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String status = jsonResponse.getString("status");
                        if ("success".equals(status)) {
                            deletk(pId,position);
                        } else {
                            Toast.makeText(context, "Failed to delete video.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Failed to delete video.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to delete video.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteImage(String imageUrl,String pId,int position) {
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
                    Toast.makeText(context, "Failed to delete image.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                final String responseBody = response.body().string();
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            String status = jsonResponse.getString("status");
                            if ("success".equals(status)) {
                               deletk(pId,position);


                            } else {
                                Toast.makeText(context, "Failed to delete image.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Failed to delete image.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Failed to delete image.", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri1 = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri1);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        Objects.requireNonNull(downloadManager).enqueue(request);
    }


    private void deletk(String pId,int position){
        DeleteRequestBody requestBody = new DeleteRequestBody(pId);

        Call<DeleteResponse> call = apiService.deletePost(requestBody);
        call.enqueue(new retrofit2.Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    removeItem(position);
                    DatabaseReference savesRef = FirebaseDatabase.getInstance().getReference().child("Saves");
                    savesRef.child(pId).removeValue().addOnSuccessListener(aVoid1 -> {
                        // Successfully removed the post from Saves
                    }).addOnFailureListener(e -> {
                        // Handle failure
                    });
                    Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
            }
        });

    }


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
    public String formatViews(long views) {
        if (views < 1000) {
            return String.valueOf(views); // No formatting needed
        } else if (views < 1_00_000) {
            // Convert to "1k", "10k", "999k" etc.
            if (views % 1000 == 0) {
                return String.format("%.0fk", views / 1000.0);
            } else {
                return String.format("%.1fk", views / 1000.0);
            }
        } else if (views < 1_00_00_000) {
            // Convert to "1 lakh", "10 lakh", "99 lakh" etc.
            if (views % 1_00_000 == 0) {
                return String.format("%.0f lakh", views / 1_00_000.0);
            } else {
                return String.format("%.1f lakh", views / 1_00_000.0);
            }
        } else if (views < 1_000_00_00_000L) {
            // Convert to "1 crore", "10 crore", "999 crore" etc.
            if (views % 1_00_00_000 == 0) {
                return String.format("%.0f crore", views / 1_00_00_000.0);
            } else {
                return String.format("%.1f crore", views / 1_00_00_000.0);
            }
        } else {
            // Convert to "1 billion", "10 billion", "999 billion" etc.
            if (views % 1_000_00_00_000L == 0) {
                return String.format("%.0f billion", views / 1_000_00_00_000.0);
            } else {
                return String.format("%.1f billion", views / 1_000_00_00_000.0);
            }
        }
    }



    @Override
    public int getItemCount() {
        return postList.size();
    }
    public void removeItem(int position) {
        postList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, postList.size());
    }




    class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView pDp;
        final ImageView pMeme;
        final ImageView more;
        final ImageView like_img;
        final ImageView eye;
        final TextView pName;
        final TextView pType;
        final TextView pText;
        final TextView likeNo;
        final TextView commentNo;
        final TextView views;
        final ImageView pVine;
        final RelativeLayout like;
        final RelativeLayout comment;
        final RelativeLayout share;
        final RelativeLayout view_ly;
        final RelativeLayout video_share;
//        final RelativeLayout ad;
        final ImageView pause;
        final ProgressBar load;
        final ConstraintLayout constraintLayout9;
        final ConstraintLayout viewlt;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            pDp = itemView.findViewById(R.id.circleImageView3);
            eye = itemView.findViewById(R.id.eye);
            pMeme = itemView.findViewById(R.id.imageView2);
            pName = itemView.findViewById(R.id.name);
//            ad = itemView.findViewById(R.id.ad);
            pType = itemView.findViewById(R.id.username);
            likeNo = itemView.findViewById(R.id.likeNo);
            commentNo = itemView.findViewById(R.id.commentNo);
            load = itemView.findViewById(R.id.load);
            views = itemView.findViewById(R.id.views);
            view_ly = itemView.findViewById(R.id.view_ly);
            more = itemView.findViewById(R.id.more);
            pVine = itemView.findViewById(R.id.videoView);
            pause = itemView.findViewById(R.id.exomedia_controls_play_pause_btn);
            like_img = itemView.findViewById(R.id.like_img);
            pText = itemView.findViewById(R.id.textView2);
            viewlt = itemView.findViewById(R.id.viewlt);
            like = itemView.findViewById(R.id.relativeLayout);
            comment = itemView.findViewById(R.id.relativeLayout6);
            share = itemView.findViewById(R.id.meme_share);
            video_share = itemView.findViewById(R.id.vine_share);
            constraintLayout9 = itemView.findViewById(R.id.constraintLayout9);
            MobileAds.initialize(context, initializationStatus -> {

            });
            AdLoader.Builder builder = new AdLoader.Builder(context, context.getString(R.string.native_ad_unit_id));
//            builder.forUnifiedNativeAd(unifiedNativeAd -> {
//                TemplateView templateView = itemView.findViewById(R.id.my_template);
//                templateView.setNativeAd(unifiedNativeAd);
//            });

            AdLoader adLoader = builder.build();
            AdRequest adRequest = new AdRequest.Builder().build();
            adLoader.loadAd(adRequest);

        }
    }

}

