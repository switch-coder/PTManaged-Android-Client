package com.example.trainer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.trainer.recyclerview.User_list_adapter;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class Fragment extends AppCompatActivity implements View.OnClickListener {

    Button btn_main_activity, btn_user_list_activity, btn_chatting_list, btn_setting;
    FragmentManager fm;
    FragmentTransaction fragmentTransaction;

    Main_Activity main_activity;
    User_list_activity user_list_activity;
    User_detail_fragment user_detail_fragment;
    Chatting_list_Activity chatting_list_activity;
    Setting_Activity setting_activity;
    ContentValues values =new ContentValues();
    Context mContext;
    boolean autoLogin;

    String user_type;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        mContext=this;

        fm = getSupportFragmentManager();
        btn_main_activity = findViewById(R.id.btn_main);
        btn_user_list_activity = findViewById(R.id.btn_user_list);
        btn_chatting_list = findViewById(R.id.btn_chatting_list);
        btn_setting = findViewById(R.id.btn_setting);

        btn_main_activity.setOnClickListener(this);
        btn_chatting_list.setOnClickListener(this);
        btn_user_list_activity.setOnClickListener(this);
        btn_setting.setOnClickListener(this);

        user_type = Sharedprefence.getString(mContext,"user_type");
        if (user_type.equals("general")){
            btn_user_list_activity.setText("운동기록");
        }

        main_activity = new Main_Activity();
        user_list_activity = new User_list_activity();
        chatting_list_activity = new Chatting_list_Activity();
        setting_activity = new Setting_Activity();
        user_detail_fragment = new User_detail_fragment();
        Intent intent = new Intent(getApplicationContext(),Socket_service.class);
        startService(intent);

        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.FrameLayout,main_activity).commitAllowingStateLoss();
        setFrag(0);



    }


    private long pressedTime =0;        //뒤로가기 버튼 입력시간

    public  interface  OnBackPressedListener{   //리슨어
         void onBack();
    }

    private OnBackPressedListener backPressedListener;  //리스너 객체

    public void setOnBackPressedListener(OnBackPressedListener listener){   //리스너 설정메소드
        backPressedListener = listener;
    }
    @Override
    public void onBackPressed() {


        if (backPressedListener !=null){
            backPressedListener.onBack();
            Log.e("backPressedListener", "Listener is not null");
        }else {
            if ( pressedTime ==0){
                Snackbar.make(findViewById(R.id.FrameLayout),"한 번 더 누르면 종료됩니다.",Snackbar.LENGTH_LONG).show();
                pressedTime = System.currentTimeMillis();// 첫번쩨 누른 시간 가져오기
            }else{
                int seconds = (int) (System.currentTimeMillis() - pressedTime );// 한번더 누른 시간이랑 첫번재 누른 시간이랑 비교
                if (seconds >2000){                                             // 2초보다 낮으면 종료 아니면 종료 안됨
                    Snackbar.make(findViewById(R.id.FrameLayout),"한 번 더 누르면 종료됩니다.",Snackbar.LENGTH_LONG).show();
                    pressedTime =0;
                }else {
                    super.onBackPressed();finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

        fragmentTransaction = fm.beginTransaction();
        switch (v.getId()){
            case R.id.btn_main:
                setFrag(0);

                break;

            case R.id.btn_user_list:
                setFrag(1);
                break;

            case R.id.btn_chatting_list:
                setFrag(2);
                break;

            case R.id.btn_setting:
                setFrag(3);
                break;
        }
    }


    public void setFrag(int n) {
        fm = getSupportFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        switch (n){
            case 0:
                fragmentTransaction.replace(R.id.FrameLayout,main_activity);
                fragmentTransaction.commit();
                break;
            case 1:
                if (user_type.equals("general")){
                    fragmentTransaction.replace(R.id.FrameLayout, user_detail_fragment);
                }else{
                    fragmentTransaction.replace(R.id.FrameLayout,user_list_activity);
                }

                fragmentTransaction.commit();
                break;
            case 2:
                fragmentTransaction.replace(R.id.FrameLayout, chatting_list_activity);
                fragmentTransaction.commit();
                break;

            case 3:
                fragmentTransaction.replace(R.id.FrameLayout, setting_activity);
                fragmentTransaction.commit();
                break;
        }
    }




}
