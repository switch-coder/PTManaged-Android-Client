package com.example.trainer;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.trainer.recyclerview.PT_log_item;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.trainer.Public.url_address;

public class User_detail_Activity extends AppCompatActivity {

    ImageView view_user_image;
    ImageButton message_room,btn_add_post;
    TextView view_user_age,view_user_name,view_user_tall,view_user_weight,view_empty;
    RadioGroup group_inBody;
    RadioButton radio_weight,radio_fat,radio_muscle;
    Button edit_profile,btn_PT_log;
    Context mContext;
    LineChart lineChart;
    ContentValues values =new ContentValues();

    String customer_id,customer_name,customer_image,weight,age,stature;
    ArrayList<PT_log_item> items = new ArrayList<>();
    ArrayList<Entry> entry_weight = new ArrayList<>();
    ArrayList<Entry> entry_muscle_mass = new ArrayList<>();
    ArrayList<Entry> entry_body_fat_mass = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd");
    int post_upload =9900;
    String TAG = "user_detail_activity";
    String url = "PT_log.php";
    int edit_request =900;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        mContext = this;

        view_user_image = findViewById(R.id.user_image);
        message_room = findViewById(R.id.message_room);
        btn_add_post = findViewById(R.id.add_post);
        view_user_age = findViewById(R.id.user_age);
        view_user_name = findViewById(R.id.name);
        view_user_tall = findViewById(R.id.user_tall);
        view_user_weight = findViewById(R.id.user_weight);
        view_empty = findViewById(R.id.empty);
        edit_profile = findViewById(R.id.btn_edit_profile);
        lineChart = findViewById(R.id.line_chart);
        btn_PT_log = findViewById(R.id.dialog_PT_log);
        group_inBody = findViewById(R.id.select_inBody);
        radio_weight = findViewById(R.id.select_weight);
        radio_fat = findViewById(R.id.select_fat);
        radio_muscle = findViewById(R.id.select_muscle);

        edit_profile.setVisibility(View.GONE);


        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            customer_image = bundle.getString("user_image");
            customer_name = bundle.getString("user_name");
            customer_id = bundle.getString("user_id");
            age = bundle.getString("age");
            stature = bundle.getString("stature");
            weight = bundle.getString("weight");

            view_user_tall.setText(stature);
            view_user_age.setText(age);
            view_user_weight.setText(weight);
            view_user_name.setText(customer_name);
            if (customer_image.equals("")){
                view_user_image.setImageResource(R.mipmap.default_user_image);
            }else{
                Picasso.get().load(customer_image).into(view_user_image);
            }
        }





        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setDrawLabels(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new Formatter_x());                             //x축 포맷 커스텀(날짜)
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);                          //x축 위치

        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);                                               //범례안보이게 하기

        lineChart.setVisibleXRangeMinimum(60 * 60 * 24 * 1000 * 5); //라인차트에서 최대로 보여질 X축의 데이터 설정
        lineChart.animateY(100);

        get_date_PT();


        group_inBody.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),Initial_setting_Activity.class);
                intent1.putExtra("id",customer_id);
                intent1.putExtra("key",900);
                intent1.putExtra("first",1);

                startActivityForResult(intent1,edit_request);
            }
        });

        btn_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, add_pt_log_Activity.class);
                intent.putExtra("customer_id",customer_id);
                startActivity(intent);
            }
        });

        message_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent message_Intent = new Intent(mContext, Chatting_room_activity.class);
                message_Intent.putExtra("user_id",customer_id);
                message_Intent.putExtra("user_name",customer_name);
                message_Intent.putExtra("user_image",customer_image);

                startActivity(message_Intent);
                finish();

            }
        });

        btn_PT_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User_detail_dialog user_detail_dialog = new User_detail_dialog(mContext,items);
                user_detail_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int result = user_detail_dialog.getResult();

                        if (result != 9999){
                            int index = Integer.parseInt(items.get(result).getIndex());
                            Intent intent = new Intent(mContext, PT_log_detail_Activity.class);
                            Log.d(TAG,index+":index");
                            intent.putExtra("index",index);
                            startActivity(intent);
                        }
                    }
                });
                user_detail_dialog.show();
            }
        });

    }

    //x축을 날짜로 변경하는 포맷터
    public class Formatter_x extends ValueFormatter {

        public String getFormattedValue(float value) {

            Date date = new Date((long) value);
            //날짜 월/일 로 바꾸기
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd",Locale.getDefault());
            return sdf.format(date);

        }
    }

    //초기 설정 - pt했던 날짜 가져오기
    private void get_date_PT(){

        values.clear();

        //jsonObject 형태로 만들어서 보냄
        String json_date = "{\"customer_id\"" + ":" + "\"" + customer_id + "\"" + ","
                + "\"request_key\"" + ":" + "\"" + "PT_log" + "\"" + "}";
        Log.d(TAG,json_date);
        values.put("data", json_date);

        new NetworkTask(url, values) .execute();


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
                    //채팅 저장 성공
                    String reason = jsonObject.getString("data");
                    Log.d("chat_save",reason);

                }else if(request_result.equals("PT_log_ok")) {
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
                    JSONArray jsonArray = jsonObject1.optJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {


                        JSONObject jsonChildNode = jsonArray.getJSONObject(i);
                        String trainer_id = jsonChildNode.getString("trainer_id");
                        String day = jsonChildNode.getString("day");
                        String index = jsonChildNode.getString("index_");
                        String weight = jsonChildNode.getString("weight");
                        String muscle_mass = jsonChildNode.getString("muscle_mass");
                        String body_fat_mass = jsonChildNode.getString("body_fat_mass");
                        Date date = simpleDateFormat.parse(day,new ParsePosition(0));
                        long entry_x = date.getTime();
                        entry_weight.add(new Entry(entry_x,Integer.parseInt(weight)));
                        entry_muscle_mass.add(new Entry(entry_x,Integer.parseInt(muscle_mass)));
                        entry_body_fat_mass.add(new Entry(entry_x,Integer.parseInt(body_fat_mass)));
                        items.add(new PT_log_item(index, day, "", trainer_id,""));
                        Log.d(TAG, day);
                        //등록된인바디가 없습니다 <- 지우기


                        view_empty.setVisibility(View.GONE);
                    }
                    LineDataSet dataSet = new LineDataSet(entry_weight,"체중");
                    LineData data = new LineData(dataSet);
                    lineChart.setData(data);
                    lineChart.invalidate();
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

    //라디오 그룹 클릭 리스너
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.select_weight){
                LineDataSet dataSet = new LineDataSet(entry_weight,"체중");
                LineData data = new LineData(dataSet);
                lineChart.setData(data);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }else if(i == R.id.select_muscle){
                LineDataSet dataSet = new LineDataSet(entry_muscle_mass,"골격근량");
                LineData data = new LineData(dataSet);
                lineChart.setData(data);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }else if(i == R.id.select_fat){
                LineDataSet dataSet = new LineDataSet(entry_body_fat_mass,"체지방량");
                LineData data = new LineData(dataSet);
                lineChart.setData(data);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {

            if (requestCode == edit_request && resultCode == RESULT_OK
                    && null != data) {

                Bundle bundle = data.getExtras() ;
                if (bundle !=null){
                    view_user_weight.setText(bundle.getString("weight"));
                    view_user_tall.setText(bundle.getString("stature"));
                    view_user_age.setText(bundle.getString("age"));
                }



//
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
