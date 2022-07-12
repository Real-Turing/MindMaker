package com.example.mind_maker.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDB extends SQLiteOpenHelper {
    public String TABLE_NAME="Todo";
    public final String create_table_sql="create table "+TABLE_NAME +" (_id integer primary key autoincrement,title text,Time text,update_time text,state text,time_delay int,service_id text);\n";
    public TodoDB(Context context)
    {
        super(context,"Todo.db",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(create_table_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void InsertData(String title,String time,long update_time,String state,int time_delay,int service_id)//向数据库插入数据
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("title",title);//插入文章标题
        value.put("time",time);//传入创建时间
        value.put("state",state);//传入待办状态(完成或未完成)
        value.put("time_delay",time_delay);//通知时延
        value.put("update_time",update_time);//最新更新时间
        value.put("service_id",service_id);//服务器todo_id
        db.insert(TABLE_NAME,null,value);
    }
    public void UpdateDelay(int time_delay,int id)
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("time_delay",time_delay);//通知时延
        db.update(TABLE_NAME,value,"_id like ?",new String[]{""+id});
    }
    public void UpdateData_service_id(int service_id,int id)//向数据库更新数据
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("service_id",service_id);//插入文章标题
        //value.put("time",time);//传入内容
        db.update(TABLE_NAME,value,"_id like ?",new String[]{""+id});
        //db.insert(TABLE_NAME,null,value);
    }

    public void UpdateData(String title,String time,int id)//向数据库更新数据
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("title",title);//插入文章标题
        value.put("time",time);//传入内容
        db.update(TABLE_NAME,value,"_id like ?",new String[]{""+id});
        //db.insert(TABLE_NAME,null,value);
    }
    public Cursor QueryData()//数据库查询最大值
    {
        SQLiteDatabase db=getWritableDatabase();
        return db.query(TABLE_NAME,null,null,null,null,null, null);//成绩排序
    }
    public Cursor QueryData_id(int id)//查询service_id是否存在与本地数据库中
    {
        SQLiteDatabase db=getWritableDatabase();
        return db.query(TABLE_NAME,new String []{"service_id"},"service_id like ?",new String[]{""+id},null,null, null);
    }
    public void DeleteData(int id)//数据库删除数据
    {
        SQLiteDatabase db=getWritableDatabase();
        db.delete(TABLE_NAME,"_id like ?",new String[]{""+id});
        //return db.query(TABLE_NAME,null,null,null,null,null, null);//成绩排序
    }
}
