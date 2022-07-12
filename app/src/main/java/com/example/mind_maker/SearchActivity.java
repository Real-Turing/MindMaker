package com.example.mind_maker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mind_maker.Dao.NoteDB;
import com.example.mind_maker.Dao.RecordsSqliteHelper;
import com.example.mind_maker.record.Note;
import com.example.mind_maker.util.ListViewForScrollView;

public class SearchActivity extends AppCompatActivity {

    EditText mEditSearch;
    TextView mTvSearch;
    TextView mTvTip;
    ListViewForScrollView mListView;
    TextView mTvClear;

    SimpleCursorAdapter adapter;
    NoteDB searchSqliteHelper;
    RecordsSqliteHelper recordsSqliteHelper;
    SQLiteDatabase db_search;
    SQLiteDatabase db_records;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mEditSearch = (EditText) findViewById(R.id.edit_search);
        mTvSearch = (TextView) findViewById(R.id.tv_search);
        mTvTip = (TextView) findViewById(R.id.tv_tip);
        mListView = (ListViewForScrollView) findViewById(R.id.listView);
        mTvClear = (TextView) findViewById(R.id.tv_clear);
    }

    private void initData() {
        searchSqliteHelper = new NoteDB(this);
        recordsSqliteHelper = new RecordsSqliteHelper(this);
        // 初始化本地数据库
        //initializeData();
        //尝试从保存查询纪录的数据库中获取历史纪录并显示
        cursor = recordsSqliteHelper.getReadableDatabase().rawQuery("select * from table_records", null);
        adapter = new SimpleCursorAdapter(this, R.layout.simple_list_item, cursor
                , new String[]{"_id","title", "content"}, new int[]{R.id.text_id,R.id.text1, R.id.text2}
                , CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mListView.setAdapter(adapter);
    }

    /**d
     * 避免重复初始化数据
     */
    private void deleteData() {
        db_search = searchSqliteHelper.getWritableDatabase();
        db_search.execSQL("delete from table_search");
        //db_search.close();
    }

    /**
     * 初始化数据
     */
    private void initializeData() {
        deleteData();
        db_search = searchSqliteHelper.getWritableDatabase();
        for (int i = 0; i < 20; i++) {
            db_search.execSQL("insert into Note values(null,?,?)",
                    new String[]{"name" + i + 10, "pass" + i + "word"});
        }
        //db_search.close();
    }

    /**
     * 初始化事件监听
     */
    private void initListener() {
        /**
         * 清除历史纪录
         */
        mTvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecords();
            }
        });
        /**
         * 搜索按钮保存搜索纪录，隐藏软键盘
         */
        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏键盘
//                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
//                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                //保存搜索记录
                insertRecords(mEditSearch.getText().toString().trim());

            }
        });
        /**
         * EditText对键盘搜索按钮的监听，保存搜索纪录，隐藏软件盘
         */
        mEditSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
//                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
//                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //保存搜索记录
                    insertRecords(mEditSearch.getText().toString().trim());
                }
                return false;
            }
        });
        /**
         * EditText搜索框对输入值变化的监听，实时搜索
         */
        // 使用TextWatcher实现对实时搜索
        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEditSearch.getText().toString().equals("")) {
                    mTvTip.setText("搜索历史");
                    mTvClear.setVisibility(View.VISIBLE);
                    cursor = recordsSqliteHelper.getReadableDatabase().rawQuery("select * from table_records", null);
                    //refreshListView();
                } else {
                    mTvTip.setText("搜索结果");
                    mTvClear.setVisibility(View.GONE);
                    String searchString = mEditSearch.getText().toString();
                    queryData(searchString);
                }
            }
        });

        /**
         * ListView的item点击事件
         */
        // TODO: 2017/8/10 5、listview的点击 做你自己的业务逻辑 保存搜索纪录
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String fid = ((TextView) view.findViewById(R.id.text_id)).getText().toString();
                String title = ((TextView) view.findViewById(R.id.text1)).getText().toString();
                String content = ((TextView) view.findViewById(R.id.text2)).getText().toString();
                Log.e("Skylark ", fid+"   "+title + "---" + content);
                Cursor cursor=searchSqliteHelper.QueryData();
                String temp_title,temp_content;
                int note_id=-1;
                byte[] span_bytes =null;
                int temp_id;
                byte[] temp_span_bytes;
                while(cursor.moveToNext())
                {
                    temp_title=cursor.getString(cursor.getColumnIndex("title"));
                    temp_content = cursor.getString(cursor.getColumnIndex("content"));
                    temp_id=cursor.getInt(cursor.getColumnIndex("_id"));
                    temp_span_bytes=cursor.getBlob(cursor.getColumnIndex("restorespans"));
                    if(title.equals(temp_title)&&content.equals(temp_content))
                    {
                        span_bytes=temp_span_bytes;
                        note_id=temp_id;
                    }

                }
                Intent intent=new Intent();
                intent.setClass(SearchActivity.this, EditActivity.class);
                intent.putExtra("content",content);
                intent.putExtra("name",title);
                intent.putExtra("note_id",note_id);
                intent.putExtra("span_bytes",span_bytes);
                Log.e("skylark", "onItemClick: "+note_id);
                startActivity(intent);
                // TODO: 2017/8/10 做自己的业务逻辑

            }
        });

    }

    /**
     * 保存搜索纪录
     *
     * @param username
     */
    private void insertRecords(String username) {
        if (!hasDataRecords(username)) {
            db_records = recordsSqliteHelper.getWritableDatabase();
            db_records.execSQL("insert into table_records values(null,?,?)", new String[]{username, ""});
            //db_records.close();
        }
    }

    /**
     * 检查是否已经存在此搜索纪录
     *
     * @param records
     * @return
     */
    private boolean hasDataRecords(String records) {

        cursor = recordsSqliteHelper.getReadableDatabase()
                .rawQuery("select _id,title from table_records where title = ?"
                        , new String[]{records});

        return cursor.moveToNext();
    }

    /**
     * 搜索数据库中的数据
     *
     * @param searchData
     */
    private void queryData(String searchData) {
        cursor = searchSqliteHelper.getReadableDatabase()
                .rawQuery("select * from Note where title like '%" + searchData + "%' or content like '%" + searchData + "%'", null);
        refreshListView();
    }

    /**
     * 删除历史纪录
     */
    private void deleteRecords() {
        db_records = recordsSqliteHelper.getWritableDatabase();
        db_records.execSQL("delete from table_records");

        cursor = recordsSqliteHelper.getReadableDatabase().rawQuery("select * from table_records", null);
        if (mEditSearch.getText().toString().equals("")) {
            refreshListView();
        }
    }

    /**
     * 刷新listview
     */
    private void refreshListView() {
        adapter.notifyDataSetChanged();
        adapter.swapCursor(cursor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db_records != null) {
            //db_records.close();
        }
        if (db_search != null) {
            //db_search.close();
        }
    }
}