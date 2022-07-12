package com.example.mind_maker.apter;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import com.example.mind_maker.apter.TfIdfAnalyzer;
import com.example.mind_maker.apter.Keyword;

import java.util.ArrayList;

public class UserTest {
    User nowUser=new User();    //当前用户（被推荐的用户）
    Integer UserNum=5;  //用户数量
    ArrayList<User> userArr=new ArrayList<User>();
    MonkText monkText=new MonkText();
    @RequiresApi(api = Build.VERSION_CODES.N)
    public User init(Context context)
    {
        //初始化当前用户:
        String nows="“毕业出路”成为大学生活的“指挥棒”，人们无法也不能对这样的结果给予非黑即白式的评价。对不同的学校、不同的专业、不同的生活环境下的大学生而言，是否需要从大一就开始考虑毕业出路，是没法给出定式答案的。只不过，将这一问题放置于社会发展视角下看，仍然得说大学生活不应该唯“毕业出路”论。而更需要指出的是，令人焦虑的“毕业出路”，其核心不是担忧找不到工作，而是希望找到一份社会认知意义上的好的工作——进大厂亦或考公。从高校连年扩招能够看出，高等教育已经完全具备成为一项普惠性质的知识服务的条件。这意味着高等教育不再是一种精英化教育。";
        nowUser.PageString.add(nows);
        for(int i=0;i<nowUser.PageString.size();i++) {
            ArrayList<Keyword> list = keywords(nowUser.PageString.get(i), context);
            for (Keyword word : list) {
                nowUser.addKeyWordMap(word.getName(), (float) word.getTfidfvalue());
            }
        }
        nowUser.updateKeyWordList();

        for(int i=0;i<UserNum;i++) {
            User user=new User();
            userArr.add(user);
        }
        //给每个用户默认分配三篇收藏笔记
        userArr.get(0).PageString.add(monkText.textList[0]);
        userArr.get(0).PageString.add(monkText.textList[1]);
        userArr.get(0).PageString.add(monkText.textList[2]);

        userArr.get(1).PageString.add(monkText.textList[3]);
        userArr.get(1).PageString.add(monkText.textList[4]);
        userArr.get(1).PageString.add(monkText.textList[5]);

        userArr.get(2).PageString.add(monkText.textList[6]);
        userArr.get(2).PageString.add(monkText.textList[7]);
        userArr.get(2).PageString.add(monkText.textList[8]);

        userArr.get(3).PageString.add(monkText.textList[9]);
        userArr.get(3).PageString.add(monkText.textList[10]);
        userArr.get(3).PageString.add(monkText.textList[11]);

        userArr.get(4).PageString.add(monkText.textList[12]);
        userArr.get(4).PageString.add(monkText.textList[13]);
        userArr.get(4).PageString.add(monkText.textList[14]);

        //计算这些用户的关键字列表
        for (int j=0;j< userArr.size();j++) {
            for (int i = 0; i < userArr.get(j).PageString.size(); i++) {
                ArrayList<Keyword> list = keywords(userArr.get(j).PageString.get(i), context);
                for (Keyword word : list) {
                    userArr.get(j).addKeyWordMap(word.getName(), (float) word.getTfidfvalue());
                }
            }
            userArr.get(j).updateKeyWordList();
        }
        //协同过滤
        User simuser=findSimilarUser(nowUser);
        return simuser;

    }
    public ArrayList<Keyword> keywords(String content, Context context) {
        //去除空格和特殊字符
        String regEx = "[\n\r\t`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？^p]";
        String aa = "";
        content = content.replaceAll(regEx, aa).replaceAll("　", "").replaceAll(" ", "");
        ArrayList<String> keywords = new ArrayList<>();
        //提取10个关键词
        TfIdfAnalyzer tfIdfAnalyzer = new TfIdfAnalyzer();
        int top = 5;
        ArrayList<Keyword> list = tfIdfAnalyzer.analyze(content, top, context);
//        for (Keyword word : list) {
//            keywords.add(word.getName());
//        }
//        return keywords;
        return list;
    }


    public User findSimilarUser(User user)
    {//找到与当前用户相似度最高的几个用户
        ArrayList<User> otherUserList=userArr;
        otherUserList.remove(user);
        Integer SimilarValue[]=new Integer[otherUserList.size()];
        Integer maxSimilarValue=-999;
        Integer maxIndex=-1;
        for(int i=0;i<otherUserList.size();i++)
        {
            SimilarValue[i]=computeUserSimilar(user,otherUserList.get(i));
            if(SimilarValue[i]>maxSimilarValue)
            {
                maxIndex=i;
                maxSimilarValue=SimilarValue[i];
            }
        }
        return otherUserList.get(maxIndex);
    }

    public Integer computeUserSimilar(User u1,User u2) //计算用户的相似度
    {
        ArrayList<String> keyWord1=u1.getKeyWordList();
        ArrayList<String> keyWord2=u2.getKeyWordList();
        Integer SimilarNum=0;       //匹配到的关键词个数
        for(String str:keyWord1)
        {
            if(keyWord2.contains(str))
            {
                SimilarNum++;
            }
        }
        return SimilarNum;
    }
}
