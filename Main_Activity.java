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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.trainer.recyclerview.Profile_images_adapter;
import com.example.trainer.recyclerview.Schedule_adapter;
import com.example.trainer.recyclerview.Schedule_item;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

import static android.app.Activity.RESULT_OK;
import static com.example.trainer.Public.url_address;


public class Main_Activity extends Fragment  {

    View view;
    ImageView view_user_image;
    Context mContext;
    TextView view_user_name;
    private SimpleDateFormat format;
    ContentValues values = new ContentValues();

    private String choose_date,user_id,user_name,user_type,trainer_id,user_image;
    private ImageButton btn_add_schedule;
    private int add_schedule_result =1;
    private int initial;
    private static  final String TAG = "Main_Activity";

     RecyclerView recyclerview_schedule;
    private Schedule_adapter schedule_adapter;
     LinearLayoutManager layout_manager_schedule;
    private ArrayList<Schedule_item> schedule_items= new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main,container,false);
        mContext=getActivity();

        //사용자 정보 가져오기
        user_type= Sharedprefence.getString(mContext,"user_type");
        user_id= Sharedprefence.getString(mContext,"user_id");
        user_name= Sharedprefence.getString(mContext,"user_name");
        user_image= Sharedprefence.getString(mContext,"user_image");
        initial = Sharedprefence.getInt(mContext,"initial_setting");
        view_user_image = view.findViewById(R.id.user_image);
        view_user_name = view.findViewById(R.id.user_name);
        btn_add_schedule = view.findViewById(R.id.btn_add_schedule);

        //유저 이름+ (유저아이디)
        view_user_name.setText(user_name+"("+user_id+")");
        Log.d("userImage",user_image);
        Picasso.get().load(user_image).into(view_user_image);

        //일반회원일 경우 화면 설정
        if(user_type.equals("general")) {
            btn_add_schedule.setVisibility(View.GONE);
        }

        if (initial ==0 && user_type.equals("general")){
            Intent intent = new Intent(getContext(),Initial_setting_Activity.class);
            intent.putExtra("id",user_id);
            startActivity(intent);
        }



        //달력 설정
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -5);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 5);
        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarView)
                .range(startDate,endDate)
                .datesNumberOnScreen(5) //화면에 보여주는 날짜의 수
                .build();


        // 선택한 날짜를 simpleDateFormat으로 년/월/일 으로 값 변환
        format = new SimpleDateFormat("yyyy-MM-dd");

        //오늘날짜를 넣어줌
        choose_date =format.format(Calendar.getInstance().getTime());

        //서버에서 데이터 가져옴
        get_date_schedule(choose_date);

        //리사이클러뷰 - 스케줄 목록
        recyclerview_schedule = view.findViewById(R.id.recyclerview_schedule);
        layout_manager_schedule= new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerview_schedule.setLayoutManager(layout_manager_schedule);
        recyclerview_schedule.setItemAnimator(new DefaultItemAnimator());
        schedule_adapter = new Schedule_adapter(mContext,schedule_items);
        recyclerview_schedule.setAdapter(schedule_adapter);

        //캘린더 날짜 선택시
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                //해당날짜를 포맷에 맞춰 넣어준다
                choose_date = format.format(date.getTime());
                Log.d(TAG,choose_date);
                get_date_schedule(choose_date);

            }
        });

        //스케줄 등록창으로 이동
        btn_add_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Add_schedule.class);
                startActivityForResult(intent,add_schedule_result);
            }
        });



        return  view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == add_schedule_result && resultCode == RESULT_OK
                && null != data) {
            // 선택한 날, 시작 시간, 끝나는 시간
            String select_days = data.getStringExtra("selectDays");
            String start_time = data.getStringExtra("start_time");
            String end_time = data.getStringExtra("end_time");
            Log.d(TAG,"date"+select_days+"time"+start_time+"/"+end_time+"userId:"+user_id);

//            서버에 보낼 데이터값 values에 넣기

            String url = "Add_schedule.php";
            values.clear();
            //jsonObject 형태로 만들어서 보냄
            String json_date = "{\"select_days\"" + ":" + "\"" + select_days + "\"" + ","
                    + "\"start_time\"" + ":" + "\"" + start_time + "\""+ ","
                    + "\"end_time\"" + ":" + "\"" + end_time + "\"" + ","
                    + "\"user_id\"" + ":" + "\"" + user_id + "\"" + "}";

            values.put("data", json_date);
            NetworkTask networkTask = new NetworkTask(url, values);
            networkTask.execute();

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

                if (request_result.equals("ok")){
                    //스케쥴 업로드 했을때
                    String reason = jsonObject.getString("message");
                    Toast.makeText(mContext,reason,Toast.LENGTH_SHORT).show();

                    //서버에서 다시 해당날짜의 스케쥴 가져오기
                    get_date_schedule(choose_date);
                }else if(request_result.equals("schedule_ok")){
                    //스케줄 불러올때
                    JSONObject jsonObject1 =new JSONObject(jsonObject.getString("data"));
                    JSONArray jsonArray = jsonObject1.optJSONArray("data");
                    schedule_items.clear();

                    for(int i= 0; i<jsonArray.length();i++){

                        Log.d(TAG,"rows:"+jsonArray.length());
                        JSONObject jsonChildNode = jsonArray.getJSONObject(i);
                        int index = jsonChildNode.getInt("num");
                        String start_time = jsonChildNode.getString("start_time").substring(0,5);
                        String end_time =  jsonChildNode.getString("end_time").substring(0,5);
                        String general_id = jsonChildNode.getString("customer_id");
                        String general_name = jsonChildNode.getString("customer_name");
                        String general_image = jsonChildNode.getString("customer_image");
                        Log.d(TAG,start_time);

                        schedule_items.add(new Schedule_item(index,start_time,end_time,general_name,general_id,general_image));

                    }
                    schedule_adapter.notifyDataSetChanged();


                }else if(request_result.equals("fail")){
                    String reason = jsonObject.getString("message");
                    String error_log =jsonObject.getString("error");
                    Toast.makeText(mContext,reason,Toast.LENGTH_SHORT).show();
                    Log.d(TAG,error_log);

                }else if(request_result.equals("schedule_empty")){
                    //스케쥴 비어있을때
                    schedule_items.clear();
                    schedule_adapter.notifyDataSetChanged();
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

    public void get_date_schedule(String today){
        String url = "Main_activity.php";
        values.clear();
        // 트레이너가 열어놓은 시간 가져오기
        // 일반회원일 경우 담당 트레이너의 아이디
        // 트레이너일 경우 본인 아이디
        if(user_type.equals("general")){
            trainer_id = Sharedprefence.getString(mContext,"my_trainer");
        }else{
            trainer_id = user_id;
        }
        //jsonObject 형태로 만들어서 보냄

        String json_date = "{\"user_id\"" + ":" + "\"" + trainer_id + "\"" + ","
                + "\"today\"" + ":" + "\"" + today + "\""+ ","
                + "\"request_key\"" + ":" + "\"" + "schedule" + "\"" + "}";

        values.put("data", json_date);

        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();

    }





}
