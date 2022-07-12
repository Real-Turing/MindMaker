package com.example.mind_maker.record;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mind_maker.R;

import java.util.List;
import java.util.zip.Inflater;

public class Todo_adapter extends RecyclerView.Adapter<Todo_adapter.ViewHolder> {

    private List<Todo> mntodolist;
    private Context mcontext;
    private LayoutInflater inflater;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date;
        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.todo_name);
            date = view.findViewById(R.id.todo_date);
        }
    }

    public Todo_adapter(Context context,List<Todo> list){
        mntodolist=list;
        mcontext=context;
        inflater=LayoutInflater.from(mcontext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Todo todo=mntodolist.get(position);
        holder.name.setText(todo.getName());
        holder.date.setText(todo.getDate());
    }

    @Override
    public int getItemCount(){
        return mntodolist.size();
    }

}
