package com.example.mind_maker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Enroll_Activity extends AppCompatActivity implements View.OnClickListener {
    private Button confirm;//确认按钮
    private LinearLayout confirm_password;//再次确认密码的布局
    private EditText et_ps,et_re_ps;//输入密码位置
    private TextView content_need;
    private EditText username,phone_number,verification_code,age;//注册所需信息
    private String str_username,str_phone_number,str_verification_code,str_age,str_sex,str_occupation,str_password,str_re_password;//获取的字符串
    private RadioGroup sex;//性别选择
    private Spinner occupation_spinner;//职业选择
    private static String url = "jdbc:postgresql://116.63.183.142:15432/postgres"+ "?characterEncoding=utf8";//postgres为所要连接的数据库
    private static String user = "gaussdb";//PgSQL登录名
    private static String pass = "Secretpassword@123";//数据库所需连接密码
    private static Connection con;//连接
    private static Statement statement=null;//语句
    private static ResultSet resultSet=null;//返回结果
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        init();
        click_init();

    }


    private void click_init() {
        confirm.setOnClickListener(this);
        sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.e("zjc", "onCheckedChanged: "+i);//123
                if(i==1)
                    str_sex="男";
                else if(i==2)
                    str_sex="女";
            }
        });
        occupation_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("zjc", "onCheckedChanged: "+i);//012
                switch(i)
                {
                    case 0:
                        str_occupation="学生";
                        break;
                    case 1:
                        str_occupation="教师";
                        break;
                    case 2:
                        str_occupation="农民";
                        break;
                    case 3:
                        str_occupation="宇航员";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void init()//初始化
    {
        confirm=findViewById(R.id.confirm);
        confirm_password=findViewById(R.id.confirm_password);
        et_ps=findViewById(R.id.et_ps);
        content_need=findViewById(R.id.content_need);
        username=findViewById(R.id.username);//用户名
        phone_number=findViewById(R.id.phone_number);//手机号码
        verification_code=findViewById(R.id.verification_code);//验证码
        age=findViewById(R.id.age);//年龄
        sex=findViewById(R.id.sex);//性别
        occupation_spinner=findViewById(R.id.occupation_spinner);//职业选择
        et_re_ps=findViewById(R.id.et_re_ps);//用户第二次输入的密码
    }
    private boolean read_content()//读取用户输入的注册信息
    {
        str_username=username.getText().toString();
        str_phone_number=phone_number.getText().toString();
        str_age=age.getText().toString();
        str_verification_code=verification_code.getText().toString();
        str_password=et_ps.getText().toString();//用户输入的密码
        str_re_password=et_re_ps.getText().toString();//用户第二次输入的密码
        //输入的信息不能为空
        return !str_username.equals("") && !str_phone_number.equals("") && !str_age.equals("") && !str_password.equals("") && !str_re_password.equals("");
    }
    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.confirm:
                if(!et_ps.getText().toString().equals(""))
                {
//                    confirm_password.setVisibility(View.VISIBLE);//设置为可见
//                    content_need.setVisibility(View.VISIBLE);
                    if(read_content())
                    {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(get_a_user())//向数据库传送注册信息并返回注册是否成功
                                {
                                    //Toast.makeText(Enroll_Activity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.setClass(Enroll_Activity.this, RecordActivity.class);//跳转活动
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }).start();
                    }
                    else
                    {
                        Toast.makeText(Enroll_Activity.this,"输入的数据不能为空！",Toast.LENGTH_SHORT).show();
                    }
                }

                else
                    Toast.makeText(Enroll_Activity.this,"请输入密码！",Toast.LENGTH_SHORT).show();
        }
    }
    private String message_sql() //注册所需信息SQL
    {
        return "insert into login(ps,age,sex,occupation,phonenumber) values ('"+str_password+"','"+str_age+"','"+str_sex+"','"+str_occupation+"','"+str_phone_number+"')";
    }
    private boolean get_a_user()//数据库创建一个新用户
    {
        try {
            //注册JDBC驱动
            Class.forName("org.postgresql.Driver");
            //建立连接
            con = DriverManager.getConnection(url, user, pass);
            if (!con.isClosed()) {
                System.out.println("数据库连接成功");
                Log.e("zjc", "onCreate: 成功");
                statement=con.createStatement();
                String sql=message_sql();
                /*if(statement.executeUpdate(sql))
                    Toast.makeText(Enroll_Activity.this,"注册成功",Toast.LENGTH_SHORT).show();*/
                String str="insert into login(ps,age,sex,occupation,phonenumber) values ('123','34','女','教师','13029093455')";
                statement.execute(sql);
                //Toast.makeText(Enroll_Activity.this,statement.executeUpdate(sql),Toast.LENGTH_SHORT).show();
                con.close();
                //Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
                return true;
            }
        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动没有安装");
            Log.e("zjc", "onCreate: 数据库未安装");
            //Toast.makeText(MainActivity.this, "未安装", Toast.LENGTH_SHORT).show();
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库连接失败");
            Log.e("zjc", "onCreate: 失败");
            Looper.prepare();
            Toast.makeText(Enroll_Activity.this,"服务器数据库未启动",Toast.LENGTH_SHORT).show();
            Looper.loop();
            //Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;//一般执行不到
    }
}
