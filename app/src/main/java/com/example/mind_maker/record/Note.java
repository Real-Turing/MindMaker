package com.example.mind_maker.record;

public class Note {
    private String name;//笔记名
    private String date;//时间
    private int time;//整型时间
    private String style;//类型
    private int content_style;//笔记0 录音1 视频2
    private boolean is_top=false;//是否置顶
    private String content;//笔记内容
    private int note_id;//笔记的唯一标识符
    private int duration=-1;//时长
    private int start_time=-1;//语音播放时间
    private byte[] span_bytes;//文本样式二进制信息
    private byte[] file;//媒体文件二进制数组
    private int service_id=-1;
    public Note(String name,String content,byte[] span_bytes,int note_id,int service_id){
        this.name=name;
        this.content=content;
        this.span_bytes=span_bytes;
        this.note_id=note_id;
        this.service_id=service_id;
    }

    public Note(String name,String date,String content,int time,String style,int content_style,byte[] span_bytes,int note_id){//文本加图片
        this.content_style=content_style;
        this.name=name;
        this.date=date;
        this.time=time;
        this.style=style;
        this.content=content;
        this.note_id=note_id;
        this.span_bytes=span_bytes;

    }
    public Note(String name,String date,String content,int time,String style,int content_style,int duration,int start_time){
        this.content_style=content_style;
        this.name=name;
        this.date=date;
        this.time=time;
        this.style=style;
        this.content=content;
        this.duration=duration;
        this.start_time=start_time;
    }
    public Note(String name,String date,String content,int time,String style,int content_style,int duration,int start_time,int note_id){
        this.content_style=content_style;
        this.name=name;
        this.date=date;
        this.time=time;
        this.style=style;
        this.content=content;
        this.note_id=note_id;
        this.duration=duration;
        this.start_time=start_time;
    }
    /*public Note(String name,String date,String content,int time,String style,int content_style,byte[] file,int note_id){
        this.content_style=content_style;
        this.name=name;
        this.date=date;
        this.time=time;
        this.style=style;
        this.content=content;
        this.note_id=note_id;
        this.file=file;
    }*/
    public Note(String name,String date,String content,int time,String style,int content_style,int note_id){
        this.content_style=content_style;
        this.name=name;
        this.date=date;
        this.time=time;
        this.style=style;
        this.content=content;
        this.note_id=note_id;
    }
    public Note(String name,String date,int time,String style){
        this.name=name;
        this.date=date;
        this.time=time;
        this.style=style;
    }
    public Note(String name,String date,int time){
        this.name=name;
        this.date=date;
        this.time=time;
        this.style="未分类笔记";
    }
    public Note(){
    }

    public int getService_ID() {
        return service_id;
    }
    public byte[] getSpan_bytes(){return span_bytes;}
    public void setSpan_bytes(byte[] bytes){this.span_bytes=bytes;}
    public void setDuration(int duration){this.duration=duration;}
    public void setStartTime(int start_time){this.start_time=start_time;}
    public int getDuration(){return duration;}
    public int getStart_time(){return start_time;}
    public int getContent_style(){return content_style;}
    public String getContent(){
        return content;
    }
    public String getName(){
        return name;
    }
    public String getData(){
        return date;
    }
    public String getStyle(){return style;}
    public int getNote_id(){return note_id;}
    public int getTime(){return time;}
    public boolean getTop(){return is_top;}
    public void setContent(String content){this.content=content;}
    public void change_top()//改变置顶状态
    {
        this.is_top=!this.is_top;
    }
}
