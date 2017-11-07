package com.example.teja.inclass09;

/**
 * Created by yash_ on 11/6/2017.
 */

public class Message {

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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    String user_fname,user_lname,user_id,id,message,created_at;

      /*"user_fname": "Bob",
              "user_lname": "Smith",
              "user_id": "1",
              "id": "8",
              "message": "testing message",
              "created_at": "2017-11-06 23:42:00"*/
}
