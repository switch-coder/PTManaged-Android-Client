package com.example.trainer;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.trainer.recyclerview.Schedule_adapter;
import com.example.trainer.recyclerview.Schedule_item;
import com.example.trainer.recyclerview.User_list_adapter;
import com.example.trainer.recyclerview.user_list_item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.example.trainer.Public.url_address;

public class User_list_activity extends Fragment {

    View view;
    Context mContext;
    ImageButton btn_add_user;
    String user_type,user_id;
    int find_request = 1111;
    String TAG = "User_list_activity";
    ContentValues values =new ContentValues();

    RecyclerView recyclerview_user_list;
    User_list_adapter list_adapter;
    LinearLayoutManager layout_manager_list;
    ArrayList<user_list_item> items = new ArrayList();


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_user_list,container,false);
        mContext=getActivity();

        btn_add_user = view.findViewById(R.id.add_user);

        //사용자 정보 가져오기
        user_type= Sharedprefence.getString(mContext,"user_type");
        user_id= Sharedprefence.getString(mContext,"user_id");




        recyclerview_user_list = view.findViewById(R.id.recyclerview_user_list);
        layout_manager_list= new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerview_user_list.setLayoutManager(layout_manager_list);
        recyclerview_user_list.setItemAnimator(new DefaultItemAnimator());
        list_adapter = new User_list_adapter(mContext,items);
        recyclerview_user_list.setAdapter(list_adapter);

        btn_add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,find_user_Activity.class);
                startActivityForResult(intent,find_request);
            }
        });


        return  view;
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

                JSONObject jsonObject = new JSONObject(s);
                String request_result =  jsonObject.getString("result");
                Log.d(TAG,request_result);

                if (request_result.equals("ok")){
                    //스케쥴 업로드 했을때
                    String reason = jsonObject.getString("message");
                    Toast.makeText(mContext,reason,Toast.LENGTH_SHORT).show();

                    //서버에서 다시 해당날짜의 스케쥴 가져오기

                }else if(request_result.equals("list_ok")){
                    //스케쥴 불러올때
                    JSONObject jsonObject1 =new JSONObject(jsonObject.getString("data"));
                    JSONArray jsonArray = jsonObject1.optJSONArray("data");
                    items.clear();
                    for(int i= 0; i<jsonArray.length();i++){
                        Log.d(TAG,"rows:"+jsonArray.length());
                        JSONObject jsonChildNode = jsonArray.getJSONObject(i);

                        String general_id = jsonChildNode.getString("id");
                        String general_name = jsonChildNode.getString("name");
                        String general_image = jsonChildNode.getString("user_image");
                        Log.d(TAG,"id"+general_id+"/name"+general_name);
                        items.add(new user_list_item(general_id,general_name,url_address+general_image));
                        list_adapter.notifyDataSetChanged();

                    }

                }else if(request_result.equals("list_empty")){
                    //스케쥴 비어있을때
                    items.clear();
                    list_adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(mContext,"다시 시도해주세요",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,s);//오류 로그
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        get_user_list();
        Log.d("onstart","onstart");

    }

    private void get_user_list(){
        String url = "Find_user.php";
        values.clear();
        //jsonObject 형태로 만들어서 보냄
        String json_date = "{\"user_id\"" + ":" + "\"" + user_id + "\"" + ","
                + "\"request_key\"" + ":" + "\"" + "user_list" + "\"" + "}";

        values.put("data", json_date);

        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();

    }



}
