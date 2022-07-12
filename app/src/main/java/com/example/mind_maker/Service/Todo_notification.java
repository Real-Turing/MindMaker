package com.example.mind_maker.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mind_maker.Dao.TodoDB;
import com.example.mind_maker.R;
import com.example.mind_maker.RecordActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Todo_notification extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        send_notification("");
        return null;
    }

    @SuppressLint("Range")
    @Override
    public void onCreate() {
        super.onCreate();


        do_notification_job();
//        while(cursor.moveToNext())
//        {
//            Log.e("zjc_service", "onCreate: service"+cursor.getString(cursor.getColumnIndex("time")));
//        }
        //send_notification("");




    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e("zjc_service", "onStart: " );
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                do_notification_job();
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 1000); //启动定时任务
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (Thread.currentThread()) {
                    try {
                        Thread.currentThread().wait(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                timer.cancel();
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("zjc_service", "onDestroy: " );
    }

    private void send_notification(String Content)
    {
        Intent intent=new Intent();
        PendingIntent pending=PendingIntent.getActivity(this,0,intent,0);

        int id = (int) (System.currentTimeMillis() / 1000);
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);//NotificationManager实例对通知进行管理

        NotificationChannel notificationChannel = new NotificationChannel("AppTestNotificationId", "AppTestNotificationName", NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(notificationChannel);
        Notification notification= new Notification.Builder(this)
                .setContentTitle("思享云记事本")
                .setContentText(Content)
                .setChannelId("AppTestNotificationId")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pending)//设置点击动作
                .setSmallIcon(R.drawable.ic_launcher_foreground)//设置小图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_background))//设置大图标

                .build();
        notification.defaults = Notification.DEFAULT_SOUND;//通知声音
        notification.defaults |= Notification.DEFAULT_VIBRATE;//振动
        notification.flags |= Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
        notification.ledARGB= Color.GREEN;//控制通知的led灯颜色
        notification.ledOnMS=1000;//通知灯的显示时间
        notification.ledOffMS=1000;
        notification.flags=Notification.FLAG_SHOW_LIGHTS;
        manager.notify(id,notification);//调用NotificationManager的notify方法使通知显示
    }

    private long get_regular_time_db(String time)
    {
        time=time.trim();
        String regex="([1-9]\\d*\\.?\\d+)|(0\\.\\d*[1-9])|(\\d+)";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(time);
        //int [] result=new int[6];
        StringBuilder result= new StringBuilder();
        int i=0;
        while(matcher.find()) {
            if(Integer.parseInt(matcher.group())<10)
            {
                result.append("0").append(matcher.group());
            }
            else
                result.append(matcher.group());
            i++;
            //Log.e("zjc_text", "get_regular_time: "+matcher.group());
        }
        Log.e("zjc_text", "get_regular_time: "+result.toString());
        return Long.parseLong(result.toString());
    }
    private long get_regular_time(String time)
    {
        time=time.trim();
        String regex="([1-9]\\d*\\.?\\d+)|(0\\.\\d*[1-9])|(\\d+)";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(time);
        //int [] result=new int[6];
        StringBuilder result= new StringBuilder();
        int i=0;
        while(matcher.find()) {
            result.append(matcher.group());
            i++;
            //Log.e("zjc_text", "get_regular_time: "+matcher.group());
        }
        Log.e("zjc_text", "get_regular_time: "+result.toString());
        return Long.parseLong(result.toString());
    }

    @SuppressLint("Range")
    private  void do_notification_job()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm"); //设置时间格式
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08")); //设置时区
        Date curDate = new Date(System.currentTimeMillis()); //获取当前时间
        String createDate = formatter.format(curDate);   //格式转换
        Log.e("zjc_service", "onCreate: service"+createDate);
        long time_now=get_regular_time(createDate);
        try
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TodoDB todoDB=new TodoDB(Todo_notification.this);
                    Cursor cursor=todoDB.QueryData();
                    while(cursor.moveToNext())
                    {
                        synchronized (Thread.currentThread()) {
                            try {
                                Thread.currentThread().wait(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        long temp_time=0;
                        try{
                            temp_time=get_regular_time_db(cursor.getString(cursor.getColumnIndex("Time")));
                        }
                        catch (Exception e)
                        {
                            continue;
                        }

                        Log.e("zjc_time", "run: "+temp_time+"  "+ time_now);
                        if(temp_time<time_now)
                        {
                            //Log.e("zjc_time", "run: "+cursor.getString(cursor.getColumnIndex("title")) );
                            try{
                                if(cursor.getString(cursor.getColumnIndex("state")).equals("未完成")) {
                                    Log.e("zjc_time", "run: "+cursor.getString(cursor.getColumnIndex("title")) );
                                    if(cursor.getInt(cursor.getColumnIndex("time_delay"))>0)
                                    {
                                        int temp_delay=cursor.getInt(cursor.getColumnIndex("time_delay"))-1000;
                                        if(temp_delay<0)
                                        {
                                            temp_delay=0;
                                        }
                                        todoDB.UpdateDelay(temp_delay,cursor.getInt(cursor.getColumnIndex("_id")));
                                    }
                                    else {
                                        send_notification(cursor.getString(cursor.getColumnIndex("title")));
                                        todoDB.UpdateDelay(cursor.getInt(cursor.getColumnIndex("time_delay"))+1000*60*30,cursor.getInt(cursor.getColumnIndex("_id")));
                                    }
                                }
                            }
                            catch (Exception e)
                            {

                            }

                        }
                    }
                }
            }).start();

        }
        catch (Exception e)
        {

        }
    }
}
