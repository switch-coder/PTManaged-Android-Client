package com.example.trainer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.trainer.recyclerview.Schedule_item;

import java.util.ArrayList;

public class Schedule_dialog extends Dialog {

    private OnDismissListener dismissListener;
    private Context context;
    private int index,result,key;
    private String  user_id,user_type,booker,trainer,start_time,end_time;
    private String TAG = "schedule_dialog";
    private Schedule_item mitem;

    public Schedule_dialog(Context context, final int position, Schedule_item item){
        super(context);
        this.context = context;
        this.index = item.getIndex();
        this.booker = item.getUser_id();
        this.mitem = item;
    }

    public  void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_click_schedule);

        final Button btn_first = findViewById(R.id.button_first);
        final Button btn_second = findViewById(R.id.button_second);
        final Button btn_close = findViewById(R.id.button_close);

        user_id = Sharedprefence.getString(context,"user_id");
        user_type = Sharedprefence.getString(context,"user_type");
        Log.d(TAG, booker);

        key = 203;
        //예약자가 없고 접속자가 일반회원일 경우 예약하기 버튼으로 바꿈
        if(user_type.equals("general") && booker.equals("null")){
            btn_first.setText("예약하기");
            btn_second.setVisibility(View.GONE);
            key =101;
        }

        //예약이 되어있고 예약자가 본인일 경우
        if (booker.equals(user_id)){
            btn_first.setText("예약취소");
            btn_second.setVisibility(View.GONE);
            key=102;
        }

        //접속자가 트레이너 본인이고 예약자가 없을때 삭제가능
        if (user_type.equals("trainer") && booker.equals("null")){
            btn_second.setVisibility(View.VISIBLE);
            btn_second.setText("삭제하기");
        }

        //접속자가 트레이너이고 예약자가 있을때
        if(user_type.equals("trainer") && !(booker.equals("null"))){
            btn_first.setText("예약자 확인");
            btn_second.setVisibility(View.GONE);
            key = 202;
        }

        //트레이너일 때
        //예약자 확인 or 시간 변경
        //일반회원일때
        //예약하기 or 예약 취소하기
        btn_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (key ==101){
                    //예약하기
                    book_gym();
                }else if(key == 102){
                    //예약 취소하기
                    book_cancel();
                }else if(key==202){
                    //예약자 확인하기
                    booker_detail();
                }else if(key==203){
                    modify_time();
                }
            }
        });

        btn_second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result = 201;
                        dismiss();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void book_gym(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("예약하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result = 101;
                dismiss();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void book_cancel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(index+"예약을 취소하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result = 102;
                dismiss();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void booker_detail(){
        result= 202;
       dismiss();



    }

    private void modify_time(){
        final DisplayMetrics dm = context.getResources().getDisplayMetrics(); //디바이스 화면크기를 구하기위해

        final Modify_time_dialog custom_dialog = new Modify_time_dialog(getContext(), mitem);
        WindowManager.LayoutParams wm = custom_dialog.getWindow().getAttributes();  //다이얼로그의 높이 너비 설정하기위해
        wm.width = dm.widthPixels / 2;  //화면 너비의 절반
        wm.height = dm.heightPixels / 2;  //화면 높이의 절반
        wm.copyFrom(custom_dialog.getWindow().getAttributes());  //여기서 설정한값을 그대로 다이얼로그에 넣겠다는의미

        custom_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                int request_key = custom_dialog.getResult();
//                dialog_result(result,mItems.get(position),v,position);
                if (request_key == 203){
                     start_time = custom_dialog.getStart_time();
                     end_time = custom_dialog.getEnd_time();
                     result= 203;
                     dismiss();
                }


            }
        });
        custom_dialog.show();
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
    public int getResult() {return result;}
    public void setResult(int result){this.result= result;}
}
