package com.example.trainer;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Setting_Activity extends Fragment {

     View view;
     Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_setting_,container,false);
        mContext=getActivity();

        Button btn_edit_profile = view.findViewById(R.id.btn_edit_profile);
        final String user_type = Sharedprefence.getString(mContext,"user_type");

        //회원프로필 설정으로 가기
        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user_type.equals("trainer")){
                    Intent intent = new Intent(getActivity(),Edit_profile_trainer_activity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(),Edit_profile_activity.class);
                    startActivity(intent);
                }

            }
        });

        return  view;
    }
}
