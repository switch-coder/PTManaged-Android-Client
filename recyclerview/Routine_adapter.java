package com.example.trainer.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainer.R;

import java.util.ArrayList;

public class Routine_adapter extends RecyclerView.Adapter<Routine_adapter.Routine_Viewholder>{
    private ArrayList<Routine_item> mItems;
    Context mContext;
    String page ;

    public Routine_adapter(Context mContext, ArrayList itemList,String page){
            this.mContext =mContext;// 객채화
            mItems = itemList;
            this.page = page;
            }

    @Nullable
    @Override
    public Routine_adapter.Routine_Viewholder onCreateViewHolder(@Nullable ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.routine_item,parent,false);
        mContext = parent.getContext();

        return new Routine_Viewholder(view);
    }


    @Override
    public  void  onBindViewHolder(final Routine_Viewholder holder, final int position){
        holder.exercise_name.setText(mItems.get(position).exercise_name);
        holder.training_set.setText(mItems.get(position).set_number);
        holder.training_count.setText(mItems.get(position).repetition);

        holder.delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItems.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.exercise_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mItems.get(position).setExercise_name(s.toString());
            }
        });
        holder.training_set.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mItems.get(position).setSet_number(s.toString());
            }
        });
        holder.training_count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mItems.get(position).setRepetition(s.toString());
            }
        });
        if (page.equals("PT_detail")){
            holder.training_count.setBackgroundColor(Color.WHITE);
            holder.exercise_name.setBackgroundColor(Color.WHITE);
            holder.training_set.setBackgroundColor(Color.WHITE);
            holder.training_set.setTextColor(Color.BLACK);
            holder.exercise_name.setTextColor(Color.BLACK);
            holder.training_count.setTextColor(Color.BLACK);
            holder.training_set.setEnabled(false);
            holder.exercise_name.setEnabled(false);
            holder.training_count.setEnabled(false);
            holder.delete_item.setVisibility(View.GONE);
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

    public class Routine_Viewholder extends RecyclerView.ViewHolder{
        EditText exercise_name,training_count,training_set;
        ImageButton delete_item;
        Routine_Viewholder(View itemView) {
            super(itemView);
            exercise_name = itemView.findViewById(R.id.exercise_name);
            training_count = itemView.findViewById(R.id.training_count);
            training_set =  itemView.findViewById(R.id.training_set);
            delete_item = itemView.findViewById(R.id.delete_item);
        }
    }

}
