package com.example.trainer.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainer.PT_log_detail_Activity;
import com.example.trainer.R;
import com.example.trainer.User_detail_Activity;

import java.util.ArrayList;

public class User_detail_adapter extends RecyclerView.Adapter<User_detail_adapter.User_detail_Viewholder> implements On_PT_detail_click_listener {

    private On_PT_detail_click_listener listener;
    private ArrayList<PT_log_item> mItems;
     Context mContext;

    public User_detail_adapter(Context mContext, ArrayList itemList){
        this.mContext =mContext;// 객채화
        mItems = itemList;
    }

    @Nullable
    @Override
    public User_detail_adapter.User_detail_Viewholder onCreateViewHolder(@Nullable ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pt_log_item,parent,false);
        mContext = parent.getContext();

        return new User_detail_Viewholder(view);
    }

    @Override
    public  void  onBindViewHolder(final User_detail_Viewholder holder, final int position){
        holder.date.setText(mItems.get(position).date);
        holder.week.setText(mItems.get(position).week);
        holder.trainer.setText(mItems.get(position).start_time);//트레이너 입력
        holder.end_time.setVisibility(View.GONE);
        holder.PT_none.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onItemClick(holder,v,position);
                }
            }
        });
    }

    @Override
    public void onItemClick(User_detail_Viewholder holder, View view, int position){
        if(listener !=null){
            listener.onItemClick(holder,view,position);
        }

    }

    public void setOnItemClickListener(On_PT_detail_click_listener listener){
        this.listener = listener;

    }

    @Override
    public int getItemCount() {
        return  mItems.size();
    }
    @Override
    public int getItemViewType(int position){

        return position;
    }

    public class User_detail_Viewholder extends RecyclerView.ViewHolder{

        TextView date,week,trainer,end_time,PT_none;
        User_detail_Viewholder(View itemView) {
            super(itemView);
            PT_none = itemView.findViewById(R.id.PT_log_none);// "~" 이거 표시 삭제
            end_time = itemView.findViewById(R.id.PT_end_time);
            date = itemView.findViewById(R.id.PT_date);
            week = itemView.findViewById(R.id.PT_week);
            trainer =  itemView.findViewById(R.id.PT_start_time);//트레이너 입력

        }
    }
}
