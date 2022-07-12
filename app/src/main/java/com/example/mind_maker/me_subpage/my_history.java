package com.example.mind_maker.me_subpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mind_maker.R;
import com.example.mind_maker.adapter.History_Recycleradapter;
import com.example.mind_maker.adapter.Recommend_Recycleradapter;
import com.example.mind_maker.adapter.blog;
import com.example.mind_maker.adapter.history;
import com.example.mind_maker.adapter.myRecycleradapter;

import java.util.ArrayList;
import java.util.List;

public class my_history extends AppCompatActivity implements View.OnClickListener {
    View tab;
    private List<history> mDatas;
    private History_Recycleradapter recycleAdapter;//历史浏览滑轮
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_history);
        init();
        init_recyclerview();
    }

    private void init_recyclerview()
    {
        history fl=new history();
        for(int i=0;i<5;i++)
            mDatas.add(fl);
        recycleAdapter=new History_Recycleradapter(this,mDatas);
        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.history_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        recyclerView.setAdapter(recycleAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void init()//初始化控件
    {
        LayoutInflater mLayoutInflater = LayoutInflater.from(this);
        mDatas=new ArrayList<history>();
        tab = mLayoutInflater.inflate(R.layout.my_history, null);//添加历史浏览界面
    }

    @Override
    public void onClick(View view) {

    }
}
