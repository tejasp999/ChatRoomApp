package com.example.teja.inclass09;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by teja on 11/6/17.
 */

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {
    static ArrayList<ThreadClass> threadResults;
    static Context context;
    static Fragment fragment;
    //IselectedInstructor inst;
    public ThreadAdapter(ArrayList<ThreadClass> results, Context context, Fragment fragment) {
        this.threadResults = results;
        this.context = context;
        this.fragment = fragment;
        //this.inst = iselectedInstructor;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_thread_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ThreadClass instructor = threadResults.get(position);
        holder.messageName.setText(instructor.getTitle());
    }

    @Override
    public int getItemCount() {
        return threadResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageName;
        Button removeButton;
        ThreadClass threadClass;
        public ViewHolder(final View itemView) {
            super(itemView);
            int i = getAdapterPosition();
            threadClass = threadResults.get(i);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.getFragmentManager().beginTransaction().replace(R.id.container,new ChatRoomFragment(threadClass.thread_id,threadClass.getTitle()));
                }
            });
            messageName = (TextView) itemView.findViewById(R.id.threadName);
            removeButton = (Button) itemView.findViewById(R.id.remove);
        }
    }

    public String getToken(){
        SharedPreferences preferences = context.getSharedPreferences("Hello",context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        User user = gson.fromJson(json, type);
        return user.getToken();
    }
}
