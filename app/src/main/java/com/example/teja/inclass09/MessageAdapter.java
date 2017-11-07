/*Assignment Inclass09
Yash Ghia
Prabhakar Teja Seeda
*/

package com.example.teja.inclass09;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.ocpsoft.prettytime.PrettyTime;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by teja on 11/3/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    static ArrayList<Message> messagesArrayList;
    static Fragment fragment;
    static int pos;
    static IdeletedMessage ideletedMessage;

    public MessageAdapter(ArrayList<Message> messages, Fragment fragment, IdeletedMessage ideletedMessage) {
        this.fragment = fragment;
        this.messagesArrayList = messages;
        this.ideletedMessage = ideletedMessage;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PrettyTime p = new PrettyTime();
        User user = new User();
        Message message = messagesArrayList.get(position);
        holder.details = message;
        holder.messageTextView.setText(message.getMessage());
        holder.nameTextView.setText(message.getUser_fname()+" "+message.getUser_lname());
        Date date= null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS").parse(message.getCreated_at());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.timeTextView.setText(p.format(date));
        user = getSharedValues();
        if(user.getUser_id().equalsIgnoreCase(message.getUser_id())){
            holder.deleteButton.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView messageTextView, nameTextView,timeTextView;
        Button deleteButton;
        static Message details;
        public ViewHolder(View itemView) {
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(R.id.messageChat);
            nameTextView = (TextView) itemView.findViewById(R.id.username);
            timeTextView = (TextView) itemView.findViewById(R.id.time);
            deleteButton = (Button) itemView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("deleteMessage","delete button clicked");
                    Message m = new Message();
                    pos = getAdapterPosition();
                    Log.d("position","Pos :"+pos);
                    m = messagesArrayList.get(pos);
                    Log.d("message","message :"+m.getMessage());
                    messagesArrayList.remove(pos);
                    ideletedMessage.deletedMessageRefresh(m);
                }
            });
        }
    }
    interface IdeletedMessage{
        void deletedMessageRefresh(Message message);
    }

    public User getSharedValues(){
        SharedPreferences preferences = fragment.getActivity().getSharedPreferences("Hello",fragment.getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        User user = gson.fromJson(json, type);
        return user;
    }
}
