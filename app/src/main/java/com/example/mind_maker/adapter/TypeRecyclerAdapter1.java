package com.example.mind_maker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mind_maker.media.play;
import com.example.mind_maker.EditActivity;
import com.example.mind_maker.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mind_maker.record.Note;
import com.example.mind_maker.record.Note_adapter;
import com.example.mind_maker.util.KeyboardUtil;
import com.example.mind_maker.util.SaveTextAppearanceSpan;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeRecyclerAdapter1 extends
        RecyclerView.Adapter<TypeRecyclerAdapter1.BaseViewHolder> {

    private String baseInfo = ",这里是一个纯文字的标题的简介部分内容，这里是一个纯文字的标题的简介部分内容，这里是一个纯文字的标题的简介部分内容，这里是一个纯文字的标题的简介部分内容";
    private List<Note> datas;
    private Context context;
    public OnItemClickListener onItemClickListener;
    public interface OnItemClickListener//长按接口
    {
        public void OnItemClick(View v,int position);
    }

    public void setOnItemClickListener(TypeRecyclerAdapter1.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public TypeRecyclerAdapter1(Context context, List<Note> datas){
        this.context = context;
        this.datas = datas;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                               int viewType) {
        BaseViewHolder viewHolder = null;
        switch (viewType){
            case 0:
                View itemView0 = LayoutInflater.from(context).inflate(
                        R.layout.adapter_note_word,
                        parent,false);
                viewHolder = new WordHolder(itemView0);
                break;
            case 1:
                View itemView1 = LayoutInflater.from(context).inflate(
                        R.layout.adapter_note_radio,
                        parent,false);
                viewHolder = new RadioHolder(itemView1);
                break;
            case 2:
                View itemView2 = LayoutInflater.from(context).inflate(
                        R.layout.adapter_notes_video,
                        parent,false);
                viewHolder = new VideoHolder(itemView2);
                break;
                /*throw new IllegalStateException("Unexpected value: " + viewType);*/
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder,
                                 int position) {

        //holder.nameTxt.setText(datas.get(position).getContent());
        int type = getItemViewType(position);
        switch (type){
            case 0:
                WordHolder wordHolder = (WordHolder) holder;
                bindWord(wordHolder,position);
                init_edit(wordHolder.descriptionText,position);
                break;
            case 1:
                RadioHolder radioHolder = (RadioHolder) holder;
                bindRadio(radioHolder,position);
                break;
            case 2:
                VideoHolder videoHolder = (VideoHolder) holder;
                bindVideo(videoHolder,position);
                break;
        }
    }

    private void bindRadio(RadioHolder holder,int position){
        holder.radio_name.setText(datas.get(position).getName());
        if(datas.get(position).getStart_time()>=datas.get(position).getDuration())
        {
            holder.seekBar.setProgress(1);
            holder.iv_play.setImageResource(R.drawable.play);
        }
        if(datas.get(position).getStart_time()==-1)
            holder.tv_start.setText("00:00");
        else {
            holder.seekBar.setProgress(datas.get(position).getStart_time());
            holder.tv_start.setText(calculateTime(datas.get(position).getStart_time() / 1000));
            if(datas.get(position).getStart_time()>=datas.get(position).getDuration())
            {
                holder.seekBar.setProgress(0);
                holder.tv_start.setText("00:00");
                //holder.iv_play.setImageResource(R.drawable.play);
            }
        }
        if(datas.get(position).getDuration()==-1)
            holder.tv_end.setText("00:00");
        else {
            holder.seekBar.setMax(datas.get(position).getDuration());
            holder.tv_end.setText(calculateTime(datas.get(position).getDuration()/1000));
        }
        holder.iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.OnItemClick(view,position);//设置点击事件
            }
        });
    }

    private void bindVideo(VideoHolder holder,int position){
        holder.video_name.setText(datas.get(position).getName());
        holder.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(context,play.class);
                intent.putExtra("path",datas.get(position).getContent());
                context.startActivity(intent);
            }
        });
    }

    private void bindWord(WordHolder holder,int position){
        holder.descriptionText.setText(datas.get(position).getContent());
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

    private void init_edit(EditText note_new_editText, int position) //实现加粗，斜体等改变
    {
        //note_title.setText(title);
        //note_new_editText.setText(Content);
        /*if(note_new_editText.getTag() instanceof TextWatcher)
        {
            note_new_editText.removeTextChangedListener((TextWatcher)note_new_editText.getTag());
        }*/
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
                if(EditActivity.multi_flag)
                {

                }
                else {
                    int id;
                    id = 2131755235;
                    //String m_str;
                    setSpan(s, startPosition, startPosition + textCount, id);
                    Log.e("zjc_text", "afterTextChanged: " + "ty" + startPosition + " " + (startPosition + textCount));
                    Log.e("zjc", "afterTextChanged: " + s.toString());
                    byte[] bytes = get_Span(s);
                    datas.get(position).setSpan_bytes(bytes);
                    datas.get(position).setContent(s.toString());
                    EditActivity.m_change_flag = true;
                    EditActivity.change_flag = true;
                }
            }
        };
        note_new_editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    note_new_editText.addTextChangedListener(watcher);
                }
                else
                {
                    note_new_editText.removeTextChangedListener(watcher);
                    KeyboardUtil.hideKeyboard((Activity) context);
                }
            }
        });
        if(EditActivity.min!=0||EditActivity.max!=0)
        {
            setSpan(note_new_editText.getText(), EditActivity.min, EditActivity.max, 0);
            Log.e("zjc_text", "afterTextChanged: "+"tyw"+EditActivity.min+"  "+EditActivity.max);
            //Log.e("zjc", "afterTextChanged: "+s.toString());
            byte[] bytes=get_Span(note_new_editText.getText());
            datas.get(position).setSpan_bytes(bytes);
            datas.get(position).setContent(note_new_editText.getText().toString());
            EditActivity.min=0;
            EditActivity.max=0;
        }
        //note_new_editText.setTag(watcher);
        if(datas.get(position).getContent()!=null) {
            Log.e("zjcdebug", "init_edit: "+datas.get(position).getContent() );
            contentToSpanStr(note_new_editText, datas.get(position).getContent(), datas.get(position).getSpan_bytes());
            /*Matcher matcher = pattern.matcher(Content);
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
                    SpannableString ss = new SpannableString("<img src='" + temp.substring(matcher_sub.start(), matcher_sub.end()) + "'/>");
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    ss.setSpan(imageSpan, 0, ss.length(),
                            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //SpannableString s = new SpannableString("\n");
                    //note_new_editText.append(s);
                    note_new_editText.append(ss, 0, ss.length());
                }
                index = matcher.end() + 1;
            }
            note_new_editText.append(Content.substring(index));*/
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

    public void contentToSpanStr(EditText note_new_editText, String noteContent, byte[] bytes)//恢复富文本
    {
        String PatternNoteContent = noteContent;
        int text_start=-1,text_end=-1;
        if(EditActivity.Content==null)
        {
            text_start=0;
            text_end=noteContent.length();
        }
        else
        {
            Pattern pattern=Pattern.compile(noteContent);
            Log.e("zjcdebug", "contentToSpanStr: "+EditActivity.Content );
            Matcher matcher=pattern.matcher(EditActivity.Content);
            while(matcher.find())
            {
                text_start=matcher.start();
                text_end=matcher.end();
            }
        }
        note_new_editText.setText(noteContent);
        Log.e("zjcdebug", "contentToSpanStr: "+text_start+text_end );
        if(text_start==-1||text_end==-1)//未找到匹配项
            return;
        if(bytes==null)
            return;
        Log.e("zjcdebug", "contentToSpanStr: "+noteContent);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(PatternNoteContent);
//        getWordsSize(context);
        Parcel parcel1 = Parcel.obtain();
        parcel1.unmarshall(bytes, 0, bytes.length);
        parcel1.setDataPosition(0);
        SaveTextAppearanceSpan data = parcel1.readParcelable(SaveTextAppearanceSpan.class.getClassLoader());
        ArrayList<byte[]> temp = data.getSpansList();
        Log.e("TAGzjc", "contentToSpanStr: "+temp.size() );
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
            Editable editable = note_new_editText.getText();
            Log.e("zjc_span", "contentToSpanStr: "+editable.toString() );
            /*if(noteContent.substring(data.getSpanStartList().get(i)-text_start+1,data.getSpanStartList().get(i)-text_start+3)=="img")
            {
                Log.e("zjc_span", "contentToSpanStr: "+noteContent.substring(data.getSpanStartList().get(i)-text_start+1,data.getSpanEndList().get(i)-text_start));
            }*/
            if(data.getSpanStartList().get(i)-text_start>editable.toString().length())
                break;
            else if(data.getSpanStartList().get(i)-text_start<0)
                continue;
            else if(data.getSpanEndList().get(i)-text_start>editable.toString().length())
            {
                editable.setSpan(temp1, data.getSpanStartList().get(i)-text_start, editable.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            }
            else
                editable.setSpan(temp1, data.getSpanStartList().get(i)-text_start, data.getSpanEndList().get(i)-text_start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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

    private void setSpan(Editable editable, int start, int end, int styleId) {
        //Resources resources = getResource();
        if(start >= 0 && end >= 0 && start != end) {
            removeSpan(editable, start, end);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FF0000"));
            if(EditActivity.flag_red) {
                TextAppearanceSpan redSpan = new TextAppearanceSpan(
                        null, Typeface.NORMAL, 60,
                        ColorStateList.valueOf(Color.parseColor("#FF0000")), null
                );
                editable.setSpan(redSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(EditActivity.flag_black) {
                TextAppearanceSpan redSpan = new TextAppearanceSpan(
                        null, Typeface.NORMAL, 60,
                        ColorStateList.valueOf(Color.parseColor("#000000")), null
                );
                editable.setSpan(redSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(EditActivity.flag_blue) {
                TextAppearanceSpan redSpan = new TextAppearanceSpan(
                        null, Typeface.NORMAL, 60,
                        ColorStateList.valueOf(Color.parseColor("#00E5FF")), null
                );
                editable.setSpan(redSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(EditActivity.flag_green) {
                TextAppearanceSpan redSpan = new TextAppearanceSpan(
                        null, Typeface.NORMAL, 60,
                        ColorStateList.valueOf(Color.parseColor("#00FF00")), null
                );
                editable.setSpan(redSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(EditActivity.flag_deep_blue) {
                TextAppearanceSpan redSpan = new TextAppearanceSpan(
                        null, Typeface.NORMAL, 60,
                        ColorStateList.valueOf(Color.parseColor("#3D5AFE")), null
                );
                editable.setSpan(redSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(EditActivity.flag_yellow) {
                TextAppearanceSpan redSpan = new TextAppearanceSpan(
                        null, Typeface.NORMAL, 60,
                        ColorStateList.valueOf(Color.parseColor("#FFFF00")), null
                );
                editable.setSpan(redSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(EditActivity.flag_bold) {
                TextAppearanceSpan boldSpan = new TextAppearanceSpan(
                        null, Typeface.BOLD, 60,
                        null, null
                );
                editable.setSpan(boldSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(EditActivity.flag_italic) {
                TextAppearanceSpan italicSpan = new TextAppearanceSpan(
                        null, Typeface.ITALIC, 60,
                        null, null
                );
                editable.setSpan(italicSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(EditActivity.flag_underline) {
                TextAppearanceSpan italicSpan = new TextAppearanceSpan(
                        null, Typeface.BOLD_ITALIC, 60,
                        null, null
                );
                editable.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(EditActivity.flag_fontde)//0.5f表示默认字体大小的一半
            {
                TextAppearanceSpan deSpan = new TextAppearanceSpan(
                        null,Typeface.NORMAL, 30,
                        null, null
                );
                editable.setSpan(deSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if(EditActivity.flag_fontin)//2.0f表示默认字体大小的两倍
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

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).getContent_style();
    }


     class BaseViewHolder extends RecyclerView.ViewHolder{

        TextView nameTxt;

        public BaseViewHolder(View itemView) {
            super(itemView);
            //nameTxt =itemView.findViewById(R.id.ad_news_title);
        }
    }

    class WordHolder extends BaseViewHolder{

        EditText descriptionText;

        public WordHolder(View itemView) {
            super(itemView);
            descriptionText = itemView.findViewById(R.id.note_new_editText);
        }
    }

    class VideoHolder extends BaseViewHolder{
        LinearLayout video;
        TextView video_name;
        public VideoHolder(View itemView) {
            super(itemView);
            video=itemView.findViewById(R.id.video);
            video_name=itemView.findViewById(R.id.video_name);
            //descriptionText = itemView.findViewById(R.id.note_new_editText);

        }
    }

    class RadioHolder extends BaseViewHolder{

        //ImageView image;
        TextView radio_name,tv_start,tv_end;
        ImageView iv_play;
        SeekBar seekBar;
        public RadioHolder(View itemView) {
            super(itemView);
            radio_name=itemView.findViewById(R.id.radio_name);
            tv_start=itemView.findViewById(R.id.tv_start);
            tv_end= itemView.findViewById(R.id.tv_end);
            iv_play=itemView.findViewById(R.id.iv_play);
            seekBar=itemView.findViewById(R.id.pb);
            //image =itemView.findViewById(R.id.ad_news_image0);
        }
    }


}
