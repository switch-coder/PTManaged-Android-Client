package com.example.trainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.OrientationHelper;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.selection.BaseCriteriaSelectionManager;
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager;
import com.applikeysolutions.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.model.CalendarEvent;
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate;

public class Add_schedule extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    Button btn_complete,btn_close;
    TextView start_time_view,end_time_view;
    String start_time,end_time;
    String set_time;
    SimpleDateFormat format_time;
    CalendarView calendarView;
    Spinner spinner_date;
    LinearLayout btn_start,btn_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        start_time_view = findViewById(R.id.start_time);
        end_time_view = findViewById(R.id.end_time);
        btn_start = findViewById(R.id.linearLayout_start);
        btn_end = findViewById(R.id.linearLayout_end);
        btn_complete = findViewById(R.id.btn_complete);
        btn_close = findViewById(R.id.btn_cancel);
        calendarView = findViewById(R.id.cosmo_calendar);
        spinner_date = findViewById(R.id.spinner_date_pick);

        //달력설정
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);

        //현재시간 넣어주기
        format_time = new SimpleDateFormat("HH:mm");
        start_time=""+format_time.format(Calendar.getInstance().getTime());
        end_time=""+format_time.format(Calendar.getInstance().getTime());



        //완료버튼
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //선택한 날짜들 리스트에 데이터 넣어준 후 "/"구분자를 사용해서 문자열로 변환
                List<Calendar> days =calendarView.getSelectedDates();
                String  selectDays ="";
                for (int i=0; i<days.size(); i++){
                    Calendar calendar = days.get(i);
                    final int day = calendar.get(Calendar.DAY_OF_MONTH);
                    final int month = calendar.get(Calendar.MONTH);
                    final int year = calendar.get(Calendar.YEAR);
                    String result = year+"-"+(month+1)+"-"+day;
                    selectDays += (result+"/");
                }


                if (selectDays.equals("")){
                    Toast.makeText(Add_schedule.this,"날짜를 선택해주세요",Toast.LENGTH_LONG).show();
                }else{
                    //마지막 구분자 제거
                    selectDays = selectDays.substring(0,selectDays.length()-1);

                    Intent intent = new Intent();
                    intent.putExtra("selectDays",selectDays);//선택한 날짜들
                    intent.putExtra("start_time",start_time+":00");//시작 시간
                    intent.putExtra("end_time",end_time+":00");//끝나는 시간
                    setResult(RESULT_OK,intent);
                    finish();
                }

            }
        });

        //날짜 선택 타입 선택
        spinner_date.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Add_schedule.this,position+"/"+parent.getSelectedItemPosition(),Toast.LENGTH_LONG).show();
                clearSelectionMenuClick();

                //날짜 다중 선택
                if(position == 0){
                    calendarView.setSelectionType(SelectionType.MULTIPLE);

                //날짜 범위 선택
                }else{
                    calendarView.setSelectionType(SelectionType.RANGE);
                    Toast.makeText(Add_schedule.this,"시작하는 날과 끝나는 날을 선택해주세요",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                calendarView.setSelectionType(SelectionType.MULTIPLE);
            }
        });

        //시작하는 시간 선택
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_time = "start_time";
                DialogFragment timepicker = new TimePickerDialogFragment();
                timepicker.show(getSupportFragmentManager(),"timepicker");
            }
        });

        //끝나는 시간 선택
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_time ="end_time";
                DialogFragment timepicker = new TimePickerDialogFragment();
                timepicker.show(getSupportFragmentManager(),"timepicker");
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void clearSelectionMenuClick(){
        calendarView.clearSelections();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay,int minute){
        //타임픽커 형태 변환해주는 메소드
        String time = hourOfDay+":"+minute;
        ParsePosition pos = new ParsePosition(0);
        Date frmTime = format_time.parse(time,pos);
        if (set_time.equals("start_time")) {
            start_time =format_time.format(frmTime);
            start_time_view.setText(start_time);
        }else{
            end_time = format_time.format(frmTime);
            end_time_view.setText(end_time);
        }
    }

}
