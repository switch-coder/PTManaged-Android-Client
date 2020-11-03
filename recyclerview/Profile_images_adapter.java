package com.example.trainer.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.example.trainer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Profile_images_adapter extends RecyclerView.Adapter<Images_Viewholder> {

    private ArrayList<Profile_images_item> mItems;

    Context mContext;
    public Profile_images_adapter(Context mContext,ArrayList itemList){
        this.mContext =mContext;// 객채화
        mItems = itemList;
    }

    @Nullable
    @Override
    public Images_Viewholder onCreateViewHolder(@Nullable ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_edit_profile_image,parent,false);
        mContext = parent.getContext();
        Images_Viewholder holder = new Images_Viewholder(v);
        return holder;
    }

    @Override
    public  void  onBindViewHolder( Images_Viewholder holder, final  int poition){
        Picasso.get().load(mItems.get(poition).image).into(holder.ImageView_item);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("adater","click store");
//
//            }
//        });
    }
    @Override
    public int getItemCount() {
        return  mItems.size();
    }

}


/////////////////////////리사이클러뷰 뷰홀더///////////////////////////////////////
/////////////////////////리사이클러뷰 뷰홀더///////////////////////////////////////
/////////////////////////리사이클러뷰 뷰홀더///////////////////////////////////////
class Images_Viewholder extends RecyclerView.ViewHolder{
    public ImageView ImageView_item;
    public  Images_Viewholder(View itemView) {
        super(itemView);
        ImageView_item = (ImageView) itemView.findViewById(R.id.recyclerview_images);

    }

}
