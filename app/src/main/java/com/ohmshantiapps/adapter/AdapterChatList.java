package com.ohmshantiapps.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ohmshantiapps.R;
import com.ohmshantiapps.model.Users;
import com.ohmshantiapps.shareChat.Chat;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder> {

    final Context context;
    final List<Users> userList;
    private final HashMap<String, String> lastMessageMap;

    public AdapterChatList(Context context, List<Users> userList) {
        this.context = context;
        this.userList = userList;
     lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.chatlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String hisUid = String.valueOf(userList.get(position).getId());
        String dp = userList.get(position).getPhoto();
        String name = userList.get(position).getName();
        String lastMessage = lastMessageMap.get(hisUid);
        String id =userList.get(position).getBio();



        holder.mName.setText(name);
        if (lastMessage == null || lastMessage.equals("default")){
            holder.message.setVisibility(View.GONE);
        }else {
            holder.message.setVisibility(View.VISIBLE);
            holder.message.setText(lastMessage);
        }
        try{
            Picasso.get().load(dp).placeholder(R.drawable.avatar).into(holder.mDp);
        }catch (Exception e){
            Picasso.get().load(R.drawable.avatar).into(holder.mDp);
        }

        if (userList.get(position).getStatus().equals("online")){
          holder.status.setImageResource(R.drawable.online);
        }else {
            holder.status.setImageResource(R.drawable.offline);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat.class);
            intent.putExtra("hisUid", id);
            context.startActivity(intent);
        });

    }

    public void setLastMessageMap(String userId, String lastMessage){
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView mDp;
        final ImageView status;
        final TextView mName;
        final TextView message;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mDp = itemView.findViewById(R.id.circleImageView);
            status = itemView.findViewById(R.id.status);
            mName = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.username);
        }
    }

}
