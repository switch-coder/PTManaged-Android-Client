package com.example.trainer.recyclerview;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainer.R;
import com.example.trainer.RequestHttpURLConnection;
import com.example.trainer.User_detail_Activity;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;

public class User_list_adapter extends RecyclerView.Adapter<User_list_Viewholder> {

    private ArrayList<user_list_item> mItems;
    Context mContext;
    private ContentValues values =  new ContentValues();
    String url = "Find_user.php";
    private String TAG = "User_list_adapter";
    public User_list_adapter(Context mContext, ArrayList itemList) {
        this.mContext = mContext;// 객채화
        mItems = itemList;
    }

    @Nullable
    @Override
    public User_list_Viewholder onCreateViewHolder(@Nullable ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        mContext = parent.getContext();
        User_list_Viewholder holder = new User_list_Viewholder(v);
        return holder;
    }


    @Override
    public void onBindViewHolder(User_list_Viewholder holder, final int poition) {
        if (mItems.get(poition).user_image.equals("")){
            holder.user_image.setImageResource(R.mipmap.default_user_image);
        }else{
            Picasso.get().load(mItems.get(poition).user_image).into(holder.user_image);
        }

        holder.user_id.setText(mItems.get(poition).user_id);
        holder.user_name.setText(mItems.get(poition).user_name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //아이템이 클릭됬을때 이벤트

                get_user_detail(mItems.get(poition).user_id,poition,v);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private void get_user_detail(String user_id,int position,View v){

        values.clear();

        //jsonObject 형태로 만들어서 보냄
        String json_date = "{\"customer_id\"" + ":" + "\"" + user_id + "\"" + ","
                + "\"request_key\"" + ":" + "\"" + "user_detail" + "\"" + "}";
        Log.d(TAG,json_date);
        values.put("data", json_date);

        new NetworkTask(url, values,position,v) .execute();


    }

    //httpConnect 서버통신
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private int position;
        private View v;

        public NetworkTask(String url, ContentValues values,int position,View v) {
            this.url = url;
            this.values = values;
            this.position= position;
            this.v=v;
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


                if (request_result.equals("user_detail_fail")){
                    //채팅 저장 성공
                    String reason = jsonObject.getString("data");
                    Log.d("chat_save",reason);

                }else if(request_result.equals("user_detail_ok")) {
                    String workout_exp = jsonObject.getString("workout_exp");
                    String uniqueness = jsonObject.getString("uniqueness");
                    String job = jsonObject.getString("job");
                    String age = jsonObject.getString("age");
                    String stature = jsonObject.getString("stature");
                    String weight = jsonObject.getString("weight");
                    String sex = jsonObject.getString("sex");
                    Intent intent = new Intent(v.getContext(), User_detail_Activity.class);
                    intent.putExtra("workout_exp",workout_exp);
                    intent.putExtra("uniqueness",uniqueness);
                    intent.putExtra("job",job);
                    intent.putExtra("age",age);
                    intent.putExtra("stature",stature);
                    intent.putExtra("weight",weight);
                    intent.putExtra("sex",sex);
                    intent.putExtra("user_id",mItems.get(position).user_id);
                    intent.putExtra("user_name",mItems.get(position).user_name);
                    intent.putExtra("user_image",mItems.get(position).user_image);
                    //v.getContext() 넣어야 그 화면에서 넘어감
                    Log.d(TAG,mItems.get(position).user_id);
                    v.getContext().startActivity(intent);

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

}

/////////////////////////리사이클러뷰 뷰홀더///////////////////////////////////////
/////////////////////////리사이클러뷰 뷰홀더///////////////////////////////////////
/////////////////////////리사이클러뷰 뷰홀더///////////////////////////////////////
class User_list_Viewholder extends RecyclerView.ViewHolder{
    ImageView user_image;
    TextView user_name,user_id;

    public  User_list_Viewholder(View itemView) {
        super(itemView);
        user_image = (ImageView) itemView.findViewById(R.id.user_image);
        user_id = (TextView) itemView.findViewById(R.id.user_id);
        user_name = (TextView) itemView.findViewById(R.id.user_name);

    }


}