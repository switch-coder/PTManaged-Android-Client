package com.example.trainer.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainer.Chatting_room_activity;
import com.example.trainer.R;
import com.example.trainer.User_detail_Activity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Chat_list_adapter extends RecyclerView.Adapter<Chat_list_Viewholder>{
    private ArrayList<Chat_list_item> mItems;
    private String my_Id ;
    Context mContext;
    public Chat_list_adapter(Context mContext,ArrayList itemList){
        this.mContext =mContext;// 객채화
        mItems = itemList;
    }

    @Nullable
    @Override
    public Chat_list_Viewholder onCreateViewHolder(@Nullable ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item,parent,false);
        mContext = parent.getContext();
        Chat_list_Viewholder holder = new Chat_list_Viewholder(v);
        return holder;
    }


    @Override
    public  void  onBindViewHolder(Chat_list_Viewholder holder, final int poition){
        if (mItems.get(poition).user_image.equals("")){
            holder.user_image.setImageResource(R.mipmap.default_user_image);
        }else{
            Picasso.get().load(mItems.get(poition).user_image).into(holder.user_image);
        }
        holder.user_id.setText(mItems.get(poition).user_id);
        holder.message.setText(mItems.get(poition).message);
        holder.time.setText(mItems.get(poition).time);
        holder.user_name.setText(mItems.get(poition).user_name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Chatting_room_activity.class);
                intent.putExtra("user_id",mItems.get(poition).user_id);
                intent.putExtra("user_name",mItems.get(poition).user_name);
                intent.putExtra("user_image",mItems.get(poition).user_image);
                //v.getContext() 넣어야 그 화면에서 넣어감
                mContext.startActivity(intent);

            }
        });
    }
    @Override
    public int getItemCount() {
        return  mItems.size();
    }

    @Override
    public int getItemViewType(int position){

        return position;
    }

}

/////////////////////////리사이클러뷰 뷰홀더///////////////////////////////////////
/////////////////////////리사이클러뷰 뷰홀더///////////////////////////////////////
/////////////////////////리사이클러뷰 뷰홀더///////////////////////////////////////
class Chat_list_Viewholder extends RecyclerView.ViewHolder{
    ImageView user_image;
    TextView user_name,user_id,time,message;
    LinearLayout list_item;
    public  Chat_list_Viewholder(View itemView) {
        super(itemView);
        user_image = (ImageView) itemView.findViewById(R.id.user_image);
        user_id = (TextView) itemView.findViewById(R.id.user_id);
        user_name = (TextView) itemView.findViewById(R.id.user_name);
        time = (TextView) itemView.findViewById(R.id.time);
        message = (TextView) itemView.findViewById(R.id.message);
        list_item = (LinearLayout) itemView.findViewById(R.id.linearLayout_list_item);
    }


}
