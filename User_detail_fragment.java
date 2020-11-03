package com.example.trainer;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.example.trainer.Public.url_address;
import static org.webrtc.ContextUtils.getApplicationContext;

public class User_detail_fragment extends Fragment {

    ImageView view_user_image;
    ImageButton message_room, btn_add_post;
    TextView view_user_age, view_user_name, view_user_tall, view_user_weight, view_empty;
    LinearLayout only_trainer;
    RadioGroup group_inBody;
    RadioButton radio_weight, radio_fat, radio_muscle;
    Button edit_profile, btn_PT_log;
    LineChart lineChart;
    ContentValues values = new ContentValues();

    String customer_id, customer_name, customer_image;
    ArrayList<PT_log_item> items = new ArrayList<>();
    ArrayList<Entry> entry_weight = new ArrayList<>();
    ArrayList<Entry> entry_muscle_mass = new ArrayList<>();
    ArrayList<Entry> entry_body_fat_mass = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd");
    int post_upload = 9900;
    String TAG = "user_detail_activity";
    String url = "PT_log.php";
    private int edit_request = 900;

    Context mContext;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_user_detail, container, false);
        mContext = getActivity();

        view_user_image = view.findViewById(R.id.user_image);
        message_room = view.findViewById(R.id.message_room);
        btn_add_post = view.findViewById(R.id.add_post);
        view_user_age = view.findViewById(R.id.user_age);
        view_user_name = view.findViewById(R.id.name);
        view_user_tall = view.findViewById(R.id.user_tall);
        view_user_weight = view.findViewById(R.id.user_weight);
        view_empty = view.findViewById(R.id.empty);
        edit_profile = view.findViewById(R.id.btn_edit_profile);
        lineChart = view.findViewById(R.id.line_chart);
        btn_PT_log = view.findViewById(R.id.dialog_PT_log);
        group_inBody = view.findViewById(R.id.select_inBody);
        radio_weight = view.findViewById(R.id.select_weight);
        radio_fat = view.findViewById(R.id.select_fat);
        radio_muscle = view.findViewById(R.id.select_muscle);
        only_trainer = view.findViewById(R.id.linearLayout_only_trainer);

        only_trainer.setVisibility(View.GONE);



            customer_image = Sharedprefence.getString(mContext,"user_image");
            customer_name = Sharedprefence.getString(mContext,"user_name");
            customer_id = Sharedprefence.getString(mContext,"user_id");
            view_user_name.setText(customer_name);
            Picasso.get().load(customer_image).into(view_user_image);




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





        group_inBody.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(mContext,Initial_setting_Activity.class);
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
                intent.putExtra("customer_id", customer_id);
                startActivity(intent);
            }
        });



        btn_PT_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User_detail_dialog user_detail_dialog = new User_detail_dialog(mContext, items);
                user_detail_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        int result = user_detail_dialog.getResult();

                        if (result != 9999) {
                            int index = Integer.parseInt(items.get(result).getIndex());
                            Intent intent = new Intent(mContext, PT_log_detail_Activity.class);
                            Log.d(TAG, index + ":index");
                            intent.putExtra("index", index);
                            startActivity(intent);
                        }
                    }
                });
                user_detail_dialog.show();
            }
        });

        return view;
    }

    //x축을 날짜로 변경하는 포맷터
    public class Formatter_x extends ValueFormatter {

        public String getFormattedValue(float value) {

            Date date = new Date((long) value);
            //날짜 월/일 로 바꾸기
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());
            return sdf.format(date);

        }
    }

    //초기 설정 - pt했던 날짜 가져오기
    private void get_date_PT() {

        values.clear();

        //jsonObject 형태로 만들어서 보냄
        String json_date = "{\"customer_id\"" + ":" + "\"" + customer_id + "\"" + ","
                + "\"request_key\"" + ":" + "\"" + "PT_log" + "\"" + "}";
        Log.d(TAG, json_date);
        values.put("data", json_date);

        new NetworkTask(url, values).execute();


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
            try {
                Log.d(TAG, s);
                //jsonobject로 서버에서 받아 왔기때문에 파싱해준다
                JSONObject jsonObject = new JSONObject(s);
                String request_result = jsonObject.getString("result");


                if (request_result.equals("PT_log_fail")) {
                    //채팅 저장 성공
                    String reason = jsonObject.getString("data");
                    Log.d("chat_save", reason);

                } else if (request_result.equals("PT_log_ok")) {
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
                        Date date = simpleDateFormat.parse(day, new ParsePosition(0));
                        long entry_x = date.getTime();
                        entry_weight.add(new Entry(entry_x, Integer.parseInt(weight)));
                        entry_muscle_mass.add(new Entry(entry_x, Integer.parseInt(muscle_mass)));
                        entry_body_fat_mass.add(new Entry(entry_x, Integer.parseInt(body_fat_mass)));
                        items.add(new PT_log_item(index, day, "", trainer_id, ""));
                        Log.d(TAG, day);
                        //등록된인바디가 없습니다 <- 지우기


                        view_empty.setVisibility(View.GONE);
                    }
                    LineDataSet dataSet = new LineDataSet(entry_weight, "체중");
                    LineData data = new LineData(dataSet);
                    lineChart.setData(data);
                    lineChart.invalidate();
                }
                else if(request_result.equals("user_detail_ok")) {
                    String workout_exp = jsonObject.getString("workout_exp");
                    String uniqueness = jsonObject.getString("uniqueness");
                    String job = jsonObject.getString("job");
                    String age = jsonObject.getString("age");
                    String stature = jsonObject.getString("stature");
                    String weight = jsonObject.getString("weight");
                    String sex = jsonObject.getString("sex");
                    view_user_tall.setText(stature);
                    view_user_age.setText(age);
                    view_user_weight.setText(weight);
                    get_date_PT();
                }else {
                        String data = jsonObject.getString("data");
                        Log.d(TAG, data);//오류 로그
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
            }
                //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.


        }
    }

    private void get_user_detail(String user_id){

        values.clear();

        //jsonObject 형태로 만들어서 보냄
        String json_date = "{\"customer_id\"" + ":" + "\"" + user_id + "\"" + ","
                + "\"request_key\"" + ":" + "\"" + "user_detail" + "\"" + "}";
        Log.d(TAG,json_date);
        values.put("data", json_date);

        new NetworkTask("Find_user.php", values) .execute();


    }


    //라디오 그룹 클릭 리스너
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if (i == R.id.select_weight) {
                LineDataSet dataSet = new LineDataSet(entry_weight, "체중");
                LineData data = new LineData(dataSet);
                lineChart.setData(data);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            } else if (i == R.id.select_muscle) {
                LineDataSet dataSet = new LineDataSet(entry_muscle_mass, "골격근량");
                LineData data = new LineData(dataSet);
                lineChart.setData(data);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            } else if (i == R.id.select_fat) {
                LineDataSet dataSet = new LineDataSet(entry_body_fat_mass, "체지방량");
                LineData data = new LineData(dataSet);
                lineChart.setData(data);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }
        }
    };





    @Override
    public void onResume() {
        super.onResume();
        get_user_detail(customer_id);

    }
}