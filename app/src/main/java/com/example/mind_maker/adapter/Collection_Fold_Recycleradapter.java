package com.example.mind_maker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mind_maker.R;

import java.util.List;

public class Collection_Fold_Recycleradapter extends RecyclerView.Adapter<Collection_Fold_Recycleradapter.MyViewHolder> {

    private List<collection_file> mDatas;

    private Context mContext;

    private LayoutInflater inflater;



    public Collection_Fold_Recycleradapter(Context context, List<collection_file> datas){

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



        View view = inflater.inflate(R.layout.cardview_collection_fold,parent, false);

        MyViewHolder holder= new MyViewHolder(view);

        return holder;

    }



    @Override

    public void onBindViewHolder(MyViewHolder holder, final int position) {

        collection_file da=mDatas.get(position);
        /*holder.time.setText(da.getTime());
        holder.out_time.setText(da.getOut_Time());
        holder.back_time.setText(da.getBack_time());
        holder.reason.setText(da.getReason());
        holder.arrival.setText(da.getArrival());*/
    }



    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView time,out_time,back_time,reason,arrival;
        View v;

        public MyViewHolder(View view) {

            super(view);

            /*time=(TextView) view.findViewById(R.id.time);

            out_time=(TextView)view.findViewById(R.id.out_time);
            back_time=(TextView)view.findViewById(R.id.back_time);
            reason=(TextView)view.findViewById(R.id.reason);
            arrival=(TextView)view.findViewById(R.id.arrival);*/
            v=view;
        }



    }

}
