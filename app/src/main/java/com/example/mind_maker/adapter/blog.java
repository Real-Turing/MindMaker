package com.example.mind_maker.adapter;

public class blog {
    String name;//用户昵称
    String time="之前";//发表时间
    String content;//发表内容
    int praised_num=0;//点赞数量
    int comment_num=0;//评论数量
    int collect_num=0;//收藏数量
    int share_num=0;//分享数量
    boolean is_follow=false;//是否被关注
    public blog(){};
    public blog(String name,String time,String content,int praised_num,int comment_num,int collect_num,int share_num,boolean is_follow)//构造函数
    {
        this.name=name;
        this.time=time;
        this.content=content;
        this.praised_num=praised_num;
        this.comment_num=comment_num;
        this.collect_num=collect_num;
        this.share_num=share_num;
        this.is_follow=is_follow;
    }
    public blog(String name,String content)
    {
        this.name=name;
        this.content=content;
    }
}
