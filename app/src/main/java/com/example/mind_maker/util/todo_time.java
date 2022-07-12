package com.example.mind_maker.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.mind_maker.R;

import java.util.Calendar;

public class todo_time extends Activity {
    //定义五个当前时间的变量
    private int year ;
    private int month ;
    private int day ;
    private int hour ;
    private int minute ;
    private String time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_timechoose);
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        //获取当前日期/时间
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        //为DatePicker添加监听事件
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                todo_time.this.year = year;
                todo_time.this.month =monthOfYear ;
                todo_time.this.day = dayOfMonth;
                //显示用户选择的 日期 和 时间
                showDate(year,month,day,hour,minute);
            }
        });
        //TimePicker选择监听器
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                todo_time.this.hour = hourOfDay;
                todo_time.this.minute = minute;
                showDate(year,month,day,hour,minute);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.e("zjc_time", "onBackPressed: "+time );
        Intent intent=new Intent();
        intent.putExtra("time",time);
        todo_time.this.setResult(26,intent);
        todo_time.this.finish();
    }

    private void showDate(int year , int month , int day , int hour , int minute ){
        TextView textView = (TextView) findViewById(R.id.show);
        textView.setText("您选择的时间为：" + year+"年 " + (month+1)+"月 " + day+"日 " + hour +"时 " + minute +"分");
        time=year+"年 " + (month+1)+"月 " + day+"日 " + hour +"时 " + minute +"分";
    }
}