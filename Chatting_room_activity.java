package com.example.trainer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainer.recyclerview.Chat_list_item;
import com.example.trainer.recyclerview.Chatting_adapter;
import com.example.trainer.recyclerview.Schedule_item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.trainer.Public.url_address;

public class Chatting_room_activity extends AppCompatActivity {

    TextView view_user_name;
    Button  send_message;
    ImageButton btn_call;
    EditText input_message;
    Context mContext;
    String my_name,my_id,user_name,user_id,user_image,room_name,my_type;

    String SERVER_IP = "ec2-18-224-107-194.us-east-2.compute.amazonaws.com";
    String url = "Chat_data.php";
    int SERVER_PORT = 5000;
    Socket socket = new Socket();
    SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    PrintWriter printWriter;

    int chat_index = 0;
    String TAG = "Chatting_room_activity";
    private Messenger mServiceMessenger =null;
    private boolean mIsBound;

    ArrayList<Chat_list_item> chat_list_items= new ArrayList();
    RecyclerView recyclerView_chat;
    Chatting_adapter chatting_adapter;
    LinearLayoutManager layoutManager;
    Handler handler = new Handler();
    ContentValues values = new ContentValues();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_room);
        mContext=this;

        view_user_name = findViewById(R.id.room_name);
        send_message = findViewById(R.id.send_message);
        input_message = findViewById(R.id.input_message);
        btn_call =findViewById(R.id.btn_call);

        Intent intent =getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            user_image = bundle.getString("user_image");
            user_name = bundle.getString("user_name");
            user_id = bundle.getString("user_id");
            Log.d("room_activity","user_id"+user_id+"/name"+user_name);
            view_user_name.setText(user_name);
        }

        my_id= Sharedprefence.getString(mContext,"user_id");
        my_name = Sharedprefence.getString(mContext,"user_name");
        my_type = Sharedprefence.getString(mContext,"user_type");

        recyclerView_chat = findViewById(R.id.recyclerView_chat);
        layoutManager= new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView_chat.setLayoutManager(layoutManager);
        recyclerView_chat.setItemAnimator(new DefaultItemAnimator());
        chatting_adapter = new Chatting_adapter(mContext,chat_list_items,my_name);
        recyclerView_chat.setAdapter(chatting_adapter);


        //room_name 이름은 트레이너아이디/일반회원 아이디
        if (my_type.equals("general")){
            //일반회원일 경우
            room_name = user_id+"#"+my_id;
        }else{
            //트레이너일 경우
            room_name = my_id+"#"+user_id;
        }
        get_chat_data(url,room_name);

        //소켓연결 시작
        ConnectionThread connectionThread = new ConnectionThread(socket,handler);
        connectionThread.start();



            bindService(new Intent(this, Socket_service.class), mConnection, Context.BIND_AUTO_CREATE);



        //메시지 보내기
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long mNow;
                Date date;
                mNow  =System.currentTimeMillis();
                date = new Date(mNow);
                String time = format.format(date);
                String message = input_message.getText().toString();
                String msg =  "message:#"+message+":#"+room_name+":#"+time+":#"+"\r\n";
                new send_message(socket,msg).start();
                input_message.setText(" ");
                input_message.requestFocus();
            }

        });

        //face_time 영상통화
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long mNow;
                Date date;
                mNow  =System.currentTimeMillis();
                date = new Date(mNow);
                String time = format.format(date);
                String msg= "face_time:#"+user_name+":#"+room_name+":#"+time+":#"+"\r\n";
                sendMessageToService(msg);
                Log.d(TAG,msg);
            }
        });

    }

    //채팅 데이터 저장
    @Override
    protected void onStop() {
        super.onStop();
        //채팅내용 서버에 저장하기

        try{

            JSONArray jsonArray = new JSONArray();
            //채팅내용들 jsonArray 로 변환
            for(int i = chat_index;i<chat_list_items.size(); i++){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("caller",chat_list_items.get(i).getUser_name());
                jsonObject.put("message",chat_list_items.get(i).getMessage());
                jsonObject.put("time",chat_list_items.get(i).getTime());
                if (chat_list_items.get(i).getUser_name().equals(my_name)){
                    jsonArray.put(jsonObject);
                }
            }
            Log.d("chat_array",jsonArray.toString());
            values.clear();
            //보내는값 채팅 내용들(jsonArray),방이름,리퀘스트 키값
            String json_date = "{\"room_name\"" + ":" + "\"" +   room_name + "\"" + ","
                    + "\"request_key\"" + ":" + "\"" + "save" + "\"" + "}";
            Log.d("json_date",json_date);
            values.put("data",json_date);
            values.put("chat_data",jsonArray.toString());
            NetworkTask networkTask = new NetworkTask(url,values);
            networkTask.execute();

        }catch (JSONException e){
            e.printStackTrace();
        }



    }

    //기존채팅 데이터 가져오기
    private   void get_chat_data(String url,String room_name){
        values.clear();
        //jsonObject 형태로 만들어서 보냄
        String data = "{\"room_name\"" + ":" + "\"" + room_name + "\"" + ","
                + "\"request_key\"" + ":" + "\"" + "load" + "\"" + "}";

        values.put("data", data);

        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();

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
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                while(true) {
                    String msg = br.readLine();
                    Log.d("msg",msg);
                    String[] token = msg.split(":#");
                    //token[0] = 발송인
                    //token[1] = 메세지 내용
                    //token[2] = 시간
                    //token[3] = 방이름
                    if (token[3].equals(room_name)){
                        add_items(token[0],token[1],token[2]);
                        //리사이클러뷰 데이터 변경
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatting_adapter.notifyDataSetChanged();
                            }
                        });
                    }



                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);


                // ':'구분자로 구분 해서 보냄
                // 0번째 어떤값을 보내는지  1번째 값 / 2번째 방이름 / 3번째 시간

                printWriter.println(message);
                Log.d(TAG,message);

            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }


    //httpConnect 서버통신
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
                Log.d(TAG,s);
                //jsonobject로 서버에서 받아 왔기때문에 파싱해준다
                JSONObject jsonObject = new JSONObject(s);
                String request_result =  jsonObject.getString("result");


                if (request_result.equals("save_ok")){
                    //채팅 저장 성공
                    String reason = jsonObject.getString("data");
                    Log.d("chat_save",reason);


                }else if(request_result.equals("save_fail")){
                    //채팅 저장 실패
                    String reason = jsonObject.getString("data");
                    Log.d("chat_save",reason);
                    Log.d("chat_save",jsonObject.getString("error"));

                }else if(request_result.equals("chat_data_ok")){
                    //채팅내용 가져오기
                    JSONObject jsonObject1 =new JSONObject(jsonObject.getString("data"));
                    JSONArray jsonArray = jsonObject1.optJSONArray("data");

                    for(int i= 0; i<jsonArray.length();i++){

                        JSONObject jsonChildNode = jsonArray.getJSONObject(i);
                        String caller = jsonChildNode.getString("caller");
                        String message =  jsonChildNode.getString("message");
                        String time = jsonChildNode.getString("time");

                        //채팅내용 아이템에 추가 (작성자,메세지내용,시간)
                        add_items(caller,message,time);
                        }

                    chat_index = jsonArray.length();
                    chatting_adapter.notifyDataSetChanged();

                }
                else{
                    String data = jsonObject.getString("data");
                    Log.d(TAG,data);//오류 로그
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.


        }
    }

    private void add_items(String user_name,String message,String time){

        if (user_name.equals(my_name)){
            //내가 보낸 채팅일 경우
            chat_list_items.add(new Chat_list_item("",user_name,message,time,""));
        }else{
            //상대방이 보낸 채팅일 겨우
            chat_list_items.add(new Chat_list_item("",user_name,message,time,user_image));

        }
    }

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
                    int value1 = msg.getData().getInt("fromService");
                    String value2 = msg.getData().getString("test");
                    Log.i("test","act : value1 "+value1);
                    Log.i("test","act : value2 "+value2);
                    break;
            }
            return false;
        }
    }));

}
