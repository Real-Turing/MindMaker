package com.example.mind_maker;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.bugly.beta.Beta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.nlopez.smartlocation.SmartLocation;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_login, btn_enroll;
    private String UID;//用户输入的账户
    private String password;//用户输入的密码
    private EditText UID_et, password_et;//用户输入位置的控件
    private static String url = "jdbc:postgresql://116.63.183.142:15432/postgres" + "?characterEncoding=utf8";//postgres为所要连接的数据库
    private static String user = "gaussdb";//PgSQL登录名
    private static String pass = "Secretpassword@123";//数据库所需连接密码
    private static Connection con;//连接
    private static Statement statement = null;//语句
    private static ResultSet resultSet = null;//返回结果
    private static String ps_query;//从数据库返回的UID
    private boolean flag = false;//登录成功或失败的标志
    ImageView head_login;
    private Bitmap image_bitmap;//从数据库获取的用户头像

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        click_init();
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (Thread.currentThread()) {
                    try {
                        get_image();
                        /*head_login.setImageBitmap(image_bitmap);*/
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    /*get_password(1);*/
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                Beta.checkUpgrade();
            }
        }).start();
    }

    private void init2() {
        try {
            //注册JDBC驱动
            Class.forName("org.postgresql.Driver");
            //建立连接
            con = DriverManager.getConnection(url, user, pass);
            if (!con.isClosed()) {
                System.out.println("数据库连接成功");
                Log.e("zjc", "onCreate: 成功");
                statement = con.createStatement();
                String sql = "select ps from user where " + 1 + "= user.uid";
                resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    String ps = resultSet.getString("ps");
                    Log.e("zjc", ps);
                }
                //Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
            }
            con.close();
        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动没有安装");
            Log.e("zjc", "onCreate: 数据库未安装");
            //Toast.makeText(MainActivity.this, "未安装", Toast.LENGTH_SHORT).show();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库连接失败");
            Log.e("zjc", "onCreate: 失败");
            //Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        btn_login = findViewById(R.id.btn_login);
        btn_enroll = findViewById(R.id.btn_enroll);
        UID_et = findViewById(R.id.UID);
        password_et = findViewById(R.id.password);
        head_login = findViewById(R.id.head_login);
    }

    private void click_init() {
        btn_login.setOnClickListener(this);
        btn_enroll.setOnClickListener(this);
        head_login.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                UID = UID_et.getText().toString();//得到用户输入的账号
                password = password_et.getText().toString();//得到用户输入的密码
                if(UID.equals(""))
                {
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, RecordActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    intent.putExtra("flag",1);
                    startActivity(intent);
                    finish();
                }
                if (!is_legal(UID, password))
                    break;
                try {
                    login();
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "出现异常", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_enroll:
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, Enroll_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                //finish();
                break;
            case R.id.head_login:
                Intent imgIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //intent = new Intent(Intent.ACTION_PICK, null);
                //intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(imgIntent, 2);
        }
    }

    private void login() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (Thread.currentThread()) {
                    get_password(Integer.parseInt(UID));
                    if (ps_query != null) {
                        if (ps_query.trim().equals(password)) {
                            flag = true;
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, RecordActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("UID",UID);//传送用户输入的UID绑定用户
                            startActivity(intent);
                            finish();
                            Looper.prepare();
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Looper.prepare();
                            Toast.makeText(LoginActivity.this, "登录失败,密码或账号错误！", Toast.LENGTH_SHORT).show();
                        }
                        Looper.loop();
                    }
                }
                if (flag) {
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, RecordActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    //intent.putExtra("flag",1);
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }

    private boolean id_all_digit(String str)//判断输入的账号是否是纯数字
    {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher((CharSequence) str);
        boolean result = matcher.matches();
        return result;
    }

    private boolean is_legal(String uid, String ps)//判断输入的账号和密码是否合法
    {
        if (UID.equals("") | password.equals("")) {
            Toast.makeText(LoginActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!id_all_digit(UID)) {
            Toast.makeText(LoginActivity.this, "账号存在非数字字符", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void get_password(int uid)//从数据库获取密码，如果输入的用户名不存在就返回一个空值
    {
        try {
            //注册JDBC驱动
            Class.forName("org.postgresql.Driver");
            //建立连接
            con = DriverManager.getConnection(url, user, pass);
            if (!con.isClosed()) {
                System.out.println("数据库连接成功");
                Log.e("zjc", "onCreate: 成功");
                statement = con.createStatement();
                String sql = "select \"user\".ps from \"user\" where " + uid + "= \"user\".uid";
                resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    ps_query = resultSet.getString("ps");
                    Log.e("zjc", ps_query);

                }
                con.close();
            }

        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动没有安装");
            Log.e("zjc", "onCreate: 数据库未安装");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库连接失败");
            Log.e("zjc", "onCreate: 失败");
            Looper.prepare();
            Toast.makeText(LoginActivity.this, "服务器数据库未启动", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
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
                String query = "SELECT data, LENGTH(data) FROM image WHERE id = 4";
                statement = con.createStatement();
                ResultSet rs = statement.executeQuery(query);
                rs.next();
                int len = rs.getInt(2);
                byte[] buf = rs.getBytes("data");
                if (buf!=null) {
                    //将字节数组转化为位图
                    image_bitmap = BitmapFactory.decodeByteArray(buf, 0, buf.length);
                    //将位图显示为图片
                    head_login.post(new Runnable() {
                        @Override
                        public void run() {
                            head_login.setImageBitmap(image_bitmap);
                        }
                    });
                    Looper.prepare();
                    Toast.makeText(this, "显示成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }else {
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
            Toast.makeText(LoginActivity.this, "服务器数据库未启动", Toast.LENGTH_SHORT).show();
            Looper.loop();
            //Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
        }
    }
        @Override
    protected void onActivityResult(int requestCode, int resultCode, /*@Nullable @org.jetbrains.annotations.Nullable */Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            // 从相册返回的数据
            Log.e(this.getClass().getName(), "Result:" + data.toString());
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                head_login.setImageURI(uri);
                Log.e(this.getClass().getName(), "Uri:" + String.valueOf(uri));
            }
        }
    }

}
