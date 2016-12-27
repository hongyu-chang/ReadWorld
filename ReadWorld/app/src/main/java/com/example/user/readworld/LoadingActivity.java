package com.example.user.readworld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;

public class LoadingActivity extends AppCompatActivity {

    TextView t;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        StatusBarUtil.setColor(LoadingActivity.this, 0xbd7427);

        t = (TextView) findViewById(R.id.titl);
        pb = (ProgressBar) findViewById(R.id.progressBar2);

        pb.setVisibility(View.VISIBLE);

        Typeface mainType = Typeface.createFromAsset(getAssets(),"fonts/HPSimplified_Bd.ttf");
        t.setTypeface(mainType);


        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        boolean isFirst = setting.getBoolean("isFirst", true);
        //String id = setting.getString("id", "0");
        if(!isFirst) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent().setClass(LoadingActivity.this, MainActivity.class));
                    finish();
                }
            }, 2000);

        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent().setClass(LoadingActivity.this, SignIn.class));
                    finish();
                }
            }, 3000);
        }


        //LoadingActivity.this.finish();


    }
}
