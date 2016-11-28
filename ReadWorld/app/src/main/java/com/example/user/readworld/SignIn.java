package com.example.user.readworld;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.jaeger.library.StatusBarUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.user.readworld.R.id.flipper;

public class SignIn extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView maintitle;                 // app 標題
    private TextView subtitle;                  // app 副標題
    private ImageView iconimage;                // app icon
    private ViewFlipper f;                      // 圖片自動播放
    private SignInButton googleLogin;           // google登入button
    private Button guestButton;                 // 訪客登入button

    private GoogleApiClient googleApiClient;    // 建立用戶端
    private GoogleSignInOptions signInOptions;  //
    private static final int REQUEST_CODE = 100;// unknown
    private boolean isFirst = true;             // 第一次登入嗎?

    String name;
    String email;
    String id;
    Uri photoUri;
    String photoString;

    private static Boolean isExit = false;
    private static Boolean hasTask = false;

    // Databases
    private DBHelper profile = null;

    // 自動播放圖片(目前10張)
    private int[] resid = {R.drawable.a01, R.drawable.a02, R.drawable.a03, R.drawable.a04, R.drawable.a05, R.drawable.a06, R.drawable.a07, R.drawable.a08, R.drawable.a09, R.drawable.a10};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        StatusBarUtil.setTransparent(SignIn.this);

        readSetting();

        // 物件初始化
        maintitle = (TextView) findViewById(R.id.mainTitle);
        subtitle = (TextView) findViewById(R.id.subTitle);
        iconimage = (ImageView) findViewById(R.id.iconImage);
        f = (ViewFlipper) findViewById(flipper);
        googleLogin = (SignInButton) findViewById(R.id.googleSignIn_button);
        guestButton = (Button) findViewById(R.id.guest);
        //

        // 建立字體
        Typeface mainType = Typeface.createFromAsset(getAssets(),"fonts/HPSimplified_Bd.ttf");
        Typeface subType = Typeface.createFromAsset(getAssets(),"fonts/HPSimplified.ttf");
        //

        // 設定標題和副標題的字體
        maintitle.setTypeface(mainType);
        subtitle.setTypeface(subType);

        // 設定icon
        iconimage.setImageResource(R.drawable.icon);

        // 把resid中的圖片加入viewFlipper中
        for(int i = 0; i < resid.length; i++) {
            f.addView(getImageView(resid[i]));
        }

        f.setInAnimation(this, R.anim.left_in);     // 進來的動畫
        f.setOutAnimation(this, R.anim.left_out);   // 離開的動畫
        f.setFlipInterval(3000);                    // 間隔3秒
        f.startFlipping();                          // 開始自動播放
        //f.stopFlipping();                           // 停止自動播放

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .addApi(Plus.API) // for google plus
                .build();

        googleLogin.setSize(SignInButton.SIZE_WIDE);
        googleLogin.setScopes(signInOptions.getScopeArray());

        // [START googleLogIn]
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent,REQUEST_CODE);
            }
        });
        // [END googleLogIn]

        // [START guestLogIn]
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "訪客登入", Toast.LENGTH_SHORT).show();

                // 進入下一個activity
                Intent intent = new Intent();
                intent.setClass(SignIn.this, MainActivity.class);
                intent.putExtra("id", 0);
                startActivity(intent);
                SignIn.this.finish();
            }
        });
        // [END guestLogIn]

    } // [END onCreate]

    // [START onActivityResult] 登入google後
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();

            name = account.getDisplayName(); // 姓名
            email = account.getEmail();      // 信箱
            id = account.getId();            // *使用id來記錄, 辨識user
            Uri photoImage = account.getPhotoUrl(); // 個人相片
            photoString = String.valueOf(photoImage);

            // 存進資料庫
            profile = new DBHelper(this);
            SQLiteDatabase db = profile.getWritableDatabase();
            profile.addInProfile(id, name, email, photoString, db);
            //

            // 設定檔, 下次開啟會跳過此activity
            saveSetting();

            // 傳送個人資訊, 進入下一個activity
            Intent intent = new Intent();
            intent.setClass(SignIn.this, MainActivity.class);

            //Toast.makeText(this.getApplicationContext(), name, Toast.LENGTH_SHORT).show();

            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("id", id);
            intent.putExtra("photoString", photoString);

            startActivity(intent);
            SignIn.this.finish();

            // for google plus
            //Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
        }
    } // [END onActivityResult]

    // [START getImageView]
    private ImageView getImageView(int resId){

        ImageView image = new ImageView(this);
        image.setBackgroundResource(resId);
        return image;

    } // [END getImageView]

    // [START saveSetting] 存設定狀態
    private void saveSetting() {
        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        setting.edit().putBoolean("isFirst", false).commit();
        //setting.edit().putString("id", null).commit();
        //setting.edit().putString("name", null).commit();
        //setting.edit().putString("email", null).commit();
        //setting.edit().putString("photoUri", null).commit();
    } // [END saveSetting]

    // [START readSetting] 讀取設定狀態
    private void readSetting() {
        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        boolean isFirst = setting.getBoolean("isFirst", true);
        //String id = setting.getString("id", "0");
        if(!isFirst) {

            Intent intent = new Intent();
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("id", id);
            intent.putExtra("photoString", photoString);
            intent.setClass(SignIn.this, MainActivity.class);
            startActivity(intent);
            SignIn.this.finish();

        }
    } // [END readSetting]

    Timer timerExit = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            isExit = false;
            hasTask = true;
        }
    };

    // 按虛擬按鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否要退出
            if(isExit == false) {

                isExit = true;
                Toast.makeText(this, "再按一次離開", Toast.LENGTH_SHORT).show();

                if (!hasTask) {
                    timerExit.schedule(task, 2000);
                }
            }
            else {
                finish();
            }
        }
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
