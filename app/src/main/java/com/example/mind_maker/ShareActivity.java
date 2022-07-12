package com.example.mind_maker;

import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener {
    private static String url = "jdbc:postgresql://116.63.183.142:15432/postgres" + "?characterEncoding=utf8";//postgres为所要连接的数据库
    private static String user = "gaussdb";//PgSQL登录名
    private static String pass = "Secretpassword@123";//数据库所需连接密码
    private static Connection con;//连接
    private static Statement statement = null;//语句
    private static ResultSet resultSet = null;//返回结果
    public static boolean multi_flag=false;
    private EditText share_title,edit_share,invite_id;
    private Button invite_btn;
    private String invite_UID;
    private String UID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiry_edit_new);
        init();
        init_edit();

    }

    private void init_db() //在云端数据库创建词条
    {
        String sql="insert into share(creator,invitor) values ("+UID+",\'"+invite_UID+"\');";
        //Log.e("zjc_cloud", "send_note_to_service: "+sql);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //注册JDBC驱动
                    Class.forName("org.postgresql.Driver");
                    //建立连接
                    con = DriverManager.getConnection(url, user, pass);
                    if (!con.isClosed()) {
                        System.out.println("数据库连接成功");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        statement = con.createStatement();
                        statement.execute(sql);
                        con.close();
                        Log.e("zjc_cloud", "run: latch-1" );
                        Toast.makeText(ShareActivity.this, "邀请成功", Toast.LENGTH_SHORT).show();
                        //todoDB.UpdateData_service_id(todo.getService_id(),todo.getId());
                        //Log.e("zjc_cloud", "onCreate: todo上传成功"+service_id+"笔记id："+todo.getId());
                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("数据库驱动没有安装");
                    Log.e("zjc", "onCreate: 数据库未安装");
                    //Toast.makeText(MainActivity.this, "未安装", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("数据库连接失败");
                    Log.e("zjc_cloud", "onCreate: todo上传失败");
                    Looper.prepare();
                    Toast.makeText(ShareActivity.this, "服务器数据库未启动", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private void init_edit()
    {
        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("zjc_text_watcher", "beforeTextChanged: "+charSequence );
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("zjc_text_watcher", "onTextChanged: "+charSequence );
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e("zjc_text_watcher", "afterTextChanged: "+editable.toString() );

            }
        };
        edit_share.addTextChangedListener(textWatcher);
    }

    private void init()
    {
        UID=getIntent().getStringExtra("UID");
        share_title=findViewById(R.id.share_title);
        edit_share=findViewById(R.id.edit_share);
        invite_id=findViewById(R.id.invite_id);
        invite_btn=findViewById(R.id.invite_btn);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.invite_btn:
                invite_UID=invite_id.getText().toString();
                init_db();
                break;
        }
    }
}
