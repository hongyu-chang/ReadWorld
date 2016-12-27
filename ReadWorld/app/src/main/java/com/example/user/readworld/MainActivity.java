package com.example.user.readworld;

import android.Manifest;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.vision.text.Text;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    SearchView searchView;
    GoogleMap mMap;
    DrawerLayout drawerLayout;
    TextView nameOrGuest;               // 有登入顯示姓名, 否則顯示 "訪客"
    TextView emailOrSignIn;             // 有登入顯示email, 否則顯示 "點這裡登入"
    ImageView profilePic;               // 頭像;

    // for  dynamic view, 動態新增物件在main裡
    LinearLayout linearLayout;          // 外面大框框 (從這個view丟進去)
    ListView storeList;                 // 列出所有書店的list
    TextView empty;                     // 點擊其他功能要先把目前的view清空
    SwipeRefreshLayout refresh;         // 下滑重新整理(還沒做)
    CardView card;                      // 用卡片的方式來列出所有書店
    RecyclerView recycle;               // 卡片要裝進這個view來顯示
    RecyclerView recycle2;              // 搜尋用recycle
    RecyclerView recycle3;              // 我的最愛recycle
    TextView about;                     // 關於
    TextView explan;                    // 說明
    ImageView aboutcover;

    // for google user information, google登入後的資訊
    String userName;                    // 使用者姓名
    String userEmail;                   // 使用者信箱
    String id;                          // *使用者id
    Uri photoUri;                       // 使用者頭貼(uri)
    String photoString;                 // 使用者頭貼(string)

    // for Json Read information
    private String urlArray = "http://cloud.culture.tw/frontsite/trans/emapOpenDataAction.do?method=exportEmapJson&typeId=M";
    int dataCount = 0;                  // 資料數

    // 從Json抓下來的資料先存這
    String storeName = "";              // 店名
    String storeRepresentImage = "";    // 圖像
    String storeIntro = "";             // 簡介
    String storeCityName = "";          // 城市
    String storeAddress = "";           // 地址
    String storeLongitude = "";         // 經度
    String storeLatitude = "";          // 緯度
    String storeOpenTime = "";          // 開放時間
    String storePhone = "";             // 連絡電話
    String storeEmail = "";             // 電子郵件
    String storeFacebook = "";          // 臉書
    String storeWebsite = "";           // 網站
    String storeArriveWay = "";         // 如何到達

    // 再把上面一串資料以"\n"做分割存成array
    String[] name = new String[]{};
    String[] representImage = new String[]{};
    String[] intro = new String[]{};
    String[] cityName = new String[]{};
    String[] address = new String[]{};
    String[] longitude = new String[]{};
    String[] latitude = new String[]{};
    String[] openTime = new String[]{};
    String[] phone = new String[]{};
    String[] email = new String[]{};
    String[] facebook = new String[]{};
    String[] website = new String[]{};
    String[] arriveWay = new String[]{};

    // 不知道是甚麼(?
    private int navItemId;

    // 離開時需要點兩下所記錄的boolean值
    private static Boolean isExit = false;
    private static Boolean hasTask = false;

    // 和定位有關的
    private LocationManager mLocationManager;

    private SensorManager mSensorManager;       // 體感(Sensor)使用管理
    private Sensor mSensor;                     // 體感(Sensor)類別
    private float mLastX;                       // x軸體感(Sensor)偏移
    private float mLastY;                       // y軸體感(Sensor)偏移
    private float mLastZ;                       // z軸體感(Sensor)偏移
    private double mSpeed;                      // 甩動力道數度
    private long mLastUpdateTime;               // 觸發時間
    private boolean goToUp = false;

    //甩動力道數度設定值 (數值越大需甩動越大力，數值越小輕輕甩動即會觸發)
    private static final int SPEED_SHRESHOLD = 3000;

    //觸發間隔時間
    private static final int UPTATE_INTERVAL_TIME = 70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toast.makeText(this.getApplicationContext(), userName, Toast.LENGTH_SHORT).show();

        searchView = new SearchView(this);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        //recycle = (RecyclerView) findViewById(R.id.recycler_view);
        recycle = new RecyclerView(this);
        recycle2 = new RecyclerView(this);
        recycle3 = new RecyclerView(this);
        card = (CardView) findViewById(R.id.card_view);
        about = new TextView(this);
        aboutcover = new ImageView(this);
        explan = new TextView(this);
        //storeList = new ListView(this); // 目前不使用listview, 用cardview

        //empty = new TextView(this); // 清空的

        // 下拉重新整理(還沒做...)
        /*
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });
        */

        // 解析json (背景運作)
        new JsonParse().execute(urlArray);

        // 點擊商店後的事件 (因為用cardview取代所以目前不需要)
        /*
        storeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), position+1+"", Toast.LENGTH_SHORT).show();
            }
        });
        */

        // 設定狀態欄顏色
        StatusBarUtil.setColor(MainActivity.this, 0x6C4113);

        // 將 ToolBar設為 ActionBar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 這和drawer_header有關
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        View header = view.inflateHeaderView(R.layout.drawer_header);
        view.setItemIconTintList(null);

        MenuItem signOutOption = view.getMenu().findItem(R.id.signOut);
        final MenuItem myFavoritesOption = view.getMenu().findItem(R.id.myFavorites);

        nameOrGuest = (TextView) header.findViewById(R.id.NameOrGuest);
        emailOrSignIn = (TextView) header.findViewById(R.id.EmailOrSignIn);
        profilePic = (ImageView) header.findViewById(R.id.ProfilePic);

        // 接收google帳戶資訊
        Intent intent = this.getIntent();
        userName = intent.getStringExtra("name");
        userEmail = intent.getStringExtra("email");
        id = intent.getStringExtra("id");

        photoString = intent.getStringExtra("photoString");

        //Toast.makeText(this.getApplicationContext(), userName, Toast.LENGTH_SHORT).show();

        // 若google登入
        if (id != null) { // 有id表示有登入
            nameOrGuest.setText(userName);
            emailOrSignIn.setText(userEmail);
            if (photoString != null) { // 如果有照片
                Picasso.with(this.getApplicationContext()).load(photoString).fit().centerCrop().into(profilePic);
            }
        }
        // 從SharedPreferences找
        // 重開app, 資料會存在SharedPreferences, 而"接收google帳戶資訊"會沒資料, 所以要從SharedPreferences找

        else {
            readSetting();
            if (id != null) {
                nameOrGuest.setText(userName);
                emailOrSignIn.setText(userEmail);
                if (photoString != null) { // 如果有照片
                    Picasso.with(this.getApplicationContext()).load(photoString).fit().centerCrop().into(profilePic);
                }
            }
            else { // 找不到資料的話, 就是訪客了
                nameOrGuest.setText("訪客");
                emailOrSignIn.setText("點這裡登入");
                myFavoritesOption.setVisible(false);    // 沒有我的最愛選項
                signOutOption.setVisible(false);        // 沒有登出選項(因為沒登入)
            }
        }



        // 點這裡登入
        emailOrSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id != null) {
                } // 若有登入則不會有動作
                else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SignIn.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            }
        });
        // 點大頭貼也可登入
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id != null) {
                } // 若有登入則不會有動作
                else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SignIn.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            }
        });

        // 按側邊攔功能的動作
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.overview:
                        toolbar.setTitle(R.string.overview);
                        menuItem.setChecked(true);
                        overview();
                        break;
                    case R.id.map:
                        //toolbar.setTitle(R.string.map);
                        menuItem.setChecked(false);
                        menuItem.setCheckable(false);
                        map();
                        break;
                    case R.id.myFavorites:
                        toolbar.setTitle(R.string.myFavorites);
                        menuItem.setChecked(true);
                        myFavorite();
                        break;
                    case R.id.about:
                        toolbar.setTitle(R.string.about);
                        menuItem.setChecked(true);
                        about();
                        break;
                    case R.id.info:
                        toolbar.setTitle(R.string.info);
                        menuItem.setChecked(true);
                        info();
                        break;
                    case R.id.signOut:
                        toolbar.setTitle(R.string.signOut);
                        menuItem.setChecked(true);
                        signOut();
                        break;
                    case R.id.exit:
                        savingSetting();
                        finish();
                        break;
                    default:
                        break;

                }

                //Toast.makeText(MainActivity.this, menuItem.getTitle() + " pressed", Toast.LENGTH_LONG).show();
                //contentView.setText(menuItem.getTitle());

                menuItem.setChecked(true);
                drawerLayout.closeDrawers(); // 按完要關起來
                return true;
            }
        });

        // 加上 actionBarDrawerToggle
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            // 關起來側邊攔
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            // 打開側邊攔
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // 不知道
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (null != savedInstanceState) {
            navItemId = savedInstanceState.getInt("NAV_ITEM_ID", R.id.overview);
        } else {
            navItemId = R.id.overview;
        }

        navigateTo(view.getMenu().findItem(navItemId));


        //取得體感(Sensor)服務使用權限
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        //取得手機Sensor狀態設定
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //註冊體感(Sensor)甩動觸發Listener
        mSensorManager.registerListener(SensorListener, mSensor,SensorManager.SENSOR_DELAY_GAME);


    } // [END onCreate]

    private SensorEventListener SensorListener = new SensorEventListener()
    {
        public void onSensorChanged(SensorEvent mSensorEvent)
        {
            //當前觸發時間
            long mCurrentUpdateTime = System.currentTimeMillis();

            //觸發間隔時間 = 當前觸發時間 - 上次觸發時間
            long mTimeInterval = mCurrentUpdateTime - mLastUpdateTime;

            //若觸發間隔時間< 70 則return;
            if (mTimeInterval < UPTATE_INTERVAL_TIME) return;

            mLastUpdateTime = mCurrentUpdateTime;

            //取得xyz體感(Sensor)偏移
            float x = mSensorEvent.values[0];
            float y = mSensorEvent.values[1];
            float z = mSensorEvent.values[2];

            //甩動偏移速度 = xyz體感(Sensor)偏移 - 上次xyz體感(Sensor)偏移
            float mDeltaX = x - mLastX;
            float mDeltaY = y - mLastY;
            float mDeltaZ = z - mLastZ;

            mLastX = x;
            mLastY = y;
            mLastZ = z;

            //體感(Sensor)甩動力道速度公式
            mSpeed = Math.sqrt(mDeltaX * mDeltaX + mDeltaY * mDeltaY + mDeltaZ * mDeltaZ)/ mTimeInterval * 10000;

            //若體感(Sensor)甩動速度大於等於甩動設定值則進入 (達到甩動力道及速度)
            if (mSpeed >= SPEED_SHRESHOLD)
            {
                //達到搖一搖甩動後要做的事情
                if(goToUp == true) {
                    recycle.smoothScrollToPosition(0);
                    goToUp = false;
                }
                else {
                    recycle.smoothScrollToPosition(name.length);
                    goToUp = true;
                }

                Log.d("TAG","搖一搖中...");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //在程式關閉時移除體感(Sensor)觸發
        mSensorManager.unregisterListener(SensorListener);
    }


    // 跟按兩下關閉有關
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

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 按了返回鍵

            // 如果側邊攔有開啟要先關閉
            if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
            }
            else if(!searchView.isIconified()){
                searchView.setIconified(true);
            }
            // 如果沒開啟就是要退出app
            else {
                // 是否要退出
                if (isExit == false) {

                    isExit = true;
                    Toast.makeText(this, "再按一次離開", Toast.LENGTH_SHORT).show();

                    if (!hasTask) {
                        timerExit.schedule(task, 2000);
                    }
                } else {
                    savingSetting();
                    finish();
                }
            }

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // for search
        MenuItem menuSearchItem = menu.findItem(R.id.my_search);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menuSearchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.isEmpty()) {
                    linearLayout.removeView(recycle);
                    linearLayout.removeView(recycle2);
                    linearLayout.addView(recycle);
                }
                else {
                    linearLayout.removeView(recycle);
                    linearLayout.removeView(recycle2);

                    int count = 0;
                    ArrayList<String> a = new ArrayList<>();   // 店名
                    ArrayList<String> b = new ArrayList<>();   // 縣市
                    ArrayList<String> c = new ArrayList<>();   // 地址
                    ArrayList<String> d = new ArrayList<>();   // 營業時間
                    ArrayList<String> e = new ArrayList<>();   // 圖片

                    for(int i = 0; i < name.length; i++) {

                        if(name[i].indexOf(newText) != -1 || cityName[i].indexOf(newText) != -1 || address[i].indexOf(newText) != -1) {
                            a.add(name[i]);
                            b.add(cityName[i]);
                            c.add(address[i]);
                            d.add(openTime[i]);
                            e.add(representImage[i]);
                            count++;
                            //Toast.makeText(MainActivity.this,name[i],Toast.LENGTH_SHORT).show();
                        }
                    }

                    MyAdapter myAdapter = new MyAdapter(a, b, c, d, e);
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recycle2.setLayoutManager(layoutManager);
                    recycle2.setAdapter(myAdapter);

                    linearLayout.addView(recycle2);
                }

                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    linearLayout.removeView(recycle2);
                    linearLayout.removeView(recycle);
                    linearLayout.addView(recycle);
                    searchView.setIconified(true);
                }

            }
        });

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // 這邊讓icon可以還原到搜尋的icon
        searchView.setIconifiedByDefault(true);

        return true;
    }

    private void navigateTo(MenuItem menuItem) {
        //contentView.setText(menuItem.getTitle());

        navItemId = menuItem.getItemId();
        menuItem.setChecked(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("NAV_ITEM_ID", navItemId);
    }

    // 登出
    private void signOut() {
        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        setting.edit().putBoolean("isFirst", true).commit();
        cleanSetting();

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SignIn.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    // 儲存user資料
    private void savingSetting() {
        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        setting.edit().putString("id", id).commit();
        setting.edit().putString("name", userName).commit();
        setting.edit().putString("email", userEmail).commit();
        setting.edit().putString("photoString", photoString).commit();
    }

    // 讀取user資料
    private void readSetting() {
        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        id = setting.getString("id", null);
        userName = setting.getString("name", null);
        userEmail = setting.getString("email", null);
        photoString = setting.getString("photoString", null);
    }

    // 清掉user資料 (登出時呼叫)
    private void cleanSetting() {
        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        setting.edit().putString("id", null).commit();
        setting.edit().putString("name", null).commit();
        setting.edit().putString("email", null).commit();
        setting.edit().putString("photoString", null).commit();
    }

    // 把string以\n分割 存成string[]
    private void splitStoreString() {
        name = storeName.split("\n");
        representImage = storeRepresentImage.split("\n");
        intro = storeIntro.split("\n");
        cityName = storeCityName.split("\n");
        address = storeAddress.split("\n");
        longitude = storeLongitude.split("\n");
        latitude = storeLatitude.split("\n");
        openTime = storeOpenTime.split("\n");
        phone = storePhone.split("\n");
        email = storeEmail.split("\n");
        facebook = storeFacebook.split("\n");
        website = storeWebsite.split("\n");
        arriveWay = storeArriveWay.split("\n");
    }

    // 將書店秀出來 (這是listview, 目前用不到)
    private void display() {

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < dataCount; i++) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("name", name[i]);
            item.put("addr", address[i]);
            list.add(item);
        }
        SimpleAdapter adapter1 = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, new String[]{"name", "addr"}, new int[]{android.R.id.text1, android.R.id.text2});
        storeList.setAdapter(adapter1);

        linearLayout.addView(storeList);
    }

    // 總覽
    private void overview() {
        // 先移除所有的動態view
        linearLayout.removeView(recycle);   // overview
        linearLayout.removeView(recycle2);  // search
        linearLayout.removeView(recycle3);  // myFavorite
        linearLayout.removeView(about);
        linearLayout.removeView(explan);
        /*
        *
        *
        */


        // 再新增自己的view
        linearLayout.addView(recycle);
    }
    // 地圖
    private void map() {
        // 先移除所有的動態view
        //linearLayout.removeView(recycle);
        //linearLayout.removeView(recycle2);

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, MapsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putStringArray("name", name);
        bundle.putStringArray("cityName", cityName);
        bundle.putStringArray("address", address);
        bundle.putStringArray("longitude", longitude);
        bundle.putStringArray("latitude", latitude);

        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in_2, R.anim.left_out_2);

    }
    // 我的最愛
    private void myFavorite() {
        // 先移除所有的動態view
        linearLayout.removeView(recycle);
        linearLayout.removeView(recycle2);
        linearLayout.removeView(recycle3);
        linearLayout.removeView(about);
        linearLayout.removeView(explan);
        /*
        *
        *
        */

        DBHelper myFavorites = new DBHelper(this);
        SQLiteDatabase db = myFavorites.getWritableDatabase();

        ArrayList<String> a = new ArrayList<>();   // 店名
        ArrayList<String> b = new ArrayList<>();   // 縣市
        ArrayList<String> c = new ArrayList<>();   // 地址
        ArrayList<String> d = new ArrayList<>();   // 營業時間
        ArrayList<String> e = new ArrayList<>();   // 圖片

        Cursor cr = db.rawQuery("SELECT * FROM " + myFavorites.myFavoritesTableName, null);

        // 沒有資料表示沒有任何一家加入最愛過
        if(cr.getCount() == 0) {
            //Toast.makeText(StoreInfoActivity.this, "沒資料QQ", Toast.LENGTH_SHORT).show();
        }

        //
        else {
            cr.moveToFirst();
            do {
                int index = cr.getInt(2);
                a.add(name[index]);
                b.add(cityName[index]);
                c.add(address[index]);
                d.add(openTime[index]);
                e.add(representImage[index]);

            } while(cr.moveToNext());


            MyAdapter myAdapter = new MyAdapter(a, b, c, d, e);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recycle3.setLayoutManager(layoutManager);
            recycle3.setAdapter(myAdapter);

            linearLayout.addView(recycle3);
        }

    }
    // 關於我們
    private void about() {
        // 先移除所有的動態view
        linearLayout.removeView(recycle);
        linearLayout.removeView(recycle2);
        linearLayout.removeView(recycle3);
        linearLayout.removeView(about);
        linearLayout.removeView(explan);
        /* TODO 關於我們
        *
        *
        */

        String text = "\n\n\n開發成員 : \n\n陳亮宇\n陳崧源\n\n\n\n\t資料來源 : 行政院文化局";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new AbsoluteSizeSpan(80), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF533210")), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        spannable.setSpan(new AbsoluteSizeSpan(100), 3, 7, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannable.setSpan(new AbsoluteSizeSpan(100), 24, 28, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        about.setText(spannable);
        linearLayout.addView(about);

    }
    // 說明
    private void info() {
        // 先移除所有的動態view
        linearLayout.removeView(recycle);
        linearLayout.removeView(recycle2);
        linearLayout.removeView(recycle3);
        linearLayout.removeView(about);
        linearLayout.removeView(explan);
        /* TODO 說明
        *
        *
        */
        String text = "\n\n\n    這是一個彙整台灣獨立書店的app，資料取自文化局的開放資源，主要目的在於提供沒接觸過或是剛接觸獨立書店的民眾，可以找尋到自己生活周遭哪裡有獨立書店，該如何拜訪、參觀，並且了解各個獨立店家的特色";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new AbsoluteSizeSpan(80), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF533210")), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        explan.setText(spannable);
        linearLayout.addView(explan);

    }

    // [START inner class] 背景運行
    class JsonParse extends AsyncTask<String , Integer , String> {

        @Override
        protected void onPreExecute() {
            //執行前 設定可以在這邊設定
            super.onPreExecute();
            //Toast.makeText(getApplicationContext(), "更新中~~", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {

            String sourceUrl = params[0];

            String[] item = new String[] {};

            try {

                // create a connection
                URL urlArr = new URL(sourceUrl);
                HttpURLConnection conn = (HttpURLConnection) urlArr.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // read data
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String jsonString1 = reader.readLine();
                reader.close();

                // parse json
                String jsonString = jsonString1;
                JSONArray jsonObj = new JSONArray(jsonString);

                // store data
                dataCount = jsonObj.length();
                for(int i = 0; i < dataCount ; i++) {

                    storeName += jsonObj.getJSONObject(i).opt("name")+"\n";
                    storeAddress += jsonObj.getJSONObject(i).opt("address")+"\n";
                    storeLongitude += jsonObj.getJSONObject(i).opt("longitude")+"\n";
                    storeLatitude += jsonObj.getJSONObject(i).opt("latitude")+"\n";
                    storeOpenTime += jsonObj.getJSONObject(i).opt("openTime")+"\n";
                    storePhone += jsonObj.getJSONObject(i).opt("phone")+"\n";
                    storeEmail += jsonObj.getJSONObject(i).opt("email")+"\n";
                    storeWebsite += jsonObj.getJSONObject(i).opt("website")+"\n";
                    storeArriveWay += jsonObj.getJSONObject(i).opt("arriveWay")+"\n";
                    storeCityName += jsonObj.getJSONObject(i).get("cityName")+"\n";
                    storeFacebook += jsonObj.getJSONObject(i).opt("facebook")+"\n";

                    if(jsonObj.getJSONObject(i).opt("representImage") == null || jsonObj.getJSONObject(i).opt("representImage") == "null") {
                        storeRepresentImage += "null\n";
                    }
                    else {
                        storeRepresentImage += jsonObj.getJSONObject(i).opt("representImage")+"\n";
                    }

                    String temp = jsonObj.getJSONObject(i).opt("intro")+"";
                    String temp2 = temp.replace("\n", "");
                    if(temp2 == null || temp2 == "" || temp2 == "null" || jsonObj.getJSONObject(i).opt("intro") == null){
                        storeIntro += "無簡介"+"\n";
                    }
                    else {
                        storeIntro += temp2+"\n";
                    }

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return storeName;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //執行中 可以在這邊告知使用者進度
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String result) {
            //執行後 完成背景任務
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "更新完成", Toast.LENGTH_SHORT).show();
            splitStoreString(); // 分割字串
            //display(); // display
            card();
        }

    } // [END inner class]

    private void card() {

        ArrayList<String> myDataset = new ArrayList<>();    // 店名
        ArrayList<String> myDataset2 = new ArrayList<>();   // 縣市
        ArrayList<String> myDataset3 = new ArrayList<>();   // 地址
        ArrayList<String> myDataset4 = new ArrayList<>();   // 營業時間
        ArrayList<String> myDataset5 = new ArrayList<>();   // 圖片

        for(int i = 0; i < dataCount; i++){
            myDataset.add(name[i]);
            myDataset2.add(cityName[i]);
            myDataset3.add(address[i]);
            myDataset4.add(openTime[i]);
            myDataset5.add(representImage[i]);
        }

        MyAdapter myAdapter = new MyAdapter(myDataset, myDataset2, myDataset3, myDataset4, myDataset5);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(layoutManager);
        recycle.setAdapter(myAdapter);

        linearLayout.addView(recycle);
    }

    // inner class
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> mData;
        private List<String> mData2;
        private List<String> mData3;
        private List<String> mData4;
        private List<String> mData5;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public RelativeLayout cardrelative;
            public TextView mainTitle;
            public TextView cityTitle;
            public TextView addrTitle;
            public TextView timeTitle;
            public ImageView storeTitle;


            public ViewHolder(View v) {
                super(v);
                cardrelative = (RelativeLayout) v.findViewById(R.id.cardRelative);
                mainTitle = (TextView) v.findViewById(R.id.main);
                cityTitle = (TextView) v.findViewById(R.id.city);
                addrTitle = (TextView) v.findViewById(R.id.addr);
                timeTitle = (TextView) v.findViewById(R.id.time);
                storeTitle = (ImageView) v.findViewById(R.id.store_image);
            }
        }

        public MyAdapter(List<String> data, List<String> data2, List<String> data3, List<String> data4, List<String> data5) {
            mData = data;
            mData2 = data2;
            mData3 = data3;
            mData4 = data4;
            mData5 = data5;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mainTitle.setText(mData.get(position));
            holder.cityTitle.setText(mData2.get(position));
            holder.addrTitle.setText(mData3.get(position));
            holder.timeTitle.setText(mData4.get(position));
            //holder.mainTitle.setTextColor(R.color.material_blue_grey_800);

            //new DownloadImageTask(holder.storeTitle).execute(mData5.get(position));

            if(mData5.get(position) == null || mData5.get(position) == "null" || mData5.get(position).isEmpty()) {
                Picasso.with(MainActivity.this).load(R.drawable.bookstore).into(holder.storeTitle);
            }
            else {
                Picasso.with(MainActivity.this).load(mData5.get(position)).error(R.drawable.bookstore).placeholder(R.drawable.bookstore).fit().centerCrop().into(holder.storeTitle);
            }

            // 按卡片觸發的事件
            holder.cardrelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int count = 0;
                    //Toast.makeText(MainActivity.this, holder.mainTitle.getText()+" pressed", Toast.LENGTH_SHORT).show();


                    for(int i = 0; i < name.length; i++) {
                        if(holder.mainTitle.getText().equals(name[i])) {
                            count = i;
                            break;
                        }
                    }

                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, StoreInfoActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putStringArray("name", name);
                    bundle.putStringArray("representImage", representImage);
                    bundle.putStringArray("intro", intro);
                    bundle.putStringArray("cityName", cityName);
                    bundle.putStringArray("address", address);
                    bundle.putStringArray("longitude", longitude);
                    bundle.putStringArray("latitude", latitude);
                    bundle.putStringArray("openTime", openTime);
                    bundle.putStringArray("phone", phone);
                    bundle.putStringArray("email", email);
                    bundle.putStringArray("facebook", facebook);
                    bundle.putStringArray("website", website);
                    bundle.putStringArray("arriveWay", arriveWay);
                    bundle.putInt("index", count);
                    bundle.putString("id", id);
                    bundle.putString("userName", userName);

                    intent.putExtras(bundle);
                    startActivity(intent);

                    overridePendingTransition(R.anim.left_in_2, R.anim.left_out_2);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

}
