package com.ohmshantiapps.shareChat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ohmshantiapps.Adpref;
import com.ohmshantiapps.R;
import com.ohmshantiapps.adapter.AdapterChatList;
import com.ohmshantiapps.adapter.AdapterPost;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.model.ModelChat;
import com.ohmshantiapps.model.ModelChatlist;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.model.ModelUser;
import com.ohmshantiapps.search.ProfileSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends Fragment {

    ImageView search;
    RecyclerView recyclerView;
    AdapterPost adapterPost;
    List<ModelPost> postList;
    ApiService apiInterface;
    ShimmerFrameLayout shimmerFrameLayout;

    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        search =view.findViewById(R.id.imageView4);
        apiInterface = RetrofitClient.getClient().create(ApiService.class);
        search.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileSearch.class);
            startActivity(intent);
        });
        postList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        shimmerFrameLayout = view.findViewById(R.id.shimmer);
        shimmerFrameLayout.startShimmer();

        getAllVideo();





        return view;
    }

    private void getAllVideo() {


    }
}





