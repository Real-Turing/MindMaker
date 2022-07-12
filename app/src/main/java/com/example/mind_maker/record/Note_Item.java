package com.example.mind_maker.record;

public class Note_Item {
    private int id;
    private String name;//笔记名
    private String date;//时间
    private long time;//整型时间
    private String style;//类型
    private String content;//笔记内容
    private byte[] span_bytes;//文本样式二进制信息
    private int service_id=-1;//服务器笔记id
    public Note_Item(){};
    public Note_Item(int id,String name,String date,long time,String style,String content,byte[] span_bytes,int service_id){
        this.id=id;
        this.name=name;
        this.date=date;
        this.time=time;
        this.style=style;
        this.content=content;
        this.span_bytes=span_bytes;
        this.service_id=service_id;
    }
    public Note_Item(String name,String date,long time,String style,String content,byte[] span_bytes,int service_id){
        //this.id=id;
        this.name=name;
        this.date=date;
        this.time=time;
        this.style=style;
        this.content=content;
        this.span_bytes=span_bytes;
        this.service_id=service_id;
    }
    public Note_Item(String name,String date,long time,String style,String content,byte[] span_bytes){//服务端的笔记无需存取本地笔记id
        this.name=name;
        this.date=date;
        this.time=time;
        this.style=style;
        this.content=content;
        this.span_bytes=span_bytes;
        //this.service_id=service_id;
    }
    //public int getService_id(){return service_id;}
    public void setName(String name){this.name=name;}
    public void setDate(String date){this.date=date;}
    public void setTime(int time){this.time=time;}
    public void setStyle(String style){this.style=style;}
    public void setContent(String Content){this.content=content;}
    public void setSpan_bytes(byte[] span_bytes){this.span_bytes=span_bytes;}
    public void setId(int id){this.id=id;}
    public String getContent()
    {
        return content;
    }
    public int getId(){return id;}
    public String getName(){return name;}
    public String getDate(){return date;}
    public long getTime(){return time;}
    public String getStyle(){return style;}
    public byte[] getSpan_bytes(){return span_bytes;}
    public int getService_id(){return service_id;}
    public void setService_id(int service_id){this.service_id=service_id;}
}
