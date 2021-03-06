package com.example.mind_maker;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.example.mind_maker.Dao.NoteDB;
import com.example.mind_maker.Dao.TodoDB;
import com.example.mind_maker.Service.Todo_notification;
import com.example.mind_maker.record.Note;
import com.example.mind_maker.record.Note_Item;
import com.example.mind_maker.record.Note_adapter;
import com.example.mind_maker.record.Todo;
import com.example.mind_maker.record.Todo_adapter;
import com.example.mind_maker.util.todo_time;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener{
    private NoteDB notedb;
    private TodoDB todoDB;
    private List<Note> datas=new ArrayList<>();


    private static String url = "jdbc:postgresql://116.63.183.142:15432/postgres" + "?characterEncoding=utf8";//postgres???????????????????????????
    private static String user = "gaussdb";//PgSQL?????????
    private static String pass = "Secretpassword@123";//???????????????????????????
    private static Connection con;//??????
    private static Statement statement = null;//??????
    private static ResultSet resultSet = null;//????????????
    private PreparedStatement dbStat=null;

    private NotificationManager manager;
    private Notification notification;

    private TextView choose_time,todo_size;
    private ImageView search;
    private Spinner spinner,spinner_style,spinner_menu;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button button_note,button_todo,select_type_text;//?????????????????????
    public static List<Note> note_list=new ArrayList<Note>();
    public static List<Note> note_classification=new ArrayList<Note>();
    private List<Note> note_list_collection=new ArrayList<Note>();//?????????????????????
    private List<Todo> todo_list=new ArrayList<Todo>();
    private int select_type=3;
    private ImageView find,me,top;
    public static String UID;//??????UID ??????????????????,????????????????????????
    RecyclerView mRecyclerView;
    RecyclerView recyclerView;
    Note_adapter note_adapter;
    Todo_adapter todo_adapter;
    View tab01,tab02;//?????????TODO??????
    boolean isFirst;//??????spinner??????????????????????????????
    private ViewPager mViewPager;// ????????????????????????
    private List<View> mViews = new ArrayList<View>();// ????????????Tab01-02
    private PagerAdapter mPagerAdapter;// ?????????View?????????
    private TextView note_count;
    boolean flag_layout=false;//?????????????????????
    boolean flag_sort=true;//?????????????????????
    boolean isFirst_type=true;
    boolean database_connection=true;//???????????????????????????????????????true??????????????????false??????????????????
    public static int position=-1;
    public ImageView add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        //sendBroadcast(new Intent("finish"));
        Intent intent=getIntent();//???????????????activity???intent??????
        UID=intent.getStringExtra("UID");
        //test_radio_upload();//??????????????????????????????
        //init_notes();
        init();
        try {
            init_mViewPager();
            if(getIntent().getIntExtra("flag",0)!=1)
            {
                Log.e("zjc_local", "onCreate: ?????????" );
                init_todo();
                init_note();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        init_click();
        init_fresh();//????????????
        select_type=3;
        note_classification.clear();
        note_classification.addAll(Note_Classification(select_type));
        note_adapter.notifyDataSetChanged();
        note_count.post(new Runnable() {
            @Override
            public void run() {
                note_count.setText(note_classification.size()+"");
            }
        });

        select_type_text.setText("????????????");

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    get_note_list();
//                    Log.e("zjc", "run: "+"??????????????????");
//                } catch (SQLException throwables) {
//                    throwables.printStackTrace();
//                }
//            }
//        }).start();


    }

    private void test_radio_upload()//??????????????????
    {
        String sql="Insert Into Radio (radio_content) Value (?)";
        Log.e("zjc_cloud", "send_note_to_service: "+sql);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //??????JDBC??????
                    Class.forName("org.postgresql.Driver");
                    //????????????
                    con = DriverManager.getConnection(url, user, pass);
                    if (!con.isClosed()) {
                        System.out.println("?????????????????????");
                        File file=new File("");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        dbStat = con.prepareStatement(sql);
                        //Blob blob=new SerialBlob();
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        dbStat.setBinaryStream(1, inputStream,
                                file.length());
                        //dbStat.setInt(1, Integer.parseInt(UID));
                        //statement = con.createStatement();
                        //statement.execute(sql);
                        dbStat.execute();
                        con.close();
                        Log.e("zjc_cloud", "onCreate: ??????????????????");

                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("???????????????????????????");
                    Log.e("zjc", "onCreate: ??????????????????");
                    //Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("?????????????????????");
                    Log.e("zjc_cloud", "onCreate: ??????");
//                    Looper.prepare();
//                    Toast.makeText(RecordActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private void start_service()
    {
        Intent intent=new Intent();
        intent.setClass(RecordActivity.this,Todo_notification.class);
        startService(intent);
//        startService(intent);
//        startService(intent);
//        stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.list:
                Log.e("zjc", "onOptionsItemSelected: "+item.toString());
                break;
            case R.id.delete:
                Log.e("zjc", "onOptionsItemSelected: "+item.toString());
                break;
            case R.id.sort:
                Log.e("zjc", "onOptionsItemSelected: "+item.toString());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init_fresh() //??????
    {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //???????????????????????????
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (Thread.currentThread()) {
                            try {
                                Thread.currentThread().wait(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        Looper.prepare();
                        Toast.makeText(RecordActivity.this,"????????????",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();
            }
        });

    }
    private void set_click_adapter()
    {

    }


    private void init_click() //?????????????????????
    {
        button_todo.setOnClickListener(this);
        button_note.setOnClickListener(this);
        find.setOnClickListener(this);
        me.setOnClickListener(this);
        add.setOnClickListener(this);
        search.setOnClickListener(this);
    }

    private void send_notification(String Content)
    {
        Intent intent=new Intent();
        PendingIntent pending=PendingIntent.getActivity(this,0,intent,0);

        int id = (int) (System.currentTimeMillis() / 1000);
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);//NotificationManager???????????????????????????

        NotificationChannel notificationChannel = new NotificationChannel("AppTestNotificationId", "AppTestNotificationName", NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(notificationChannel);
        Notification notification= new Notification.Builder(this)
                .setContentTitle("??????????????????")
                .setContentText("This is content text")
                .setChannelId("AppTestNotificationId")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pending)//??????????????????
                .setSmallIcon(R.drawable.ic_launcher_foreground)//???????????????
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_background))//???????????????

                .build();
        notification.defaults = Notification.DEFAULT_SOUND;//????????????
        notification.defaults |= Notification.DEFAULT_VIBRATE;//??????
        notification.flags |= Notification.FLAG_AUTO_CANCEL; //??????????????????????????????????????????????????????
        notification.ledARGB= Color.GREEN;//???????????????led?????????
        notification.ledOnMS=1000;//????????????????????????
        notification.ledOffMS=1000;
        notification.flags=Notification.FLAG_SHOW_LIGHTS;
        manager.notify(id,notification);//??????NotificationManager???notify?????????????????????
    }


    private ArrayList<Note> Note_Classification(int style)//????????????
    {
        ArrayList<Note> temp=new ArrayList<Note>();
        switch(style)
        {
            case 1:
                for(Note note : note_list)
                {
                    if(note.getStyle().equals("????????????"))
                        temp.add(note);
                }
                break;
            case 2:
                for(Note note : note_list)
                {
                    if(note.getStyle().equals("???????????????"))
                        temp.add(note);
                }
                break;
            case 3:
                temp.addAll(note_list);
                break;
        }
        return temp;
    }

    private void init_spinner_menu()//???????????????
    {
        Resources res =getResources();
        String[] menu_style=res.getStringArray(R.array.option);//menu
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,menu_style);//??????Arrayadapter?????????
        spinner_menu=findViewById(R.id.spinner_menu);
        spinner_menu.setAdapter(adapter);
        spinner_menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text= spinner.getItemAtPosition(i).toString();
                //Toast.makeText(RecordActivity.this,text,Toast.LENGTH_SHORT).show();
                /*note_classification=Note_Classification(i);
                note_adapter.notifyDataSetChanged();*/
                switch(i)
                {
                    case 0:
                        flag_layout=!flag_layout;
                        Log.e("zjc", "onItemSelected: ??????");
                        //recyclerView=(RecyclerView) tab01.findViewById(R.id.recyclerView_1);
                        recyclerView.setLayoutManager(flag_layout?new LinearLayoutManager(tab01.getContext()):new GridLayoutManager(tab01.getContext(),2));
                        //????????????????????????item????????????????????????????????????????????????????????????
                        recyclerView.setHasFixedSize(true);
                        //???????????????Adapter
                        recyclerView.setAdapter(note_adapter);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        break;
                    case 1:
                        Log.e("zjc", "onItemSelected: ????????????");
                        break;
                    case 2:
                        Log.e("zjc", "onItemSelected: ????????????");
                        flag_sort=!flag_sort;
                        if(flag_sort)
                        {
                            note_classification.sort(Comparator.comparing(Note::getTime));//??????
                        }
                        else
                            note_classification.sort(Comparator.comparing(Note::getTime).reversed());//??????
                        //note_classification.sort(Comparator.comparing(Note::getTime));//??????
                        //note_classification.sort(Comparator.comparing(Note::getTime).reversed());//??????
                        note_adapter.notifyDataSetChanged();
                        break;
                    case 3:
                        Log.e("zjc", "onItemSelected: ??????");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_menu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    Class<?> clazz = AdapterView.class;
                    Field field = clazz.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);
                    field.setInt(spinner_menu, AdapterView.INVALID_POSITION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    private void init_note_style()//?????????????????????spanner????????????
    {
        Resources res =getResources();
        String[] note_style=res.getStringArray(R.array.option1);//????????????
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,note_style);//??????Arrayadapter?????????
        spinner_style=tab01.findViewById(R.id.spinner_style);
        spinner_style.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text= spinner.getItemAtPosition(i).toString();
                Toast.makeText(RecordActivity.this,text,Toast.LENGTH_SHORT).show();
                if(isFirst_type)
                {
                    isFirst_type=!isFirst_type;
                    return;
                }
                /*note_classification=Note_Classification(i);
                note_adapter.notifyDataSetChanged();*/
                switch(i)
                {
                    case 0:
                        select_type=0;
                        Log.e("zjc", "onItemSelected: ????????????");
                        note_count.post(new Runnable() {
                            @Override
                            public void run() {
                                note_count.setText(note_classification.size()+"");
                                select_type_text.setText("????????????");
                            }
                        });
                        break;
                    case 1:
                        select_type=1;
                        Log.e("zjc", "onItemSelected: ??????");
                        note_classification.clear();
                        note_classification.addAll(Note_Classification(i));
                        note_adapter.notifyDataSetChanged();
                        note_count.post(new Runnable() {
                            @Override
                            public void run() {
                                note_count.setText(note_classification.size()+"");
                                select_type_text.setText("??????");
                            }
                        });
                        break;
                    case 2:
                        select_type=2;
                        Log.e("zjc", "onItemSelected: ???????????????");
                        note_classification.clear();
                        note_classification.addAll(Note_Classification(i));
                        note_adapter.notifyDataSetChanged();
                        note_count.post(new Runnable() {
                            @Override
                            public void run() {
                                note_count.setText(note_classification.size()+"");
                                select_type_text.setText("???????????????");
                            }
                        });
                        break;
                    case 3:
                        select_type=3;
                        Log.e("zjc", "onItemSelected: ????????????");
                        note_classification.clear();
                        note_classification.addAll(Note_Classification(i));
                        note_adapter.notifyDataSetChanged();
                        note_count.post(new Runnable() {
                            @Override
                            public void run() {
                                note_count.setText(note_classification.size()+"");
                                select_type_text.setText("????????????");
                            }
                        });
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showPopupWindow(View view,int position)//????????????
    {
        //1.???????????????
        //2.??????
        //3.??????
        View v=LayoutInflater.from(this).inflate(R.layout.popupwindow_note_item,null);//????????????????????????????????????
        PopupWindow popupWindow=new PopupWindow(v,450,200,true);
        popupWindow.setTouchable(true);
        View parent=popupWindow.getContentView();
        ImageView delete,top,share_note;
        delete=parent.findViewById(R.id.delete);
        top=parent.findViewById(R.id.top);
        share_note=parent.findViewById(R.id.share_note);
        //popupWindow.showAsDropDown(view);
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = v.getMeasuredWidth();//????????????????????????
        int popupHeight = v.getMeasuredHeight();
        int[] location = new int[2];//???????????????????????????
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY,(location[0] + view.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);//???????????????No_Gravity??????center??????
        set_PopupWindow_click(delete,top,share_note,position,popupWindow);
    }
    private void set_PopupWindow_click(ImageView delete,ImageView top,ImageView share_note,int position,PopupWindow popupWindow)//???PopupWindow???????????????????????????
    {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("zjc", "onClick: "+view.toString());
                for(int i=0;i<note_list.size();i++)
                {
                    Log.e("zjc_delete", "onClick: "+note_list.get(i).getNote_id()+note_classification.get(position).getNote_id());
                    if(note_list.get(i).getNote_id()==(note_classification.get(position).getNote_id())) {
                        try {
                            notedb.DeleteData(note_list.get(i).getNote_id());
                            note_list.remove(i);//??????????????????????????????
                            Toast.makeText(RecordActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(RecordActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                        }


                    }
                }
                note_classification.remove(position);//?????????????????????
                note_adapter.notifyDataSetChanged();//????????????
                if(popupWindow.isShowing())//???????????????
                    popupWindow.dismiss();
            }
        });
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("zjc", "onClick: "+view.toString());
                for(int i=0;i<note_list.size();i++)
                {
                    if(note_list.get(i).equals(note_classification.get(position)))
                        note_list.remove(i);//??????????????????????????????
                }
                //note_classification.remove(position);//?????????????????????
                note_classification.add(0,note_classification.remove(position));
                note_classification.get(0).change_top();
                note_adapter.notifyDataSetChanged();
                if(popupWindow.isShowing())//???????????????
                    popupWindow.dismiss();
            }
        });
        share_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<note_list.size();i++)
                {
                    Log.e("zjc_delete", "onClick: "+note_list.get(i).getNote_id()+note_classification.get(position).getNote_id());
                    if(note_list.get(i).getNote_id()==(note_classification.get(position).getNote_id())) {
                        try {
                            share(RecordActivity.this,note_list.get(i).getContent());
                            //Toast.makeText(RecordActivity.this, "??????", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(RecordActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                        }


                    }
                }
                if(popupWindow.isShowing())//???????????????
                    popupWindow.dismiss();

            }
        });
    }

    @SuppressLint("Range")
    private void init_todo() {//????????????????????????
        Log.e("zjc_cloud", "init_todo: ");
        Cursor cursor = todoDB.QueryData();
        String title,time,state;
        long update_time;
        int id,service_id;
        //System.out.println(dateFormat.format(date));
        while (cursor.moveToNext()) {
            id=cursor.getInt(cursor.getColumnIndex("_id"));
            title=cursor.getString(cursor.getColumnIndex("title"));
            time = cursor.getString(cursor.getColumnIndex("Time"));
            state=cursor.getString(cursor.getColumnIndex("state"));
            update_time=cursor.getLong(cursor.getColumnIndex("update_time"));
            service_id = cursor.getInt(cursor.getColumnIndex("service_id"));
            todo_list.add(new Todo(id,title,time,update_time,state,service_id));
        }
        List<Todo> todos=new ArrayList<>();
        final CountDownLatch latch=new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    todos.addAll(get_todo_list());
                    latch.countDown();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }).start();
        try {
            latch.await();
            Log.e("zjc_cloud", "init_todo: "+"todolist_Size"+todo_list.size()+" todos:"+todos.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("zjc_cloud", "init_todo: "+database_connection);
        if(database_connection)
        {
            for(Todo todo : todo_list)
            {
                //Log.e("zjc_cloud", "init_note: "+ todo.getService_id());
                if(todo.getService_id()==-1)
                {
                    CountDownLatch latch1=new CountDownLatch(1);
                    send_todo_to_service(todo,latch1);
                    //latch1.countDown();
                    try {
                        latch1.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    CountDownLatch latch2=new CountDownLatch(1);
                    update_local_service_id(todo,latch2);
                    try {
                        latch2.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Log.e("zjc_cloud", "init_todo_?????????????????????id: "+todo.getService_id() );

                    //Log.e("zjc_cloud", "onCreate: ???????????????todo???????????????");
                }
                else
                {
                    Todo service_todo=get_todo_from_service(todo.getService_id());
                    //Log.e("zjc_cloud", "init_note: "+ service_todo.getName()+"??????????????????"+service_todo.getUpdate_time()+"???????????????"+todo.getUpdate_time());
                    if(service_todo.getUpdate_time()<todo.getUpdate_time())
                    {
                        update_todo_to_service(todo.getService_id(),todo);
                        //Log.e("zjc_cloud", "init_note:????????????????????????");
                    }
                    else if(service_todo.getUpdate_time()>todo.getUpdate_time())
                    {
                        update_local_todo(todo.getId(),service_todo);//??????????????????
                        //Log.e("zjc_cloud", "init_note:??????????????????????????????");
                    }
                }
            }
            for(Todo todo : todos)
            {
                //Log.e("zjc_cloud", "init_note: "+ todos.size());
                Cursor cursor1=todoDB.QueryData_id(todo.getService_id());
                int flag=0;
                int todo_id;

                while(cursor1.moveToNext())
                {
                    flag=1;
                    //todo_id=cursor1.getInt(cursor1.getColumnIndex(""))
                }
                if(flag==0)//????????????todo
                {
                    //get_todo_from_service(todo.);
                    todoDB.InsertData(todo.getName(),todo.getDate(),todo.getUpdate_time(),todo.getState(),0,todo.getService_id());
                    todo_list.add(new Todo(todo.getName(),todo.getDate()));
                    //Log.e("zjc_cloud", "init_todo: "+todo.getName());
                    todo_adapter.notifyDataSetChanged();
                    //Log.e("zjc_cloud", "onCreate: ????????????todo???????????????");
                }
            }
        }
    }

    private void update_local_service_id(Todo todo,CountDownLatch latch)//?????????????????????service_id
    {
        String sql_s="select * from todo where uid="+UID;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //??????JDBC??????
                    Class.forName("org.postgresql.Driver");
                    //????????????
                    con = DriverManager.getConnection(url, user, pass);
                    if (!con.isClosed()) {
                        System.out.println("?????????????????????");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        statement = con.createStatement();
                        //statement.execute(sql);

                        //statement.execute(sql);
                        //statement = con.createStatement();
                        ResultSet rs = statement.executeQuery(sql_s);
                        int service_id = -1;
//                        rs.last();
                        while(rs.next())
                        {
                            service_id=rs.getInt("id");
                        }
                        //service_id=rs.getInt("id");
                        todo.setService_id(service_id);
                        //dbStat.execute();
                        con.close();
                        todoDB.UpdateData_service_id(todo.getService_id(),todo.getId());
                        latch.countDown();
                        Log.e("zjc_cloud", "onCreate: todo????????????"+service_id+"??????id???"+todo.getId());

                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("???????????????????????????");
                    Log.e("zjc", "onCreate: ??????????????????");
                    //Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("?????????????????????");
                    Log.e("zjc_cloud", "onCreate: todo????????????");
//                    Looper.prepare();
//                    Toast.makeText(RecordActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private void send_todo_to_service(Todo todo,CountDownLatch latch) //????????????????????????????????????????????????todo
    {
        String sql="insert into todo(uid,title,time,update_time,state) values ("+UID+",\'"+todo.getName()+"\',\'"+todo.getDate()+"\',"+todo.getUpdate_time()+",\'"+todo.getState()+"\');";
        //Log.e("zjc_cloud", "send_note_to_service: "+sql);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //??????JDBC??????
                    Class.forName("org.postgresql.Driver");
                    //????????????
                    con = DriverManager.getConnection(url, user, pass);
                    if (!con.isClosed()) {
                        System.out.println("?????????????????????");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        statement = con.createStatement();
                        statement.execute(sql);
                        con.close();
                        latch.countDown();
                        Log.e("zjc_cloud", "run: latch-1" );
                        //todoDB.UpdateData_service_id(todo.getService_id(),todo.getId());
                        //Log.e("zjc_cloud", "onCreate: todo????????????"+service_id+"??????id???"+todo.getId());

                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("???????????????????????????");
                    Log.e("zjc", "onCreate: ??????????????????");
                    //Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("?????????????????????");
                    Log.e("zjc_cloud", "onCreate: todo????????????");
//                    Looper.prepare();
//                    Toast.makeText(RecordActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private void update_local_todo(int id, Todo service_todo) //???????????????????????????????????????????????????
    {
        todoDB.UpdateData(service_todo.getName(),service_todo.getDate(),id);//???????????????????????????????????????
    }

    private void update_todo_to_service(int service_id, Todo todo)//???????????????????????????????????????
    {
        String sql="update note set title=\'"+todo.getName()+"\',data=\'"+todo.getDate()+"\',update_time=\'"+todo.getUpdate_time()+"\',state="+todo.getState()+" where id="+service_id+";";//????????????
        //Log.e("zjc_cloud", "update_todo_to_service: "+sql);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //??????JDBC??????
                    Class.forName("org.postgresql.Driver");
                    //????????????
                    con = DriverManager.getConnection(url, user, pass);
                    if (!con.isClosed()) {
                        System.out.println("?????????????????????");
                        //Log.e("zjc_re", "onCreate: ?????????????????????");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        statement = con.createStatement();
                        statement.execute(sql);
                        con.close();
                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("???????????????????????????");
                    Log.e("zjc", "onCreate: ??????????????????");
                    //Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("?????????????????????");
                    Log.e("zjc_re", "onCreate: ?????????????????????");
//                    Looper.prepare();
//                    Toast.makeText(RecordActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private Todo get_todo_from_service(int service_id) {
        String sql="select * from todo where id="+service_id;
        final Todo[] result = {new Todo()};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //??????JDBC??????
                    Class.forName("org.postgresql.Driver");
                    //????????????
                    con = DriverManager.getConnection(url, user, pass);
                    if (!con.isClosed()) {
                        System.out.println("?????????????????????");
                        Log.e("zjc_re", "onCreate: ??????");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        statement = con.createStatement();
                        ResultSet rs = statement.executeQuery(sql);
                        String title;
                        String date;
                        long update_time;
                        String state;
                        long update_time_millis;
                        byte[] span=null;
                        //int note_id;
                        while(rs.next())
                        {
                            title = rs.getString("title");
                            date = rs.getString("date");
                            update_time=rs.getLong("update_time");
                            state=rs.getString("state");
                            //update_time_millis=rs.getLong("update_time_millis");//???????????????????????????
                            //span=rs.getBytes("span");
                            //note_id=rs.getInt("note_id");
                            Todo temp_result=new Todo(title,date,update_time,state);
                            result[0] =temp_result;
                        }
                        con.close();
                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("???????????????????????????");
                    Log.e("zjc", "onCreate: ??????????????????");
                    //Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("?????????????????????");
                    Log.e("zjc_re", "onCreate: ??????");
//                    Looper.prepare();
//                    Toast.makeText(RecordActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
        return result[0];
    }


    @SuppressLint("Range")
    private void init_note() {//????????????????????????
        Cursor cursor = notedb.QueryData();
        String temp,title;
        byte[] span_bytes;
        int id;
        int i=0;
        long time;
        int service_id;
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //System.out.println(dateFormat.format(date));
        List<Note_Item> client_temp=new ArrayList<>();

        while (cursor.moveToNext()) {
            time=cursor.getLong(cursor.getColumnIndex("update_time_millis"));
            title=cursor.getString(cursor.getColumnIndex("title"));
            temp = cursor.getString(cursor.getColumnIndex("content"));
            id=cursor.getInt(cursor.getColumnIndex("_id"));
            span_bytes=cursor.getBlob(cursor.getColumnIndex("restorespans"));
            service_id=cursor.getInt(cursor.getColumnIndex("service_id"));
            //time=cursor.getLong(cursor.getColumnIndex("time"));
            client_temp.add(new Note_Item(id,title,"??????",time,"????????????",temp,span_bytes,service_id));//???????????????????????????
        }
        final CountDownLatch latch=new CountDownLatch(1);
        List<Note_Item> service_temp=new ArrayList<>();
        //latch.countDown();//????????????
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    service_temp.addAll(get_note_list());//??????????????????????????????????????????service_temp
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                latch.countDown();//????????????
            }
        }).start();
        try {
            latch.await();
            try {
                Log.e("zjc_cloud", "init_note: " + client_temp.size() + "  " + service_temp.size());
                Log.e("zjc_cloud", "init_note: " + service_temp.get(0).getContent());
            }catch(Exception e)
            {

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        database_connection=true;
        if(database_connection)
        {
            for(Note_Item note_item : client_temp)
            {

                Log.e("zjc_cloud_note", "init_note: "+ note_item.getService_id());
                if(note_item.getService_id()==-1)
                {
                    CountDownLatch latch1=new CountDownLatch(1);
                    init_service_content(note_item);//???????????????????????????????????????
                    send_note_to_service(note_item,latch1);//????????????????????????????????????
                    try {
                        latch1.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    CountDownLatch latch2=new CountDownLatch(1);
                    update_local_note_service_id(note_item,latch2);//?????????????????????????????????????????????service_id
                    try {
                        latch2.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e("zjc_cloud", "onCreate: ????????????????????????????????????");
                    //continue;
                }
                else
                {
                    CountDownLatch latch1=new CountDownLatch(1);
                    Note_Item service_note_item = null;

                    get_note_from_service(note_item.getService_id(),latch1,note_item);
                    try {
                        latch1.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Log.e("zjc_cloud_note", "init_note: "+ service_note_item.getName()+"??????????????????"+service_note_item.getTime()+"???????????????"+note_item.getTime());


                }
                note_list.add(new Note(note_item.getName(),note_item.getContent(),note_item.getSpan_bytes(),note_item.getId(),note_item.getService_id()));
            }
            for(Note_Item note_item : service_temp)//?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            {
                //Log.e("zjc_cloud", "init_note: "+ todos.size());
                Cursor cursor1=notedb.QueryData_id(note_item.getService_id());
                int flag=0;
                int todo_id;

                while(cursor1.moveToNext())
                {
                    flag=1;
                    //todo_id=cursor1.getInt(cursor1.getColumnIndex(""))
                }
                if(flag==0)//????????????note
                {
                    notedb.InsertData(note_item.getName(),note_item.getContent(),note_item.getDate(),note_item.getTime(),note_item.getSpan_bytes(),note_item.getService_id());
                    Cursor cursor2=notedb.QueryData();
                    int n_id=-1;
                    while(cursor2.moveToNext())
                    {
                        n_id=cursor2.getInt(cursor2.getColumnIndex("_id"));
                    }
                    note_item.setId(n_id);
                    note_list.add(new Note(note_item.getName(),note_item.getContent(),note_item.getSpan_bytes(),note_item.getId(),note_item.getService_id()));
                    //Log.e("zjc_cloud", "init_todo: "+todo.getName());
                    todo_adapter.notifyDataSetChanged();
                    //Log.e("zjc_cloud", "onCreate: ????????????todo???????????????");
                }
            }
        }
        else
        {
            for(Note_Item note_item:client_temp) {//???????????????????????????????????????????????????
                note_list.add(new Note(note_item.getName(),note_item.getContent(),note_item.getSpan_bytes(),note_item.getId(),note_item.getService_id()));
            }
        }


    }

    private void update_local_note_service_id(Note_Item note_item,CountDownLatch latch)
    {
        String sql_s="select * from note where uid="+UID;//??????UID?????????????????????
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //??????JDBC??????
                    Class.forName("org.postgresql.Driver");
                    //????????????
                    con = DriverManager.getConnection(url, user, pass);
                    if (!con.isClosed()) {
                        System.out.println("?????????????????????");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        statement = con.createStatement();
                        //statement.execute(sql);

                        //statement.execute(sql);
                        //statement = con.createStatement();
                        ResultSet rs = statement.executeQuery(sql_s);
                        int service_id = -1;
//                        rs.last();
                        while(rs.next())
                        {
                            service_id=rs.getInt("note_id");
                        }
                        //service_id=rs.getInt("id");
                        note_item.setService_id(service_id);
                        //dbStat.execute();
                        con.close();
                        notedb.UpdateData_service_id(note_item.getService_id(),note_item.getId());
                        latch.countDown();
                        Log.e("zjc_cloud", "onCreate: todo????????????"+service_id+"??????id???"+note_item.getId());

                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("???????????????????????????");
                    Log.e("zjc", "onCreate: ??????????????????");
                    //Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("?????????????????????");
                    Log.e("zjc_cloud", "onCreate: todo????????????");
//                    Looper.prepare();
//                    Toast.makeText(RecordActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private void update_local_note(int id,Note_Item service_note_item) {
        notedb.UpdateData(service_note_item.getName(),service_note_item.getContent(),service_note_item.getDate(),service_note_item.getTime(),service_note_item.getSpan_bytes(),id);//???????????????????????????????????????
    }

    private void update_note_to_service(int _id,Note_Item note_item)//????????????id??????????????????
    {
        String sql="update note set title=\'"+note_item.getName()+"\',client_content=\'"+note_item.getContent()+"\',style=\'"+note_item.getStyle()+"\',update_time_millis="+note_item.getTime()+" where note_id="+_id+";";//????????????
        Log.e("zjc_cloud", "update_note_to_service: "+sql);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //??????JDBC??????
                    Class.forName("org.postgresql.Driver");
                    //????????????
                    con = DriverManager.getConnection(url, user, pass);
                    if (!con.isClosed()) {
                        System.out.println("?????????????????????");
                        Log.e("zjc_re", "onCreate: ?????????????????????");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        statement = con.createStatement();
                        statement.execute(sql);
                        con.close();
                        //latch.countDown();
                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("???????????????????????????");
                    Log.e("zjc", "onCreate: ??????????????????");
                    //Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("?????????????????????");
                    Log.e("zjc_re", "onCreate: ?????????????????????");
//                    Looper.prepare();
//                    Toast.makeText(RecordActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private Note_Item get_note_from_service(int _id,CountDownLatch latch,Note_Item note_item)//????????????id??????????????????
    {
        String sql="select * from note where note_id="+_id ;
        final Note_Item[] result = {new Note_Item()};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //??????JDBC??????
                    Class.forName("org.postgresql.Driver");
                    //????????????
                    con = DriverManager.getConnection(url, user, pass);
                    if (!con.isClosed()) {
                        System.out.println("?????????????????????");
                        Log.e("zjc_re", "onCreate: ??????");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        statement = con.createStatement();
                        ResultSet rs = statement.executeQuery(sql);
                        String title;
                        String content;
                        String create_time;
                        String style;
                        long update_time_millis;
                        byte[] span=null;
                        //int note_id;
                        while(rs.next())
                        {
                            title = rs.getString("title");
                            content = rs.getString("client_content");
                            create_time=rs.getString("create_time");
                            style=rs.getString("style");
                            update_time_millis=rs.getLong("update_time_millis");//???????????????????????????
                            span=rs.getBytes("span");
                            //note_id=rs.getInt("note_id");
                            Note_Item temp_result=new Note_Item(title,create_time,update_time_millis,style,content,span);
                            result[0] =temp_result;
                            Log.e("zjc_cloud_note", "run: "+result[0].getName());
                            //Note_Item note_item=(Note_Item)temp_result;
                        }


                        con.close();
                        Note_Item service_note_item=result[0];
                        if(service_note_item!=null) {

                            if (service_note_item.getTime() < note_item.getTime()) {
                                //CountDownLatch latch1=new CountDownLatch(1);
                                update_note_to_service(note_item.getService_id(), note_item);
                                Log.e("zjc_cloud", "init_note:????????????????????????");
                            } else if (service_note_item.getTime() > note_item.getTime()) {
                                update_local_note(note_item.getId(), service_note_item);//??????????????????
                                Log.e("zjc_cloud", "init_note:??????????????????????????????");
                                note_item.setName(service_note_item.getName());
                                note_item.setContent(service_note_item.getContent());
                                note_item.setSpan_bytes(service_note_item.getSpan_bytes());
                                note_item.setService_id(service_note_item.getId());
                                //note_list.add(new Note(service_note_item.getName(),service_note_item.getContent(),service_note_item.getSpan_bytes(),service_note_item.getId()));
                            }
                        }
                        latch.countDown();

                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("???????????????????????????");
                    Log.e("zjc", "onCreate: ??????????????????");
                    //Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("?????????????????????");
                    Log.e("zjc_re", "onCreate: ??????");
//                    Looper.prepare();
//                    Toast.makeText(RecordActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
        return note_item;
        //return result[0];
    }
    private void send_note_to_service(Note_Item note_item,CountDownLatch latch)
    {
//        String sql="insert into note(uid,title,client_content,style,create_time,update_time_millis,span) values ("+UID+",\'"+note_item.getName()+"\',\'"+note_item.getContent()+"\',\'"+note_item.getStyle()+",\'"+note_item.getDate()+"\',"+note_item.getTime()+",?);";

        StringBuilder service_content= new StringBuilder();
        for(Note temp : datas)
        {
            service_content.append(temp.getContent());
        }
        String sql="insert into note(uid,span,title,client_content,style,create_time,update_time_millis,service_content) values (?,?,\'"+note_item.getName()+"\',\'"+note_item.getContent().replace("\'","\"")+"\',\'"+note_item.getStyle()+"\',\'"+note_item.getDate()+"\',"+note_item.getTime()+",\'"+service_content.toString()+"\');";
        Log.e("zjc_cloud", "send_note_to_service: "+sql);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //??????JDBC??????
                    Class.forName("org.postgresql.Driver");
                    //????????????
                    con = DriverManager.getConnection(url, user, pass);
                    if (!con.isClosed()) {
                        Log.e("zjc_cloud", "onCreate: ?????????????????????");
                        System.out.println("?????????????????????");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        dbStat = con.prepareStatement(sql);
                        InputStream inputStream = new ByteArrayInputStream(note_item.getSpan_bytes());
                        dbStat.setBinaryStream(2, inputStream,
                                note_item.getSpan_bytes().length);
                        dbStat.setInt(1, Integer.parseInt(UID));
                        //statement = con.createStatement();
                        //statement.execute(sql);
                        dbStat.execute();
                        con.close();
                        latch.countDown();
                        Log.e("zjc_cloud", "onCreate: ???????????????????????????");
                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("???????????????????????????");
                    Log.e("zjc", "onCreate: ??????????????????");
                    //Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("?????????????????????");
                    Log.e("zjc_cloud", "onCreate: ??????");
//                    Looper.prepare();
//                    Toast.makeText(RecordActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }


    private void init_service_content(Note_Item note_item) //????????????????????????????????????
    {
        boolean flag=false;
        String Content=note_item.getContent();
        if(Content==null)
        {
            datas.add(new Note());
        }
        else {
            Pattern pattern = Pattern.compile("src=\"(.*?)\"/>");//????????????img???radio???video???????????????
            Pattern pattern_sub = Pattern.compile("\"(.*?)\"");//????????????''?????????
            //Log.e("zjc", "onClick: "+note_new_editText.getText().toString());
            Matcher matcher = pattern.matcher(Content);
            Matcher matcher_sub;
            String temp;
            int text_start=0,text_end=0;
            //Log.e("zjc_size", "init_datas: "+span_bytes.length );
            boolean img_flag=true;
            while (matcher.find())//matcher.find()???????????????????????????????????????????????????true
            {
                flag=true;
                temp = Content.substring(matcher.start() - 7, matcher.end());
                Log.e("zjc", "onClick: " + temp);
                matcher_sub = pattern_sub.matcher(temp);
                if(matcher.start()-6>0&&matcher.start()-7!=text_start && img_flag)
                {
                    Log.e("zjc_text", "save_note: " + Content.substring(text_start, matcher.start()-5));
                    if(Content.substring(matcher.start() - 6, matcher.start() - 5).equals("<r")||Content.substring(matcher.start() - 6, matcher.start() - 5).equals("<v"))
                    {
                        datas.add(new Note(" ","1",Content.substring(text_start, matcher.start() - 7),1,"????????????",0,null,-1));
                    }
                    else
                        datas.add(new Note(" ","1",Content.substring(text_start, matcher.start() - 5),1,"????????????",0,null,-1));
                    text_start=matcher.end();
                }
                else if(matcher.start()-6>0&&matcher.start()-7!=text_start && !img_flag)//?????????????????????????????????????????????
                {
                    if(Content.substring(matcher.start() - 6, matcher.start() - 5).equals("<r")||Content.substring(matcher.start() - 6, matcher.start() - 5).equals("<v"))
                    {
                        datas.get(datas.size()-1).setContent(datas.get(datas.size()-1).getContent()+Content.substring(text_start, matcher.start() - 7));
                    }
                    else
                        datas.get(datas.size()-1).setContent(datas.get(datas.size()-1).getContent()+Content.substring(text_start, matcher.start() - 5));

                }
                img_flag=false;
                while (matcher_sub.find()) {
                    if(Content.substring(matcher.start()-4, matcher.start()-1).equals("img")) {
                        //Log.e("zjc", "save_note: " + Content.substring(matcher.start()-6, matcher.start()-2));
                        text_start=matcher.end();
                        if (!img_flag) {
                            Log.e("zjc_text", "save_note: " + datas.get(datas.size()-1).getContent()+"<img src="+temp.substring(matcher_sub.start(), matcher_sub.end())+"/>");
                            datas.get(datas.size()-1).setContent(datas.get(datas.size()-1).getContent()+"<img src="+temp.substring(matcher_sub.start(), matcher_sub.end())+"/>");
                        } else {
                            datas.add(new Note("?????????.amr","1","<img src="+temp.substring(matcher_sub.start(), matcher_sub.end())+"/>",1,"????????????",0,-1));
                        }
                    }
                    if(Content.substring(matcher.start()-6, matcher.start()-1).equals("radio")) {
                        img_flag=true;
                        Log.e("zjc", "save_note: " + Content.substring(matcher.start()-6, matcher.start()-2));
                        text_start=matcher.end();
                        datas.add(new Note("?????????.amr","1",temp.substring(matcher_sub.start() + 1, matcher_sub.end() - 1),1,"????????????",1,-1));
                    }
                    if(Content.substring(matcher.start()-6, matcher.start()-1).equals("video")) {
                        img_flag=true;
                        //Log.e("zjc", "save_note: " + temp.substring(matcher_sub.start() + 1, matcher_sub.end() - 1));
                        text_start=matcher.end();
                        Log.e("zjc", "save_note: " + temp);
                        Log.e("zjc", "save_note: " + temp.substring(matcher_sub.start()+1,matcher_sub.end()-1));
                        File file=new File(temp.substring(matcher_sub.start()+1,matcher_sub.end()-1));
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(file);//???????????????????????????
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        byte[] byt = null;
                        try {
                            byt = new byte[inputStream.available()];
                            inputStream.read(byt);//????????????????????????
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        CountDownLatch latch=new CountDownLatch(1);
                        send_video_to_service(UID,file,latch);
                        CountDownLatch latch1=new CountDownLatch(1);
                        get_video_id (UID,latch1,byt);

                        //Log.e("zjc", "save_note: " + temp.substring(matcher_sub.start(), matcher_sub.end()));
                    }
                }
            }
            if(!flag)
            {
//                datas.add(new Note(" ","1",Content,1,"????????????",0,span_bytes,note_id));
            }
            else
            {
                if(!img_flag)
                {
                    datas.get(datas.size()-1).setContent(datas.get(datas.size()-1).getContent()+Content.substring(text_start, Content.length()));
                }
//                else
//                    datas.add(new Note(" ","1",Content.substring(text_start, Content.length()),1,"????????????",0,span_bytes,note_id));
            }
        }
       //typeRecyclerAdapter1.notifyDataSetChanged();
    }

    private int get_video_id(String uid,CountDownLatch latch,byte[] byt) {
            String sql="select * from video where uid="+uid;
        final int[] video_id = {-1};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //??????JDBC??????
                    Class.forName("org.postgresql.Driver");
                    //????????????
                    con = DriverManager.getConnection(url, user, pass);
                    if (!con.isClosed()) {
                        System.out.println("?????????????????????");
                        Log.e("zjc_re", "onCreate: ??????");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        statement = con.createStatement();
                        ResultSet rs = statement.executeQuery(sql);
                        while(rs.next())
                        {
                            video_id[0] =rs.getInt("video_id");
                            //Log.e("zjc_cloud_note", "run: "+result[0].getName());
                            //Note_Item note_item=(Note_Item)temp_result;
                        }


                        con.close();
                        datas.add(new Note("?????????.mp4","1","<video src=\""+video_id[0]+"\"/>",1,"????????????",2,byt,-1));
                        //Note_Item service_note_item=result[0];
                        latch.countDown();

                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("???????????????????????????");
                    Log.e("zjc", "onCreate: ??????????????????");
                    //Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("?????????????????????");
                    Log.e("zjc_re", "onCreate: ??????");
//                    Looper.prepare();
//                    Toast.makeText(RecordActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
        return video_id[0];
    }

    private void send_video_to_service(String uid, File file, CountDownLatch latch) {
        String sql="insert into video(uid,video_content) values ("+uid+",?);";
        Log.e("zjc_cloud", "send_video_to_service: "+sql);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //??????JDBC??????
                    Class.forName("org.postgresql.Driver");
                    //????????????
                    con = DriverManager.getConnection(url, user, pass);
                    if (!con.isClosed()) {
                        System.out.println("?????????????????????");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        dbStat = con.prepareStatement(sql);
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(
                                    file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        dbStat.setBinaryStream(1, inputStream,file.length());
                        //dbStat.setInt(1, Integer.parseInt(UID));
                        //statement = con.createStatement();
                        //statement.execute(sql);
                        dbStat.execute();
                        con.close();
                        latch.countDown();
                        Log.e("zjc_cloud", "onCreate: ????????????????????????????????????");

                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("???????????????????????????");
                    Log.e("zjc", "onCreate: ??????????????????");
                    //Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("?????????????????????");
                    Log.e("zjc_cloud", "onCreate: ??????");
//                    Looper.prepare();
//                    Toast.makeText(RecordActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }


    private void init()
    {

        search=findViewById(R.id.search);
        notedb=new NoteDB(this);
        todoDB=new TodoDB(this);
        spinner_menu=findViewById(R.id.spinner_menu);
        init_spinner_menu();
        swipeRefreshLayout=findViewById(R.id.refresh_layout);
        button_note=findViewById(R.id.button_note);
        button_todo=findViewById(R.id.button_todo);
        mViewPager=findViewById(R.id.viewpager_note_todo);
        LayoutInflater mLayoutInflater = LayoutInflater.from(this);
        tab01 = mLayoutInflater.inflate(R.layout.note, null);//??????????????????
        tab02 = mLayoutInflater.inflate(R.layout.todo, null);//??????todo??????
        mViews.add(tab01);
        mViews.add(tab02);
        select_type_text=tab01.findViewById(R.id.select_type_text);
        todo_size=tab02.findViewById(R.id.todo_size);
        isFirst=true;
        find=findViewById(R.id.find);
        me=findViewById(R.id.me);
        Resources res =getResources();
        String[] city=res.getStringArray(R.array.option2);//???province????????????????????????city???
        String[] note_style=res.getStringArray(R.array.option1);//????????????
        //spinner = (Spinner) findViewById(R.id.spacer1);//?????????spacer1
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,city);//??????Arrayadapter?????????
        note_count=tab01.findViewById(R.id.note_count);
        spinner=tab01.findViewById(R.id.spinner);
        add=tab02.findViewById(R.id.add0);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {//????????????????????????????????????????????????
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isFirst) {
                    isFirst = false;
                    return;
                }
                String text= spinner.getItemAtPosition(i).toString();
                Log.e(TAG, "onItemSelected: "+text);
                //Toast.makeText(RecordActivity.this,text,Toast.LENGTH_SHORT).show();
                if(i==0)
                {
                    Date dt;
                    dt = new Date();
                    String str_time = dt.toLocaleString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm E");
                    str_time = sdf.format(dt);
                    Intent intent=new Intent();
                    intent.setClass(RecordActivity.this,EditActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityForResult(intent,25);
                    //Log.e("zjc_edit", "OnItemClick: ??????EditActivity ??????");
                    //startActivity(intent);
                }
                else if(i==3)
                {
                    ShareActivity.multi_flag=true;//???????????????
                    Intent intent=new Intent();
                    intent.setClass(RecordActivity.this,ShareActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("UID",UID);
                    startActivityForResult(intent,25);
                }
                else
                {
                    Toast.makeText(RecordActivity.this,text,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    Class<?> clazz = AdapterView.class;
                    Field field = clazz.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);
                    field.setInt(spinner, AdapterView.INVALID_POSITION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });


        todo_size.setText(""+todo_list.size());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("zjc_edit", "onActivityResult: Edit??????" +resultCode);
        if(resultCode==26&&data!=null)
        {

            Log.e("zjc_edit", "onActivityResult: Edit??????" +data.getExtras().getString("time"));
            choose_time.setText(data.getExtras().getString("time"));
        }
        if(resultCode==25)//?????????editactivity????????????
        {
            Log.e("zjc_edit", "onActivityResult: Edit??????" );
            if(EditActivity.m_change_flag)
            {
                EditActivity.m_change_flag=!EditActivity.m_change_flag;
                //select_type=1;
                Log.e("zjc", "onItemSelected: ??????");
                note_classification.clear();
                note_classification.addAll(Note_Classification(select_type));
                note_adapter.notifyDataSetChanged();
                note_count.post(new Runnable() {
                    @Override
                    public void run() {
                        note_count.setText(note_classification.size()+"");

                    }
                });
            }
        }
    }

    private void init_mViewPager() throws IOException, ClassNotFoundException//?????????viewpager??????????????????
    {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Log.e("??????", "???????????????" + position);
                if(position==0)
                {
                    button_todo.setTextColor(Color.parseColor("#000000"));
                    button_note.setTextColor(Color.parseColor("#ffffff"));
                }
                if(position==1)
                {
                    button_todo.setTextColor(Color.parseColor("#ffffff"));
                    button_note.setTextColor(Color.parseColor("#000000"));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPagerAdapter = new PagerAdapter() {

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(mViews.get(position));

            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mViews.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {

                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return mViews.size();
            }

        };
        mViewPager.setAdapter(mPagerAdapter);
        Note fl=new Note();
        /*for(int i=0;i<25;i++)
            note_list.add(fl);*/
        //Todo todo=new Todo();
        for(int i=0;i<0;i++)
        {
            Todo todo=new Todo("JAVA??????","??????7:00");
            todo_list.add(todo);
        }
        Note temp;
        int i=0;
        for(Note note : note_list)
        {
            i++;
            temp=new Note(note.getName(),note.getData(),note.getContent(),(int) System.currentTimeMillis()+i,note.getStyle(),note.getContent_style(),note.getSpan_bytes(),note.getNote_id());
            note_classification.add(temp);
        }
        note_adapter=new Note_adapter(tab01.getContext(),note_classification);
        todo_adapter=new Todo_adapter(tab02.getContext(),todo_list);
        //tab01
        recyclerView=(RecyclerView) tab01.findViewById(R.id.recyclerView_1);

        recyclerView.setLayoutManager(new GridLayoutManager(tab01.getContext(),2));

        //????????????????????????item????????????????????????????????????????????????????????????

        recyclerView.setHasFixedSize(true);

        //???????????????Adapter

        recyclerView.setAdapter(note_adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setEventListener();
        //tab02
        RecyclerView tab02_recyclerView=(RecyclerView) tab02.findViewById(R.id.recyclerView_2);
        tab02_recyclerView.setLayoutManager(new LinearLayoutManager(tab02.getContext()));

        //????????????????????????item????????????????????????????????????????????????????????????

        tab02_recyclerView.setHasFixedSize(true);

        //???????????????Adapter

        tab02_recyclerView.setAdapter(todo_adapter);

        tab02_recyclerView.setItemAnimator(new DefaultItemAnimator());
        init_note_style();
    }

    private void setEventListener()//??????item??????????????????
    {
        note_adapter.setOnItemLongClickListener(new Note_adapter.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(View v, int position) {
                Log.e("zjc", "OnItemLongClick: "+position );
                showPopupWindow(v,position);
                /*for(int i=0;i<note_list.size();i++)
                {
                    if(note_list.get(i).equals(note_classification.get(position)))
                        note_list.remove(i);//??????????????????????????????
                }
                note_classification.remove(position);//?????????????????????
                note_adapter.notifyDataSetChanged();//????????????*/

            }
        });
        note_adapter.setOnItemClickListener(new Note_adapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                Note note=note_classification.get(position);
                Log.e("zjc", "onTouchEvent:??????" );
                Intent intent=new Intent();
                intent.setClass(RecordActivity.this, EditActivity.class);
                intent.putExtra("content",note.getContent());
                intent.putExtra("name",note.getName());
                intent.putExtra("note_id",note.getNote_id());
                intent.putExtra("span_bytes",note.getSpan_bytes());
                intent.putExtra("service_id",note.getService_ID());
                //mContext.startAc
                Log.e("zjc_edit", "OnItemClick: ??????EditActivity");
                startActivityForResult(intent,25);
                //startActivity(intent);
            }
        });
    }


    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        switch(view.getId())
        {
            case R.id.find:
                intent.setClass(RecordActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.me:
                intent.setClass(RecordActivity.this,MeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.button_note:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.button_todo:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.add0:
                Window(view);
                break;
            case R.id.search:
                intent.setClass(RecordActivity.this,SearchActivity.class);
                startActivity(intent);

                break;
        }
    }


    private List<Note_Item> get_note_list() throws SQLException {
        List<Note_Item> result=new ArrayList<>();
        try {
            //??????JDBC??????
            Class.forName("org.postgresql.Driver");
            //????????????
            con = DriverManager.getConnection(url, user, pass);
            if (!con.isClosed()) {
                System.out.println("?????????????????????");
                Log.e("zjc_re", "onCreate: ????????????");
                String query = "SELECT note_id,title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                statement = con.createStatement();
                ResultSet rs = statement.executeQuery(query);
                int service_id=-1;
                String title;
                String content;
                String create_time;
                String style;
                long update_time_millis;
                byte[] span=null;
                //int note_id;
                while(rs.next())
                {
                    service_id=rs.getInt("note_id");
                    title = rs.getString("title");
                    content = rs.getString("client_content");
                    create_time=rs.getString("create_time");
                    style=rs.getString("style");
                    update_time_millis=rs.getLong("update_time_millis");//???????????????????????????
                    span=rs.getBytes("span");
                    //note_id=rs.getInt("note_id");
                    result.add(new Note_Item(title,create_time,update_time_millis,style,content,span,service_id));
                }
                con.close();
            }

        } catch (ClassNotFoundException e) {
            System.out.println("???????????????????????????");
            Log.e("zjc", "onCreate: ??????????????????");
            //Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();

        } catch (SQLException e) {
            e.printStackTrace();
            database_connection=false;
            System.out.println("?????????????????????");
            Log.e("zjc_re", "onCreate: ????????????");
//            Looper.prepare();
//            Toast.makeText(RecordActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
//            Looper.loop();
            //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
        }
        return result;
    }


    private List<Todo> get_todo_list() throws SQLException {
        List<Todo> result=new ArrayList<>();
        try {
            //??????JDBC??????
            Class.forName("org.postgresql.Driver");
            //????????????
            con = DriverManager.getConnection(url, user, pass);
            if (!con.isClosed()) {
                System.out.println("?????????????????????");
                Log.e("zjc_re", "onCreate: todo????????????");
                String query = "SELECT title,time,update_time,state,id FROM todo WHERE uid = "+UID;//??????????????????????????????????????????????????????
                statement = con.createStatement();
                ResultSet rs = statement.executeQuery(query);
                String title;
                String time;
                String state;
                int service_id;
                long update_time;
                //byte[] span=null;
                //int note_id;
                while(rs.next())
                {
                    title = rs.getString("title");
                    time = rs.getString("time");
                    state=rs.getString("state");
                    service_id=rs.getInt("id");
                    update_time=rs.getLong("update_time");//???????????????????????????
                    //span=rs.getBytes("span");
                    //note_id=rs.getInt("note_id");
                    result.add(new Todo(title,time,update_time,state,service_id));
                }
                database_connection=true;
                con.close();
            }

        } catch (ClassNotFoundException e) {
            System.out.println("???????????????????????????");
            Log.e("zjc", "onCreate: ??????????????????");
            //Toast.makeText(MainActivity.this, "?????????", Toast.LENGTH_SHORT).show();

        } catch (SQLException e) {
            e.printStackTrace();
            database_connection=false;
            System.out.println("?????????????????????");
            Log.e("zjc_re", "onCreate: todo??????");
//            Looper.prepare();
//            Toast.makeText(RecordActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
//            Looper.loop();
            //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
        }
        return result;
    }



    private void Window(View view )//????????????
    {
        //1.???????????????
        //2.??????
        //3.??????
        View v=LayoutInflater.from(this).inflate(R.layout.todo_add,null);//????????????????????????????????????
        PopupWindow popupWindow=new PopupWindow(v, RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setTouchable(true);
        View parent=popupWindow.getContentView();
        //TextView choose_time;
        EditText name;
        Button save;
        ImageButton time;
        choose_time=parent.findViewById(R.id.choose_time);//???????????????
        save=parent.findViewById(R.id.todo_save);//??????????????????
        name=parent.findViewById(R.id.todo_name0);//??????????????????
        time=parent.findViewById(R.id.todo_time0);//??????????????????
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = v.getMeasuredWidth();//????????????????????????
        int popupHeight = v.getMeasuredHeight();
        int[] location = new int[2];//???????????????????????????
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        //popupWindow.showAtLocation(v, Gravity.NO_GRAVITY,(location[0] + view.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);//???????????????No_Gravity??????center??????
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RecordActivity.this, todo_time.class);//
                startActivityForResult(intent,25);
                // startActivity(intent);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                todo_list.add(new Todo(name.getText().toString(),choose_time.getText().toString().trim(),System.currentTimeMillis(),"?????????",-1));
                todoDB.InsertData(name.getText().toString(),choose_time.getText().toString().trim(),System.currentTimeMillis(),"?????????",0,-1);
                Cursor cursor= todoDB.QueryData();
                while(cursor.moveToNext())
                {
                    Log.e("zjc_text", "onClick: "+cursor.getString(cursor.getColumnIndex("_id")));
                }
                todo_adapter.notifyDataSetChanged();
                todo_size.setText(""+todo_list.size());
                //get_regular_time(choose_time.getText().toString().trim());
                popupWindow.dismiss();
            }
        });
        if(popupWindow.isShowing())
            Toast.makeText(RecordActivity.this,"??????",Toast.LENGTH_SHORT).show();
    }

    private int[] get_regular_time(String time)
    {
        time=time.trim();
        String regex="([1-9]\\d*\\.?\\d+)|(0\\.\\d*[1-9])|(\\d+)";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(time);
        int [] result=new int[5];
        int i=0;
        while(matcher.find()) {
            result[i]=Integer.parseInt(matcher.group());
            i++;
            Log.e("zjc_text", "get_regular_time: "+matcher.group());
        }
        return result;
    }

    /**
     * ??????activity?????????
     */
    private BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //??????????????????notification???service
            Intent intentService = new Intent(RecordActivity.this, Todo_notification.class);
            startService(intentService);
            //?????????????????????
            unregisterReceiver(mFinishReceiver);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //????????????
        sendBroadcast(new Intent("finish"));
    }
    public void share(Context context,String text){

        Intent sendIntent = new Intent();

        sendIntent.setAction(Intent.ACTION_SEND);

        sendIntent.putExtra(Intent.EXTRA_TEXT, text);

        sendIntent.setType("text/plain");

        context.startActivity(sendIntent);

    }

}

