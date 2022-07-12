package com.example.mind_maker.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.TextView;


import com.example.mind_maker.R;

import java.util.List;



public class Recommend_Recycleradapter extends RecyclerView.Adapter<Recommend_Recycleradapter.MyViewHolder> {

    private List<blog> mDatas;

    private Context mContext;

    private LayoutInflater inflater;



    public Recommend_Recycleradapter(Context context, List<blog> datas){

        this. mContext=context;

        this. mDatas=datas;

        inflater=LayoutInflater. from(mContext);

    }



    @Override

    public int getItemCount() {

        return mDatas.size();

    }

    @Override

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        View view = inflater.inflate(R.layout.cardview_recommend,parent, false);

        MyViewHolder holder= new MyViewHolder(view);

        return holder;

    }



    @Override

    public void onBindViewHolder(MyViewHolder holder, final int position) {

        blog da=mDatas.get(position);
        holder.user_name.setText(da.name);
        holder.content.setText(da.content);
        /*holder.time.setText(da.getTime());
        holder.out_time.setText(da.getOut_Time());
        holder.back_time.setText(da.getBack_time());
        holder.reason.setText(da.getReason());
        holder.arrival.setText(da.getArrival());*/
    }



    class MyViewHolder extends RecyclerView.ViewHolder {

        //TextView time,out_time,back_time,reason,arrival;
        TextView user_name,content;
        View v;

        public MyViewHolder(View view) {

            super(view);
            user_name=view.findViewById(R.id.user_name);
            content=view.findViewById(R.id.content);
            /*time=(TextView) view.findViewById(R.id.time);

            out_time=(TextView)view.findViewById(R.id.out_time);
            back_time=(TextView)view.findViewById(R.id.back_time);
            reason=(TextView)view.findViewById(R.id.reason);
            arrival=(TextView)view.findViewById(R.id.arrival);*/
            v=view;
        }



    }

}


