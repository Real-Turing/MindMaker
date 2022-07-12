package com.example.mind_maker.record;

public class Todo {
    private int id;
    private String name;
    private String date;
    private long update_time;
    private String state;
    private int service_id=-1;

    public Todo(int id,String name,String date,long update_time,String state,int service_id){
        this.id=id;
        this.name=name;
        this.date=date;
        this.update_time=update_time;
        this.state=state;
        this.service_id=service_id;
    }
    public Todo(String name,String date,long update_time,String state){
        this.name=name;
        this.date=date;
        this.update_time=update_time;
        this.state=state;
        //this.service_id=service_id;
    }
    public Todo(String name,String date,long update_time,String state,int service_id){
        this.name=name;
        this.date=date;
        this.update_time=update_time;
        this.state=state;
        this.service_id=service_id;
    }
    public Todo(String name,String date){
        this.name=name;
        this.date=date;
    }
    public Todo(){}
    public void setId(int id){this.id=id;}
    public int getId(){return id;}
    public String getState(){return state;}
    public void setService_id(int service_id){this.service_id=service_id;};
    public int getService_id(){return service_id;}
    public long getUpdate_time(){return update_time;}
    public String getName(){
        return name;
    }
    public String getDate(){
        return date;
    }
}
