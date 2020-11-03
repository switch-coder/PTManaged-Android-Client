package com.example.trainer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainer.recyclerview.PT_log_item;
import com.example.trainer.recyclerview.Routine_adapter;
import com.example.trainer.recyclerview.Routine_item;
import com.example.trainer.recyclerview.Schedule_adapter;
import com.example.trainer.recyclerview.Schedule_item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.trainer.Public.url_address;

public class add_pt_log_Activity extends AppCompatActivity {

    //트레이너가 회원 PT 수업을 끝낸후 수업내용을 기록하는 화면

    RecyclerView recyclerView_Routine;
    Routine_adapter routine_adapter;
    LinearLayoutManager layout_manager_routine;
    ArrayList<Routine_item> items = new ArrayList();

    ImageButton btn_add_routine,btn_back,btn_search_date;
    Button btn_complete;
    TextView select_date,select_start_time,select_end_time,select_week,none_select;
    EditText input_weight,input_muscle_mass,input_fat_mass,input_uniqueness;
    String url = "PT_log.php";
    String TAG = "ADD_PT_ACTIVITY";
    String user_id,customer_id,schedule_index;
    ContentValues values = new ContentValues();
    Context context;
    Format format;

    ArrayList<PT_log_item> log_items = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pt_log);
        context=this;

        btn_add_routine = findViewById(R.id.add_routine);
        btn_back = findViewById(R.id.btn_back);
        btn_complete = findViewById(R.id.btn_complete);
        input_weight =  findViewById(R.id.input_weight);
        input_muscle_mass =  findViewById(R.id.muscle_mass);
        input_fat_mass = findViewById(R.id.fat_mass);
        input_uniqueness = findViewById(R.id.input_uniqueness);
        btn_search_date = findViewById(R.id.PT_date_search);
        select_date = findViewById(R.id.select_date);
        select_start_time = findViewById(R.id.select_start_time);
        select_end_time = findViewById(R.id.select_end_time);
        select_week = findViewById(R.id.select_week);
        none_select = findViewById(R.id.none_selected);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            customer_id = bundle.getString("customer_id");
        }
        user_id = Sharedprefence.getString(context,"user_id");


        items.add(new Routine_item("","",""));
        recyclerView_Routine = findViewById(R.id.recyclerView_routine);
        layout_manager_routine= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView_Routine.setLayoutManager(layout_manager_routine);
        recyclerView_Routine.setItemAnimator(new DefaultItemAnimator());
        routine_adapter = new Routine_adapter(this,items,"add_pt");
        recyclerView_Routine.setAdapter(routine_adapter);

        format = new SimpleDateFormat("yyyy-MM-dd");
        final DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics(); //디바이스 화면크기를 구하기위해

        //오늘날짜를 넣어줌
        String today =format.format(Calendar.getInstance().getTime());

        //PT완료한 날짜 가져오기
        get_date_PT(today);

        //운동루틴 아이템 추가 버튼
        btn_add_routine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.add(new Routine_item("","",""));
                routine_adapter.notifyDataSetChanged();
            }
        });

        //날짜 선택 버튼
        btn_search_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final  PT_log_dialog  pt_log_dialog= new PT_log_dialog(context,log_items);
                WindowManager.LayoutParams wm = pt_log_dialog.getWindow().getAttributes();  //다이얼로그의 높이 너비 설정하기위해
                wm.width = dm.widthPixels / 2;  //화면 너비의 절반
                wm.height = dm.heightPixels / 2;  //화면 높이의 절반
                wm.copyFrom(pt_log_dialog.getWindow().getAttributes());  //여기서 설정한값을 그대로 다이얼로그에 넣겠다는의미



                pt_log_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int result = pt_log_dialog.getResult();
                        if (result != 99999){
                            select_date.setText(log_items.get(result).getDate());
                            select_week.setText(log_items.get(result).getWeek());
                            select_start_time.setText(log_items.get(result).getStart_time());
                            select_end_time.setText(log_items.get(result).getEnd_time());
                            schedule_index = log_items.get(result).getIndex();
                            none_select.setVisibility(View.GONE);
                            select_date.setVisibility(View.VISIBLE);
                            select_week.setVisibility(View.VISIBLE);
                            select_start_time.setVisibility(View.VISIBLE);
                            select_end_time.setVisibility(View.VISIBLE);
                        }


                    }
                });
                pt_log_dialog.show();
            }
        });

        //뒤로가기 버튼
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //완료버튼
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (select_date.getText().toString().equals("")){
                    Toast.makeText(context,"날짜를 선택해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (input_uniqueness.getText().toString().equals("")){
                    Toast.makeText(context,"특이사항을 입력해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                //운동루틴을 jsonArray형식으로 바꾸고 String 으로 리턴
                String json = items_to_json();
                String fat = input_fat_mass.getText().toString();
                String muscle = input_muscle_mass.getText().toString();
                String weight = input_weight.getText().toString();
                String uniqueness = input_uniqueness.getText().toString();
                if (!json.equals("null")){
                    String json_date = "{\"fat\"" + ":" + "\"" + fat + "\"" + ","
                            + "\"muscle\"" + ":" + "\"" + muscle + "\""+ ","
                            + "\"weight\"" + ":" + "\"" + weight + "\"" + ","
                            + "\"uniqueness\"" + ":" + "\"" + uniqueness + "\"" + ","
                            + "\"date\"" + ":" + "\"" + select_date.getText().toString() + "\"" + ","
                            + "\"trainer_id\"" + ":" + "\"" + user_id + "\"" + ","
                            + "\"schedule_index\"" + ":" + "\"" + schedule_index + "\"" + ","
                            + "\"request_key\"" + ":" + "\"" + "pt_log_insert" + "\"" + ","
                            + "\"customer_id\"" + ":" + "\"" + customer_id + "\"" + "}";
                    Log.d(TAG,json_date);
                    values.put("data",json_date);
                    values.put("routine",json);
                    NetworkTask networkTask =new NetworkTask(url,values);
                    networkTask.execute();
                }

            }
        });
    }

    private String items_to_json(){
        JSONArray jsonArray = new JSONArray();
        try{


            //운동루틴 jsonArray 로 변환
            for(int i = 0;i<items.size(); i++){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("exercise_name",items.get(i).getExercise_name());
                jsonObject.put("repetition",items.get(i).getRepetition());
                jsonObject.put("set_number",items.get(i).getSet_number());

                if (items.get(i).getExercise_name().equals("")){
                    Toast.makeText(this,"운동이름을 입력해주세요",Toast.LENGTH_SHORT).show();

                    return "null";

                }
                if (items.get(i).getRepetition().equals("")){
                    Toast.makeText(this,"반복수를 입력해주세요",Toast.LENGTH_SHORT).show();
                    return "null";
                }
                if (items.get(i).getSet_number().equals("")){
                    Toast.makeText(this,"세트수를 입력해주세요",Toast.LENGTH_SHORT).show();
                    return "null";
                }

                    jsonArray.put(jsonObject);

            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonArray.toString();
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

                }else if(request_result.equals("schedule_ok")){
                    JSONObject jsonObject1 =new JSONObject(jsonObject.getString("data"));
                    JSONArray jsonArray = jsonObject1.optJSONArray("data");

                    for(int i= 0; i<jsonArray.length();i++){


                        JSONObject jsonChildNode = jsonArray.getJSONObject(i);
                        String start_time = jsonChildNode.getString("start_time").substring(0,5);
                        String end_time =  jsonChildNode.getString("end_time").substring(0,5);
                        String day = jsonChildNode.getString("day");
                        String index = jsonChildNode.getString("num");
//                        Calendar cal = Calendar.getInstance();
//                        cal.set(Calendar.DATE,Integer.parseInt(day));
//                        int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
                        log_items.add(new PT_log_item(index,day,"",start_time,end_time));
                        Log.d(TAG,day);
                    }

                }else if(request_result.equals("pt_log_ok")){
                    String data= jsonObject.getString("data");
                    Toast.makeText(context,data,Toast.LENGTH_SHORT).show();
                    finish();
                }else if (request_result.equals("pt_log_fail")){
                    String data =jsonObject.getString("data");
                    Toast.makeText(context,data,Toast.LENGTH_SHORT).show();
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

    private void get_date_PT(String today){

        values.clear();
        // 트레이너가 열어놓은 시간 가져오기
        // 일반회원일 경우 담당 트레이너의 아이디
        // 트레이너일 경우 본인 아이디

        //jsonObject 형태로 만들어서 보냄
        String json_date = "{\"customer_id\"" + ":" + "\"" + customer_id + "\"" + ","
                + "\"trainer_id\"" + ":" + "\"" + user_id + "\""+ ","
                + "\"today\"" + ":" + "\"" + today + "\""+ ","
                + "\"request_key\"" + ":" + "\"" + "schedule" + "\"" + "}";
        Log.d(TAG,json_date);
        values.put("data", json_date);

         new NetworkTask(url, values) .execute();


    }
}
