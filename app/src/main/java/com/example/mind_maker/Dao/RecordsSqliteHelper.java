package com.example.mind_maker.Dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecordsSqliteHelper extends SQLiteOpenHelper {

    private String CREATE_RECORDS_TABLE = "create table table_records(_id integer primary key autoincrement,title varchar(200),content varchar(100000))";

    public RecordsSqliteHelper(Context context) {
        super(context, "records_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_RECORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}