package com.example.trainer;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.trainer.recyclerview.Profile_images_adapter;
import com.example.trainer.recyclerview.Profile_images_item;
import com.example.trainer.recyclerview.Schedule_item;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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
import java.util.List;
import java.util.Map;

import static com.example.trainer.Public.url_address;

public class Edit_profile_trainer_activity extends AppCompatActivity {


        Context mContext;
        RecyclerView profile_image_recyclerview;
        Profile_images_adapter images_adapter;
        LinearLayoutManager layout_manager_images;
        Bitmap bitmap;
        String url_upload = "Edit_profile_update.php";
        String user_id,user_image;
        int PICK_IMAGE_MULTIPLE =1;
        List<String> imagesEncodedList;
        ArrayList<Profile_images_item> items= new ArrayList();
        ImageButton btn_profile_change;
        EditText introduce,input_recode,input_place;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_profile_trainer);
            mContext =this;

            btn_profile_change = findViewById(R.id.btn_image_change);
             introduce =  findViewById(R.id.input_introduce);
           input_recode = findViewById(R.id.input_recode);
             input_place = findViewById(R.id.place);
            EditText input_sns = findViewById(R.id.sns);
            ImageButton btn_clear = findViewById(R.id.btn_clear);
            Button btn_complete = findViewById(R.id.setting_complete);

            items.add(new Profile_images_item(url_address+"user_images/1551555524_1592504195.jpeg"));
            profile_image_recyclerview = findViewById(R.id.recyclerview_profile);
            layout_manager_images= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            profile_image_recyclerview.setLayoutManager(layout_manager_images);
            profile_image_recyclerview.setItemAnimator(new DefaultItemAnimator());



            //아이템 넣기
            images_adapter = new Profile_images_adapter(mContext,items);
            profile_image_recyclerview.setAdapter(images_adapter);

            user_id= Sharedprefence.getString(mContext,"user_id");

            ContentValues values = new ContentValues();

            values.put("request_key", "setting");
            values.put("user_id", user_id);
            NetworkTask networkTask = new NetworkTask(url_upload, values);
            networkTask.execute();

            btn_profile_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(
                            Edit_profile_trainer_activity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PICK_IMAGE_MULTIPLE
                    );
                }
            });


            //취소버튼 엑스 버튼
            btn_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            //완료버튼
            btn_complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url_address+url_upload, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //전송후 서버에서 받온 데이터
                                JSONObject jsonObject = new JSONObject(response);
                                String request_result =  jsonObject.getString("result");
                                Toast.makeText(getApplicationContext(),request_result,Toast.LENGTH_SHORT).show();
                                Log.d("Edit", jsonObject.getString("message"));
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
                            String image_data = imageToString(bitmap);
                            params.put("image",image_data);
                            params.put("input_introduce",introduce.getText().toString());
                            params.put("input_recode",input_recode.getText().toString());
                            params.put("input_place",input_place.getText().toString());
                            params.put("user_id", user_id);
                            params.put("request_key","image_upload");

                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(Edit_profile_trainer_activity.this);
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
                        btn_profile_change.setImageBitmap(bitmap);

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


                if (request_result.equals("ok")){
                    //스케쥴 업로드 했을때
                    String setting_user_image = jsonObject.getString("user_image");
                    String setting_introduce = jsonObject.getString("introduce");
                    String setting_place = jsonObject.getString("place");
                    String setting_recode = jsonObject.getString("recode");
                    introduce.setText(setting_introduce);
                    input_place.setText(setting_place);
                    input_recode.setText(setting_recode);
                    introduce.setText(setting_introduce);
                    Sharedprefence.setString(mContext,"user_image",url_address+setting_user_image);
                    Picasso.get().load(url_address+setting_user_image).into(btn_profile_change);


                    //서버에서 다시 해당날짜의 스케쥴 가져오기

                }

            }catch (JSONException e){
                e.printStackTrace();
            }
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.


        }
    }

}
