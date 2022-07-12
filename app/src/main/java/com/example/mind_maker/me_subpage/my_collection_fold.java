package com.example.mind_maker.me_subpage;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mind_maker.R;
import com.example.mind_maker.adapter.Collection_Fold_Recycleradapter;
import com.example.mind_maker.adapter.History_Recycleradapter;
import com.example.mind_maker.adapter.collection_file;
import com.example.mind_maker.adapter.history;

import java.util.ArrayList;
import java.util.List;

public class my_collection_fold extends AppCompatActivity implements View.OnClickListener {
    private List<collection_file> mDatas;
    private Collection_Fold_Recycleradapter recycleAdapter;//收藏文件夹滑轮
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_collection);
        init();
        init_recyclerview();
    }

    private void init_recyclerview() {
        collection_file fl=new collection_file();
        for(int i=0;i<5;i++)
            mDatas.add(fl);
        recycleAdapter=new Collection_Fold_Recycleradapter(this,mDatas);
        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.collection_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        recyclerView.setAdapter(recycleAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void init()//初始化
    {
        mDatas=new ArrayList<collection_file>();
    }
    @Override
    public void onClick(View view) {

    }
}
