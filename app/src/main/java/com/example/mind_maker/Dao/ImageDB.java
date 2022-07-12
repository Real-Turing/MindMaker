package com.example.mind_maker.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.DataInputStream;
import java.nio.ByteOrder;

public class ImageDB extends SQLiteOpenHelper {

    public String TABLE_NAME="Image";
    public final String create_table_sql="create table "+TABLE_NAME +" (id integer primary key autoincrement, path text);\n";
    public ImageDB(Context context)
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
    public void InsertData(String path)//向数据库插入数据
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("path",path);//传入得分
        db.insert(TABLE_NAME,null,value);
    }
    public Cursor QueryData()//数据库查询最大值
    {
        SQLiteDatabase db=getWritableDatabase();
        return db.query(TABLE_NAME,null,null,null,null,null, null);//成绩排序
    }
}
