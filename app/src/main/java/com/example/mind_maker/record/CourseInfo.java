package com.example.mind_maker.record;

import java.util.ArrayList;
import java.util.List;

public class CourseInfo extends BaseInfo {
    public int id;
    public String name;

    public List<ChapterInfo> chapterInfos = new ArrayList<>();
}
