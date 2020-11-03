package com.example.trainer.recyclerview;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainer.Chatting_room_activity;
import com.example.trainer.Main_Activity;
import com.example.trainer.Schedule_dialog;
import com.example.trainer.R;
import com.example.trainer.RequestHttpURLConnection;
import com.example.trainer.Sharedprefence;
import com.example.trainer.User_detail_Activity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Schedule_adapter extends RecyclerView.Adapter<Schedule_Viewholder>  {

        private ArrayList<Schedule_item> mItems;
        private String TAG ="Schedule_adapter";
        private String url = "Main_activity.php";
        private ContentValues values = new ContentValues();
        private String start_time,end_time;


        Context mContext;
        public Schedule_adapter(Context mContext,ArrayList itemList){
            this.mContext =mContext;// 객채화
            mItems = itemList;
        }

        @Nullable
        @Override
        public Schedule_Viewholder onCreateViewHolder(@Nullable ViewGroup parent, int viewType){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_schedule_item,parent,false);
            mContext = parent.getContext();
            Schedule_Viewholder holder = new Schedule_Viewholder(v);
            return holder;
        }

        @Override
        public  void  onBindViewHolder(Schedule_Viewholder holder, final  int position){
            final DisplayMetrics dm = mContext.getResources().getDisplayMetrics(); //디바이스 화면크기를 구하기위해

            holder.user_id.setText(mItems.get(position).user_id);
            holder.start_time.setText(mItems.get(position).start_time);
            holder.end_time.setText(mItems.get(position).end_time);
            holder.user_name.setText(mItems.get(position).user_name);
            if (mItems.get(position).user_id.equals("null")){
                holder.user_name.setText("예약자 없음");
                holder.user_id.setVisibility(View.INVISIBLE);
                Picasso.get().load(mItems.get(position).user_image).into(holder.user_image);
            }else{
                Picasso.get().load(mItems.get(position).user_image).into(holder.user_image);
            }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final Schedule_dialog custom_dialog = new Schedule_dialog(mContext, position, mItems.get(position));
                WindowManager.LayoutParams wm = custom_dialog.getWindow().getAttributes();  //다이얼로그의 높이 너비 설정하기위해
                wm.width = dm.widthPixels / 2;  //화면 너비의 절반
                wm.height = dm.heightPixels / 2;  //화면 높이의 절반
                wm.copyFrom(custom_dialog.getWindow().getAttributes());  //여기서 설정한값을 그대로 다이얼로그에 넣겠다는의미

                custom_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int result = custom_dialog.getResult();
                        dialog_result(result,mItems.get(position),v,position);
                        if (result ==203) {
                            start_time = custom_dialog.getStart_time();
                            end_time = custom_dialog.getEnd_time();
                        }
                    }
                });
                custom_dialog.show();
            }
        });
        }
        @Override
        public int getItemCount() {
            return  mItems.size();
        }

        private void dialog_result(int result,Schedule_item item,View v,int position){
           String user_id = Sharedprefence.getString(mContext,"user_id");
           String user_name = Sharedprefence.getString(mContext,"user_name");
           String user_image = Sharedprefence.getString(mContext,"user_image");
           String json_date;
            switch(result){

                case 101:// 일반회원이 예약했을때
                    values.clear();
                    //jsonObject 형태로 만들어서 보냄
                    json_date = "{\"user_id\"" + ":" + "\"" + user_id + "\"" + ","
                            + "\"user_name\"" + ":" + "\"" + user_name + "\""+ ","
                            + "\"index\"" + ":" + "\"" + item.getIndex() + "\"" + ","
                            + "\"request_key\"" + ":" + "\"" + "book" + "\"" + "}";

                    Log.d(TAG,"case 101"+json_date);
                    values.put("data", json_date);
                    values.put("userImage",user_image);
                    new NetworkTask(url,values,position).execute();
                    break;

                case 102://예약 취소했을때
                    values.clear();
                    //jsonObject 형태로 만들어서 보냄
                     json_date = "{\"index\"" + ":" + "\"" + item.getIndex() + "\"" + ","
                            + "\"request_key\"" + ":" + "\"" + "book_cancel" + "\"" + "}";
                    values.put("data", json_date);
                    Log.d(TAG,json_date);
                    new NetworkTask(url,values,position).execute();
                    break;

                case 201://트레이너가 스케줄 삭제시
                    values.clear();
                    //jsonObject 형태로 만들어서 보냄
                    json_date = "{\"index\"" + ":" + "\"" + item.getIndex() + "\"" + ","
                            + "\"request_key\"" + ":" + "\"" + "schedule_delete" + "\"" + "}";
                    values.put("data", json_date);
                    new NetworkTask(url,values,position).execute();
                    break;

                case 202://예약자 디테일페이지 이동
                    Intent intent = new Intent(v.getContext(), User_detail_Activity.class);
                    //v.getContext() 넣어야 그 화면에서 넣어감
                    intent.putExtra("user_id",item.getUser_id());
                    intent.putExtra("user_name",item.getUser_name());
                    intent.putExtra("user_image",item.getUser_image());
                    mContext.startActivity(intent);
                    break;

                case 203://시간 변경
                    values.clear();
                    //jsonObject 형태로 만들어서 보냄
                    json_date = "{\"start_time\"" + ":" + "\"" + start_time + "\"" + ","
                            + "\"end_time\"" + ":" + "\"" + end_time + "\""+ ","
                            + "\"index\"" + ":" + "\"" + item.getIndex() + "\"" + ","
                            + "\"request_key\"" + ":" + "\"" + "modify_time" + "\"" + "}";

                    Log.d(TAG,"case 203"+json_date);
                    values.put("data", json_date);
                    new NetworkTask(url,values,position).execute();
                    break;
                    default:
            }


        }



    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        private int position;

        public NetworkTask(String url, ContentValues values,int position) {
            this.position = position;
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
                String data = jsonObject.getString("data");
                if (request_result.equals("book_ok")){
                    //pt 예약 성공시
                    String customer_id = jsonObject.getString("customer_id");
                    String customer_name = jsonObject.getString("customer_name");
                    String customer_image = jsonObject.getString("customer_image");
                    mItems.get(position).setUser_id(customer_id);
                    mItems.get(position).setUser_name(customer_name);
                    mItems.get(position).setUser_image(customer_image);
                    notifyDataSetChanged();
                    Toast.makeText(mContext,data,Toast.LENGTH_SHORT).show();
                }else if(request_result.equals("book_fail")){
                    //pt 예약 실패시
                    Toast.makeText(mContext,data,Toast.LENGTH_SHORT).show();

                }else if(request_result.equals("book_cancel_ok")){
                    //예약 취소시
                    Toast.makeText(mContext,data,Toast.LENGTH_SHORT).show();
                    mItems.get(position).setUser_id("");
                    mItems.get(position).setUser_name("예약자 없음");
                    mItems.get(position).setUser_image(null);
                    notifyDataSetChanged();
                }else if(request_result.equals("book_cancel_fail")){
                    //예약 취소 실패
                    Toast.makeText(mContext,data,Toast.LENGTH_SHORT).show();

                }else if(request_result.equals("schedule_delete_ok")){
                    //스케줄 삭제
                    Toast.makeText(mContext,data,Toast.LENGTH_SHORT).show();
                    mItems.remove(position);
                    notifyDataSetChanged();

                }else if(request_result.equals("modify_time_ok")){
                    Toast.makeText(mContext,data,Toast.LENGTH_SHORT).show();
                    mItems.get(position).setStart_time(start_time);
                    mItems.get(position).setEnd_time(end_time);
                    notifyDataSetChanged();
                }else if(request_result.equals("modify_time_fail")){
                    Toast.makeText(mContext,data,Toast.LENGTH_SHORT).show();
                    String errorLog = jsonObject.getString("log");
                    Log.d(TAG,errorLog);
                }
                else{
                    Toast.makeText(mContext,"다시 시도해주세요",Toast.LENGTH_SHORT).show();

                    Log.d(TAG,s+"/"+data);//오류 로그
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.


        }
    }
    }

/////////////////////////리사이클러뷰 뷰홀더///////////////////////////////////////
    class Schedule_Viewholder extends RecyclerView.ViewHolder {
        public ImageView user_image;
        public TextView user_name,user_id,start_time,end_time;
        public  Schedule_Viewholder(View itemView) {
            super(itemView);
            user_image = (ImageView) itemView.findViewById(R.id.user_image);
            user_id = (TextView) itemView.findViewById(R.id.user_id);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            start_time = (TextView) itemView.findViewById(R.id.start_time);
            end_time = (TextView) itemView.findViewById(R.id.end_time);
        }

    }


