package com.example.trainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.trainer.apprtc.ConnectActivity;

public class Caller extends AppCompatActivity {
    ImageButton cancel_call,receive_call;
    TextView user_name;
    String room_name,caller,my_type,my_trainer,room;
    private Messenger mServiceMessenger =null;
    String TAG = "Caller_activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caller);

        cancel_call = findViewById(R.id.cancel_call);
        receive_call = findViewById(R.id.receive_call);
        user_name = findViewById(R.id.user_name);

        Intent intent1 = getIntent();
        Bundle bundle = intent1.getExtras();
        if (bundle !=null){
            room_name = bundle.getString("room_name");
            caller = bundle.getString("caller");
            room = bundle.getString("room");

        }

        user_name.setText(caller);
        String my_name = Sharedprefence.getString(this,"user_name");
        //전화건 사람이면 바로 통화준비상태로 간다
        if (caller.equals(my_name)){
            Intent intent =  new Intent(Caller.this, ConnectActivity.class);
            intent.putExtra("room_name",room_name);
            startActivity(intent);
            finish();
        }
        my_type = Sharedprefence.getString(getApplicationContext(),"user_type");
        if (my_type.equals("general")){
            my_trainer = Sharedprefence.getString(getApplicationContext(),"my_trainer");
        }


        bindService(new Intent(this, Socket_service.class), mConnection, Context.BIND_AUTO_CREATE);

        //전화 안받기
        cancel_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //서버에 전화취소했다는 내용 보내기
                String msg= "face_cancel:#"+my_name+":#"+room+":#"+" "+":#"+"\r\n";
                sendMessageToService(msg);
//                finish();
            }
        });

        receive_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //전화 받기 ConnectActivity로 이동했다가 거기서 connectToRoom 이라는 메소드를 통해 전화연결 화면으로 이동
                Intent intent =  new Intent(Caller.this, ConnectActivity.class);
                intent.putExtra("room_name",room_name);
                startActivity(intent);
                finish();
            }
        });
    }

    //소켓 서비스랑 연결
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("test","onServiceConnected");
            mServiceMessenger = new Messenger(iBinder);
            try {
                Message msg = Message.obtain(null, Socket_service.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
            }
            catch (RemoteException e) {
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };


    /** 서비스에 문자 보내기 */
    private void sendMessageToService(String str) {

        if (mServiceMessenger != null) {
            try {
                Message msg = Message.obtain(null, Socket_service.MSG_SEND_TO_SERVICE, str);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
                Log.d(TAG,str+"sendMessage");
            } catch (RemoteException e) {
            }
        }


    }

    /** Service 로 부터 message를 받음 */
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.i("test","act : what "+msg.what);
            switch (msg.what) {
                case Socket_service.MSG_SEND_TO_ACTIVITY:
                    String value2;
                    value2= msg.getData().getString("face_cancel");
                    if (value2 == null)return false;
                    if (value2.equals("cancel")){ finish(); }



                    break;
            }
            return false;
        }
    }));

}


