package com.example.trainer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.trainer.Public.url_address;

public class Login extends AppCompatActivity {

    private static  final String TAG = "Sign_up_activity";
    String file = "login_Check.php";
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context= this;

        Button btn_login = findViewById(R.id.login);
        Button btn_signup = findViewById(R.id.sign_in);

        final EditText user_id = findViewById(R.id.input_id);
        final EditText user_pw = findViewById(R.id.input_pw);


        //회원가입 버튼
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Sign_up.class);
                startActivity(intent);
            }
        });


        //로그인 버튼
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(user_id.getText().toString().length() <= 0 && user_pw.getText().toString().length() <= 0 ){
                    Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show();

                }else {
                    //서버에 보낼 데이터값 values에 넣기
                    ContentValues values = new ContentValues();
                     String url = file;

                    //jsonObject 형태로 만들어서 보냄
                    String data = "{\"user_id\"" + ":" + "\"" + user_id.getText().toString() + "\"" + ","
                            + "\"user_pw\"" + ":" + "\"" + user_pw.getText().toString() + "\"" + "}";

                    // login이라는 키와 jsonObject 형태로 만든 데이터 넣어서 전송
                    values.put("login", data);
                    NetworkTask networkTask = new NetworkTask(url, values);
                    networkTask.execute();
                }
            }
        });
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
                //가져오는 데이터
                // result = success               / fail
                //          user_id , user_name   / error = 에러코드 로그에 찍어주기위해
                Log.d(TAG,s);
                JSONObject jsonObject = new JSONObject(s);
                String request_result =  jsonObject.getString("result");
                Log.d(TAG,request_result);

                if (request_result.equals("ok")){

                    //회원정보가 있을때 유저 아이디, 유저 이름을 쉐어드에 넣어주고 메인 화면으로 이동
                    String user_id = jsonObject.getString("user_id");
                    String user_name = jsonObject.getString("user_name");
                    String user_type = jsonObject.getString("user_type");
                    String user_image = jsonObject.getString("user_image");
                    int initial_setting = jsonObject.getInt("initial_setting");
                    if (user_type.equals("general")){
                        String my_trainer = jsonObject.getString("my_trainer");
                        Sharedprefence.setString(context,"my_trainer",my_trainer);
                    }
                    Sharedprefence.setInt(context,"initial_setting",initial_setting);
                    Sharedprefence.setString(context,"user_id",user_id);
                    Sharedprefence.setString(context,"user_name",user_name);
                    Sharedprefence.setString(context,"user_type",user_type);
                    Sharedprefence.setString(context,"user_image",url_address.substring(0,url_address.length()-1)+user_image);

                    Intent intent = new Intent(Login.this,Fragment.class);
                    Login.this.startActivity(intent);
                    Login.this.finish();

                }else if(request_result.equals("fail")){
                    String reason = jsonObject.getString("message");

                    Toast.makeText(getApplicationContext(),reason,Toast.LENGTH_SHORT).show();
                    Log.d(TAG,reason);

                }else{
                    Toast.makeText(getApplicationContext(),"다시 시도해주세요",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,s);//오류 로그
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.


        }
    }
}
