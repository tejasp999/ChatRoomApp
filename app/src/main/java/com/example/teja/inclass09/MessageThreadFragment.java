/*Assignment Inclass09
Yash Ghia
Prabhakar Teja Seeda
*/

package com.example.teja.inclass09;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageThreadFragment extends Fragment implements ThreadClass.ThreadSelector, ThreadAdapter.IDeleteThread {
    RecyclerView messageList ;
    String newThread;
    Request request;
    OkHttpClient client;
    String addThreadUrl = "http://ec2-54-164-74-55.compute-1.amazonaws.com/api/thread/add";
    String deleteThreadUrl = "http://ec2-54-164-74-55.compute-1.amazonaws.com/api/thread/delete/";
    static ThreadAdapter threadAdapter;
    MainActivity activity;
    static private RecyclerView.LayoutManager mLayoutManager;
    public static String threadURL = "http://ec2-54-164-74-55.compute-1.amazonaws.com/api/thread";
    public static ArrayList<ThreadClass> threadResults = new ArrayList<ThreadClass>();
    public MessageThreadFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_thread, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reloadThreads();
        getView().findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeSharedPreferences(new User());
                getFragmentManager().beginTransaction().replace(R.id.container,new LoginFragment(),"login").commit();
            }
        });
        getView().findViewById(R.id.addThread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newThread = ((EditText) getView().findViewById(R.id.newthread)).getText().toString();
                if (newThread.length() > 0) {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("title", newThread)
                            .build();

                    request = new Request.Builder()
                            .url(addThreadUrl)
                            .header("Authorization", "BEARER " + getToken())
                            .post(requestBody)
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
                            reloadThreads();
                        }
                    });
                }
            }
        });

    }

    public void storeSharedPreferences(User userDetails) {
        SharedPreferences preferences = getActivity().getSharedPreferences("Hello", Context.MODE_PRIVATE);
        Gson gsonNew = new Gson();
        SharedPreferences.Editor prefEditor = preferences.edit();
        String json2 = gsonNew.toJson(userDetails);
        prefEditor.putString("user",json2);
        prefEditor.commit();
    }
    public String getToken(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Hello",getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        User user = gson.fromJson(json, type);
        return user.getToken();
    }
    private ArrayList<ThreadClass> parseTrackResult(String in) throws JSONException {
        ArrayList<ThreadClass> trackResults = new ArrayList<>();
        JSONObject rootObject = new JSONObject(in);
        //JSONObject jsonObject1 = rootObject.getJSONObject("threads");
        JSONArray trackArray = rootObject.getJSONArray("threads");
        for(int i=0;i<trackArray.length();i++){
            JSONObject trackJSONObject = trackArray.getJSONObject(i);
            trackResults.add(ThreadClass.createResults(trackJSONObject));
        }
        Log.d("the results","are in parse"+trackResults);
        //loadData(trackResults);
        return trackResults;
    }
    public void loadData(ArrayList<ThreadClass> listResults){
        Log.d("coursedesc","size: "+threadResults.size());

    }

    @Override
    public void selected() {
        Log.d("Hello","Please");
    }

    public void reloadThreads(){
        client = new OkHttpClient();
        String bearer = "BEARER "+getToken();
        Request request = new Request.Builder()
                .url(threadURL)
                .header("Authorization", bearer)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    threadResults = parseTrackResult(responseBody.string());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageList = (RecyclerView) getView().findViewById(R.id.messageView);
                            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            messageList.setLayoutManager(mLayoutManager);
                            threadAdapter = new ThreadAdapter(threadResults, getActivity(), MessageThreadFragment.this, MessageThreadFragment.this);
                            messageList.setAdapter(threadAdapter);
                            threadAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void deletedThreadRefresh(ArrayList<ThreadClass> threads, String threadId) {
        Log.d("Before delete from api","size"+threads.size());
        client = new OkHttpClient();
        deleteThreadUrl = deleteThreadUrl+threadId;
        String bearer = "BEARER "+getToken();
        Request request = new Request.Builder()
                .url(deleteThreadUrl)
                .header("Authorization", bearer)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                }
            }
        });
        reloadThreads();
    }
}
