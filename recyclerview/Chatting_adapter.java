package com.example.trainer.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainer.Chatting_room_activity;
import com.example.trainer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Chatting_adapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<Chat_list_item> mItems;
    private String my_Id ;
    Context mContext;
    public Chatting_adapter(Context mContext,ArrayList itemList,String my_Id){
        this.mContext =mContext;// 객채화
        this.my_Id =my_Id;
        mItems = itemList;
    }

    @Nullable
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@Nullable ViewGroup parent, int viewType){
        View view;
        switch (viewType) {
            case 0:
                Log.d("FFFF", "온크리트뷰홀더 :" + viewType);
                Log.d("FFFF", "온크리트뷰홀더 : 0인 경우");
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_my_item, parent, false);
                return new Chat_Viewholder(view);
            case 1:
                Log.d("FFFF", "온크리트뷰홀더 :" + viewType);
                Log.d("FFFF", "온크리트뷰홀더 : 1인 경우");
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_others_item, parent, false);
                return  new Chat_Viewholder_other(view);
        }

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_others_item,parent,false);
        mContext = parent.getContext();
        RecyclerView.ViewHolder holder = new Chat_Viewholder(view);
        return holder;
    }


    @Override
    public  void  onBindViewHolder(RecyclerView.ViewHolder holder, final int poition){
        final Chat_list_item model = mItems.get(poition);
            String time =mItems.get(poition).time.substring(10,16);
        if (model.user_name.equals(my_Id)){
            Chat_Viewholder holder1 = (Chat_Viewholder) holder;
            holder1.message.setText(mItems.get(poition).message);
            holder1.time.setText(time);
        }else{
            Chat_Viewholder_other other_holder = (Chat_Viewholder_other) holder;
            if (mItems.get(poition).user_image.equals("")){

                other_holder.user_image.setImageResource(R.mipmap.default_user_image);
            }else{
                Picasso.get().load(mItems.get(poition).user_image).into(other_holder.user_image);
            }
            other_holder.message.setText(mItems.get(poition).message);
            other_holder.time.setText(time);
            other_holder.user_name.setText(mItems.get(poition).user_name);
        }



    }
    @Override
    public int getItemCount() {
        return  mItems.size();
    }

    @Override
    public int getItemViewType(int position){
        Chat_list_item item = mItems.get(position);
        if (item.user_name.equals(my_Id)){
            return 0;
        }else {
            return 1;
        }
    }

    public class Chat_Viewholder extends RecyclerView.ViewHolder{
        TextView user_name,time,message;
        public  Chat_Viewholder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.chat_my_message_time);
            message = (TextView) itemView.findViewById(R.id.chat_my_message);
        }
    }

    public class Chat_Viewholder_other extends RecyclerView.ViewHolder{
        ImageView user_image;
        TextView user_name,user_id,time,message;
        public  Chat_Viewholder_other(View itemView) {
            super(itemView);
            user_image = (ImageView) itemView.findViewById(R.id.others_image);
            user_name = (TextView) itemView.findViewById(R.id.chat_others_name);
            time = (TextView) itemView.findViewById(R.id.chat_others_message_time);
            message = (TextView) itemView.findViewById(R.id.chat_others_message);
        }
    }
}

/////////////////////////리사이클러뷰 뷰홀더///////////////////////////////////////
/////////////////////////리사이클러뷰 뷰홀더///////////////////////////////////////
/////////////////////////리사이클러뷰 뷰홀더///////////////////////////////////////
