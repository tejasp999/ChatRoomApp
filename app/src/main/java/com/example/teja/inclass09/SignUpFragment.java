/*Assignment Inclass09
Yash Ghia
Prabhakar Teja Seeda
*/

package com.example.teja.inclass09;


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
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
public class SignUpFragment extends Fragment {

    EditText fname;
    EditText lname;
    EditText email;
    EditText password;
    EditText conpassword;
    private OkHttpClient client;
    User user;
    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fname = (EditText)getView().findViewById(R.id.fname);
        lname = (EditText)getView().findViewById(R.id.lname);
        email = (EditText)getView().findViewById(R.id.email);
        password = (EditText)getView().findViewById(R.id.password);
        conpassword = (EditText)getView().findViewById(R.id.conpassword);
        user = new User();
        getView().findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container,new LoginFragment()).commit();
            }
        });
        getView().findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals(conpassword.getText().toString())) {
                    OkHttpClient client1 = new OkHttpClient();

                    Log.d("email",email.getText().toString());
                    Log.d("password",password.getText().toString());
                    Log.d("fname", fname.getText().toString());
                    Log.d("lname",lname.getText().toString());

                    Log.d("enter signup method","password same");
                    RequestBody formBody = new FormBody.Builder()
                            .add("email", email.getText().toString())
                            .add("password", password.getText().toString())
                            .add("fname", fname.getText().toString())
                            .add("lname", lname.getText().toString())
                            .build();
                    Log.d("formbody",""+formBody.toString());

                    Request request = new Request.Builder()
                            .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/signup")
                            .post(formBody)
                            .build();

                    Log.d("request",""+request.body().toString());

                    client1.newCall(request).enqueue(new Callback() {
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
                                user = parseUser(response.body().string());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(user.getStatus().equalsIgnoreCase("ok")){
                                SharedPreferences preferences = getActivity().getSharedPreferences("Hello",getActivity().MODE_PRIVATE);
                                Gson gsonNew = new Gson();
                                SharedPreferences.Editor prefEditor = preferences.edit();
                                String json2 = gsonNew.toJson(user);
                                prefEditor.putString("user",json2);
                                prefEditor.commit();
                                getFragmentManager().beginTransaction().replace(R.id.container,new MessageThreadFragment()).commit();

                                //Toast.makeText(getActivity(),"Sign up Successful",Toast.LENGTH_LONG).show();

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toastSuccessful();
                                    }
                                });

                            }
                            else {
                                //Toast.makeText(getActivity(),"Sign up not successful- Status response is: "+user.getStatus(),Toast.LENGTH_LONG).show();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toastUnSuccessful();
                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getActivity(),"Password does not match",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    private User parseUser(String in) throws JSONException {
        User user = new User();
        JSONObject rootObject = new JSONObject(in);
        if(rootObject.getString("status").equalsIgnoreCase("error")){
            user.setStatus(rootObject.getString("status"));
        }
        else {
            user.setStatus(rootObject.getString("status"));
            user.setToken(rootObject.getString("token"));
            user.setUser_id(rootObject.getString("user_id"));
            user.setUser_email(rootObject.getString("user_email"));
            user.setUser_fname(rootObject.getString("user_lname"));
            user.setUser_role(rootObject.getString("user_role"));
        }
        return user;
    }

    private void toastSuccessful(){
        Toast.makeText(getActivity(),"Sign up Successful",Toast.LENGTH_LONG).show();
    }
    private void toastUnSuccessful(){
        Toast.makeText(getActivity(),"Sign up not successful- Status response is: "+user.getStatus(),Toast.LENGTH_LONG).show();
    }



}
