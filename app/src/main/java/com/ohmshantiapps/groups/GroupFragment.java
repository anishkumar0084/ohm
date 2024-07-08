package com.ohmshantiapps.groups;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ohmshantiapps.Adpref;
import com.ohmshantiapps.R;
import com.ohmshantiapps.adapter.AdapterChatListGroups;
import com.ohmshantiapps.adapter.AdapterGroups;
import com.ohmshantiapps.adapter.AdapterPost;
import com.ohmshantiapps.api.ApiService;
import com.ohmshantiapps.api.RetrofitClient;
import com.ohmshantiapps.model.ModelChatListGroups;
import com.ohmshantiapps.model.ModelGroups;
import com.ohmshantiapps.model.ModelPost;
import com.ohmshantiapps.search.Search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupFragment extends Fragment {

   ProgressBar pg;
   RecyclerView recyclerView;
    AdapterPost adapterPost;
    List<ModelPost> postList;
    ApiService apiService;

    public GroupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

         pg=view.findViewById(R.id.pg);
         recyclerView=view.findViewById(R.id.posts_rv);

        postList = new ArrayList<>();

         LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
         linearLayoutManager.canScrollVertically();
         recyclerView.setLayoutManager(linearLayoutManager);
        apiService = RetrofitClient.getClient().create(ApiService.class);



         getReels();


        return view;
    }

    private void getReels() {



    }


}