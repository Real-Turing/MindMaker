package com.example.mind_maker.apter;



import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.example.mind_maker.R;

import jieba_android.JiebaSegmenter;

public class TfIdfAnalyzer {
    /**
     * idfMap集合
     */
    static HashMap<String, Double> idfMap;
    /**
     * 停止词集合
     */
    static HashSet<String> stopWordsSet;
    /**
     * idfMedian
     */
    static double idfMedian;

    /**
     *
     * 功能描述: tfidf分析方法
     *
     * @param content 需要分析的文本/文档内容
     * @param top 需要返回的tfidf值最高的N个关键词，若超过content本身含有的词语上限数目，则默认返回全部
     * @return 关键词
     * @auther: lizongke
     * @date: 2020/1/13 9:58
     */

    public ArrayList<Keyword> analyze(String content, int top, Context context) {
        Resources resources = context.getResources();
        ArrayList<Keyword> keywordList = new ArrayList<>();
//        String stop_path = "D:\\System-final\\jieba-android-master\\app\\src\\main\\res\\dict\\stop_words.txt";
//        String idf_dict_path="D:\\System-final\\jieba-android-master\\app\\src\\main\\res\\dict\\idf_dict.txt";
//        File file1 = new File(stop_path);
//        File file2=new File(idf_dict_path);
//            if (stopWordsSet.isEmpty()) {
                stopWordsSet = new HashSet<>();
                InputStream inputStream = resources.openRawResource(R.raw.stop_words);
                loadStopWords(stopWordsSet, inputStream);
//            }
//            if (idfMap.isEmpty()) {
                idfMap = new HashMap<>();
                InputStream inputStream2 = resources.openRawResource(R.raw.idf_dict);
                loadIdfMap(idfMap, inputStream2);
                //loadIDFMap(idfMap, this.getClass().getResourceAsStream("idf_dict.txt"));
//
        Map<String, Double> tfMap = getTf(content);
        for (String word : tfMap.keySet()) {
            // 若该词不在idf文档中，则使用平均的idf值(可能定期需要对新出现的网络词语进行纳入)
            if (idfMap.containsKey(word)) {
                keywordList.add(new Keyword(word, idfMap.get(word) * tfMap.get(word)));
            } else {
                keywordList.add(new Keyword(word, idfMedian * tfMap.get(word)));
            }
        }

        Collections.sort(keywordList);

        if (keywordList.size() > top) {
            int num = keywordList.size() - top;
            for (int i = 0; i < num; i++) {
                keywordList.remove(top);
            }
        }
        return keywordList;
    }

    /**
     *
     * 功能描述: tf值计算公式 tf=N(i,j)/(sum(N(k,j) for all k))
     * N(i,j)表示词语Ni在该文档d（content）中出现的频率，sum(N(k,j))代表所有词语在文档d中出现的频率之和
     *
     * @param content 待分析文本
     * @return tf集合
     * @auther: lizongke
     * @date: 2020/1/13 10:09
     */
    private Map<String, Double> getTf(String content) {
        Map<String, Double> tfMap = new HashMap<>();
        if (content == null || "".equals(content)) {
            return tfMap;
        }
        JiebaSegmenter segmenter = new JiebaSegmenter();
        ArrayList<String> segments = segmenter.getJiebaSegmenterSingleton().getDividedString(content);
//        List<String> segments = segmenter.sentenceProcess(content);
        Map<String, Integer> freqMap = new HashMap<>();

        int wordSum = 0;
        for (String segment : segments) {
            //停用词不予考虑，单字词不予考虑
            if (!stopWordsSet.contains(segment) && segment.length() > 1) {
                wordSum++;
                if (freqMap.containsKey(segment)) {
                    freqMap.put(segment, freqMap.get(segment) + 1);
                } else {
                    freqMap.put(segment, 1);
                }
            }
        }

        // 计算double型的tf值
        for (String word : freqMap.keySet()) {
            tfMap.put(word, freqMap.get(word) * 0.1 / wordSum);
        }

        return tfMap;
    }

    /**
     * 默认jieba分词的停词表
     * url:https://github.com/yanyiwu/nodejieba/blob/master/dict/stop_words.utf8
     *
     * @param set 停止词集合
     * @param in 停止词输入流
     */
    private void loadStopWords(Set<String> set, InputStream in) {
        BufferedReader bufr;
        try {
            InputStreamReader read =new InputStreamReader (in,"utf-8");
            bufr = new BufferedReader(read);
            String line = null;
            while ((line = bufr.readLine()) != null) {
                set.add(line.trim());
            }
            try {
                bufr.close();
            } catch (IOException e) {
//                log.info(e.toString());
            }
        } catch (Exception e) {
//            log.info(e.toString());
        }
    }

    /**
     * idf值本来需要语料库来自己按照公式进行计算，不过jieba分词已经提供了一份很好的idf字典，所以默认直接使用jieba分词的idf字典
     * url:https://raw.githubusercontent.com/yanyiwu/nodejieba/master/dict/idf.utf8
     *
     * @param map idf集合
     * @param in idf输入流
     */
    private void loadIdfMap(Map<String, Double> map, InputStream in) {
        BufferedReader bufr;
        try {
            bufr = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = bufr.readLine()) != null) {
                String[] kv = line.trim().split(" ");
                map.put(kv[0], Double.parseDouble(kv[1]));
            }
            try {
                bufr.close();
            } catch (IOException e) {
//                log.info(e.toString());
            }

            // 计算idf值的中位数
            List<Double> idfList = new ArrayList<>(map.values());
            Collections.sort(idfList);
            idfMedian = idfList.get(idfList.size() / 2);
        } catch (Exception e) {
//            log.info(e.toString());
        }
    }

}
