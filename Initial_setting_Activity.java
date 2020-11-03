package com.example.trainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.trainer.Public.url_address;

public class Initial_setting_Activity extends AppCompatActivity {

    Button btn_complete;
    ImageButton btn_image_change;
    EditText input_workout_exp,input_uniqueness,input_weight,input_stature,input_age;
    RadioGroup radio_sex;
    String url_upload = "Edit_profile_update.php";
    String user_id,user_type,job;
    Bitmap bitmap;
    int PICK_IMAGE_MULTIPLE =1;
    int first;
    int EDIT_PROFILE =0;
    String TAG ="initial_settingActivity";
    Spinner job_spinner;
    ContentValues values = new ContentValues();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setting_);


        //회원가입후 아이디 데이터 가져옴
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            user_id = bundle.getString("id");
            Log.d(TAG,user_id);
            EDIT_PROFILE =bundle.getInt("key",0);
            first = bundle.getInt("first",0);
        }

        job_spinner = findViewById(R.id.input_job);
        input_workout_exp = findViewById(R.id.workout_exp);
        input_uniqueness = findViewById(R.id.uniqueness);
        input_weight = findViewById(R.id.weight);
        input_stature = findViewById(R.id.stature);
        input_age = findViewById(R.id.age);
        btn_complete = findViewById(R.id.setting_complete);
        btn_image_change = findViewById(R.id.btn_image_change);
        radio_sex = findViewById(R.id.sex);
        Log.d(TAG,first+"");

        if (first ==1){
            values.clear();
            String json_date = "{\"customer_id\"" + ":" + "\"" + user_id + "\"" + ","
                    + "\"request_key\"" + ":" + "\"" + "user_detail" + "\"" + "}";
            values.put("data", json_date);Log.d(TAG,json_date);
           new NetworkTask("Find_user.php", values).execute();
        }



        btn_image_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        Initial_setting_Activity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PICK_IMAGE_MULTIPLE
                );
            }
        });

        //날짜 선택 타입 선택
        job_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getSelectedItemId();
                job = parent.getItemIdAtPosition(position)+"";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                job = "기타";
            }
        });

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = radio_sex.getCheckedRadioButtonId();
                //getCheckedRadioButtonId() 의 리턴값은 선택된 RadioButton 의 id 값.
                RadioButton select_sex = (RadioButton) findViewById(id);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url_address+url_upload, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //전송후 서버에서 받온 데이터
                            JSONObject jsonObject = new JSONObject(response);
                            String request_result =  jsonObject.getString("result");
                            Toast.makeText(getApplicationContext(),request_result,Toast.LENGTH_SHORT).show();
                            Log.d("Edit", jsonObject.getString("message"));

                            if (request_result.equals("ok")){
                                Toast.makeText(getApplicationContext(),"업로드 되었습니다",Toast.LENGTH_SHORT).show();
                                Log.d("Edit", "result_ok and finish");

                                finish();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "error: "+error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        //서버 전송할때 보내는값
                        Map<String, String> params = new HashMap<>();
                        //이미지를 스트링으로 변환후 서버에서 다시 변환
                        if (bitmap != null){
                            String image_data = imageToString(bitmap);
                            params.put("image",image_data);
                        }else{
                            params.put("image","");
                        }
                        params.put("user_id",user_id);
                        params.put("weight",input_weight.getText().toString());
                        params.put("age",input_age.getText().toString());
                        params.put("stature",input_stature.getText().toString());
                        params.put("sex",select_sex.getText().toString());
                        params.put("workout_exp",input_workout_exp.getText().toString());
                        params.put("uniqueness", input_uniqueness.getText().toString());
                        params.put("job", job_spinner.getSelectedItem().toString());
                        params.put("request_key","customer_initial_setting");
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(Initial_setting_Activity.this);
                requestQueue.add(stringRequest);

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == PICK_IMAGE_MULTIPLE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"사진을 선택해주세요"),PICK_IMAGE_MULTIPLE);
            }else{
                Toast.makeText(getApplicationContext(),"사진권한설정을 수정해 주세요",Toast.LENGTH_SHORT);

            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {

                Uri file_path = data.getData() ;

                try {
                    InputStream inputStream = getContentResolver().openInputStream(file_path);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    btn_image_change.setImageBitmap(bitmap);

                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }

//
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String imageToString (Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodeImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return  encodeImage;
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
                JSONObject jsonObject = new JSONObject(s);
                String request_result =  jsonObject.getString("result");


                if (request_result.equals("user_detail_ok")){
                    //스케쥴 업로드 했을때
                    String workout_exp = jsonObject.getString("workout_exp");
                    String uniqueness = jsonObject.getString("uniqueness");
                    String job = jsonObject.getString("job");
                    String age = jsonObject.getString("age");
                    String stature = jsonObject.getString("stature");
                    String weight = jsonObject.getString("weight");
                    String sex = jsonObject.getString("sex");

                    input_workout_exp.setText(workout_exp);
                    input_uniqueness.setText(uniqueness);
                    input_age.setText(age);
                    input_stature.setText(stature);
                    input_weight.setText(weight);
                    Log.d(TAG,sex);
                    if (sex.equals("남성")){
                        RadioButton radioButton = findViewById(R.id.male);
                        radioButton.setChecked(true);
                    }else{
                        RadioButton radioButton = findViewById(R.id.female);
                        radioButton.setChecked(true);
                    }
                    switch (job){
                        case "사무직":
                            job_spinner.setSelection(0);
                            break;
                        case "운송직":
                            job_spinner.setSelection(1);
                            break;
                        case "현장직":
                            job_spinner.setSelection(2);
                            break;
                        case "운동선수":
                            job_spinner.setSelection(3);
                            break;
                        case "학생":
                            job_spinner.setSelection(4);
                            break;
                        case "기타":
                            job_spinner.setSelection(5);
                            break;
                            default:
                                job_spinner.setSelection(5);
                                break;

                    }
//
//                    Sharedprefence.setString(getApplicationContext(),"user_image","http://13.59.136.241"+user_image);
//                    Picasso.get().load("http://13.59.136.241"+user_image).into(btn_image_change);


                    //서버에서 다시 해당날짜의 스케쥴 가져오기

                }

            }catch (JSONException e){
                e.printStackTrace();
            }
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.


        }
    }
}
