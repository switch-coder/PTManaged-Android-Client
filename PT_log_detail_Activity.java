package com.example.trainer;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainer.recyclerview.PT_log_item;
import com.example.trainer.recyclerview.Routine_adapter;
import com.example.trainer.recyclerview.Routine_item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.trainer.Public.url_address;

public class PT_log_detail_Activity extends AppCompatActivity {
    //트레이너가 회원 PT 수업을 끝낸후 수업내용을 기록하는 화면

    RecyclerView recyclerView_Routine;
    Routine_adapter routine_adapter;
    LinearLayoutManager layout_manager_routine;
    ArrayList<Routine_item> items = new ArrayList();

    ImageButton btn_add_routine,btn_back,btn_search_date;
    Button btn_complete;
    TextView select_date,select_start_time,select_end_time,select_week,none_select,text_none,title,set_date;
    EditText input_weight,input_muscle_mass,input_fat_mass,input_uniqueness;
    String url = "PT_log.php";
    String TAG = "ADD_PT_ACTIVITY";
    String user_id;
    int index;
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
        text_none =  findViewById(R.id.text_none);
        title =  findViewById(R.id.title);
        set_date =  findViewById(R.id.set_date);
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

        text_none.setVisibility(View.GONE);
        title.setText("PT 일지");
        set_date.setText("PT 날짜");
        input_weight.setEnabled(false);
        input_fat_mass.setEnabled(false);
        input_muscle_mass.setEnabled(false);
        input_uniqueness.setEnabled(false);
        input_weight.setBackgroundColor(Color.WHITE);
        input_fat_mass.setBackgroundColor(Color.WHITE);
        input_muscle_mass.setBackgroundColor(Color.WHITE);
        input_weight.setTextColor(Color.BLACK);
        input_fat_mass.setTextColor(Color.BLACK);
        input_muscle_mass.setTextColor(Color.BLACK);
        input_uniqueness.setTextColor(Color.BLACK);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            index = bundle.getInt("index");
        }
        get_date_PT(index);
        user_id = Sharedprefence.getString(context,"user_id");


        recyclerView_Routine = findViewById(R.id.recyclerView_routine);
        layout_manager_routine= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView_Routine.setLayoutManager(layout_manager_routine);
        recyclerView_Routine.setItemAnimator(new DefaultItemAnimator());
        routine_adapter = new Routine_adapter(this,items,"PT_detail");
        recyclerView_Routine.setAdapter(routine_adapter);

        btn_search_date.setVisibility(View.GONE);
        btn_add_routine.setVisibility(View.GONE);


        format = new SimpleDateFormat("yyyy-MM-dd");
        final DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics(); //디바이스 화면크기를 구하기위해

        //오늘날짜를 넣어줌
        String today =format.format(Calendar.getInstance().getTime());

        //운동루틴 아이템 추가 버튼
        btn_add_routine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.add(new Routine_item("","",""));
                routine_adapter.notifyDataSetChanged();
            }
        });



        //뒤로가기 버튼
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_complete.setVisibility(View.GONE);


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


                if (request_result.equals("PT_log_fail")){
                    String reason = jsonObject.getString("data");

                    Log.d("chat_save",jsonObject.getString("pt_routine"));
                }else if(request_result.equals("PT_log_ok")) {
                    String day = jsonObject.getString("day");
                    String trainer_name = jsonObject.getString("trainer_name");
                    String weight = jsonObject.getString("weight");
                    String muscle_mass = jsonObject.getString("muscle_mass");
                    String body_fat_mass = jsonObject.getString("body_fat_mass");
                    String uniqueness = jsonObject.getString("uniqueness");
                    JSONObject jsonObject1 =new JSONObject(jsonObject.getString("pt_routine"));
                    JSONArray jsonArray = jsonObject1.optJSONArray("pt_routine");
                    for (int i=0; i<jsonArray.length() ;i++){
                        JSONObject jsonChildNode = jsonArray.getJSONObject(i);
                        String repetition = jsonChildNode.getString("repetition");
                        String set_number =  jsonChildNode.getString("set_number");
                        String exercise_name = jsonChildNode.getString("exercise_name");
                        items.add(new Routine_item(exercise_name,repetition,set_number));
                    }

                    select_date.setText(day);
                    select_date.setVisibility(View.VISIBLE);
                    input_weight.setText(weight);
                    input_fat_mass.setText(body_fat_mass);
                    input_muscle_mass.setText(muscle_mass);
                    input_uniqueness.setText(uniqueness);
                    none_select.setVisibility(View.GONE);
                    routine_adapter.notifyDataSetChanged();
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

    private void get_date_PT(int index ){

        values.clear();
        // 트레이너가 열어놓은 시간 가져오기
        // 일반회원일 경우 담당 트레이너의 아이디
        // 트레이너일 경우 본인 아이디

        //jsonObject 형태로 만들어서 보냄
        String json_date = "{\"index\"" + ":" + "\"" + index + "\"" + ","
                + "\"request_key\"" + ":" + "\"" + "PT_log_detail" + "\"" + "}";
        Log.d(TAG,json_date);
        values.put("data", json_date);

        new NetworkTask(url, values) .execute();


    }

}


