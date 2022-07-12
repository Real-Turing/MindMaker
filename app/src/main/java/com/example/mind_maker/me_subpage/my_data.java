package com.example.mind_maker.me_subpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.mind_maker.MeActivity;
import com.example.mind_maker.R;
import androidx.appcompat.app.AppCompatActivity;

public class my_data extends AppCompatActivity implements View.OnClickListener {
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_data);
        init();
        click_init();
    }

    private void click_init() {
        back.setOnClickListener(this);
    }

    private void init()
    {
        back=findViewById(R.id.back);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.back:
                Intent intent=new Intent();
                intent.setClass(my_data.this, MeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
        }
    }
}
