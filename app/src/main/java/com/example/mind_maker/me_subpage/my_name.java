package com.example.mind_maker.me_subpage;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mind_maker.MeActivity;
import com.example.mind_maker.R;
import com.example.mind_maker.adapter.History_Recycleradapter;
import com.example.mind_maker.adapter.history;

import java.util.List;

public class my_name extends AppCompatActivity implements View.OnClickListener {
    View tab;
    private List<history> mDatas;
    private History_Recycleradapter recycleAdapter;//关注滑轮
    private ImageView user_head;
    private TextView username,gender,occupation;
    private LinearLayout icon_layout,username_layout,gender_layout,occupation_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_name);
        init();
        init_layout();
    }
    private void init()//初始化控件
    {
        user_head=findViewById(R.id.user_head);
        username=findViewById(R.id.username);
        gender=findViewById(R.id.gender);
        occupation=findViewById(R.id.occupation);
        icon_layout=findViewById(R.id.icon_layout);
        username_layout=findViewById(R.id.username_layout);
        gender_layout=findViewById(R.id.gender_layout);
        occupation_layout=findViewById(R.id.occupation_layout);
        icon_layout.setOnClickListener(this);
        username_layout.setOnClickListener(this);
        gender_layout.setOnClickListener(this);
        occupation_layout.setOnClickListener(this);
    }
    private void init_layout()//初始化当前布局
    {
        user_head.setImageBitmap(MeActivity.image_bitmap);//设置用户头像
        username.setText(MeActivity.username.trim());//设置用户名;
        gender.setText(MeActivity.sex);//设置用户性别
        occupation.setText(MeActivity.occupation);//设置用户职位
    }
    @Override
    public void onClick(View view)//控件点击事件
    {

    }
}
