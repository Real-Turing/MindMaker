package com.example.mind_maker.apter;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {     //用于协同过滤笔记推荐的用户类

    String name;        //用户名
    private String Id;          // 唯一标识用户的Id
    Integer KeyWordSize=5;      //关键词个数
    private ArrayList<String>KeyWordList=new ArrayList<String>();       //关键词列表
    HashMap<String,Float> KeyWordMAP=new HashMap<String, Float>();  //关键词集合
    public List<String> PageString=new ArrayList<String>();
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getId() {
        return Id;
    }
    public ArrayList getKeyWordList()
    {
        return KeyWordList;
    }
    public void updateKeyWordList()
    {//在对关键词Map进行增删修改操作后要更新维护关键词列表
        computeKeyWordList();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addKeyWordMap(String str, float inf) {
        Float v=KeyWordMAP.putIfAbsent(str,inf);
        if(v!=null) //所指定的 key 已经在 HashMap 中存在，返回和这个 key 值对应的 value
        {//将其TF-IDF值进行叠加，表示用以加强用户对该关键词的感兴趣程度
            KeyWordMAP.compute(str,(key,value)->value+inf);
        }
    }
    private void computeKeyWordList()    //计算TF-IDF值前五的关键词并加入到关键词列表
    {
        KeyWordList.clear();
        List<Map.Entry<String,Float>> lis = new ArrayList(KeyWordMAP.entrySet());
        Collections.sort(lis, (o1, o2) -> (int)(10000*o2.getValue() - 10000*o1.getValue()));//升序
        for(int i=0;i<KeyWordSize;i++)
        {
            KeyWordList.add(lis.get(i).getKey());
        }
    }
}
