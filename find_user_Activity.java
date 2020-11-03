package com.example.trainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainer.recyclerview.Schedule_item;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.trainer.Public.url_address;

public class find_user_Activity extends AppCompatActivity {

    InputMethodManager imm;
    Context mContext;
    Button btn_add_user, btn_search_user;
    TextView user_name,search_result;
    EditText input_user_id;
    ImageView user_image;
    LinearLayout  linearLayout_user;
    String url = "Find_user.php";
    String TAG = "find_user_Activity";
    String my_type ;
    String request_user_id,my_id;

    ContentValues values = new ContentValues();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user_);
        mContext=this;
        btn_add_user =  findViewById(R.id.add_user);
        btn_search_user = findViewById(R.id.search_user);
        user_name = findViewById(R.id.user_name);
        input_user_id = findViewById(R.id.input_user_id);
        user_image = findViewById(R.id.user_image);
        search_result = findViewById(R.id.search_result);
        linearLayout_user = findViewById(R.id.linearLayout_user);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //로그인 한 회원정보 가져오기
        my_type = Sharedprefence.getString(mContext,"user_type");
        my_id = Sharedprefence.getString(mContext,"user_id");

        //검색 버튼
        btn_search_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = input_user_id.getText().toString();
                if (user_id.equals("")){
                    Toast.makeText(mContext,"회원 아이디를 입력해주세요",Toast.LENGTH_LONG).show();
                }else{
                    values.clear();
                    //jsonObject 형태로 만들어서 보냄
                    //회원 아이디/ 유저(사용자) 타입
                    String json_date = "{\"my_type\"" + ":" + "\"" + my_type + "\"" + ","
                            + "\"request_key\"" + ":" + "\"" + "search" + "\""+ ","
                            + "\"user_id\"" + ":" + "\"" + user_id + "\"" + "}";

                    values.put("data", json_date);
                    hideKeyboard();
                    //url,입력한 값들을 서버에 보냄
                    NetworkTask networkTask = new NetworkTask(url, values);
                    networkTask.execute();
                }


            }
        });

        //회원 추가 버튼
        btn_add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                values.clear();
                //jsonObject 형태로 만들어서 보냄
                //추가할 회원 아이디 / 내 아이디 / 리퀘스트 키값
                String json_date = "{\"user_id\"" + ":" + "\"" + request_user_id + "\"" + ","
                        + "\"request_key\"" + ":" + "\"" + "add_user" + "\""+ ","
                        + "\"my_type\"" + ":" + "\"" + my_type + "\""+ ","
                        + "\"my_id\"" + ":" + "\"" + my_id + "\"" + "}";

                values.put("data", json_date);

                //url,입력한 값들을 서버에 보냄
                NetworkTask networkTask = new NetworkTask(url, values);
                networkTask.execute();
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
            try {

                //jsonObject로 서버에서 받아 왔기때문에 파싱해준다
                Log.d(TAG, s);
                JSONObject jsonObject = new JSONObject(s);
                String request_result = jsonObject.getString("result");
                Log.d(TAG, request_result);

                if (request_result.equals("ok")) {
                    //스케쥴 업로드 했을때
                    String request_user_name = jsonObject.getString("user_name");
                    String request_user_image = jsonObject.getString("user_image");
                    request_user_id = jsonObject.getString("user_id");
                    user_name.setText(request_user_name);
                    if (request_user_image.equals("")) {
                        user_image.setImageResource(R.mipmap.default_user_image);
                    } else {
                        //디비 이미지파일 이름앞에 '/' 붙어있음
                        Picasso.get().load("http://13.59.136.241" + request_user_image).into(user_image);
                    }
                    //검색결과가 회원이 있을경우
                    linearLayout_user.setVisibility(View.VISIBLE);
                    search_result.setVisibility(View.INVISIBLE);

                } else if (request_result.equals("empty")) {
                    //검색결과 회원이 없을경우
                    linearLayout_user.setVisibility(View.GONE);
                    search_result.setVisibility(View.VISIBLE);
                    String meesage = jsonObject.getString("message");
                    Log.d(TAG, meesage);//오류 로그

                }else if(request_result.equals("add_ok")) {
                    String message = jsonObject.getString("message");
                    Toast.makeText(mContext,message,Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(mContext, "다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    linearLayout_user.setVisibility(View.GONE);
                    search_result.setVisibility(View.INVISIBLE);
                    Log.d(TAG, s);//오류 로그
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.


        }
    }
    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(input_user_id.getWindowToken(), 0);
    }

}
