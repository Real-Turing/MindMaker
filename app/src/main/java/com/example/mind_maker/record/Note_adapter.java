package com.example.mind_maker.record;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mind_maker.Dao.NoteDB;
import com.example.mind_maker.EditActivity;
import com.example.mind_maker.PassageActivity;
import com.example.mind_maker.R;
import com.example.mind_maker.RecordActivity;
import com.example.mind_maker.StartActivity;
import com.example.mind_maker.adapter.TypeRecyclerAdapter1;

import java.util.List;

public class Note_adapter extends RecyclerView.Adapter<Note_adapter.ViewHolder>{

    private List<Note> mnotelist;
    private Context mContext;

    private LayoutInflater inflater;
    public Note_adapter.OnItemClickListener onItemClickListener;
    public interface OnItemClickListener//长按接口
    {
        public void OnItemClick(View v,int position);
    }

    public void setOnItemClickListener(Note_adapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    //private OnRecyclerViewItemLongClickListener mOnItemLongClickListener;
    //private AdapterView.OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    public interface OnItemLongClickListener//长按接口
    {
        public void OnItemLongClick(View v,int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, data;
        LinearLayout layout;
        ImageView style,top;
        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.note_name);
            data = view.findViewById(R.id.note_data);
            layout=view.findViewById(R.id.layout);
            style=view.findViewById(R.id.style);//
            top=view.findViewById(R.id.top);
        }
    }

    public Note_adapter(Context context, List<Note> list){
        mnotelist=list;
        mContext=context;
        inflater=LayoutInflater. from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        //view.setOnLongClickListener(this);//注册长按点击事件
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Note note=mnotelist.get(position);
        holder.name.setText(note.getName());
        holder.data.setText(note.getData());
        holder.top.setVisibility(note.getTop()?View.VISIBLE:View.GONE);
        holder.layout.setOnLongClickListener(new View.OnLongClickListener()     {//注册长按监听
            @Override
            public boolean onLongClick(View view) {
                onItemLongClickListener.OnItemLongClick(view,position);
                return false;
            }
        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onItemClickListener.OnItemClick(view,position);//设置点击事件
//                Log.e("zjc", "onTouchEvent:触摸" );
//                Intent intent=new Intent();
//                intent.setClass(mContext, EditActivity.class);
//                intent.putExtra("content",note.getContent());
//                intent.putExtra("name",note.getName());
//                intent.putExtra("note_id",note.getNote_id());
//                intent.putExtra("span_bytes",note.getSpan_bytes());
//                //mContext.startAc
//                //mContext.startActivityForResult(intent,25);
//                mContext.startActivity(intent);
            }

        });
        /*holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.e("zjc", "onTouchEvent:长按位置"+position);
                //RecordActivity.note_list.remove(RecordActivity.position);
                RecordActivity.note_classification.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
                return false;
            }
        });*/
        try{
            if(note.getStyle().equals("我的收藏"))
            {
                holder.style.setImageResource(R.drawable.yellow);
                /*holder.layout.setBackgroundColor(Color.parseColor("#bbb4ff"));//设置笔记item颜色*/
                holder.layout.setBackgroundColor(Color.parseColor("#ffffff"));//设置笔记item颜色
                //holder.style.setTextColor(Color.parseColor("#FF9100"));
            }
            else if(note.getStyle().equals("未分类笔记"))
            {
                holder.style.setImageResource(R.drawable.blue);
                //holder.style.setTextColor(Color.parseColor("#11b4c3"));
                holder.layout.setBackgroundColor(Color.parseColor("#ffffff"));//设置笔记item颜色
            }
        }
        catch(Exception e)
        {
            holder.layout.setBackgroundColor(Color.parseColor("#ffffff"));//设置笔记item颜色
            Log.e("zjc", "onBindViewHolder: 默认" );
        }
    }

    @Override
    public int getItemCount(){
        return mnotelist.size();
    }

}
