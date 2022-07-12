package com.example.mind_maker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mind_maker.adapter.Recommend_Recycleradapter;
import com.example.mind_maker.adapter.blog;
import com.example.mind_maker.adapter.myRecycleradapter;
import com.example.mind_maker.apter.User;
import com.example.mind_maker.apter.UserTest;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import jieba_android.JiebaSegmenter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button tab_follow,tab_recommend;
    private ImageView me,record;
    View tab01,tab02;//推荐和关注页面
    private List<blog> mDatas;
    private myRecycleradapter recycleAdapter;//关注滑轮
    private Recommend_Recycleradapter recommend_recycleradapter;//推荐滑轮
    private ViewPager mViewPager;// 用来放置界面切换
    private List<View> mViews = new ArrayList<View>();// 用来存放Tab01-02
    private PagerAdapter mPagerAdapter;// 初始化View适配器
    final LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();//初始化控件
        click_init();//控件点击事件绑定
        init_mViewPager();//初始化ViewPager
        init_fresh();
        new Thread(new Runnable() {
            @Override
            public void run() {
                JiebaSegmenter.init(getApplicationContext());       //异步初始化
                UserTest test = new UserTest();
                User user = test.init(MainActivity.this);  //相似用户
                blog blog=new blog(user.getName(),user.PageString.get(0));
                Log.e("zjc_file", "run: "+user.PageString.get(0));
                mDatas.add(blog);
                mDatas.add(new blog(user.getName(),user.PageString.get(1)));
//                Message msg=Message.obtain();
//                msg.what=0;
                handler.sendEmptyMessage(0);
                //recommend_recycleradapter.notifyDataSetChanged();
            }
        }).start();

    }

    private void init_fresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //这里获取数据的逻辑
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (Thread.currentThread()) {
                            try {
                                Thread.currentThread().wait(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        Looper.prepare();
                        Toast.makeText(MainActivity.this,"测试刷新",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();
            }
        });

    }

    private void click_init() {
        me.setOnClickListener(this);
        record.setOnClickListener(this);
        tab_recommend.setOnClickListener(this);
        tab_follow.setOnClickListener(this);
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if(message.what==0)
            {
                recommend_recycleradapter.notifyDataSetChanged();
            }
            return false;
        }
    });
    private void init()//初始化变量
    {
        swipeRefreshLayout=findViewById(R.id.refresh_layout);//刷新布局
        record=findViewById(R.id.record);
        mViewPager=findViewById(R.id.id_viewpage);
        LayoutInflater mLayoutInflater = LayoutInflater.from(this);
        tab01 = mLayoutInflater.inflate(R.layout.fragment_follow, null);//添加关注fragment
        tab02 = mLayoutInflater.inflate(R.layout.fragment_recommend, null);//添加关注fragment
        mViews.add(tab01);
        mViews.add(tab02);
        mDatas=new ArrayList<blog>();
        tab_follow=findViewById(R.id.tab_follow);
        tab_recommend=findViewById(R.id.tab_recommend);
        me=findViewById(R.id.me);
    }
    private void init_mViewPager()//初始化viewpager控件切换操作
    {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Log.e("资产", "选择位置：" + position);
                if(position==0)
                {
                    tab_recommend.setTextColor(Color.parseColor("#000000"));
                    tab_follow.setTextColor(Color.parseColor("#ffffff"));
                }
                if(position==1)
                {
                    tab_recommend.setTextColor(Color.parseColor("#ffffff"));
                    tab_follow.setTextColor(Color.parseColor("#000000"));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPagerAdapter = new PagerAdapter() {

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(mViews.get(position));

            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mViews.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {

                return arg0 == arg1;
            }

            @Override
            public int getCount() {

                return mViews.size();
            }

        };
        mViewPager.setAdapter(mPagerAdapter);
//        blog fl=new blog();
//        for(int i=0;i<25;i++)
//            mDatas.add(fl);

        recycleAdapter=new myRecycleradapter(tab01.getContext(),mDatas);
        recommend_recycleradapter=new Recommend_Recycleradapter(tab02.getContext(),mDatas);
        //tab01
        RecyclerView recyclerView=(RecyclerView) tab01.findViewById(R.id.follow_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(tab01.getContext()));

        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能

        recyclerView.setHasFixedSize(true);
        //创建并设置Adapter

        recyclerView.setAdapter(recycleAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //recyclerview滑动到底部,更新数据
                    //加载更多数据
                    //Looper.loop();
                    if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
                    {
                        //Toast.makeText(MainActivity.this,"测试1",Toast.LENGTH_SHORT).show();
                        Log.e("zjc", "onScrollStateChanged: ");
                    }
                    //Looper.loop();
                }
                else {//滑上去了
                    if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
                    {
                        //Toast.makeText(MainActivity.this,"测试1",Toast.LENGTH_SHORT).show();
                        Log.e("zjc", "onScrollStateChanged: ");
                    }
                    //Looper.loop();
                    //Toast.makeText(MainActivity.this,"测试2",Toast.LENGTH_SHORT).show();
                    //Looper.loop();
                }
            }

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                if(dy<0)
                {
                    Log.e("zjc", "onScrolled: 下划" );
                }
                else if(dy>0)
                {
                    Log.e("zjc", "onScrolled: 上划" );
                }
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //tab02
        RecyclerView tab02_recyclerView=(RecyclerView) tab02.findViewById(R.id.recommend_recyclerview);
        tab02_recyclerView.setLayoutManager(new LinearLayoutManager(tab02.getContext()));

        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能

        tab02_recyclerView.setHasFixedSize(true);

        //创建并设置Adapter

        tab02_recyclerView.setAdapter(recommend_recycleradapter);

        tab02_recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        switch(view.getId())
        {
            case R.id.me://切换页面至我的
                intent.setClass(MainActivity.this,MeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.record://切换页面至记录
                intent.setClass(MainActivity.this,RecordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.tab_follow:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tab_recommend:
                mViewPager.setCurrentItem(1);
                break;
        }
    }
}