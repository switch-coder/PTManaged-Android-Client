package com.example.trainer.recyclerview;

public class PT_log_item {
    String index;
    String date;
    String week;
    String start_time;
    String end_time;

    public PT_log_item(String index, String date, String week, String start_time, String end_time) {
        this.index = index;
        this.date = date;
        this.week = week;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
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
}
