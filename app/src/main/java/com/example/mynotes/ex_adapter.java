package com.example.mynotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ex_adapter extends RecyclerView.Adapter<ex_adapter.ex_Viewholder> {
    private ArrayList<ex_item> mex_list;
    MainActivity mainActivity;

    public ex_adapter(ArrayList<ex_item> mex_list, MainActivity mainActivity) {
        this.mex_list = mex_list;
        this.mainActivity=mainActivity;
    }

    private OnItemLongClickListener mlong_listener;
    public interface OnItemLongClickListener{
        void OnItemLongClick(int position);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener long_listener){
        mlong_listener=long_listener;
    }

    public static class ex_Viewholder extends RecyclerView.ViewHolder{
        public TextView mname;
        public TextView mdesc;
        public CheckBox mcheckBox;
        MainActivity mainActivity=this.mainActivity;

        public ex_Viewholder(@NonNull View itemView, final MainActivity mainActivity, final OnItemLongClickListener long_listener) {
            super(itemView);
            mname=itemView.findViewById(R.id.name);
            mdesc=itemView.findViewById(R.id.desc);
            mcheckBox=itemView.findViewById(R.id.checkbox);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    long_listener.OnItemLongClick(position);
                    return true;
                }
            });

            mcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mainActivity.MakeSelection(buttonView,getAdapterPosition());
                }
            });

        }

    }

    @NonNull
    @Override
    public ex_Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ex_item,parent,false);
        ex_Viewholder evh = new ex_Viewholder(view,mainActivity,mlong_listener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ex_Viewholder holder, int position) {
        ex_item current_Item = mex_list.get(position);
        holder.mname.setText(current_Item.getName());
        holder.mdesc.setText(current_Item.getDesc());
        if(!mainActivity.isActionModeenabled){
            holder.mcheckBox.setVisibility(View.GONE);
        }
        else{
            holder.mcheckBox.setVisibility(View.VISIBLE);
            holder.mcheckBox.setChecked(false);
            if(MainActivity.flag_selectall){
                holder.mcheckBox.setChecked(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mex_list.size();
    }
}

