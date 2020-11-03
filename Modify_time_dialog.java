package com.example.trainer;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.trainer.recyclerview.Schedule_item;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Modify_time_dialog extends Dialog  {
    private int index ;
    private Context context;
    private String start_time,end_time;
    private Button btn_cancel,btn_complete;
    private LinearLayout btn_start,btn_end;
    private SimpleDateFormat format_time;
    private TextView start_time_view,end_time_view;
    private int result =9999;
    Schedule_item item;

    public Modify_time_dialog(Context context, Schedule_item item){
        super(context);
        this.context = context;
        this.index = item.getIndex();
        this.start_time = item.getStart_time();
        this.end_time = item.getEnd_time();
        this.item= item;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_modify_time);

        start_time_view = findViewById(R.id.start_time);
        end_time_view = findViewById(R.id.end_time);
        btn_start = findViewById(R.id.linearLayout_start);
        btn_end = findViewById(R.id.linearLayout_end);
        btn_complete = findViewById(R.id.btn_complete);
        btn_cancel = findViewById(R.id.btn_cancel);


        //현재시간 넣어주기
        format_time = new SimpleDateFormat("HH:mm");


        start_time_view.setText(start_time);
        end_time_view.setText(end_time);
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            result = 203;
            dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        //시작하는 시간 선택
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        String time = hourOfDay+":"+minute;
                        ParsePosition pos = new ParsePosition(0);
                        Date frmTime = format_time.parse(time,pos);

                            start_time =format_time.format(frmTime);
                            start_time_view.setText(start_time);
                    }
                },  Integer.parseInt(start_time.substring(0,2)), Integer.parseInt(start_time.substring(3)), true).show();

            }
        });

        //끝나는 시간 선택
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        String time = hourOfDay+":"+minute;
                        ParsePosition pos = new ParsePosition(0);
                        Date frmTime = format_time.parse(time,pos);
                        end_time = format_time.format(frmTime);
                        end_time_view.setText(end_time);
                    }
                },  Integer.parseInt(start_time.substring(0,2)), Integer.parseInt(start_time.substring(3)), true).show();


            }
        });

    }



    public int getResult() {return result;}
    public void setResult(int result){this.result= result;}
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
