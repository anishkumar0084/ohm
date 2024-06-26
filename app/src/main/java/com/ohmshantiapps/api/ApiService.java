package com.ohmshantiapps.api;

import com.google.gson.JsonElement;
import com.ohmshantiapps.model.ModelComments;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("process_registration.php")
    Call<JsonElement> registerUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );


    @FormUrlEncoded
    @POST("login_process.php")
    Call<String> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("username.php")
    Call<String> updateUsername(
            @Field("username") String username,
            @Field("user_id") String userId
    );

    @POST("UserHandler.php")
    Call<Void> insertUser(@Body User user);

    @PUT("UserHandler.php/{id}")
    Call<Void> updateUser(@Path("id") int userId, @Body User requestBody);


//    @DELETE("UserHandler.php")
//    Call<Void> deleteUser(@Body User user);

    @HTTP(method = "DELETE", path = "UserHandler.php", hasBody = true)
    Call<Void> deleteUser(@Body Usersk user);


    @GET("UserHandler.php")
    Call<User> fetchUser(@Query("id") int userId);

    @GET("UserHandler.php")
    Call<List<User>> getUsersByIds(@Query("ids") String ids);




    @POST("ModelPost.php")
    Call<Void> insertModelPost(@Body ModelPost modelPost);

    @PUT("ModelPost.php/{id}")
    Call<Void> updateModelPost(@Path("id") int postId, @Body ModelPost requestBody);

    @PUT("ModelPost.php/{pId}")
    Call<Void> updatePostByPid(@Path("pId") Long postId, @Body ModelPost requestBody);


    @HTTP(method = "DELETE", path = "ModelPost.php", hasBody = true)
    Call<DeleteResponse> deletePost(@Body DeleteRequestBody body);



    @FormUrlEncoded
    @POST("follow.php")
    Call<Void> followUser(@Field("follower_id") int followerId, @Field("following_id") int followingId);

    @GET("followall.php")
    Call<FollowersFollowingResponse> getFollowersFollowing(@Query("user_id") int userId);


    @GET("unfollow.php")
    Call<Void> deleteRelationship(
            @Query("follower_id") int followerId,
            @Query("following_id") int followingId
    );
    @GET("get_users.php")
    Call<List<User>> getUsers();


    @GET("check_following.php")
    Call<Boolean> checkFollowing(@Query("follower_id") String followerId, @Query("following_id") String followingId);

    @FormUrlEncoded
    @POST("Postsk.php")
    Call<List<Integer>> getFollowingUsers(
            @Field("action") String action,
            @Field("followerId") int followerId
    );

    @FormUrlEncoded
    @POST("Postsk.php")
    Call<List<ModelPost>> getPostsByUserId(
            @Field("action") String action,
            @Field("userId") int userId
    );
    @GET("Allpost.php")
    Call<List<ModelPost>> getAllPosts();


    @POST("pid.php")
    Call<ModelPost[]> getPostByPid(@Body ModelPostRequest request);


    @Headers("Content-Type: application/json")
    @POST("post_comment.php")
    Call<ApiResponse> postComment(@Body ModelComments comment);

    @GET("fetch_comments.php")
    Call<List<ModelComments>> getCommentsByPostId(@Query("pId") String pId);

    @GET("fetch_comments.php")
    Call<CommentCountResponse> getCommentCount(@Query("pId") String pId, @Query("count") String count);

    @GET("fetch_comments.php")
    Call<ResponseBody> deleteComment(@Query("cId") String cId);

    @GET("fetch_comments.php")
    Call<ResponseBody> editComment(@Query("cId") String cId, @Query("content") String content);



    @FormUrlEncoded
    @POST("like_post.php")
    Call<LikeResponse> toggleLike(@Field("post_id") String postId, @Field("user_id") String userId, @Field("action") String action);
    @POST("like_post.php")
    @FormUrlEncoded
    Call<LikeResponse> checkLikeStatus(@Field("post_id") String postId, @Field("user_id") String userId, @Field("action") String action);

    @FormUrlEncoded
    @POST("like_post.php")
    Call<LikeResponse> getLikes(
            @Field("post_id") String postId,
            @Field("action") String action
    );







}
