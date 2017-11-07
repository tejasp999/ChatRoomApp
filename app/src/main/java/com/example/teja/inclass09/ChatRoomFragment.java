/*Assignment Inclass09
Yash Ghia
Prabhakar Teja Seeda
*/
package com.example.teja.inclass09;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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
    static MessageAdapter messageAdapter;
    static RecyclerView messagesViewList ;
    static private RecyclerView.LayoutManager mLayoutManager;
    static ArrayList<Message> messages;
    static Request request;
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
        messagesViewList = (RecyclerView) getView().findViewById(R.id.chatRoom);
        ((TextView) getView().findViewById(R.id.threadname)).setText(threadName);
        messages = new ArrayList<>();
        getView().findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container, new MessageThreadFragment(), "messagethread").commit();
            }
        });

        user = new User();
        client = new OkHttpClient();
        user = getSharedValues();
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
                    messages = parseMessage(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (messages.size() != 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter = new MessageAdapter(messages, ChatRoomFragment.this, ChatRoomFragment.this);
                            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            messagesViewList.setLayoutManager(mLayoutManager);
                            messagesViewList.setAdapter(messageAdapter);
                            messageAdapter.notifyDataSetChanged();
                        }
                    });

                }

                getView().findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText msgText;
                        msgText = (EditText) getView().findViewById(R.id.sendmessage);
                        newMessage = msgText.getText().toString();
                        Log.d("the string", "to pass" + newMessage);

                        RequestBody formBody = new FormBody.Builder()
                                .add("message", newMessage)
                                .add("thread_id", threadid)
                                .build();

                        request = new Request.Builder()
                                .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/message/add")
                                .header("Authorization", "BEARER " + user.getToken())
                                .post(formBody)
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
                                refreshMessageList();
                            }
                        });
                    }
                });
            }
        });
    }

    public void refreshMessageList(){
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
                    messages = parseMessage(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter = new
                                MessageAdapter(messages, ChatRoomFragment.this, ChatRoomFragment.this);
                        mLayoutManager = new
                                LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                        messagesViewList.setLayoutManager(mLayoutManager);
                        messagesViewList.setAdapter(messageAdapter);
                        messageAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
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
    public void deletedMessageRefresh(Message message) {
        Request request = new Request.Builder()
                .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/message/delete/" + message.getId())
                .header("Authorization", "BEARER " + user.getToken())
                .build();

        client = new OkHttpClient();
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
                Log.d("message delete method","res :"+response.body().string());
                refreshMessageList();
            }
        });
    }
}
