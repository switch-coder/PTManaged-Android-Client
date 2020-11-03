package com.example.trainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import static com.example.trainer.Public.url_address;

public class Sign_up extends AppCompatActivity {


    private TextView myResult,valid_id;
    private EditText input_id;
    private  RadioButton user_type;
    private static  final String TAG = "Sign_up_activity";
    private   ContentValues values = new ContentValues();
    boolean id,name,password,password_confirm;
    String url ="Signup_Check.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final Button btn_sign_in =  findViewById(R.id.button_sign_in);
        myResult =(TextView)findViewById(R.id.text_result);
         input_id =findViewById(R.id.input_id);
        final EditText input_password_confirm = findViewById(R.id.input_password_confirm);
        final EditText input_name = findViewById(R.id.input_name);
        final EditText input_password = findViewById(R.id.input_password);
        final RadioGroup radioGroup = findViewById(R.id.radioGroup);

        valid_id =(TextView)findViewById(R.id.valid_id);
        final Button btn_overlap_check = findViewById(R.id.overlap_check);



        //아이디값 변화시 이벤트
        input_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
             //입력하기 전에
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력 하고 있을때
                 // 영어 숫자만 가능한 정규식표현^[a-zA-Z0-9]*$
            }

            @Override
            public void afterTextChanged(Editable s) {// 입력 끝났을때
                //입력된 값이 영문/ 숫자가 아닐때
                if(!Pattern.matches("^[a-zA-Z0-9].{5,15}$", input_id.getText().toString())) {
                    valid_id.setText("영문/숫자가 포함 5~15자 입력해주세요");
                    input_id.setTextColor(Color.RED);
                    btn_overlap_check.setEnabled(false);
                    id = false;
                }else{
                    input_id.setTextColor(Color.BLUE);
                    btn_overlap_check.setEnabled(true);
                }
            }
        });

        //이름 유효성 검사
        input_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!Pattern.matches("^[a-zA-Z가-힣].{2,10}$", input_name.getText().toString())) {
                    input_name.setTextColor(Color.RED);
                    name = false;
                }else{
                    name = true;
                    input_name.setTextColor(Color.BLUE);
                }
            }
        });

        //비밀번호 유효성 검사
        input_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!Pattern.matches("^[a-zA-Z0-9!@.#$%^&*?_~]{8,15}", input_password.getText().toString())) {
                    input_password.setTextColor(Color.RED);
                    password = false;
                }else{
                    password = true;
                    input_password.setTextColor(Color.BLUE);
                }
            }
        });

        //비밀번호 확인 검사
        input_password_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!input_password.getText().toString().equals(input_password_confirm.getText().toString())){
                    input_password_confirm.setTextColor(Color.RED);
                    password_confirm = false;
                }else{
                    input_password_confirm.setTextColor(Color.BLUE);
                    password_confirm =  true;
                }
            }
        });

        //아이디 중복검사
        btn_overlap_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                values.clear();
                values.put("id",input_id.getText().toString());
                values.put("Request_key","overlapCheck");
                NetworkTask networkTask = new NetworkTask(url,values);
                networkTask.execute();
            }
        });

        //회원가입 버튼 활성화


        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radio_id = radioGroup.getCheckedRadioButtonId();
                user_type = findViewById(radio_id);

                if(!id){
                    Toast.makeText(getApplicationContext(),"아이디 값",Toast.LENGTH_SHORT).show();
                }else if(!name) {
                    Toast.makeText(getApplicationContext(),"이름 값",Toast.LENGTH_SHORT).show();
                }else if(!password){
                    Toast.makeText(getApplicationContext(),"패스워드 값",Toast.LENGTH_SHORT).show();
                }else if(!password_confirm){
                    Toast.makeText(getApplicationContext(),"패스워드 확인 값",Toast.LENGTH_SHORT).show();
                }else{
                    values.clear();
                    values.put("Request_key","sign_up");
                    values.put("user_type",user_type.getText().toString());
                    values.put("id",input_id.getText().toString());
                    values.put("name",input_name.getText().toString());
                    values.put("pw",input_password.getText().toString());
                    NetworkTask networkTask = new NetworkTask(url,values);
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            myResult.setText(s);
            Log.d(TAG,s);//오류 로그
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
           if (s.equals("do use id")){
               valid_id.setText("이미 사용중인 아이디 입니다");
               valid_id.setTextColor(Color.RED);
           }else if(s.equals("can use id")){
               valid_id.setText("사용가능한 아이디 입니다");
               id = true;
               valid_id.setTextColor(Color.BLUE);
           }else if(s.equals("success")){
               Toast.makeText(getApplicationContext(),"회원가입 되었습니다",Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(getApplicationContext(), Fragment.class);
               intent.putExtra("id",input_id.getText().toString());
               intent.putExtra("user_type",user_type.getText().toString());
               startActivity(intent);
           }else{
                Toast.makeText(getApplicationContext(),"오류",Toast.LENGTH_SHORT).show();
               Log.d(TAG,s);//오류 로그
           }

        }
    }



}

