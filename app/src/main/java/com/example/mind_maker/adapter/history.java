package com.example.mind_maker.adapter;

public class history {
    String title;//文章标题
    String content;//文章内容
    String author;//作者
    public history(String title,String content,String author)//构造函数
    {
        this.title=title;
        this.content=content;
        this.author=author;
    }
    public history(){};//无参构造函数
}
