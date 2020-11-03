package com.example.trainer.recyclerview;

public class Schedule_item {

    public Schedule_item(int index, String start_time, String end_time, String user_name, String user_id, String user_image) {
        this.index = index;
        this.start_time = start_time;
        this.end_time = end_time;
        this.user_name = user_name;
        this.user_id = user_id;
        this.user_image = user_image;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    private int index;
    String start_time;
    String end_time;
    String user_name;
    String user_id;
    String user_image;
}
