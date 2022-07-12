package com.example.mind_maker.adapter;

public class collection_file {
    String fold_name;//文件夹名
    String collection_num;//收藏笔记数量
    String author_time;//创建人+创建时间
    public collection_file(String fold_name,String collection_num,String author_time)
    {
        this.fold_name=fold_name;
        this.collection_num=collection_num;
        this.author_time=author_time;
    }
    public collection_file(){}
}
