package com.example.trainer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.trainer.recyclerview.Chat_list_item;
import com.example.trainer.recyclerview.Schedule_item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static com.example.trainer.Public.url_address;

public class Socket_service extends Service {

    private static final String TAG = "Socket_service";

    int SERVER_PORT = 5000;
    Socket socket = new Socket();
    Handler handler = new Handler();
    String SERVER_IP = "ec2-18-224-107-194.us-east-2.compute.amazonaws.com";
    ArrayList<Chat_list_item> chat_list_items = new ArrayList<>();
    public static final int MSG_SEND_TO_SERVICE =3;
    public static final int MSG_REGISTER_CLIENT =1;
    public static final int MSG_SEND_TO_ACTIVITY =4;
    String my_name,room_name,my_id,my_type;
    boolean socket_check = false;
    ContentValues values = new ContentValues();
    private Messenger mClient = null;   // Activity 에서 가져온 Messenger
    PrintWriter printWriter;
    public Socket_service() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        unregisterRestartAlarm(); //이미 등록된 알람이 있으면 제거

        출처: https://samse.tistory.com/entry/죽지-않는-서비스-만들기 [고 투 더 멘토]
        Log.d(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");

        if (intent ==null)return START_STICKY;
        else{
            my_name = Sharedprefence.getString(getApplicationContext(),"user_name");
            my_id = Sharedprefence.getString(getApplicationContext(),"user_id");
            my_type = Sharedprefence.getString(getApplicationContext(),"user_type");
            get_member();

        }
        return super.onStartCommand(intent, flags, startId);
    }
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.w("test","ControlService - message what : "+msg.what +" , msg.obj "+ msg.obj);


            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
//                    new send_message(socket,msg.obj.toString());
                    mClient = msg.replyTo;  // activity로부터 가져온
                    break;
                case MSG_SEND_TO_SERVICE:
                    new send_message(socket,msg.obj.toString()).start();
                    Log.d(TAG,msg.obj.toString());
                    break;

            }
            return false;
        }
    }));

    private void sendMsgToActivity(String key, String value) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString(key,value);
            Message msg = Message.obtain(null, MSG_SEND_TO_ACTIVITY);
            msg.setData(bundle);
            mClient.send(msg);      // msg 보내기
        } catch (RemoteException e) {
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return  mMessenger.getBinder();
    }


    //소켓연결
    class ConnectionThread extends Thread {
        Socket socket = null;
        Handler handler =null;
        ConnectionThread(Socket socket,Handler handler){
            this.socket = socket;
            this.handler = handler;
        }
        @Override
        public void run() {

            try {
                //소켓연결 (아이피,포트)
                SocketAddress address =new InetSocketAddress(SERVER_IP,SERVER_PORT);
                socket.connect(address,5000);

                ChatClientReceiveThread chatClientReceiveThread = new ChatClientReceiveThread(socket,handler);
                chatClientReceiveThread.start();

                PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                Log.d("Tag","join" + my_name + ":#"+ room_name );
                String request = "join:#" + my_name + ":#"+ room_name +"\r\n";
                pw.println(request);


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    //채팅서버에서 보낸 채팅 받기
    private class ChatClientReceiveThread extends Thread{
        Socket socket = null;
        Handler handler;
        ChatClientReceiveThread(Socket socket, Handler handler){
            this.socket = socket;
            this.handler = handler;
        }

        public void run() {
            Log.d("service_socket_msg","start");
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                while(true) {
                    socket_check =true;
                    String msg = br.readLine();
                    Log.d("service_socket_msg",msg);
                    String[] token = msg.split(":#");
                    //token[0] = 발송인
                    //token[1] = 메세지 내용
                    //token[2] = 시간
                    //token[3] = 방이름
                    if (token[0].equals("face_time")){
                        Context context = getApplicationContext();
                        Intent intent1 = new Intent(context, Caller.class);
                        intent1.putExtra("room_name",token[3].replace("#","-"));
                        intent1.putExtra("room",token[4]);
                        intent1.putExtra("caller",token[1]);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                    }else if(token[0].equals("face_cancel")){
                        //해당 아이디로 보내는 코드 작성
//                        if (token[1].equals(my_name))return;
                        sendMsgToActivity("face_cancel","cancel");
                    }
                    else{
                        add_items(token[0],token[1],token[2]);
                    }


                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void add_items(String user_name,String message,String time){

        if (user_name.equals(my_name)){
            //내가 보낸 채팅일 경우
            chat_list_items.add(new Chat_list_item("",user_name,message,time,""));
        }else{
            //상대방이 보낸 채팅일 겨우
            chat_list_items.add(new Chat_list_item("",user_name,message,time,""));

        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        //서버로 보내고 난후 받아온 결과값 처리
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{

                //jsonobject로 서버에서 받아 왔기때문에 파싱해준다
                Log.d(TAG,s);
                JSONObject jsonObject = new JSONObject(s);
                String request_result =  jsonObject.getString("result");
                Log.d(TAG,request_result);

                if (request_result.equals("member_ok")){
                    //트레이너 일때 회원들의 아이디를 가져온다
                    String member = jsonObject.getString("member");

                    member = member.substring(0,member.length()-1);


                        //트레이너일 경우
                        room_name = my_id+"#"+member;


                    if (!socket_check){
                        ConnectionThread connectionThread = new ConnectionThread(socket,handler);
                        connectionThread.start();
                    }

                }else if(request_result.equals("member_customer_ok")){
                    //일반회원일 경우
                    room_name = Sharedprefence.getString(getApplicationContext(),"my_trainer")+"#"+my_id;
                    if (!socket_check){
                        ConnectionThread connectionThread = new ConnectionThread(socket,handler);
                        connectionThread.start();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"다시 시도해주세요",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,s);//오류 로그
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.


        }


    }
    public void get_member(){
        String url = "Main_activity.php";
        values.clear();
        // 트레이너가 열어놓은 시간 가져오기
        // 일반회원일 경우 담당 트레이너의 아이디
        // 트레이너일 경우 본인 아이디

        //jsonObject 형태로 만들어서 보냄

        String json_date = "{\"user_id\"" + ":" + "\"" + my_id + "\"" + ","
                + "\"user_type\"" + ":" + "\"" + my_type + "\""+ ","
                + "\"request_key\"" + ":" + "\"" + "member" + "\"" + "}";
        Log.d(TAG,my_id);
        values.put("data", json_date);

        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();

    }

    //메시지 보내기
    class send_message extends Thread {
        Socket socket = null;
        String message;
        send_message(Socket socket,String message){
            this.message = message;
            this.socket = socket;
        }
        @Override
        public void run() {
            try{
                Log.d(TAG,"send_message");
                printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

                // ':'구분자로 구분 해서 보냄
                // 0번째 어떤값을 보내는지  1번째 값 / 2번째 방이름 / 3번째 시간

                printWriter.println(message);


            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        registerRestartAlarm();
    }

    // support persistent of Service
    void registerRestartAlarm() {
        Log.d(TAG, "registerRestartAlarm");
        Intent intent = new Intent(getApplicationContext(), RestartService.class);
        intent.setAction(RestartService. ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 5*1000; // 10초 후에 알람이벤트 발생
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 5*1000, sender);
    }



    void unregisterRestartAlarm() {
        Log.d(TAG, "unregisterRestartAlarm");
        Intent intent = new Intent(getApplicationContext(), RestartService.class);
        intent.setAction(RestartService.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }



}
