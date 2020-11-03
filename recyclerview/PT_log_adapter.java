package com.example.trainer.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainer.R;

import java.util.ArrayList;

public class PT_log_adapter extends RecyclerView.Adapter<PT_log_adapter.Pt_log_Viewholder> implements On_PT_Item_click_listener{

    On_PT_Item_click_listener listener;

    private ArrayList<PT_log_item> mItems;
            Context mContext;

    public PT_log_adapter(Context mContext, ArrayList itemList){
            this.mContext =mContext;// 객채화
            mItems = itemList;
    }

    @Nullable
    @Override
    public PT_log_adapter.Pt_log_Viewholder onCreateViewHolder(@Nullable ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pt_log_item,parent,false);
            mContext = parent.getContext();

            return new Pt_log_Viewholder(view);
    }


    @Override
    public  void  onBindViewHolder(final Pt_log_Viewholder holder, final int position){
        holder.date.setText(mItems.get(position).date);
        holder.week.setText(mItems.get(position).week);
        holder.start_time.setText(mItems.get(position).start_time);
        holder.end_time.setText(mItems.get(position).end_time);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onItemClick(holder,v,position);
                }
            }
        });


    }


    public void setOnItemClickListener(On_PT_Item_click_listener listener){
      this.listener = listener;

    }

    @Override
    public void onItemClick(Pt_log_Viewholder holder, View view, int position){
        if(listener !=null){
            listener.onItemClick(holder,view,position);
        }

    }

    @Override
    public int getItemCount() {
            return  mItems.size();
    }
    @Override
    public int getItemViewType(int position){

            return position;
    }

    public class Pt_log_Viewholder extends RecyclerView.ViewHolder{

        TextView date,week,start_time,end_time;
        Pt_log_Viewholder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.PT_date);
            week = itemView.findViewById(R.id.PT_week);
            start_time =  itemView.findViewById(R.id.PT_start_time);
            end_time = itemView.findViewById(R.id.PT_end_time);
        }
    }
}
