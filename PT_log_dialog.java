package com.example.trainer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainer.recyclerview.On_PT_Item_click_listener;
import com.example.trainer.recyclerview.PT_log_adapter;
import com.example.trainer.recyclerview.PT_log_item;
import com.example.trainer.recyclerview.Routine_adapter;

import java.util.ArrayList;

public class PT_log_dialog extends Dialog {

    RecyclerView recyclerView_PT_log;
    PT_log_adapter pt_log_adapter;
    LinearLayoutManager layout_manager_PT_log;
    ArrayList<PT_log_item> items;
    Context context;
    int result = 99999;
    ImageButton btn_dismiss;


    public PT_log_dialog(Context context,ArrayList<PT_log_item> items ){
        super(context);
        this.context = context;
        this.items = items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pt_log);
        btn_dismiss = findViewById(R.id.btb_dismiss);

        recyclerView_PT_log = findViewById(R.id.recyclerView_PT_log);
        layout_manager_PT_log= new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView_PT_log.setLayoutManager(layout_manager_PT_log);
        recyclerView_PT_log.setItemAnimator(new DefaultItemAnimator());
        pt_log_adapter = new PT_log_adapter(context,items);
        recyclerView_PT_log.setAdapter(pt_log_adapter);


        btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        pt_log_adapter.setOnItemClickListener(new On_PT_Item_click_listener() {
            @Override
            public void onItemClick(PT_log_adapter.Pt_log_Viewholder holder, View view, int position) {
                result = position;
                dismiss();
            }
        });



    }


    public int getResult() {return result;}
    public void setResult(int result){this.result= result;}
}
