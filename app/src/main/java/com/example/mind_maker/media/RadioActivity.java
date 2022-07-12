package com.example.mind_maker.media;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import com.example.mind_maker.R;

public class RadioActivity extends AppCompatActivity {
    private static final String TAG =" ";
    private SeekBar bar;//播放进度条
    private Timer timer;//定时器
    private boolean isSeekbarChaning;//互斥变量，防止进度条和定时器冲突。
    private Button btn_control;
    private Button btn_end;
    private Button btn_play;
    private Button vid_control;
    private Button vid_end;

    private ImageView tv_play;
    private TextView tv_start;
    //private TextView tv_duration;
    private TextView tv_time;
    private TextView tv_end;
    private Button btn_play_stop;

    private MediaRecorder mr = null;
    private MediaPlayer mPlayer=null;

    android.graphics.Camera camera;
    private MediaRecorder mMediaRecorder=null;

    int flag=1;
    boolean ispause=true;
    final String yuyin="yuyin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isGrantExternalRW(RadioActivity.this)){
            return;
        }else{}
        bar=(SeekBar) findViewById(R.id.pb);
        tv_start=(TextView)findViewById(R.id.tv_start);
        tv_end=(TextView)findViewById(R.id.tv_end);
        //tv_duration=(TextView)findViewById(R.id.tv_duration);
        tv_time=(TextView)findViewById(R.id.tv_time);
        tv_play=(ImageView)findViewById(R.id.tv_play) ;
        btn_control = (Button) findViewById(R.id.btn_control);
        btn_end=(Button)findViewById(R.id.btn_end);
        btn_play=(Button)findViewById(R.id.btn_play);
        btn_play_stop=(Button)findViewById(R.id.btn_play_stop);
        vid_control=(Button) findViewById(R.id.vid_control);
        vid_end=(Button) findViewById(R.id.vid_end);

//        /* 创建一个MySQLiteOpenHelper，该语句执行是不会创建或打开连接的 */
//        helper = new MySQLiteOpenHelper(MainActivity.this, "mydb.db", null, 1);
//        /* 调用MySQLiteOpenHelper的getWriteableDatabase()方法，创建或者打开一个连接 */
//        SQLiteDatabase sqlitedatabase = helper.getWritableDatabase();
//        Toast.makeText(MainActivity.this, "数据库创建成功", Toast.LENGTH_LONG).show();
//        /* 获取一个可写的SQLiteDatabase对象,创建或打开连接 */
//        SQLiteDatabase sqliteDatabase = helper.getWritableDatabase();
//        sqliteDatabase.execSQL("create table student(id INTEGER PRIMARY KEY autoincrement,name text);");
//        sqliteDatabase.execSQL("create table "+yuyin);
//        Toast.makeText(MainActivity.this, "数据表创建成功", Toast.LENGTH_LONG).show();


        btn_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_control.setText("正在录制");
                startRecord();
            }
        });

        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecord();//录制完毕
                btn_control.setText("录制完成");
//                SQLiteDatabase sqliteDatabase = helper.getWritableDatabase();

                mPlayer = new MediaPlayer();
                File path= new File(getCacheDir().getAbsolutePath());
                if(!path.exists()){
                    path.mkdir();
                }
                File file=new File(path,"luyin.amr");
                try {
                    mPlayer.setDataSource(String.valueOf(file));
                    mPlayer.prepare();//让MediaPlayer进入到准备状态
                } catch (IOException e) {
                    //Log.e(LOG_TAG, "播放失败");
                    btn_play.setText("over");
                }
                int duration = mPlayer.getDuration() / 1000;//获取音乐总时长
                tv_end.setText(calculateTime(duration));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// HH:mm:ss
                Date date = new Date(System.currentTimeMillis());
                tv_time.setText(simpleDateFormat.format(date));
            }
        });



        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (flag) {
                    case 0:
                        mPlayer = new MediaPlayer();
                        File path1 = new File(getCacheDir().getAbsolutePath());
                        if (!path1.exists()) {
                            path1.mkdir();
                        }

                        File file = new File(path1, "luyin.amr");
                        try {
                            mPlayer.setDataSource(String.valueOf(file));
                            mPlayer.prepare();
                            mPlayer.start();
                        } catch (IOException e) {
                            //Log.e(LOG_TAG, "播放失败");
                            btn_play.setText("over");
                        }
                        break;
                    case 1:

                }
            }
        });

        btn_play_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.release();
                mPlayer = null;
            }
        });

        tv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (flag) {
                    case 1:
                        flag=0;
                        mPlayer = new MediaPlayer();
                        File path = new File(getCacheDir().getAbsolutePath());
                        if (!path.exists()) {
                            path.mkdir();
                        }

                        File file = new File(path, "luyin.amr");
                        try {
                            mPlayer.setDataSource(String.valueOf(file));
                            mPlayer.prepare();
                            mPlayer.start();
                            int duration = mPlayer.getDuration();//获取音乐总时间
                            bar.setMax(duration);//将音乐总时间设置为Seekbar的最大值
                            timer = new Timer();//时间监听器
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (!isSeekbarChaning) {
                                        bar.setProgress(mPlayer.getCurrentPosition());
                                    }
                                }
                            }, 0, 50);

                        } catch (IOException e) {
                            Log.e("hhhhh", "播放失败");
                            btn_play.setText("over");
                        }
                        flag=0;
                        tv_play.setBackground(null);
                        tv_play.setBackground(getResources().getDrawable(R.drawable.pause));
                        break;
                    case 0:
                        if(mPlayer.isPlaying()){
                            mPlayer.pause();//暂停播放
                            //mPlayer.seekTo(bar.getProgress());//在当前位置播放
                            tv_start.setText(calculateTime(mPlayer.getCurrentPosition() / 1000));
                        }
                        tv_play.setBackground(null);
                        tv_play.setBackground(getResources().getDrawable(R.drawable.play));
                        flag=1;
                        break;
                }
            }
        });

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int duration2 = mPlayer.getDuration() / 1000;//获取音乐总时长
                int position = mPlayer.getCurrentPosition();//获取当前播放的位置
                tv_start.setText(calculateTime(position / 1000));//开始时间
                tv_end.setText(calculateTime(duration2));//总时长

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekbarChaning = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekbarChaning = false;
                mPlayer.seekTo(seekBar.getProgress());//在当前位置播放
                tv_start.setText(calculateTime(mPlayer.getCurrentPosition() / 1000));
            }
        });

    }
    //播放录音初始化
    public void inital_luyin()
    {
        flag=0;
        mPlayer = new MediaPlayer();
        File path = new File(getCacheDir().getAbsolutePath());
        if (!path.exists()) {
            path.mkdir();
        }

        File file = new File(path, "luyin.amr");
        try {
            mPlayer.setDataSource(String.valueOf(file));
            mPlayer.prepare();
            mPlayer.start();
            int duration = mPlayer.getDuration();//获取音乐总时间
            bar.setMax(duration);//将音乐总时间设置为Seekbar的最大值
            timer = new Timer();//时间监听器
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!isSeekbarChaning) {
                        bar.setProgress(mPlayer.getCurrentPosition());
                    }
                }
            }, 0, 50);

        } catch (IOException e) {
            Log.e("hhhhh", "播放失败");
            btn_play.setText("over");
        }
    }

    //动态申请权限
    public static boolean   isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            }, 1);

            return false;
        }

        return true;

    }

    //开始录制
    private void startRecord(){
        if (mr==null){
            File path= new File(getCacheDir().getAbsolutePath());
            if(!path.exists()){
                path.mkdir();
            }

            File file=new File(path,"luyin.amr");
            if (!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            mr =new MediaRecorder();
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);//输入源通过话筒录音；
            mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//输出格式
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);//音频编码
            mr.setOutputFile(file.getAbsolutePath());//设置写出文件；
            Toast.makeText(this, "\"录音文件已保存至:"+path, Toast.LENGTH_SHORT).show();
            try {
                mr.prepare();
                mr.start();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    //停止录制，资源释放
    private void stopRecord() {
        if (mr != null) {
            mr.stop();
            mr.release();
            mr = null;
        }
    }

    //计算播放时间
    public String calculateTime(int time) {
        int minute;
        int second;
        if (time >= 60) {
            minute = time / 60;
            second = time % 60;
            //分钟在0~9
            if (minute < 10) {
                //判断秒
                if (second < 10) {
                    return "0" + minute + ":" + "0" + second;
                } else {
                    return "0" + minute + ":" + second;
                }
            } else {
                //分钟大于10再判断秒
                if (second < 10) {
                    return minute + ":" + "0" + second;
                } else {
                    return minute + ":" + second;
                }
            }
        } else {
            second = time;
            if (second >= 0 && second < 10) {
                return "00:" + "0" + second;
            } else {
                return "00:" + second;
            }
        }
    }

}