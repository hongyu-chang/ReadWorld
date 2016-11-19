package com.example.user.readworld;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class StoreInfoActivity extends AppCompatActivity {

    private ImageView image;
    private ImageView star;
    private TextView store;
    private TextView city;
    private TextView addr;
    private TextView intro_;
    private TextView time;
    private TextView phone_;
    private TextView howTo;
    private boolean isFavorite;

    float x1 = 0;
    float y1 = 0;
    float x2 = 0;
    float y2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_info);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        image = (ImageView) findViewById(R.id.imageView);
        star = (ImageView)findViewById(R.id.star);
        store = (TextView) findViewById(R.id.title);
        city = (TextView) findViewById(R.id.cityContent);
        addr = (TextView) findViewById(R.id.addressContent);
        intro_ = (TextView) findViewById(R.id.introContent);
        time = (TextView) findViewById(R.id.timeContent);
        phone_ = (TextView) findViewById(R.id.phoneContent);
        howTo = (TextView) findViewById(R.id.arriveContent);
        isFavorite = false;

        Intent intent = this.getIntent();
        int index = intent.getIntExtra("index", 1);
        String[] name = intent.getStringArrayExtra("name");
        String[] representImage = intent.getStringArrayExtra("representImage");
        String[] intro = intent.getStringArrayExtra("intro");
        String[] cityName = intent.getStringArrayExtra("cityName");
        String[] address = intent.getStringArrayExtra("address");
        String[] longitude = intent.getStringArrayExtra("longitude");
        String[] latitude = intent.getStringArrayExtra("latitude");
        String[] openTime = intent.getStringArrayExtra("openTime");
        String[] phone = intent.getStringArrayExtra("phone");
        String[] email = intent.getStringArrayExtra("email");
        String[] facebook = intent.getStringArrayExtra("facebook");
        String[] website = intent.getStringArrayExtra("website");
        String[] arriveWay = intent.getStringArrayExtra("arriveWay");

        if(!representImage[index].isEmpty()) {
            Picasso.with(this).load(representImage[index]).fit().centerCrop().into(image);
        }
        store.setText(name[index]);
        city.setText(cityName[index]);
        addr.setText(address[index]);
        intro_.setText(intro[index]);
        time.setText(openTime[index]);
        phone_.setText(phone[index]);
        howTo.setText(arriveWay[index]);

        star.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(isFavorite == false) {
                    star.setImageResource(R.drawable.star);
                    Toast.makeText(StoreInfoActivity.this, "已加到最愛", Toast.LENGTH_SHORT).show();
                    isFavorite = true;
                }
                else {
                    star.setImageResource(R.drawable.star0);
                    Toast.makeText(StoreInfoActivity.this, "已移除最愛", Toast.LENGTH_SHORT).show();
                    isFavorite = false;
                }

                return false;
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            this.finish();
            overridePendingTransition(R.anim.right_in_2, R.anim.right_out_2);
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            // 按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }

        if(event.getAction() == MotionEvent.ACTION_UP) {
            // 離開的时候
            x2 = event.getX();
            y2 = event.getY();

            if(y1 - y2 > 50) {
                Toast.makeText(this.getApplicationContext(), "向上滑", Toast.LENGTH_SHORT).show();
            } else if(y2 - y1 > 50) {
                Toast.makeText(this.getApplicationContext(), "向下滑", Toast.LENGTH_SHORT).show();
            } else if(x1 - x2 > 50) {
                Toast.makeText(this.getApplicationContext(), "向左滑", Toast.LENGTH_SHORT).show();
            } else if(x2 - x1 > 50) {
                this.finish();
                overridePendingTransition(R.anim.right_in_2, R.anim.right_out_2);
                Toast.makeText(this.getApplicationContext(), "向右滑", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onTouchEvent(event);
    }

}
