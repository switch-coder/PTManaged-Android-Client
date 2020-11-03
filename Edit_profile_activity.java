package com.example.trainer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.trainer.recyclerview.Profile_images_adapter;
import com.example.trainer.recyclerview.Profile_images_item;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Edit_profile_activity extends AppCompatActivity {

    Context mContext;
    RecyclerView profile_image_recyclerview;
    Profile_images_adapter images_adapter;
    LinearLayoutManager linear_manager_images;

    ArrayList<Profile_images_item> items= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_general);


        profile_image_recyclerview = findViewById(R.id.recyclerview_profile);
        linear_manager_images= new LinearLayoutManager(this);
        profile_image_recyclerview.setLayoutManager(linear_manager_images);
        //아이템 넣기
        images_adapter = new Profile_images_adapter(mContext,items);
    }


    public void postDBimageNdata(String id, String workout_exp, String uniqueness, String job, ArrayList<String> imageArray) {
        class addAskHelpDB extends AsyncTask<Void, Void, Void> {
            String id, workout_exp, uniqueness, job;
            ArrayList<String> imageArray = new ArrayList<String>();


            public addAskHelpDB(String id, String workout_exp, String uniqueness, String job, ArrayList<String> imageArray) {

                this.id = id;
                this.workout_exp = workout_exp;
                this.uniqueness = uniqueness;
                this.job = job;
                this.imageArray = imageArray;  // 이미지경로들이 들어있는 곳입니다.

            }


            @Override
            protected Void doInBackground(Void... voids) {
                try {

                    HttpURLConnection conn = null;
                    DataOutputStream dos = null;
                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";
                    int bytesRead, bytesAvailable, bufferSize;
                    byte[] buffer;
                    int maxBufferSize = 1 * 1024 * 1024;


                    URL url = new URL("http://13.59.136.241/");


                    // Open a HTTP  connection to  the URL

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());


                    // 텍스트 데이터들
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"loginMode\"\r\n\r\n" + id);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"loginMode\"\r\n\r\n" + workout_exp);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"meettingLocation\"\r\n\r\n" + uniqueness);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"content\"\r\n\r\n" + job);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");


                    // PHP 에서 반복문을 사용하기 위하여 이미지 갯수를 센다.
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"imageCount\"\r\n\r\n" + imageArray.size());
                    wr.writeBytes("\r\n--" + boundary + "\r\n");


                    // 파일의 존재 유무 확인 후 ( 파일이 없는 경우  그냥 지나간다 )
                    // 반복문으로 파일을 보낸다.
                    if (imageArray.size() > 0) {

                        for (int i = 0; i < imageArray.size(); i++) {
                            String a = String.valueOf(i);

                            File sourceFile = new File(imageArray.get(i));
                            FileInputStream fileInputStream = new FileInputStream(sourceFile);


                            //php단에서 $_FILES['uploaded_file'] 에  아래의  filename=""+ imageArray.get(i) 이들어간다
                            // 여러개를 보낼때 주의 사항은  $_FILES['uploaded_file']의  'uploaded_file' 는 키값처럼들어가는데
                            // 중복되는 경우 마지막 데이터만 전송됨으로  아래에서는 반복문의 i 값을 string으로 변환하여 구분을 주었다.
                            // php 단에서도 구분지어서 받아야 한다.
                            dos = new DataOutputStream(conn.getOutputStream());
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file" + a + "\";filename=\"" + imageArray.get(i) + "\"" + lineEnd);
                            dos.writeBytes(lineEnd);


                            // create a buffer of  maximum size
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            buffer = new byte[bufferSize];

                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);


                            while (bytesRead > 0) {
                                dos.write(buffer, 0, bufferSize);
                                bytesAvailable = fileInputStream.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            }

                            dos.writeBytes(lineEnd);
                            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                        }
                    }


                    //--------------------------
                    //   서버에서 전송받기
                    //--------------------------
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;

                    while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                        builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
                    }

                    String myResult = builder.toString();                       // 전송결과를 전역 변수에 저장

                    Log.d("bbbbbbbbbbbbbbbb", "aaaaaaaaaaaa/////" + myResult);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

        }
        addAskHelpDB gotoDBUerId = new addAskHelpDB(id, workout_exp, uniqueness, job, imageArray);
        gotoDBUerId.execute();

    }
}
