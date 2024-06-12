package com.ohmshantiapps.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ohmshantiapps.R;
import com.ohmshantiapps.SharedPref;
import com.ohmshantiapps.adapter.AdapterChatUsers;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.model.ModelUser;
import com.ohmshantiapps.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSearch extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterChatUsers adapterUsers;
    List<ModelUser> userList;
    EditText editText;
    ImageView imageView3;
    ProgressBar pg;
    SharedPref sharedPref;
    ApiService apiInterface;

    public ProfileSearch(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_search);

        recyclerView = findViewById(R.id.users);
        imageView3 = findViewById(R.id.imageView3);
        pg = findViewById(R.id.pg);
        pg.setVisibility(View.VISIBLE);

        imageView3.setOnClickListener(v -> onBackPressed());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProfileSearch.this));
        editText = findViewById(R.id.password);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    pg.setVisibility(View.VISIBLE);
//                    filter(s.toString());
                }else {
                    getAllUsers();
                }

            }
        });
        userList = new ArrayList<>();
        apiInterface = RetrofitClient.getClient().create(ApiService.class);
        getAllUsers();

    }

//    private void filter(final String query) {
//
//        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userList.clear();
//                for (DataSnapshot ds: dataSnapshot.getChildren()){
//                    ModelUser modelUser = ds.getValue(ModelUser.class);
//                    if (!Objects.requireNonNull(firebaseUser).getUid().equals(Objects.requireNonNull(modelUser).getId())){
//                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
//                        modelUser.getUsername().toLowerCase().contains(query.toLowerCase())){
//                            userList.add(modelUser);
//                            pg.setVisibility(View.GONE);
//                        }
//                    }
//                    adapterUsers = new AdapterChatUsers(ProfileSearch.this, userList);
//                    adapterUsers.notifyDataSetChanged();
//                    recyclerView.setAdapter(adapterUsers);
//                    pg.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void getAllUsers() {
        Call<List<User>> call = apiInterface.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> userList = response.body();
                    adapterUsers = new AdapterChatUsers(ProfileSearch.this, userList);
                    adapterUsers.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterUsers);
                    pg.setVisibility(View.GONE);
                    // Update RecyclerView with userList
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Handle network error
            }
        });
    }



//    private void getAllUsers() {
//
//        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userList.clear();
//                for (DataSnapshot ds: dataSnapshot.getChildren()){
//                    ModelUser modelUser = ds.getValue(ModelUser.class);
//                    if (!Objects.requireNonNull(firebaseUser).getUid().equals(Objects.requireNonNull(modelUser).getId())){
//                        userList.add(modelUser);
//                        pg.setVisibility(View.GONE);
//                    }
//                    adapterUsers = new AdapterChatUsers(ProfileSearch.this, userList);
//                    recyclerView.setAdapter(adapterUsers);
//                    pg.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
