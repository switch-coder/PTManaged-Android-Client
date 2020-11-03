package com.example.trainer.recyclerview;

public class Chat_list_item {

    String user_id;
    String user_name;
    String message;
    String time;
    String user_image;

    public Chat_list_item(String user_id, String user_name, String message, String time, String user_image) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.message = message;
        this.time = time;
        this.user_image = user_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }
}
