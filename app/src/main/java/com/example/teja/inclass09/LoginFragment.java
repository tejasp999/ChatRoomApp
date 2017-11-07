/*Assignment Inclass09
Yash Ghia
Prabhakar Teja Seeda
*/

package com.example.teja.inclass09;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    public static String loginURL = "http://ec2-54-164-74-55.compute-1.amazonaws.com/api/login";
    TextView userTextView, passTextView;
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTextView = (TextView) getView().findViewById(R.id.username);
                passTextView = (TextView) getView().findViewById(R.id.password);
                String userName = userTextView.getText().toString();
                String password = passTextView.getText().toString();
                if(userName==null||password==null||password.isEmpty()||userName.isEmpty())
                {
                    Toast.makeText(getActivity(),"Please enter your credentials",Toast.LENGTH_LONG).show();
                }
                if (!userName.equals("") && !password.equals("")) {
                    try {
                        RequestBody formBody = new FormBody.Builder()
                                .add("email", userName)
                                .add("password", password)
                                .build();

                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(loginURL)
                                .post(formBody)
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
                                    //System.out.println("the response is"+responseBody.string());
                                    //JSONObject Jobject = new JSONObject(responseBody.string());
                                    User userObject = parseUser(responseBody.string());
                                    storeSharedPreferences(userObject);
                                    getFragmentManager().beginTransaction()
                                            .replace(R.id.container, new MessageThreadFragment(), "messages")
                                            .commit();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.d("the exception is", "given as " + e);
                    }
                }

            }
        });

        getView().findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container,new SignUpFragment(),"signup")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    private User parseUser(String in) throws JSONException {
        User user = new User();
        JSONObject rootObject = new JSONObject(in);
        if(rootObject.getString("status").equalsIgnoreCase("error")){
            Toast.makeText(getActivity(),"Error message: "+rootObject.getString("message"),Toast.LENGTH_LONG);
        }
        else {
            user.setStatus(rootObject.getString("status"));
            user.setToken(rootObject.getString("token"));
            user.setUser_id(rootObject.getString("user_id"));
            user.setUser_email(rootObject.getString("user_email"));
            user.setUser_fname(rootObject.getString("user_fname"));
            user.setUser_lname(rootObject.getString("user_lname"));
            user.setUser_role(rootObject.getString("user_role"));
        }
        return user;
    }

    public void storeSharedPreferences(User userDetails) {
        SharedPreferences preferences = getActivity().getSharedPreferences("Hello", Context.MODE_PRIVATE);
        Gson gsonNew = new Gson();
        SharedPreferences.Editor prefEditor = preferences.edit();
        String json2 = gsonNew.toJson(userDetails);
        prefEditor.putString("user",json2);
        prefEditor.commit();
    }

}
