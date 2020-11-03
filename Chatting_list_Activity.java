package com.example.trainer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trainer.recyclerview.Chat_list_adapter;
import com.example.trainer.recyclerview.Chat_list_item;
import com.example.trainer.recyclerview.Schedule_adapter;
import com.example.trainer.recyclerview.Schedule_item;
import com.example.trainer.recyclerview.user_list_item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.trainer.Public.url_address;

public class Chatting_list_Activity extends Fragment {

    View view;
    Context mContext;

    RecyclerView recyclerview_chat_list;
    Chat_list_adapter chat_list_adapter;
    LinearLayoutManager layout_manager_chat_list;
    ArrayList<Chat_list_item> chat_list_items= new ArrayList();

    String my_type,my_id,my_name,my_trainer;
    String TAG = "Chatting_list_Activity";
    ContentValues values = new ContentValues();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_chatting_list_,container,false);
        mContext=getActivity();

        my_type = Sharedprefence.getString(mContext,"user_type");
        my_id = Sharedprefence.getString(mContext,"user_id");
        my_name = Sharedprefence.getString(mContext,"user_name");

        if (my_type.equals("general")){
            my_trainer = Sharedprefence.getString(mContext,"my_trainer");
        }


        recyclerview_chat_list = view.findViewById(R.id.recyclerView_chat_list);
        layout_manager_chat_list= new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerview_chat_list.setLayoutManager(layout_manager_chat_list);
        recyclerview_chat_list.setItemAnimator(new DefaultItemAnimator());
        chat_list_adapter = new Chat_list_adapter(mContext,chat_list_items);
        recyclerview_chat_list.setAdapter(chat_list_adapter);





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
                    //
                    String reason = jsonObject.getString("message");
                    Toast.makeText(mContext,reason,Toast.LENGTH_SHORT).show();

                }else if(request_result.equals("list_ok")){
                    //
                    chat_list_items.clear();
                    if (my_type.equals("general")){
                        String my_trainer_image = jsonObject.getString("image");
                        String my_trainer_name = jsonObject.getString("name");
                        chat_list_items.add(new Chat_list_item(my_trainer,my_trainer_name,"","",my_trainer_image));

                    }else{
                        JSONObject jsonObject1 =new JSONObject(jsonObject.getString("data"));
                        JSONArray jsonArray = jsonObject1.optJSONArray("data");

                        for(int i= 0; i<jsonArray.length();i++) {
                            Log.d(TAG, "rows:" + jsonArray.length());
                            JSONObject jsonChildNode = jsonArray.getJSONObject(i);

                            String general_id =  jsonChildNode.getString("id");
                            String general_name = jsonChildNode.getString("name");
                            String general_image = jsonChildNode.getString("user_image");
                            Log.d(TAG, "id" + general_id + "/name" + general_name+"/"+general_image);
                            chat_list_items.add(new Chat_list_item(general_id,general_name,"","",url_address+general_image));

                        }
                    }
                    chat_list_adapter.notifyDataSetChanged();

                }else if(request_result.equals("list_empty")){

                    //스케쥴 비어있을때
                    chat_list_items.clear();
                    chat_list_adapter.notifyDataSetChanged();
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

    private void get_user_list(){
        String url = "Find_user.php";
        values.clear();
        //jsonObject 형태로 만들어서 보냄
        //트레이너 아이디 / 유저 타입/ 리퀘스트 키 값
        String json_date;
        if (my_type.equals("general")){
             json_date = "{\"user_id\"" + ":" + "\"" + my_trainer + "\"" + ","
                    + "\"my_type\"" + ":" + "\"" + my_type + "\""+ ","
                    + "\"request_key\"" + ":" + "\"" + "chat_list" + "\"" + "}";
        }else{
             json_date = "{\"user_id\"" + ":" + "\"" + my_id + "\"" + ","
                    + "\"request_key\"" + ":" + "\"" + "user_list" + "\"" + "}";
        }


        values.put("data", json_date);

        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();

    }

    @Override
    public void onStart() {
        super.onStart();
        get_user_list();
        Log.d("onstart","onstart");

    }


}
