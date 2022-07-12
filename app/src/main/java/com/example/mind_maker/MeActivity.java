package com.example.mind_maker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mind_maker.me_subpage.my_collection_fold;
import com.example.mind_maker.me_subpage.my_data;
import com.example.mind_maker.me_subpage.my_history;
import com.example.mind_maker.me_subpage.my_name;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MeActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView find,my_data_more,record,user_head;
    private Button my_data_btn,look,my_collection_fold_btn;
    LinearLayout l_name;
    private static String url = "jdbc:postgresql://116.63.183.142:15432/postgres" + "?characterEncoding=utf8";//postgres为所要连接的数据库
    private static String user = "gaussdb";//PgSQL登录名
    private static String pass = "Secretpassword@123";//数据库所需连接密码
    private static Connection con;//连接
    private static Statement statement = null;//语句
    private static PreparedStatement dbStat = null;//语句
    private static ResultSet resultSet = null;//返回结果
    public static Bitmap image_bitmap;//全局头像
    public static String age,sex,occupation,phonenumber,username;
    private TextView uid,user_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        init();
        click_init();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    get_image();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }).start();
    }

    private void get_image() throws SQLException {
        try {
            //注册JDBC驱动
            Class.forName("org.postgresql.Driver");
            //建立连接
            con = DriverManager.getConnection(url, user, pass);
            if (!con.isClosed()) {
                System.out.println("数据库连接成功");
                Log.e("zjc", "onCreate: 成功");
                String query = "SELECT uid,age,sex,occupation,phonenumber,username,headimage FROM login WHERE uid = "+RecordActivity.UID;//获取基本信息
                //dbStat=con.prepareStatement(query);
                //dbStat.setInt(1,4);//设置第一个参数为4
                statement = con.createStatement();
                ResultSet rs = statement.executeQuery(query);
                //rs=dbStat.executeQuery();
                rs.next();
                //File myFile = new File("java_logo2.jpg");
                //int len = rs.getInt(2);
                byte[] buf = rs.getBytes("headimage");
                age=rs.getString("age");
                sex=rs.getString("sex");
                occupation=rs.getString("occupation");
                phonenumber=rs.getString("phonenumber");
                username=rs.getString("username");
                uid.post(new Runnable() {
                    @Override
                    public void run() {
                        uid.setText("UID:"+RecordActivity.UID);
                    }
                });
                user_name.post(new Runnable() {
                    @Override
                    public void run() {
                        user_name.setText(username);
                    }
                });
                if (buf != null) {
                    //将字节数组转化为位图
                    image_bitmap = BitmapFactory.decodeByteArray(buf, 0, buf.length);
                    //将位图显示为图片
                    user_head.post(new Runnable() {
                        @Override
                        public void run() {
                            user_head.setImageBitmap(image_bitmap);
                        }
                    });
                    Looper.prepare();
                    Toast.makeText(this, "显示成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    //System.out.println("显示成功");
                } else {
                    user_head.post(new Runnable() {
                        @Override
                        public void run() {
                            user_head.setImageResource(R.drawable.girl);//设置默认头像
                        }
                    });
                }
                con.close();
            }

        } catch (ClassNotFoundException e) {

            System.out.println("数据库驱动没有安装");
            Log.e("zjc", "onCreate: 数据库未安装");
            //Toast.makeText(MainActivity.this, "未安装", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {

            e.printStackTrace();
            System.out.println("数据库连接失败");
            Log.e("zjc", "onCreate: 失败");
            Looper.prepare();
            Toast.makeText(MeActivity.this, "服务器数据库未启动", Toast.LENGTH_SHORT).show();
            Looper.loop();
            //Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void click_init() //为控件绑定点击事件
    {
        record.setOnClickListener(this);
        find.setOnClickListener(this);
        look.setOnClickListener(this);
        my_data_btn.setOnClickListener(this);
        my_data_more.setOnClickListener(this);
        l_name.setOnClickListener(this);
        my_collection_fold_btn.setOnClickListener(this);
    }

    private void init()//初始化控件
    {
        record=findViewById(R.id.record);
        l_name=findViewById(R.id.l_name);
        look=findViewById(R.id.look);
        find=findViewById(R.id.find);
        my_data_more=findViewById(R.id.my_data_more);
        my_data_btn=findViewById(R.id.my_data_btn);
        my_collection_fold_btn=findViewById(R.id.my_collection_fold_btn);
        user_head=findViewById(R.id.user_head);
        uid=findViewById(R.id.uid);
        user_name=findViewById(R.id.username);
        age="null";
        sex="null";
        occupation="null";
        phonenumber="null";
        username="null";
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        switch(view.getId())
        {
            case R.id.find://切换页面至我的
                ///Intent intent=new Intent();
                intent.setClass(MeActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.my_data_more:
            case R.id.my_data_btn:
                //Intent intent=new Intent();
                intent.setClass(MeActivity.this, my_data.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.look:
                intent.setClass(MeActivity.this, my_history.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.l_name:
                intent.setClass(MeActivity.this, my_name.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.my_collection_fold_btn:
                intent.setClass(MeActivity.this, my_collection_fold.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.record:
                intent.setClass(MeActivity.this, RecordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);

                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
        }
    }
}
