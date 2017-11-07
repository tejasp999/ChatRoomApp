package com.example.teja.inclass09;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomFragment extends Fragment implements MessageAdapter.IdeletedMessage {
    OkHttpClient client;
    static User user;
    String threadid;
    String newMessage;
    MessageAdapter messageAdapter;
    RecyclerView messagesViewList ;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Message> messages = new ArrayList<>();
    Request request;
    String threadName;
    public ChatRoomFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ChatRoomFragment(String threadid, String threadName) {
        this.threadid = threadid;
        this.threadName = threadName;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TextView)getView().findViewById(R.id.threadname)).setText(threadName);

        getView().findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container,new MessageThreadFragment(),"messagethread").commit();
            }
        });

        user = new User();
        client = new OkHttpClient();
        user = getSharedValues();
        request = new Request.Builder()
                .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/messages/" + threadid)
                .header("Authorization", "BEARER " + user.getToken())
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            messages = parseMessage(response.body().toString());
            messageAdapter = new MessageAdapter(messages,ChatRoomFragment.this,ChatRoomFragment.this);
            messagesViewList.setAdapter(messageAdapter);
            messageAdapter.notifyDataSetChanged();

            getView().findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newMessage = ((EditText) getView().findViewById(R.id.message)).getText().toString();

                    RequestBody formBody = new FormBody.Builder()
                            .add("message", newMessage)
                            .add("thread_id", threadid)
                            .build();

                    request = new Request.Builder()
                            .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/message/add")
                            .header("Authorization", "BEARER " + user.getToken())
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    request = new Request.Builder()
                            .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/messages/" + threadid)
                            .header("Authorization", "BEARER " + user.getToken())
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code " + response);
                            }
                            try {
                                messages = parseMessage(response.body().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            messageAdapter = new MessageAdapter(messages, ChatRoomFragment.this, ChatRoomFragment.this);
                            messagesViewList.setAdapter(messageAdapter);
                            messageAdapter.notifyDataSetChanged();
                        }
                    });
                }
                });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_room, container, false);
    }

    private ArrayList<Message> parseMessage(String in) throws JSONException {
        ArrayList<Message> messages = new ArrayList<>();
        JSONObject rootObject = new JSONObject(in);
        JSONArray messageArray = rootObject.getJSONArray("messages");
        for(int i=0;i<messageArray.length();i++){
            JSONObject trackJSONObject = messageArray.getJSONObject(i);
            messages.add(createResults(trackJSONObject));
        }
        return messages;
    }

    public Message createResults(JSONObject obj) throws JSONException {
        Message message = new Message();
        message.setId(obj.getString("id"));
        message.setUser_fname(obj.getString("user_fname"));
        message.setUser_lname(obj.getString("user_lname"));
        message.setMessage(obj.getString("message"));
        message.setCreated_at(obj.getString("created_at"));
        message.setUser_id(obj.getString("user_id"));
        return message;
    }
    public User getSharedValues(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Hello",getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        User user = gson.fromJson(json, type);
        return user;
    }

    @Override
    public void deletedMessageRefresh(ArrayList<Message> messages) {
        messageAdapter = new MessageAdapter(messages,ChatRoomFragment.this,ChatRoomFragment.this);
        messagesViewList.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();
    }
}
