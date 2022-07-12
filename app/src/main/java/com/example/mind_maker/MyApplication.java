package com.example.mind_maker;

import android.app.Application;
import android.os.Environment;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MyApplication extends Application {
    /*public static String url = "jdbc:postgresql://116.63.183.142:15432/postgres" + "?characterEncoding=utf8";//postgres为所要连接的数据库
    public static String user = "gaussdb";//PgSQL登录名
    public static String pass = "Secretpassword@123";//数据库所需连接密码
    public static Connection con;//连接
    public static Statement statement = null;//语句
    public static ResultSet resultSet = null;//返回结果*/
    @Override
    public void onCreate() //应用初始化函数
    {
        super.onCreate();
        initBugly();//更新设置初始化
    }
    private void initBugly()//初始化bugly
    {
        //BuildConfig.BuglyAppId替换成你的App ID
        Bugly.init(getApplicationContext(),"4503079c33",false);
        Bugly.setIsDevelopmentDevice(this,false);

        Beta.autoInit = true;

        /**
         * true表示初始化时自动检查升级
         * false表示不会自动检查升级，需要手动调用Beta.checkUpgrade()方法
         */
        Beta.autoCheckUpgrade = true;
        Beta.upgradeCheckPeriod = 60 * 1000;


        /**
         * 设置升级周期为60s（默认检查周期为0s），60s内SDK不重复向后天请求策略
         */
        Beta.initDelay = 1*1000;

        /**
         * 设置通知栏大图标，largeIconId为项目中的图片资源；
         */
        Beta.largeIconId = R.mipmap.ic_launcher;

        /**
         * 设置状态栏小图标，smallIconId为项目中的图片资源id;
         */
        Beta.smallIconId = R.mipmap.ic_launcher;


        /**
         * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
         * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
         */
        Beta.defaultBannerId = R.mipmap.ic_launcher;

        /**
         * 设置sd卡的Download为更新资源保存目录;
         * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
         */
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        /**
         * 点击过确认的弹窗在APP下次启动自动检查更新时会再次显示;
         */
        Beta.showInterruptedStrategy = true;

        /**
         * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗;
         * 不设置会默认所有activity都可以显示弹窗;
         */
        Beta.canShowUpgradeActs.add(RecordActivity.class);

        //CrashReport
        Bugly.init(getApplicationContext(), "4503079c33", false);
        Bugly.setIsDevelopmentDevice(
                this, false
        );
    }
}
