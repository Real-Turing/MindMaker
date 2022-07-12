package com.example.mind_maker.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.DataInputStream;
import java.nio.ByteOrder;

public class NoteDB extends SQLiteOpenHelper {

    public String TABLE_NAME="Note";
    public final String create_table_sql="create table "+TABLE_NAME +" (_id integer primary key autoincrement,title text, content text,create_time text,update_time_millis text,restorespans BLOB(2000),service_id text);\n";
    public NoteDB(Context context)
    {
        super(context,"MySQL.db",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(create_table_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void InsertData_no_service_id(String title,String content,String create_time,long update_time_millis,byte[] bytes)//向数据库插入数据
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("title",title);//插入文章标题
        value.put("content",content);//传入内容
        value.put("create_time",create_time);//传入创建时间
        value.put("update_time_millis",update_time_millis);//传入最近更新时间
        value.put("restorespans",bytes);//富文本样式二进制信息
        value.put("service_id",-1);//服务器笔记ID
        db.insert(TABLE_NAME,null,value);
    }
    public void InsertData(String title,String content,String create_time,long update_time_millis,byte[] bytes,int service_id)//向数据库插入数据
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("title",title);//插入文章标题
        value.put("content",content);//传入内容
        value.put("create_time",create_time);//传入创建时间
        value.put("update_time_millis",update_time_millis);//传入最近更新时间
        value.put("restorespans",bytes);//富文本样式二进制信息
        value.put("service_id",service_id);//服务器笔记ID
        db.insert(TABLE_NAME,null,value);
    }
    public void UpdateData_service_id(int service_id,int id)//向数据库更新数据
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("service_id",service_id);//服务器笔记编号
        db.update(TABLE_NAME,value,"_id like ?",new String[]{""+id});
        //db.insert(TABLE_NAME,null,value);
    }
    public void UpdateData(String title,String content,String create_time,long update_time_millis,byte[] bytes,int id)//向数据库更新数据
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("title",title);//插入文章标题
        value.put("content",content);//传入内容
        value.put("create_time",create_time);//传入创建时间
        value.put("update_time_millis",update_time_millis);//传入最新更新时间
        value.put("restorespans",bytes);//富文本样式二进制信息
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
