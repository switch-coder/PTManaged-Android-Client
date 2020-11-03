package com.example.trainer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainer.recyclerview.On_PT_Item_click_listener;
import com.example.trainer.recyclerview.On_PT_detail_click_listener;
import com.example.trainer.recyclerview.PT_log_adapter;
import com.example.trainer.recyclerview.PT_log_item;
import com.example.trainer.recyclerview.User_detail_adapter;
import com.example.trainer.recyclerview.User_list_adapter;

import java.util.ArrayList;

public class User_detail_dialog extends Dialog {

     RecyclerView recyclerview_PT_log;
     User_detail_adapter user_detail_adater;
     LinearLayoutManager layout_manager_detail;
    private ArrayList<PT_log_item> items;
    int result = 9999;
     ImageButton btn_dismiss;

    private Context context;

    public User_detail_dialog(Context context, ArrayList<PT_log_item> items) {
        super(context);
        this.context = context;
        this.items = items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pt_log);
        btn_dismiss = findViewById(R.id.btb_dismiss);

        recyclerview_PT_log = findViewById(R.id.recyclerView_PT_log);
        layout_manager_detail = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerview_PT_log.setLayoutManager(layout_manager_detail);
        recyclerview_PT_log.setItemAnimator(new DefaultItemAnimator());
        user_detail_adater = new User_detail_adapter(context, items);
        recyclerview_PT_log.setAdapter(user_detail_adater);


        btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        user_detail_adater.setOnItemClickListener(new On_PT_detail_click_listener() {
            @Override
            public void onItemClick(User_detail_adapter.User_detail_Viewholder holder, View view, int position) {
                result = position;
                dismiss();
            }
        });


    }
    public int getResult() {return result;}
    public void setResult(int result){this.result= result;}
}