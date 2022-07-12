package com.example.mind_maker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.mind_maker.Dao.ImageDB;
import com.example.mind_maker.Dao.NoteDB;
import com.example.mind_maker.adapter.TypeRecyclerAdapter1;
import com.example.mind_maker.media.RadioActivity;
import com.example.mind_maker.media.VideoActivity;
import com.example.mind_maker.record.Note;
import com.example.mind_maker.record.Note_adapter;
import com.example.mind_maker.util.AndroidAdjustResizeBugFix;
import com.example.mind_maker.util.GlideImageEngine;
import com.example.mind_maker.util.KeyboardStateObserver;
import com.example.mind_maker.util.KeyboardUtil;
import com.example.mind_maker.util.SaveTextAppearanceSpan;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class EditActivity extends AppCompatActivity implements View.OnClickListener
{
    private PreparedStatement dbStat=null;
    public static int max=0,min=0;
    public static EditText mEdittext;
    private static int boldStyle;
    private static int highlightStyle;
    private static int titleStyle;
    private float wordSize;
    /*录音变量*/
    private static final String TAG =" ";
    private SeekBar bar;//播放进度条
    private Timer timer;//定时器
    private boolean isSeekbarChaning;//互斥变量，防止进度条和定时器冲突。
    private Button btn_control;
    private Button btn_end;
    private ImageView btn_play;
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

    int []xuhao=new int[]{1,2,3,4,5,6,7,8,9};
    boolean []xuhao_flag=new boolean[]{false,false,false,false,false,false,false,false,false};
    static int xu=0;

    private TypeRecyclerAdapter1 typeRecyclerAdapter1;
    private List<Note> datas= new ArrayList<>();//笔记数据
    private RecyclerView recyclerView1;
    private ImageDB imagedb;
    private NoteDB notedb;
    public AMapLocationClientOption mLocationOption=null;
    public AMapLocationClient mLocationClient = null;
    private LinearLayout layout,layout_bottom,edit_layout,rich_text_edit,more_edit,type_note;
    private EditText note_new_editText,note_title;
    private ImageView btn_bold,btn_type,btn_italic,btn_color,btn_microphone,btn_photo,save_note,btn_style,btn_video;
    private ImageView btn_align_left,btn_align_mid,btn_align_right;
    private ImageView indent_right,indent_left;
    private ImageView number,circle;


    private ImageView btn_red,btn_yellow,btn_green,btn_blue,btn_deep_blue,btn_black,btn_underline,btn_cancel;
    private TextView big_text,small_text,normal_text;
    public static boolean flag_bold = false, flag_red = false;//一些富文本标志位
    public static boolean flag_italic = false, flag_hua = false;
    public static boolean flag_fontde = false, flag_fontin = false;
    public static boolean flag_black = true, flag_green = false,flag_yellow=false,flag_blue=false,flag_deep_blue=false;
    public static boolean flag_underline=false,flag_radio=false,flag_play=false;//是否在录音
    public boolean old_flag=false;//是否是新建的笔记，true则为旧笔记，false则为新笔记
    public static boolean change_flag=false;//笔记是否更改
    public static boolean m_change_flag=false;//全程笔记是否更改
    public static boolean multi_flag=false;//多人编辑标志
    TextView time,position;
    //屏幕宽高
    int screenWidth;
    int screenHeight;
    public static String Content;
    String title;
    public static byte []span_bytes;
    int note_id;//笔记内容
    private static String url = "jdbc:postgresql://116.63.183.142:15432/postgres" + "?characterEncoding=utf8";//postgres为所要连接的数据库
    private static String user = "gaussdb";//PgSQL登录名
    private static String pass = "Secretpassword@123";//数据库所需连接密码
    private static Connection con;//连接
    private static Statement statement = null;//语句
    private static ResultSet resultSet = null;//返回结果
    private LinearLayout invite_layout;
    private EditText invite_id;
    private Button invite_btn;
    private String share_uid;
    private int service_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //share(this);
        /*测试

         */
        /*for (int i=0;i<1;i++){
            datas.add(new Note("1","1","1",2,"我的收藏",i%3,1));
        }*/
        Log.e("zjc_edit", "OnItemClick: 启动EditActivity 新建");

        recyclerView1 = findViewById(R.id.ac_recyclerview1);

        recyclerView1.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL,false));

        //RecyclerAdapter1 recyclerAdapter1 = new RecyclerAdapter1(this,datas);

        typeRecyclerAdapter1 = new TypeRecyclerAdapter1(this,datas);

        //单一类型item
//        recyclerView1.setAdapter(recyclerAdapter1);
        //多类型item
        recyclerView1.setAdapter(typeRecyclerAdapter1);


        if(!isGrantExternalRW(EditActivity.this)){
            return;
        }else{}
        bar=findViewById(R.id.pb);
        tv_start=(TextView)findViewById(R.id.tv_start);
        tv_end=(TextView)findViewById(R.id.tv_end);
        //tv_duration=(TextView)findViewById(R.id.tv_duration);
        tv_time=(TextView)findViewById(R.id.tv_time);
        tv_play=(ImageView)findViewById(R.id.iv_play) ;
        btn_control = (Button) findViewById(R.id.btn_control);
        btn_end=(Button)findViewById(R.id.btn_end);
        //btn_play=findViewById(R.id.tv_play);
        btn_play_stop=(Button)findViewById(R.id.btn_play_stop);
        vid_control=(Button) findViewById(R.id.vid_control);
        vid_end=(Button) findViewById(R.id.vid_end);
        setEventListener();

        btn_align_left=findViewById(R.id.btn_align_left);
        btn_align_mid=findViewById(R.id.btn_align_mid);
        btn_align_right=findViewById(R.id.btn_align_right);
        indent_right=findViewById(R.id.indent_right);
        indent_left=findViewById(R.id.indent_left);
        number=findViewById(R.id.number);
        circle=findViewById(R.id.circle);
        btn_align_left.setOnClickListener(this);
        btn_align_mid.setOnClickListener(this);
        btn_align_right.setOnClickListener(this);
        indent_right.setOnClickListener(this);
        indent_left.setOnClickListener(this);
        number.setOnClickListener(this);
        circle.setOnClickListener(this);








        span_bytes=getIntent().getByteArrayExtra("span_bytes");
        title=getIntent().getStringExtra("name");
        Content=getIntent().getStringExtra("content");
        note_id=getIntent().getIntExtra("note_id",-1);
        service_id=getIntent().getIntExtra("service_id",-1);
        if(note_id==-1)
        {
            old_flag=false;
            Toast.makeText(this, "新笔记", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "旧笔记", Toast.LENGTH_SHORT).show();
            old_flag = true;
        }
        //Log.e("zjc_span", "onCreate: "+span_bytes.length );
        init_datas();
        //datas.add(new Note(title,"1",Content,2,"我的收藏",0,span_bytes,note_id));
        //typeRecyclerAdapter1.notifyDataSetChanged();
        KeyboardStateObserver.getKeyboardStateObserver(this).
                setKeyboardVisibilityListener(new KeyboardStateObserver.OnKeyboardVisibilityListener() {
                    @Override
                    public void onKeyboardShow() {
                        //Toast.makeText(EditActivity.this,"键盘弹出",Toast.LENGTH_SHORT).show();
                        Log.e("zjc", "onKeyboardShow: 弹出");
                        layout_bottom.setVisibility(View.GONE);
                    }

                    @Override
                    public void onKeyboardHide() {
                        Log.e("zjc", "onKeyboardShow: 收起");
                        layout_bottom.setVisibility(View.VISIBLE);
                        //Toast.makeText(EditActivity.this,"键盘收回",Toast.LENGTH_SHORT).show();
                    }
                });
        /*Window window = getWindow();
        //设置修改状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏的颜色，和你的APP主题或者标题栏颜色一致就可以了
        window.setStatusBarColor(Color.TRANSPARENT);
        layout=findViewById(R.id.passage_layout);
        setTransparentStatusBar(EditActivity.this);
        LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) layout.getLayoutParams();
        lp1.topMargin = (int)getStatusBarHeight(EditActivity.this);
        layout.requestLayout();*/
        init();
        if(multi_flag)
        {
            invite_layout.setVisibility(View.VISIBLE);//多人编辑可视化
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true)
                    {
                        Thread.sleep(1000);
                        time.post(new Runnable() {
                            @Override
                            public void run() {
                                Date dt;
                                dt = new Date();
                                String str_time = dt.toLocaleString();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm E");
                                str_time = sdf.format(dt);
                                time.setText(str_time);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        AMapLocationClient.updatePrivacyShow(getApplicationContext(),true,true);
        AMapLocationClient.updatePrivacyAgree(getApplicationContext(),true);
        try {
            mLocationClient=new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocationClient.setLocationListener(mapLocationListener);
        mLocationOption=new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        if(null!=mLocationClient)
        {
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }

    private void init_datas() //初始化笔记布局里的数据
    {
        boolean flag=false;
        if(Content==null)
        {
            datas.add(new Note());
        }
        else {
            Pattern pattern = Pattern.compile("src=\"(.*?)\"/>");//创建匹配img、radio和video的匹配模式
            Pattern pattern_sub = Pattern.compile("\"(.*?)\"");//创建匹配''的模式
            //Log.e("zjc", "onClick: "+note_new_editText.getText().toString());
            Matcher matcher = pattern.matcher(Content);
            Matcher matcher_sub;
            String temp;
            int text_start=0,text_end=0;
            Log.e("zjc_size", "init_datas: "+span_bytes.length );
            boolean img_flag=true;
            while (matcher.find())//matcher.find()用于查找是否有这个字符，有的话返回true
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
                        datas.add(new Note(" ","1",Content.substring(text_start, matcher.start() - 7),1,"我的收藏",0,span_bytes,note_id));
                    }
                    else
                        datas.add(new Note(" ","1",Content.substring(text_start, matcher.start() - 5),1,"我的收藏",0,span_bytes,note_id));
                    text_start=matcher.end();
                }
                else if(matcher.start()-6>0&&matcher.start()-7!=text_start && !img_flag)//文字后面接的是图片无需新建控件
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
                            datas.add(new Note("未命名.amr","1","<img src="+temp.substring(matcher_sub.start(), matcher_sub.end())+"/>",1,"我的收藏",0,note_id));
                        }
                    }
                    //Log.e("zjc", "save_note: " + temp.substring(matcher_sub.start(), matcher_sub.end()));
                    //Log.e("zjc", "save_note: " + temp.substring(matcher_sub.start() + 1, matcher_sub.start() +6));
                    //Log.e("zjc", "save_note: " + Content.substring(matcher.start()-6, matcher.start()-1));
                    if(Content.substring(matcher.start()-6, matcher.start()-1).equals("radio")) {
                        img_flag=true;
                        Log.e("zjc", "save_note: " + Content.substring(matcher.start()-6, matcher.start()-2));
                        text_start=matcher.end();
                        datas.add(new Note("未命名.amr","1",temp.substring(matcher_sub.start() + 1, matcher_sub.end() - 1),1,"我的收藏",1,note_id));
                    }
                    if(Content.substring(matcher.start()-6, matcher.start()-1).equals("video")) {
                        img_flag=true;
                        //Log.e("zjc", "save_note: " + temp.substring(matcher_sub.start() + 1, matcher_sub.end() - 1));
                        text_start=matcher.end();
                        Log.e("zjc", "save_note: " + temp);
                        Log.e("zjc", "save_note: " + temp.substring(matcher_sub.start()+1,matcher_sub.end()-1));
                        File file=new File(temp.substring(matcher_sub.start()+1,matcher_sub.end()-1));
                        if(file.exists())//判断文件在本地是否存在
                            datas.add(new Note("未命名.mp4","1",temp.substring(matcher_sub.start() + 1, matcher_sub.end() - 1),1,"我的收藏",2,note_id));
                        else//文件在本地不存在，所以需要从云端拉取
                        {
                            Log.e("zjc_file", "init_datas: 本地不存在该文件" );
                            CountDownLatch latch=new CountDownLatch(1);
                            String service_con=get_video_from_service(RecordActivity.UID,service_id,latch);//获取服务器所存笔记版本
                            try {
                                latch.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.e("zjc_file", "init_datas: "+service_con );
                            int video_id=get_video_id(service_con);
                            Log.e("zjc_file", "init_datas: "+video_id );
                            latch=new CountDownLatch(1);
                            get_video(video_id,latch);
                            try {
                                latch.await();
                                Toast.makeText(this, "下载成功", Toast.LENGTH_SHORT).show();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        //Log.e("zjc", "save_note: " + temp.substring(matcher_sub.start(), matcher_sub.end()));
                    }
                }
            }
            if(!flag)
            {
                datas.add(new Note(" ","1",Content,1,"我的收藏",0,span_bytes,note_id));
            }
            else
            {
                if(!img_flag)
                {
                    datas.get(datas.size()-1).setContent(datas.get(datas.size()-1).getContent()+Content.substring(text_start, Content.length()));
                }
                else
                    datas.add(new Note(" ","1",Content.substring(text_start, Content.length()),1,"我的收藏",0,span_bytes,note_id));
            }
        }
        typeRecyclerAdapter1.notifyDataSetChanged();
    }

    private void get_video(int video_id,CountDownLatch latch) {
        String sql="select video_content from video where video_id="+video_id;
        //final String[] service_content = new String[1];
        //CountDownLatch latch1=new CountDownLatch(1);
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
                        //Log.e("zjc_re", "onCreate: 成功");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        statement = con.createStatement();
                        ResultSet rs = statement.executeQuery(sql);
                       // Log.e("zjc_file", "run: "+service_id);
                        int len=0;
                        while(rs.next())
                        {

                            //service_content[0] =rs.getString("service_content");
                            byte[] byt=rs.getBytes("video_content");
                            File path = new File(getCacheDir().getAbsolutePath());
                            if (!path.exists()) {
                                path.mkdir();
                            }

                            File file = new File(path, System.currentTimeMillis()+".mp4");
                            len=byt.length;
                            Log.e("zjc_file", "run: 下载成功"+file.getAbsolutePath()+"size"+byt.length);

                            OutputStream output = null;
                            try {
                                output = new FileOutputStream(file);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
//                            Stream t = new FileStream("video.mp4", FileMode.Create);
//                            BinaryWriter b = new BinaryWriter(t);
//                            b.Write(videoData);
//                            t.Close();

                            BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);

                            try {
                                bufferedOutput.write(byt);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("zjc_file", "run: 写失败");
                            }

                            datas.add(new Note("未命名.mp4","1","<video src=\""+file.getAbsolutePath()+"\"/>",1,"我的收藏",2,note_id));
                            //Log.e("zjc_cloud_note", "run: "+result[0].getName());
                            //Note_Item note_item=(Note_Item)temp_result;
                        }


                        con.close();
                        //Note_Item service_note_item=result[0];
                        latch.countDown();
                        Looper.prepare();
                        Toast.makeText(EditActivity.this, len+"", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        //latch1.countDown();

                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("数据库驱动没有安装");
                    Log.e("zjc", "onCreate: 数据库未安装");
                    //Toast.makeText(MainActivity.this, "未安装", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("数据库连接失败");
                    Log.e("zjc_re", "onCreate: 失败");
//                    Looper.prepare();
//                    Toast.makeText(EditActivity.this, "服务器数据库未启动", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private int get_video_id(String service_con) {
        boolean flag=false;
        String Content=service_con;
        if(Content==null)
        {
            //datas.add(new Note());
        }
        else {
            Pattern pattern = Pattern.compile("src=\"(.*?)\"/>");//创建匹配img、radio和video的匹配模式
            Pattern pattern_sub = Pattern.compile("\"(.*?)\"");//创建匹配''的模式
            //Log.e("zjc", "onClick: "+note_new_editText.getText().toString());
            Matcher matcher = pattern.matcher(Content);
            Matcher matcher_sub;
            String temp;
            int text_start=0,text_end=0;
            //Log.e("zjc_size", "init_datas: "+span_bytes.length );
            boolean img_flag=true;
            while (matcher.find())//matcher.find()用于查找是否有这个字符，有的话返回true
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
                        //datas.add(new Note(" ","1",Content.substring(text_start, matcher.start() - 7),1,"我的收藏",0,null,-1));
                    }
                    else
                        //datas.add(new Note(" ","1",Content.substring(text_start, matcher.start() - 5),1,"我的收藏",0,null,-1));
                    text_start=matcher.end();
                }
                else if(matcher.start()-6>0&&matcher.start()-7!=text_start && !img_flag)//文字后面接的是图片无需新建控件
                {
                    if(Content.substring(matcher.start() - 6, matcher.start() - 5).equals("<r")||Content.substring(matcher.start() - 6, matcher.start() - 5).equals("<v"))
                    {
                        //datas.get(datas.size()-1).setContent(datas.get(datas.size()-1).getContent()+Content.substring(text_start, matcher.start() - 7));
                    }
//                    else
                        //datas.get(datas.size()-1).setContent(datas.get(datas.size()-1).getContent()+Content.substring(text_start, matcher.start() - 5));

                }
                img_flag=false;
                while (matcher_sub.find()) {
                    if(Content.substring(matcher.start()-4, matcher.start()-1).equals("img")) {
                        //Log.e("zjc", "save_note: " + Content.substring(matcher.start()-6, matcher.start()-2));
                        text_start=matcher.end();
                        if (!img_flag) {
                            Log.e("zjc_text", "save_note: " + datas.get(datas.size()-1).getContent()+"<img src="+temp.substring(matcher_sub.start(), matcher_sub.end())+"/>");
                            //datas.get(datas.size()-1).setContent(datas.get(datas.size()-1).getContent()+"<img src="+temp.substring(matcher_sub.start(), matcher_sub.end())+"/>");
                        } else {
                            //datas.add(new Note("未命名.amr","1","<img src="+temp.substring(matcher_sub.start(), matcher_sub.end())+"/>",1,"我的收藏",0,-1));
                        }
                    }
                    if(Content.substring(matcher.start()-6, matcher.start()-1).equals("radio")) {
                        img_flag=true;
                        Log.e("zjc", "save_note: " + Content.substring(matcher.start()-6, matcher.start()-2));
                        text_start=matcher.end();
                        //datas.add(new Note("未命名.amr","1",temp.substring(matcher_sub.start() + 1, matcher_sub.end() - 1),1,"我的收藏",1,-1));
                    }
                    if(Content.substring(matcher.start()-6, matcher.start()-1).equals("video")) {
                        img_flag=true;
                        //Log.e("zjc", "save_note: " + temp.substring(matcher_sub.start() + 1, matcher_sub.end() - 1));
                        text_start=matcher.end();
                        Log.e("zjc", "save_note: " + temp);
                        Log.e("zjc", "save_note: " + temp.substring(matcher_sub.start()+1,matcher_sub.end()-1));
                        return Integer.parseInt(temp.substring(matcher_sub.start()+1,matcher_sub.end()-1));
                        //Log.e("zjc", "save_note: " + temp.substring(matcher_sub.start(), matcher_sub.end()));
                    }
                }
            }
            if(!flag)
            {
//                datas.add(new Note(" ","1",Content,1,"我的收藏",0,span_bytes,note_id));
            }
            else
            {
                if(!img_flag)
                {
                    //datas.get(datas.size()-1).setContent(datas.get(datas.size()-1).getContent()+Content.substring(text_start, Content.length()));
                }
//                else
//                    datas.add(new Note(" ","1",Content.substring(text_start, Content.length()),1,"我的收藏",0,span_bytes,note_id));
            }
        }
        return -1;
    }

    private String get_video_from_service(String uid, int service_id,CountDownLatch latch)//从服务器拉取视频文件并保存至本地
    {
        String sql="select service_content from note where uid="+uid+"and note_id="+service_id;//先获取对应的文本，再从文本获取对应的编号
        //final int[] video_id = {-1};
        final String[] service_content = new String[1];
        CountDownLatch latch1=new CountDownLatch(1);
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
                        Log.e("zjc_re", "onCreate: 成功");
                        //String query = "SELECT title,client_content,style,span,update_time_millis,create_time FROM note WHERE uid = "+UID;
                        statement = con.createStatement();
                        ResultSet rs = statement.executeQuery(sql);
                        Log.e("zjc_file", "run: "+service_id);
                        while(rs.next())
                        {

                            service_content[0] =rs.getString("service_content");

//                            File path = new File(getCacheDir().getAbsolutePath());
//                            if (!path.exists()) {
//                                path.mkdir();
//                            }
//
//                            File file = new File(path, System.currentTimeMillis()+".mp4");
//                            Log.e("zjc_file", "run: 下载成功"+file.getAbsolutePath()+"size"+byt.length);
//                            OutputStream output = null;
//                            try {
//                                output = new FileOutputStream(file);
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            }
//
//                            BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
//
//                            try {
//                                bufferedOutput.write(byt);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                Log.e("zjc_file", "run: 写失败");
//                            }
//
//                            datas.add(new Note("未命名.mp4","1","<video src=\""+file.getAbsolutePath()+"\"/>",1,"我的收藏",2,note_id));
                            //Log.e("zjc_cloud_note", "run: "+result[0].getName());
                            //Note_Item note_item=(Note_Item)temp_result;
                        }


                        con.close();
                        //Note_Item service_note_item=result[0];
                        latch.countDown();
                        latch1.countDown();

                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("数据库驱动没有安装");
                    Log.e("zjc", "onCreate: 数据库未安装");
                    //Toast.makeText(MainActivity.this, "未安装", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("数据库连接失败");
                    Log.e("zjc_re", "onCreate: 失败");
//                    Looper.prepare();
//                    Toast.makeText(EditActivity.this, "服务器数据库未启动", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
        try {
            latch1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return service_content[0];
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

    public AMapLocationListener mapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    double latitude = aMapLocation.getLatitude();
                    double Longitude = aMapLocation.getLongitude();
                    String province = aMapLocation.getProvince();
                    String city = aMapLocation.getCity();
                    String district = aMapLocation.getDistrict();
                    String streetNumber = aMapLocation.getStreetNum();
                    String text = "经度: " + Longitude + "\n"
                            + "纬度: " + latitude + "\n"
                            + "详细位置: " + province + city + district + streetNumber;
                    position.setText(text);
                } else {
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                    position.setText("定位失败");
                }
            }
        }
    };

    private void init()//初始化
    {
        normal_text=findViewById(R.id.normal_text);
        small_text=findViewById(R.id.small_text);
        big_text=findViewById(R.id.big_text);
        invite_layout=findViewById(R.id.invite_layout);
        invite_id=findViewById(R.id.invite_id);
        invite_btn=findViewById(R.id.invite_btn);
        type_note=findViewById(R.id.type_note);
        mEdittext=findViewById(R.id.mEditText);
        btn_video=findViewById(R.id.btn_video);
        btn_cancel=findViewById(R.id.cancel);
        btn_underline=findViewById(R.id.btn_underline);
        btn_red=findViewById(R.id.btn_red);
        btn_yellow=findViewById(R.id.btn_yellow);
        btn_green=findViewById(R.id.btn_green);
        btn_blue=findViewById(R.id.btn_blue);
        btn_deep_blue=findViewById(R.id.btn_deep_blue);
        btn_black=findViewById(R.id.btn_black);
        rich_text_edit=findViewById(R.id.rich_text_edit);
        more_edit=findViewById(R.id.more_edit);
        notedb=new NoteDB(this);
        imagedb=new ImageDB(this);
        //AndroidAdjustResizeBugFix.assistActivity(this);
        //得到屏幕分辨率
        save_note=findViewById(R.id.save_note);
        note_title=findViewById(R.id.note_title);
        note_title.setText(title);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight  = display.getHeight();
        time=findViewById(R.id.time);
        position=findViewById(R.id.position);
        layout_bottom=findViewById(R.id.layout_bottom);
        edit_layout=findViewById(R.id.edit_layout);
        note_new_editText=findViewById(R.id.note_new_editText);
        btn_bold=findViewById(R.id.btn_bold);
        //btn_type=findViewById(R.id.btn_type);
        btn_style=findViewById(R.id.font_style);
        btn_italic=findViewById(R.id.btn_italic);
        //btn_color=findViewById(R.id.btn_color);
        btn_microphone=findViewById(R.id.btn_microphone);
        btn_photo=findViewById(R.id.btn_photo);
        normal_text.setOnClickListener(this);
        small_text.setOnClickListener(this);
        big_text.setOnClickListener(this);
        invite_btn.setOnClickListener(this);
        btn_bold.setOnClickListener(this);
        //btn_type.setOnClickListener(this);
        btn_italic.setOnClickListener(this);
        //btn_color.setOnClickListener(this);
        btn_video.setOnClickListener(this);
        btn_microphone.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        btn_style.setOnClickListener(this);
        save_note.setOnClickListener(this);
        btn_red.setOnClickListener(this);
        btn_black.setOnClickListener(this);
        btn_yellow.setOnClickListener(this);
        btn_blue.setOnClickListener(this);
        btn_deep_blue.setOnClickListener(this);
        btn_green.setOnClickListener(this);
        btn_blue.setOnClickListener(this);
        btn_underline.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        //note_new_editText.setOnClickListener(this);
        //init_edit();
        //init_note();//读取本地笔记
    }

    private static int statusBarHeight;
    private double getStatusBarHeight(Context context){

        double statusBarHeight = Math.ceil(25 * context.getResources().getDisplayMetrics().density);

        return statusBarHeight;

    }
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                @SuppressLint("PrivateApi") Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    public static void setTransparentStatusBar(Activity activity) {
        //5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            //4.4到5.0
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    @Override
    public void onClick(View view) {
        note_new_editText=recyclerView1.getChildAt(0).findViewById(R.id.note_new_editText);
        max = note_new_editText.getSelectionEnd();
        min = note_new_editText.getSelectionStart();
        if (max < min) {
            int temp = max;
            max = min;
            min = temp;
        }
        switch(view.getId())
        {
            case R.id.big_text:
                flag_fontin=true;
                flag_fontde=false;
                big_text.setTextColor(Color.parseColor("#3e6aff"));
                small_text.setTextColor(Color.parseColor("#000000"));
                normal_text.setTextColor(Color.parseColor("#000000"));
                break;
            case R.id.small_text:
                flag_fontde=true;
                flag_fontin=false;
                big_text.setTextColor(Color.parseColor("#000000"));
                small_text.setTextColor(Color.parseColor("#3e6aff"));
                normal_text.setTextColor(Color.parseColor("#000000"));
                break;
            case R.id.normal_text:
                flag_fontde=false;
                flag_fontin=false;
                big_text.setTextColor(Color.parseColor("#000000"));
                small_text.setTextColor(Color.parseColor("#000000"));
                normal_text.setTextColor(Color.parseColor("#3e6aff"));
                break;
            case R.id.btn_align_left:
                Layout layout = note_new_editText.getLayout();
                String text = note_new_editText.getText().toString();
                int start = 0;
                int end;
                int selectionStart = Selection.getSelectionStart(note_new_editText.getText());
                int mouse_line=layout.getLineForOffset(selectionStart);
                String page="";
                //循环遍历打印每一行
                for (int i = 0; i < note_new_editText.getLineCount(); i++) {
                    end = layout.getLineEnd(i);
                    String line = text.substring(start, end); //指定行的内容
                    if(i==mouse_line) {
                        line = line.trim();
                    }
                    start = end;
                    if(i==mouse_line){
                        page=page+line+'\n';
                    }else{
                        page=page+line;
                    }
                    //Toast.makeText(this,page, Toast.LENGTH_SHORT).show();
                }
                note_new_editText.setText(page);
                break;
            case R.id.btn_align_mid:
                Layout layout1 = note_new_editText.getLayout();
                String text1 = note_new_editText.getText().toString();
                int start1 = 0;
                int end1;
                int selectionStart1 = Selection.getSelectionStart(note_new_editText.getText());
                int mouse_line1=layout1.getLineForOffset(selectionStart1);
                String page1="";
                int end_line_mid = layout1.getLineEnd(0);
                String line_1_mid = text1.substring(0, end_line_mid);
                int length_mid=line_1_mid.getBytes().length;
                //循环遍历打印每一行
                for (int i = 0; i < note_new_editText.getLineCount(); i++) {
                    end1 = layout1.getLineEnd(i);
                    String line = text1.substring(start1, end1); //指定行的内容
                    if(i==mouse_line1) {
                        line=line.trim();
                        int jian2_mid = line.getBytes().length;
                        int gapSize=(length_mid-jian2_mid)/2;
                        while (gapSize>0){
                            //line = StringUtils.left(line, gapSize + length(line) ," ");
                            line="\t"+line;
                            gapSize--;
                        }
                    }
                    start1 = end1;
                    if(i==mouse_line1){
                        page1=page1+line+'\n';
                    }else{
                        page1=page1+line;
                    }
                    //Toast.makeText(this,page, Toast.LENGTH_SHORT).show();
                }
                note_new_editText.setText(page1);
                break;
            case R.id.btn_align_right:
                Layout layout2 = note_new_editText.getLayout();
                String text2 = note_new_editText.getText().toString();
                int start2 = 0;
                int end2;
                int selectionStart2 = Selection.getSelectionStart(note_new_editText.getText());
                int mouse_line2=layout2.getLineForOffset(selectionStart2);
                String page2="";
                int end_line = layout2.getLineEnd(0);
                String line_1 = text2.substring(0, end_line);
                int length=line_1.getBytes().length;
                //循环遍历打印每一行
                for (int i = 0; i < note_new_editText.getLineCount(); i++) {
                    end2 = layout2.getLineEnd(i);
                    String line = text2.substring(start2, end2); //指定行的内容
                    if(i==mouse_line2) {
                        line=line.trim();
                        //int jian2=line.length();
                        int jian2 = line.getBytes().length;
                        int gapSize=length-jian2;
                        while (gapSize>0){
                            //line = StringUtils.left(line, gapSize + length(line) ," ");
                            line="\t"+line;
                            gapSize--;
                        }
                    }
                    start2 = end2;
                    if(i==mouse_line2){
                        page2=page2+line+'\n';
                    }else{
                        page2=page2+line;
                    }
                    //Toast.makeText(this,page, Toast.LENGTH_SHORT).show();
                }
                note_new_editText.setText(page2);
                break;
            case R.id.indent_right:
                Layout layout3 = note_new_editText.getLayout();
                String text3 = note_new_editText.getText().toString();
                int start3 = 0;
                int end3=0;
                int selectionStart3 = Selection.getSelectionStart(note_new_editText.getText());
                int mouse_line3=layout3.getLineForOffset(selectionStart3);
                String page3="";
                //循环遍历打印每一行
                for (int i = 0; i < note_new_editText.getLineCount(); i++) {
                    end3 = layout3.getLineEnd(i);
                    String line = text3.substring(start3, end3); //指定行的内容
                    if(i==mouse_line3) {
                        line="\t\t\t\t"+line;
                    }
                    start3 = end3;
                    page3=page3+line;
                    //Toast.makeText(this,page, Toast.LENGTH_SHORT).show();
                }
                note_new_editText.setText(page3);

                break;
            case R.id.indent_left:
                Layout layout4 = note_new_editText.getLayout();
                String text4 = note_new_editText.getText().toString();
                int start4 = 0;
                int end4=0;
                int selectionStart4 = Selection.getSelectionStart(note_new_editText.getText());
                int mouse_line4=layout4.getLineForOffset(selectionStart4);
                String page4="";
                //循环遍历打印每一行
                for (int i = 0; i < note_new_editText.getLineCount(); i++) {
                    end4 = layout4.getLineEnd(i);
                    String line = text4.substring(start4, end4); //指定行的内容
                    if(i==mouse_line4) {
                        line=trimstart(line,"\t\t\t\t");
                        //line="\t\t\t\t"+line;
                    }
                    start4 = end4;
                    page4=page4+line;
                    //Toast.makeText(this,page, Toast.LENGTH_SHORT).show();
                }
                note_new_editText.setText(page4);
                break;
            case R.id.number:
                String[] a = new String[]{"1.","2.","3.","4.","5.","6.","7.","8.","9."};
                Layout layout5 = note_new_editText.getLayout();
                String text5 = note_new_editText.getText().toString();
                int start5 = 0;
                int end5;
                int selectionStart5 = Selection.getSelectionStart(note_new_editText.getText());
                int mouse_line5 = layout5.getLineForOffset(selectionStart5);
                String page5 = "";
//                for (int i = 0; i < note_new_editText.getLineCount(); i++) {
//                    end5 = layout5.getLineEnd(i);
//                    String line = text5.substring(start5, end5); //指定行的内容
//                    page5 = page5 + line;
//                }
//                for(int p=0;p<9;p++){
//                    if(page5.contains(a[p])==false)  {xuhao_flag[p]=false;}
//                    else{
//                        xuhao_flag[p]=true;
//                    }
//                }
//                page5="";
                //循环遍历打印每一行
                for (int i = 0; i < note_new_editText.getLineCount(); i++) {
                    end5 = layout5.getLineEnd(i);
                    String line = text5.substring(start5, end5); //指定行的内容
                    if (i == mouse_line5) {
                        line = line.trim();
                        if(xuhao_flag[xu]==false) {
                            line = xuhao[xu] + "." + line;
                            xuhao_flag[xu]=true;
                            for(int j=0;j<9;j++) {
                                if(xuhao_flag[j]==false) {
                                    xu=j;break;
                                }
                            }
                        }

                    }
                    start5 = end5;
                    page5 = page5 + line;
                    //Toast.makeText(this,page, Toast.LENGTH_SHORT).show();
                }
                note_new_editText.setText(page5);
                break;
            case R.id.circle:
                String circ="○";
                Layout layout6 = note_new_editText.getLayout();
                String text6 = note_new_editText.getText().toString();
                int start6 = 0;
                int end6;
                int selectionStart6 = Selection.getSelectionStart(note_new_editText.getText());
                int mouse_line6=layout6.getLineForOffset(selectionStart6);
                String page6="";
                //循环遍历打印每一行
                for (int i = 0; i < note_new_editText.getLineCount(); i++) {
                    end6 = layout6.getLineEnd(i);
                    String line = text6.substring(start6, end6); //指定行的内容
                    if(i==mouse_line6) {
                        line = line.trim();
                        if(line.startsWith(circ)==false) {
                            line = circ + line;
                        }
                    }
                    start6 = end6;
                    page6=page6+line;
                }
                note_new_editText.setText(page6);
                break;
            case R.id.note_new_editText:
                rich_text_edit.setVisibility(View.VISIBLE);
                more_edit.setVisibility(View.GONE);
                layout_bottom.setVisibility(View.VISIBLE);
                break;
            case R.id.cancel:
                rich_text_edit.setVisibility(View.VISIBLE);
                more_edit.setVisibility(View.GONE);
                layout_bottom.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_bold:
                flag_bold=!flag_bold;
                /*if (flag_bold) {
                    //btn_bold.setBackgroundColor(Color.parseColor("#2196f3"));
                    //flag_bold=false;
                    setSpan(note_new_editText.getText(), min, max, boldStyle);
//                    removeSpan(note_new_editText.getText(), min, max);
                }
                else
                {
                    //btn_bold.setBackgroundColor(Color.parseColor("#808080"));
                    //flag_bold=true;
                    setSpan(note_new_editText.getText(), min, max, boldStyle);
                }*/
                if(flag_bold)
                    btn_bold.setImageResource(R.drawable.bold_selected);
                else
                    btn_bold.setImageResource(R.drawable.bold);
                //typeRecyclerAdapter1.notifyDataSetChanged();
                break;
            /*case R.id.btn_type:
                flag_fontde=!flag_fontde;
                break;*/
            case R.id.btn_italic:
                flag_italic=!flag_italic;
                note_new_editText=recyclerView1.getChildAt(0).findViewById(R.id.note_new_editText);
                max = note_new_editText.getSelectionEnd();
                min = note_new_editText.getSelectionStart();
                if (max < min) {
                    int temp = max;
                    max = min;
                    min = temp;
                }
                if (flag_italic) {
                    //btn_bold.setBackgroundColor(Color.parseColor("#2196f3"));
                    //flag_bold=false;
                    setSpan(note_new_editText.getText(), min, max, boldStyle);
//                    removeSpan(note_new_editText.getText(), min, max);
                }
                else
                {
                    //btn_bold.setBackgroundColor(Color.parseColor("#808080"));
                    //flag_bold=true;
                    setSpan(note_new_editText.getText(), min, max, boldStyle);
                }
                if(flag_italic)
                    btn_italic.setImageResource(R.drawable.italic_selected);
                else
                    btn_italic.setImageResource(R.drawable.italic);
                break;
            case R.id.btn_underline:
                flag_underline=!flag_underline;
                note_new_editText=recyclerView1.getChildAt(0).findViewById(R.id.note_new_editText);
                max = note_new_editText.getSelectionEnd();
                min = note_new_editText.getSelectionStart();
                if (max < min) {
                    int temp = max;
                    max = min;
                    min = temp;
                }
                if (flag_underline) {
                    //btn_bold.setBackgroundColor(Color.parseColor("#2196f3"));
                    //flag_bold=false;
                    setSpan(note_new_editText.getText(), min, max, boldStyle);
//                    removeSpan(note_new_editText.getText(), min, max);
                }
                else
                {
                    //btn_bold.setBackgroundColor(Color.parseColor("#808080"));
                    //flag_bold=true;
                    setSpan(note_new_editText.getText(), min, max, boldStyle);
                }
                if(flag_underline)
                    btn_underline.setImageResource(R.drawable.underline_selected);
                else
                    btn_underline.setImageResource(R.drawable.underline);
                break;
            case R.id.btn_red:
                flag_red=true;
                if(flag_black)
                {
                    flag_black=false;
                    btn_black.setImageResource(R.drawable.black_circle);
                }
                else if(flag_yellow)
                {
                    flag_yellow=false;
                    btn_yellow.setImageResource(R.drawable.yellow_circle);
                }
                else if(flag_green)
                {
                    flag_green=false;
                    btn_green.setImageResource(R.drawable.green_circle);
                }
                else if(flag_blue)
                {
                    flag_blue=false;
                    btn_blue.setImageResource(R.drawable.blue_circle);
                }
                else if(flag_deep_blue)
                {
                    flag_deep_blue=false;
                    btn_deep_blue.setImageResource(R.drawable.deep_blue_circle);
                }
                note_new_editText=recyclerView1.getChildAt(0).findViewById(R.id.note_new_editText);
                max = note_new_editText.getSelectionEnd();
                min = note_new_editText.getSelectionStart();
                if (max < min) {
                    int temp = max;
                    max = min;
                    min = temp;
                }
                setSpan(note_new_editText.getText(), min, max, boldStyle);
                btn_red.setImageResource(R.drawable.red_circle_selected);
                break;
            case R.id.btn_yellow:
                flag_yellow=true;
                if(flag_black)
                {
                    flag_black=false;
                    btn_black.setImageResource(R.drawable.black_circle);
                }
                else if(flag_red)
                {
                    flag_red=false;
                    btn_red.setImageResource(R.drawable.red_circle);
                }
                else if(flag_green)
                {
                    flag_green=false;
                    btn_green.setImageResource(R.drawable.green_circle);
                }
                else if(flag_blue)
                {
                    flag_blue=false;
                    btn_blue.setImageResource(R.drawable.blue_circle);
                }
                else if(flag_deep_blue)
                {
                    flag_deep_blue=false;
                    btn_deep_blue.setImageResource(R.drawable.deep_blue_circle);
                }
                note_new_editText=recyclerView1.getChildAt(0).findViewById(R.id.note_new_editText);
                max = note_new_editText.getSelectionEnd();
                min = note_new_editText.getSelectionStart();
                if (max < min) {
                    int temp = max;
                    max = min;
                    min = temp;
                }
                setSpan(note_new_editText.getText(), min, max, boldStyle);
                btn_yellow.setImageResource(R.drawable.yellow_circle_selected);
                break;
            case R.id.btn_green:
                flag_green=true;
                if(flag_black)
                {
                    flag_black=false;
                    btn_black.setImageResource(R.drawable.black_circle);
                }
                else if(flag_red)
                {
                    flag_red=false;
                    btn_red.setImageResource(R.drawable.red_circle);
                }
                else if(flag_yellow)
                {
                    flag_yellow=false;
                    btn_yellow.setImageResource(R.drawable.yellow_circle);
                }
                else if(flag_blue)
                {
                    flag_blue=false;
                    btn_blue.setImageResource(R.drawable.blue_circle);
                }
                else if(flag_deep_blue)
                {
                    flag_deep_blue=false;
                    btn_deep_blue.setImageResource(R.drawable.deep_blue_circle);
                }
                note_new_editText=recyclerView1.getChildAt(0).findViewById(R.id.note_new_editText);
                max = note_new_editText.getSelectionEnd();
                min = note_new_editText.getSelectionStart();
                if (max < min) {
                    int temp = max;
                    max = min;
                    min = temp;
                }
                setSpan(note_new_editText.getText(), min, max, boldStyle);
                btn_green.setImageResource(R.drawable.green_circle_selected);
                break;
            case R.id.btn_blue:
                flag_blue=true;
                if(flag_black)
                {
                    flag_black=false;
                    btn_black.setImageResource(R.drawable.black_circle);
                }
                else if(flag_red)
                {
                    flag_red=false;
                    btn_red.setImageResource(R.drawable.red_circle);
                }
                else if(flag_green)
                {
                    flag_green=false;
                    btn_green.setImageResource(R.drawable.green_circle);
                }
                else if(flag_yellow)
                {
                    flag_yellow=false;
                    btn_yellow.setImageResource(R.drawable.yellow_circle);
                }
                else if(flag_deep_blue)
                {
                    flag_deep_blue=false;
                    btn_deep_blue.setImageResource(R.drawable.deep_blue_circle);
                }
                note_new_editText=recyclerView1.getChildAt(0).findViewById(R.id.note_new_editText);
                max = note_new_editText.getSelectionEnd();
                min = note_new_editText.getSelectionStart();
                if (max < min) {
                    int temp = max;
                    max = min;
                    min = temp;
                }
                setSpan(note_new_editText.getText(), min, max, boldStyle);
                btn_blue.setImageResource(R.drawable.blue_circle_selected);
                break;
            case R.id.btn_deep_blue:
                flag_deep_blue=true;
                if(flag_black)
                {
                    flag_black=false;
                    btn_black.setImageResource(R.drawable.black_circle);
                }
                else if(flag_red)
                {
                    flag_red=false;
                    btn_red.setImageResource(R.drawable.red_circle);
                }
                else if(flag_green)
                {
                    flag_green=false;
                    btn_green.setImageResource(R.drawable.green_circle);
                }
                else if(flag_blue)
                {
                    flag_blue=false;
                    btn_blue.setImageResource(R.drawable.blue_circle);
                }
                else if(flag_yellow)
                {
                    flag_yellow=false;
                    btn_yellow.setImageResource(R.drawable.yellow_circle);
                }
                note_new_editText=recyclerView1.getChildAt(0).findViewById(R.id.note_new_editText);
                max = note_new_editText.getSelectionEnd();
                min = note_new_editText.getSelectionStart();
                if (max < min) {
                    int temp = max;
                    max = min;
                    min = temp;
                }
                setSpan(note_new_editText.getText(), min, max, boldStyle);
                btn_deep_blue.setImageResource(R.drawable.deep_blue_circle_selected);
                break;
            case R.id.btn_black:
                flag_black=true;
                if(flag_yellow)
                {
                    flag_yellow=false;
                    btn_yellow.setImageResource(R.drawable.yellow_circle);
                }
                else if(flag_red)
                {
                    flag_red=false;
                    btn_red.setImageResource(R.drawable.red_circle);
                }
                else if(flag_green)
                {
                    flag_green=false;
                    btn_green.setImageResource(R.drawable.green_circle);
                }
                else if(flag_blue)
                {
                    flag_blue=false;
                    btn_blue.setImageResource(R.drawable.blue_circle);
                }
                else if(flag_deep_blue)
                {
                    flag_deep_blue=false;
                    btn_deep_blue.setImageResource(R.drawable.deep_blue_circle);
                }
                note_new_editText=recyclerView1.getChildAt(0).findViewById(R.id.note_new_editText);
                max = note_new_editText.getSelectionEnd();
                min = note_new_editText.getSelectionStart();
                if (max < min) {
                    int temp = max;
                    max = min;
                    min = temp;
                }
                setSpan(note_new_editText.getText(), min, max, boldStyle);
                btn_black.setImageResource(R.drawable.black_circle_selected);
                break;
            case R.id.btn_microphone:
                /*datas.add(new Note("1","1","1",1,"我的收藏",1,2));
                datas.add(new Note("1","1","",1,"我的收藏",0,2));
                typeRecyclerAdapter1.notifyDataSetChanged();*/
                flag_radio=!flag_radio;
                if(flag_radio)//开始录音
                {
                    btn_microphone.setImageResource(R.drawable.recording);
                    startRecord();
                }
                else
                {
                    Log.e("zjc_radio", "onClick:1" );
                    btn_microphone.setImageResource(R.drawable.speaker);
                    stopRecord();//录制完毕
                    Log.e("zjc_radio", "onClick:2" );
                    Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
                    mPlayer = new MediaPlayer();
                    File path= new File(getCacheDir().getAbsolutePath());
                    if(!path.exists()){
                        path.mkdir();
                    }
                    String Radio_Name=System.currentTimeMillis()+".amr";
                    String radio_path=path+Radio_Name;
                    File file=new File(path,Radio_Name);
                    Log.e("zjc_radio", "onClick:3" );
                    datas.add(new Note(Radio_Name,"1",radio_path,1,"我的收藏",1,2));
                    Log.e("zjc_radio", "onClick:4" );
                    datas.add(new Note("","1","",1,"我的收藏",0,2));

                    //Log.e("zjc_span", "onClick: "+temp_editable.length() );
                    typeRecyclerAdapter1.notifyDataSetChanged();
                    Log.e("zjc_radio", "onClick:5" );
                    try {
                        mPlayer.setDataSource(String.valueOf(file));
                        mPlayer.prepare();//让MediaPlayer进入到准备状态
                    } catch (IOException e) {
                        //Log.e(LOG_TAG, "播放失败");
                        //btn_play.setText("over");
                    }
                    int duration = mPlayer.getDuration() / 1000;//获取音乐总时长
                    Log.e("zjc_radio", "onClick:6" );
                    //tv_end.setText(calculateTime(duration));
                    //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// HH:mm:ss
                    //Date date = new Date(System.currentTimeMillis());
                    //tv_time.setText(simpleDateFormat.format(date));
                }
                break;
            case R.id.btn_video:
                Intent intent=new Intent();
                intent.setClass(EditActivity.this, VideoActivity.class);
                startActivityForResult(intent, 30);
                /*datas.add(new Note("1","1","1",1,"我的收藏",2,2));
                datas.add(new Note("1","1","",1,"我的收藏",0,2));
                typeRecyclerAdapter1.notifyDataSetChanged();*/
                break;
            case R.id.btn_photo:
                callGallery();
                //openAlbum();
                break;
            case R.id.save_note:
                save_note();
                break;
            case R.id.font_style:
                popup_style();//弹出样式选择框
                break;
            case R.id.tv_play:
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
                            //btn_play.setText("over");
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
    }


    private void setEventListener()//设置item点击事件
    {
        typeRecyclerAdapter1.setOnItemClickListener(new TypeRecyclerAdapter1.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                flag_play=!flag_play;
                tv_play=v.findViewById(R.id.iv_play);
                tv_end=v.findViewById(R.id.tv_end);
                tv_start=v.findViewById(R.id.tv_start);
                bar=v.findViewById(R.id.pb);
                //Toast.makeText(EditActivity.this,"用户点击了"+position+"的播放键",Toast.LENGTH_SHORT).show();
                if(flag_play) {
                    mPlayer = new MediaPlayer();
                    File path = new File(getCacheDir().getAbsolutePath());
                    if (!path.exists()) {
                        path.mkdir();
                    }

                    File file = new File(path, "luyin.amr");
                    test_radio_upload(file);//测试录音文件的上传
                    try {
                        mPlayer.setDataSource(String.valueOf(file));
                        mPlayer.prepare();
                        mPlayer.start();
                        int duration = mPlayer.getDuration();//获取音乐总时间
                        datas.get(position).setDuration(duration);
                        //bar.setMax(duration);//将音乐总时间设置为Seekbar的最大值
                        timer = new Timer();//时间监听器
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (!isSeekbarChaning) {
                                    datas.get(position).setStartTime(mPlayer.getCurrentPosition());
                                    if(mPlayer.getCurrentPosition()>=duration)
                                        handler.sendEmptyMessage(1);
                                    handler.sendEmptyMessage(0);
                                    //bar.setProgress(mPlayer.getCurrentPosition());
                                }
                            }
                        }, 0, 500);

                    } catch (IOException e) {
                        Log.e("hhhhh", "播放失败");
                        //btn_play.setText("over");
                    }
                    tv_play.setImageResource(R.drawable.pause);
                    //tv_play.setBackground(null);
                    //tv_play.setBackground((R.drawable.pause));
                    //break;
                }
                else {
                    timer.cancel();
                    if (mPlayer.isPlaying()) {
                        mPlayer.pause();//暂停播放
                        //mPlayer.seekTo(bar.getProgress());//在当前位置播放
                        datas.get(position).setStartTime(mPlayer.getCurrentPosition() / 1000);
                        typeRecyclerAdapter1.notifyDataSetChanged();
                        //tv_start.setText(calculateTime(mPlayer.getCurrentPosition() / 1000));
                    }
                    tv_play.setImageResource(R.drawable.play);
                    //flag = 1;
                }
            }
        });
    }

    public Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if(message.what==0)
            {
                typeRecyclerAdapter1.notifyDataSetChanged();
            }
            else
                timer.cancel();
            return false;
        }
    });


    private void test_radio_upload(File file)//测试文件上传
    {
        String sql="Insert Into Radio (radio_content) Values (?)";
        Log.e("zjc_cloud", "send_note_to_service: "+sql);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //注册JDBC驱动
                    Class.forName("org.postgresql.Driver");
                    //建立连接
                    con = DriverManager.getConnection(url, user, pass);
                    if (!con.isClosed()) {
                        //System.out.println("数据库连接成功");
                        //File file=new File(file_name);
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
                        Log.e("zjc_cloud", "onCreate: 文件上传成功+file.length()");

                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("数据库驱动没有安装");
                    Log.e("zjc", "onCreate: 数据库未安装");
                    //Toast.makeText(MainActivity.this, "未安装", Toast.LENGTH_SHORT).show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("数据库连接失败");
                    Log.e("zjc_cloud", "onCreate: 语音失败+file.length()");
                    Looper.prepare();
                    Toast.makeText(EditActivity.this, "服务器数据库未启动", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    //Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }
    /*
     * 删除开头字符串
     */
    public static String trimstart(String inStr, String prefix) {
        if (inStr.startsWith(prefix)) {
            return (inStr.substring(prefix.length()));
        }
        return inStr;
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



    private void popup_style() {
        rich_text_edit.setVisibility(View.GONE);
        more_edit.setVisibility(View.VISIBLE);
        layout_bottom.setVisibility(View.GONE);
        KeyboardUtil.hideKeyboard(this);
    }


    private void save_note()//保存笔记
    {
        StringBuilder mContent= new StringBuilder();
        byte[] bytes = null;
//        byte[] bytes = null;
        Note data=null;
        StringBuilder t_str = new StringBuilder();
        for(int data_item=0;data_item<datas.size();data_item++)
        {
            data=datas.get(data_item);//得到当前view子控件
            if(data.getContent_style()==0)
            {
                t_str.append(data.getContent());
            }
            else if(data.getContent_style()==1)//语音
            {
                t_str.append("<radio src=\"").append(data.getContent()).append("\"/>");
            }
            else if(data.getContent_style()==2)//语音
            {
                t_str.append("<video src=\"").append(data.getContent()).append("\"/>");
            }
        }
        mEdittext.setText(t_str.toString());
        int start=0;int end=0;
        for(int data_item=0;data_item<datas.size();data_item++)
        {
            data=datas.get(data_item);//得到当前view子控件
            if(data.getContent_style()==0)
            {
                if(start<end)
                    start=end;
                end=start+data.getContent().length();
                Log.e("zjc_span", "save_note: "+end);
                EditText note_new_editText=recyclerView1.getChildAt(data_item).findViewById(R.id.note_new_editText);
                Editable editableContent = note_new_editText.getText();
                //Editable editableContent = note_new_editText.getText();
                ArrayList<byte[]> spanArrayList = new ArrayList<>();
                ArrayList<Integer> spanStartList = new ArrayList<>();
                ArrayList<Integer> spanEndList = new ArrayList<>();
//        TextAppearanceSpan[] spans = note_new_editText.getText().getSpans
//                (0,note_new_editText.length(), TextAppearanceSpan.class);
                TextAppearanceSpan[] spans = note_new_editText.getText().getSpans
                        (0, note_new_editText.length(), TextAppearanceSpan.class);
                Log.e("zjc_span", "save_note: "+note_new_editText.length());
                for (int i = 0; i < spans.length; i++) {
                    Parcel parcel = Parcel.obtain();
                    spans[i].writeToParcel(parcel, 0);
                    spanArrayList.add(i, parcel.marshall());
                    spanStartList.add(i, editableContent.getSpanStart(spans[i]));
                    spanEndList.add(i, editableContent.getSpanEnd(spans[i]));
                    parcel.recycle();
                }
//        Log.d("二进制", "AddNote: " + spans.length + " " + spanArrayList.size());
                Parcel parcel = Parcel.obtain();
                parcel.setDataPosition(0);
                parcel.writeParcelable(new SaveTextAppearanceSpan(spanArrayList, spanStartList, spanEndList), 0);
                bytes=parcel.marshall();
                contentToSpanStr_patchwork(this,data.getContent(),bytes,start,end);
                Log.d("二进制", "AddNote: " + new String(Base64.encode(parcel.marshall(), Base64.DEFAULT)));
                parcel.recycle();
                mContent.append(note_new_editText.getText().toString());
                Log.d("mContent:", "用户输入的内容是" + mContent);
                //notedb.InsertData(note_title.getText().toString(),note_new_editText.getText().toString(),"今天",bytes);
            }
            else if(data.getContent_style()==1)//语音
            {

                mContent.append("<radio src=\"").append(data.getContent()).append("\"/>");
                if(start<end)
                    start=end;
                end=start+15+data.getContent().length();
                Log.e("zjc_span", "save_note: "+end);
                //contentToSpanStr_patchwork(this,data.getContent(),bytes,start,end);
            }
            else if(data.getContent_style()==2)//视频
            {
                if(start<end)
                    start=end;
                end=start+15+data.getContent().length();
                mContent.append("<video src=\"").append(data.getContent()).append("\"/>");
                //contentToSpanStr_patchwork(this,data.getContent(),,start,end);
            }

            //imagedb.InsertData();
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        save_upload_note();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }).start();*/
        }
        bytes=get_Span(mEdittext.getText());
        if(old_flag)
        {
            notedb.UpdateData(note_title.getText().toString(),mContent.toString(),"今天",(System.currentTimeMillis()),bytes,note_id);
            try {
                for(int i=0;i<RecordActivity.note_list.size();i++)
                {
                    if(RecordActivity.note_list.get(i).getNote_id()==note_id)
                    {
                        RecordActivity.note_list.set(i,new Note(note_title.getText().toString(),"之前",mContent.toString(),1,"我的收藏",1,bytes,note_id));
                        break;
                    }
                }
                typeRecyclerAdapter1.notifyDataSetChanged();
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                change_flag=false;
            }catch (Exception e)
            {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            try {
                notedb.InsertData_no_service_id(note_title.getText().toString(), mContent.toString(), "今天",System.currentTimeMillis(), bytes);
                NoteDB noteDB=new NoteDB(this);
                Cursor cursor=noteDB.QueryData();
                if(cursor.moveToLast())
                {
                    RecordActivity.note_list.add(new Note(note_title.getText().toString(),"之前",mContent.toString(),1,"我的收藏",1,bytes,cursor.getInt(0)));
                    change_flag=false;
                    typeRecyclerAdapter1.notifyDataSetChanged();
                }
//                while(cursor.moveToNext())
//                {
//                    if(cursor.getString(cursor.getColumnIndex("title")).equals(note_title.getText().toString()))
//                }
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();

            }catch (Exception ignored)
            {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(change_flag) {
            AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(this);
            alertdialogbuilder.setMessage("是否保存？");
            alertdialogbuilder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(EditActivity.this, "点击了是", Toast.LENGTH_SHORT).show();
                    save_note();
                    Intent intent=new Intent();
                    intent.putExtra("flag","editactivity");
                    setResult(25,intent);
                    finish();
                }
            });
            alertdialogbuilder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(EditActivity.this, "点击了否", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent();
                    intent.putExtra("flag","editactivity");
                    EditActivity.this.setResult(25,intent);
                    EditActivity.this.finish();
                   // finish();
                }
            });
            AlertDialog alertdialog1 = alertdialogbuilder.create();
            alertdialog1.show();
        }
        else
        {
            Intent intent=new Intent();
            intent.putExtra("flag","editactivity");
            EditActivity.this.setResult(25,intent);
            EditActivity.this.finish();
            finish();
        }


    }

    public byte[] get_Span(Editable editable)
    {
        ArrayList<byte[]> spanArrayList = new ArrayList<>();
        ArrayList<Integer> spanStartList = new ArrayList<>();
        ArrayList<Integer> spanEndList = new ArrayList<>();
//        TextAppearanceSpan[] spans = note_new_editText.getText().getSpans
//                (0,note_new_editText.length(), TextAppearanceSpan.class);
        TextAppearanceSpan[] spans = editable.getSpans
                (0, editable.length(), TextAppearanceSpan.class);
        for (int i = 0; i < spans.length; i++) {
            Parcel parcel = Parcel.obtain();
            spans[i].writeToParcel(parcel, 0);
            spanArrayList.add(i, parcel.marshall());
            spanStartList.add(i, editable.getSpanStart(spans[i]));
            spanEndList.add(i, editable.getSpanEnd(spans[i]));
            parcel.recycle();
        }
//        Log.d("二进制", "AddNote: " + spans.length + " " + spanArrayList.size());
        Parcel parcel = Parcel.obtain();
        parcel.setDataPosition(0);
        parcel.writeParcelable(new SaveTextAppearanceSpan(spanArrayList, spanStartList, spanEndList), 0);
        return  parcel.marshall();
    }

    private void save_note_formerly()//保存笔记
    {
        Pattern pattern = Pattern.compile("<img src=\"(.*?)\"/>");//创建匹配img的模式
        Pattern pattern_sub = Pattern.compile("\"(.*?)\"");//创建匹配''的模式
        Log.e("zjc", "onClick: "+note_new_editText.getText().toString());
        Matcher matcher = pattern.matcher(note_new_editText.getText().toString());
        Matcher matcher_sub;
        String temp;
        while (matcher.find())//matcher.find()用于查找是否有这个字符，有的话返回true
        {
            temp=note_new_editText.getText().toString().substring(matcher.start(),matcher.end());
            Log.e("zjc", "onClick: "+temp );
            matcher_sub=pattern_sub.matcher(temp);
            while(matcher_sub.find())
            {
                Log.e("zjc", "save_note: "+temp.substring(matcher_sub.start(),matcher_sub.end()));

            }
        }
        //notedb.InsertData(note_title.getText().toString(),note_new_editText.getText().toString(),"今天");
        //imagedb.InsertData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    save_upload_note();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }).start();
    }


    private void save_upload_note() throws SQLException //更新并上传笔记
    {
        try {
            //注册JDBC驱动
            Class.forName("org.postgresql.Driver");
            //建立连接
            con = DriverManager.getConnection(url, user, pass);
            if (!con.isClosed()) {
                System.out.println("数据库连接成功");
                Log.e("zjc", "onCreate: 成功");
                String update_content=note_new_editText.getText().toString();
                String query = "Update note set content = '"+update_content+"' where note_id="+note_id;
                statement = con.createStatement();
                statement.executeUpdate(query);
                con.close();
            }
            Looper.prepare();
            Toast.makeText(EditActivity.this,"数据库更新成功",Toast.LENGTH_SHORT).show();
            Looper.loop();

        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动没有安装");
            Log.e("zjc", "onCreate: 数据库未安装");
            //Toast.makeText(MainActivity.this, "未安装", Toast.LENGTH_SHORT).show();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库连接失败");
            Log.e("zjc", "onCreate: 失败");
            Looper.prepare();
            Toast.makeText(EditActivity.this, "服务器数据库未启动", Toast.LENGTH_SHORT).show();
            Looper.loop();
            //Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void init_edit() //实现加粗，斜体等改变
    {
        note_title.setText(title);
        //note_new_editText.setText(Content);
        TextWatcher watcher = new TextWatcher() {
            private int spanStart = -1;
            private int spanEnd = -1;
            Editable editable = note_new_editText.getText();
            int startPosition;
            int textCount;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                editable = note_new_editText.getText();
                Log.d("zjc", "beforeTextChanged: " + start + " " + count + " " + after + " " + s.toString() + " " + editable.toString());
                if (start <= 0) {
                    return;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("zjc", "onTextChanged: " + start + " " + before + " " + " " + count + " " + s.toString() + " " + editable.toString() + " long ");
                if (spanEnd > -1 && spanStart > -1) {
                    note_new_editText.removeTextChangedListener(this);
                    editable.replace(start - (spanEnd - spanStart - 1), start, "");
                    note_new_editText.addTextChangedListener(this);
                    Log.d("zjc", "onTextChanged: " + spanStart + " " + spanEnd);
                    spanStart = -1;
                    spanEnd = -1;
                }
                startPosition = start;
                textCount = count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (startPosition < 0) {
                    return;
                }
                int id;

                id = 2131755235;
                setSpan(s, startPosition, startPosition + textCount, id);
            }

        };
        note_new_editText.addTextChangedListener(watcher);
        Pattern pattern = Pattern.compile("<img src=\"(.*?)\"/>");//创建匹配img的模式
        Pattern pattern_sub=Pattern.compile("\"(.*?)\"");//创建匹配img的模式
        if(Content!=null) {
            Matcher matcher = pattern.matcher(Content);
            Matcher matcher_sub;
            int index = 0;
            while (matcher.find())//matcher.find()用于查找是否有这个字符，有的话返回true
            {
                SpannableString s = new SpannableString(Content.substring(index, matcher.start() - 1));
                note_new_editText.append(s);
                //Log.e("zjc", "onClick: "+temp );
                String temp = Content.substring(matcher.start(), matcher.end());
                matcher_sub = pattern_sub.matcher(temp);
                while (matcher_sub.find()) {
                    Log.e("zjc", "save_note: " + temp.substring(matcher_sub.start(), matcher_sub.end()));
                    Drawable drawable = null;
                    drawable = Drawable.createFromPath(temp.substring(matcher_sub.start() + 1, matcher_sub.end() - 1));
                    int width = 1048;
                    drawable.setBounds(0, 0, width, drawable.getIntrinsicHeight() * width / drawable.getIntrinsicWidth());
                    //drawable.setBounds(0,0, drawable.getIntrinsicWidth() , drawable.getIntrinsicHeight());
                    SpannableString ss = new SpannableString("<img src=\"" + temp.substring(matcher_sub.start(), matcher_sub.end()) + "\"/>");
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    ss.setSpan(imageSpan, 0, ss.length(),
                            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //SpannableString s = new SpannableString("\n");
                    //note_new_editText.append(s);
                    note_new_editText.append(ss, 0, ss.length());

                }
                index = matcher.end() + 1;
            }
            note_new_editText.append(Content.substring(index));
        }
    }


    private void setSpan(Editable editable, int start, int end, int styleId) {
        Resources resources=getResources();
        Log.e("zjc_text", "afterTextChanged: "+"edit" );
        if(start >= 0 && end >= 0 && start != end) {
            removeSpan(editable, start, end);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.RED);
            if(flag_red) {
                TextAppearanceSpan redSpan = new TextAppearanceSpan(
                        null, Typeface.NORMAL, 60,
                        ColorStateList.valueOf(resources.getColor(R.color.red)), null
                );
                editable.setSpan(redSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(flag_black) {
                TextAppearanceSpan redSpan = new TextAppearanceSpan(
                        null, Typeface.NORMAL, 60,
                        ColorStateList.valueOf(resources.getColor(R.color.black)), null
                );
                editable.setSpan(redSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(flag_blue) {
                TextAppearanceSpan redSpan = new TextAppearanceSpan(
                        null, Typeface.NORMAL, 60,
                        ColorStateList.valueOf(resources.getColor(R.color.blue)), null
                );
                editable.setSpan(redSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(flag_green) {
                TextAppearanceSpan redSpan = new TextAppearanceSpan(
                        null, Typeface.NORMAL, 60,
                        ColorStateList.valueOf(resources.getColor(R.color.green)), null
                );
                editable.setSpan(redSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(flag_deep_blue) {
                TextAppearanceSpan redSpan = new TextAppearanceSpan(
                        null, Typeface.NORMAL, 60,
                        ColorStateList.valueOf(resources.getColor(R.color.deep_blue)), null
                );
                editable.setSpan(redSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(flag_yellow) {
                TextAppearanceSpan redSpan = new TextAppearanceSpan(
                        null, Typeface.NORMAL, 60,
                        ColorStateList.valueOf(resources.getColor(R.color.yellow)), null
                );
                editable.setSpan(redSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(flag_bold) {
                TextAppearanceSpan boldSpan = new TextAppearanceSpan(
                        null, Typeface.BOLD, 60,
                        null, null
                );
                editable.setSpan(boldSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(flag_italic) {
                TextAppearanceSpan italicSpan = new TextAppearanceSpan(
                        null, Typeface.ITALIC, 60,
                        null, null
                );
                editable.setSpan(italicSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(flag_underline) {
                TextAppearanceSpan italicSpan = new TextAppearanceSpan(
                        null, Typeface.BOLD_ITALIC, 60,
                        null, null
                );
                editable.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(flag_fontde)//0.5f表示默认字体大小的一半
            {
                TextAppearanceSpan deSpan = new TextAppearanceSpan(
                        null,Typeface.NORMAL, 30,
                        null, null
                );
                editable.setSpan(deSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(flag_fontin)//2.0f表示默认字体大小的两倍
            {
                TextAppearanceSpan inSpan = new TextAppearanceSpan(
                        null,Typeface.NORMAL, 120,
                        null, null
                );
                editable.setSpan(inSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private void removeSpan(Editable editable, int start, int end) {
        if (start == end) {
            return;
        }
        TextAppearanceSpan[] spans = editable.getSpans(start, end, TextAppearanceSpan.class);
        Log.d("richtext", "onClick: "+spans.length);

        for (TextAppearanceSpan span : spans) {
            editable.removeSpan(span);
        }
    }

    private void openAlbum()
    {
        //Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        //startActivityForResult(intent, 2);
        startActivityForResult(intent,2); //打开相册
        /*Intent imgIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imgIntent, 2);*/
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==20)
        {
            String video_path=data.getStringExtra("path");//得到视频的路径
            String video_name=data.getStringExtra("VideoName");//得到视频名字
            datas.add(new Note(video_name,"1",video_path,1,"我的收藏",2,2));
            typeRecyclerAdapter1.notifyDataSetChanged();
        }
        if(resultCode==RESULT_OK){
        //if (requestCode == 2) {
            // 从相册返回的数据
            Log.e(this.getClass().getName(), "Result:" + data.toString());
            if (data != null) {
                // 得到图片的全路径
                //Uri uri = data.getData();
                List<String> pathList = Matisse.obtainPathResult(data);
                Log.e("zjc", "onActivityResult: "+pathList.get((0)));
                try {
                    //String str="sdcard/DCIM/Camera/2022-06-10-124038027.png.jpg";
                    //str="/storage/emulated/0/DCIM/Camera/2022-06-10-124038027.png.jpg";
                    //str=getImageAbsolutePath(this,uri);
                    //Uri tempuri=Uri.fromFile(new File(str));
                    Bitmap temp = null;
                    Toast.makeText(this, pathList.get(0), Toast.LENGTH_SHORT).show();
                    temp=decode_Filepath(pathList.get(0));
                    /*if(tempuri.equals(uri))
                        temp=decodeUri(uri,str);*/
                    //String fileName = System.currentTimeMillis() + ".jpg";
                    //saveBitmapToAPPDirectroy(uri.toString(),fileName);
                    add_photo(temp,pathList.get(0));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void callGallery() {
        Matisse.from(EditActivity.this)
                //.choose(MimeType.ofImage())   //照片显示
                .choose(MimeType.ofAll())   //照片显示
                .countable(true)
                .maxSelectable(1)
                .capture(true) //是否提供拍照功能
                /*.addFilter(new Filter() {
                    @Override
                    protected Set<MimeType> constraintTypes() {
                        return new HashSet()
                        {
                            add(MimeType.VIDEO);
                        }
                    }

                    @Override
                    public IncapableCause filter(Context context, Item item) {
                        return null;
                    }
                })*/
                .captureStrategy(new CaptureStrategy(true,"9c9a4d8df.fileProvider"))
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(320)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)  // 缩略图的比例
                .imageEngine(new GlideImageEngine()) //使用的图片加载引擎
                //.showPreview(false) // Default is `true`
                .forResult(23);  // 设置作为标记的请求码
    }



    private Bitmap decode_Filepath(String selectedImage) throws FileNotFoundException, FileNotFoundException {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile((selectedImage), o);
        // The new size we want to scale to
        final int REQUIRED_SIZE =720;//700
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp <= screenWidth
                    && height_tmp <= screenHeight) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeFile((selectedImage), o2);
    }


    private Bitmap decodeUri(Uri selectedImage,String str) throws FileNotFoundException, FileNotFoundException {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        //BitmapFactory.decodeFile(str,o);
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE =720;//700
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp <= screenWidth
                    && height_tmp <= screenHeight) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
        //return BitmapFactory.decodeFile(str,  o2);
    }

    private void add_photo(Bitmap temp,String photo_name) {
        ImageSpan imageSpan = new ImageSpan(EditActivity.this, temp,ImageSpan.ALIGN_BOTTOM);

        SpannableString ss = new SpannableString("<img src=\""+photo_name+"\"/>");

                /*demo.getEditableText().setSpan(imageSpan, 0, 3,
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);*/
        ss.setSpan(imageSpan, 0, ss.length(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString s = new SpannableString("\n");
        EditText note_new_editText=recyclerView1.getChildAt(0).findViewById(R.id.note_new_editText);
        note_new_editText.append(s);
        note_new_editText.append(ss,0,ss.length());
        note_new_editText.append(s);
    }



    //保存图片至app私有目录
    private void saveBitmapToAPPDirectroy(String path, String fileName) {
        File file = null;
        //File fileDirectory = C.FilePath.getPicturesDirectory();//即：/storage/emulated/0/Android/data/包名/files/Pictures/
        file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

        //改成保存图片的名字
        Uri imageUri;
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(file);
        } else {
            imageUri = FileProvider.getUriForFile(EditActivity.this, "com.example.mind_maker.fileProvider", file);
        }
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromStream(getContentResolver().openInputStream(imageUri),path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        /*int width = ContentToSpannableString.getScreenRealWidth(NoteNewActivity.this);*/
        int width = 1048;
        drawable.setBounds(0,0, width, drawable.getIntrinsicHeight() * width / drawable.getIntrinsicWidth());
        //drawable.setBounds(0,0, drawable.getIntrinsicWidth() , drawable.getIntrinsicHeight());
        SpannableString spannableString = new SpannableString("<img src=\"" + fileName + "\"/>");
        //Log.d("图片Uri",tempUri.toString());
        //BitmapDrawable bd = (BitmapDrawable) drawable;
        //Bitmap bp = bd.getBitmap();
        //bp.setDensity(160);
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        spannableString.setSpan(span,0,spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Log.d("spanString：",spannableString.toString());
    }

    private void getWordsSize() {
        SharedPreferences sharedPreferences = getSharedPreferences("Setting", MODE_PRIVATE);
        String wordSizePrefs = sharedPreferences.getString("WordSize", "正常");
        int[] boldStyles = {R.style.BoldStyle, R.style.BoldStyle2, R.style.BoldStyle3};
        int[] highlightStyles = {R.style.HighlightStyle, R.style.HighlightStyle2, R.style.HighlightStyle3};
        int[] titleStyles = {R.style.TitleStyle, R.style.TitleStyle2, R.style.TitleStyle3};

        switch (wordSizePrefs) {
            case "正常":
                boldStyle = boldStyles[0];
                highlightStyle = highlightStyles[0];
                titleStyle = titleStyles[0];
                wordSize = 20;
                break;
            case "大":
                boldStyle = boldStyles[1];
                highlightStyle = highlightStyles[1];
                titleStyle = titleStyles[1];
                wordSize = 25;
                break;
            case "超大":
                boldStyle = boldStyles[2];
                highlightStyle = highlightStyles[2];
                titleStyle = titleStyles[2];
                wordSize = 30;
                break;
        }
    }


    public void contentToSpanStr_patchwork(Context context, String noteContent, byte[] bytes,int start,int end)//恢复富文本
    {
        String PatternNoteContent = noteContent;
        //note_new_editText.setText(noteContent);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(PatternNoteContent);
        //getWordsSize(context);
        Parcel parcel1 = Parcel.obtain();
        parcel1.unmarshall(bytes, 0, bytes.length);
        parcel1.setDataPosition(0);
        SaveTextAppearanceSpan data = parcel1.readParcelable(SaveTextAppearanceSpan.class.getClassLoader());
        ArrayList<byte[]> temp = data.getSpansList();
        for (int i = 0; i < temp.size(); i++){
            Parcel parcel2 = Parcel.obtain();
            parcel2.unmarshall(temp.get(i),0,temp.get(i).length);
            parcel2.setDataPosition(0);
            //Parcel parcel3 = parcel2.readParcelable(Parcel.class.getClassLoader());

            Log.d("二进制", "AddNote: "+new String(Base64.encode(parcel2.marshall(),Base64.DEFAULT)));
            TextAppearanceSpan temp1 = new TextAppearanceSpan(parcel2);
            parcel2.recycle();
            int sp = pxToSp(context, temp1.getTextSize());
//            Log.d("TextAppearanceSpan", "ContentToSpanStr: "+temp1.getTextSize()+temp1.getTextColor()+"sp" +sp);
//            Log.d("二进制", "AddNote: "+data.getSpanStartList().get(i) +" "+ data.getSpanEndList().get(i));
            Editable editable = mEdittext.getText();
            Log.e("zjc_span", "contentToSpanStr_patchwork: "+mEdittext.getText().toString() );
            if(data.getSpanStartList().get(i)+start>mEdittext.length())
                break;
            if(data.getSpanEndList().get(i)+start>mEdittext.length())
                editable.setSpan(temp1, data.getSpanStartList().get(i)+start,mEdittext.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            else
                editable.setSpan(temp1, data.getSpanStartList().get(i)+start, data.getSpanEndList().get(i)+start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            spannableStringBuilder.setSpan(temp1,data.getSpanStartList().get(i),data.getSpanEndList().get(i),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            spannableStringBuilder.setSpan(setStyle(context,sp,temp1.getTextColor()),data.getSpanStartList().get(i),data.getSpanEndList().get(i),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
//        return spannableStringBuilder;//.append("\u200b");
    }
    public static int pxToSp(Context context, float pxValue) {
        if (context == null) {
            return -1;
        }

        final float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(pxValue / scaledDensity + 0.5f);
    }


    public void share(Context context){

        Intent sendIntent = new Intent();

        sendIntent.setAction(Intent.ACTION_SEND);

        sendIntent.putExtra(Intent.EXTRA_TEXT, "测试");

        sendIntent.setType("text/plain");

        context.startActivity(sendIntent);

    }




}
