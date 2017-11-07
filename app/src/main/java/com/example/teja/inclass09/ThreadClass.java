package com.example.teja.inclass09;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by teja on 11/6/17.
 */

public class ThreadClass {
    String createdat;
    String title;
    String user_id;
    String user_email;
    String user_fname;
    String user_lname;
    String thread_id;

    public String getCreatedat() {
        return createdat;
    }

    public void setCreatedat(String createdat) {
        this.createdat = createdat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_fname() {
        return user_fname;
    }

    public void setUser_fname(String user_fname) {
        this.user_fname = user_fname;
    }

    public String getUser_lname() {
        return user_lname;
    }

    public void setUser_lname(String user_lname) {
        this.user_lname = user_lname;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }
    static public ThreadClass createResults(JSONObject jsonObject) throws JSONException {
        ThreadClass threadObject = new ThreadClass();
        threadObject.setUser_fname(jsonObject.getString("user_fname"));
        threadObject.setUser_lname(jsonObject.getString("user_lname"));
        threadObject.setUser_id(jsonObject.getString("user_id"));
        threadObject.setThread_id(jsonObject.getString("id"));
        threadObject.setTitle(jsonObject.getString("title"));
        threadObject.setCreatedat(jsonObject.getString("created_at"));
        return threadObject;
    }
    interface ThreadSelector{
        void selected();
    }
}
