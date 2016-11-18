package com.example.user.readworld;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TextView contentView;
    TextView nameOrGuest;
    TextView emailOrSignIn;
    ImageView profilePic;

    // for  dynamic view
    LinearLayout linearLayout;
    ListView storeList;
    TextView empty;                     // 清空

    // for google user information
    String userName;                    // 使用者姓名
    String userEmail;                   // 使用者信箱
    String id;                          // *使用者id
    Uri photoUri;                       // 使用者頭貼(uri)
    String photoString;                 // 使用者頭貼(string)

    // for Json Read information
    private String urlArray = "http://cloud.culture.tw/frontsite/trans/emapOpenDataAction.do?method=exportEmapJson&typeId=M";
    int dataCount = 0;

    String[] name = new String[] {};
    String[] representImage = new String[] {};
    String[] intro = new String[] {};
    String[] cityName = new String[] {};
    String[] address = new String[] {};
    String[] longitude = new String[] {};
    String[] latitude = new String[] {};
    String[] openTime = new String[] {};
    String[] phone = new String[] {};
    String[] email = new String[] {};
    String[] facebook = new String[] {};
    String[] website = new String[] {};
    String[] arriveWay = new String[] {};

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

    private int navItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        storeList = new ListView(this);
        empty = new TextView(this);

        // 解析json (背景運作)
        new JsonParse().execute(urlArray);
        // 點擊商店後的事件
        storeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                Toast.makeText(getApplicationContext(), position+1+"", Toast.LENGTH_SHORT).show();
            }
        });

        // 狀態欄顏色
        StatusBarUtil.setColor(MainActivity.this, 0x6C4113);

        // 將 ToolBar設為 ActionBar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contentView = (TextView) findViewById(R.id.content_view);
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

        // 若google登入
        if(id != null) {
            nameOrGuest.setText(userName);
            emailOrSignIn.setText(userEmail);
            if(photoString != null) { // 有照片
                Picasso.with(this.getApplicationContext()).load(photoString).into(profilePic);
            }
        }
        // 從SharedPreferences找
        else {
            readSetting();
            if (id != null) {
                nameOrGuest.setText(userName);
                emailOrSignIn.setText(userEmail);
                //photoUri = Uri.parse(photoString);

                if (photoString != null) { // 有照片
                    Picasso.with(this.getApplicationContext()).load(photoString).into(profilePic);
                }
            }
            else { // 訪客登入
                nameOrGuest.setText("訪客");
                emailOrSignIn.setText("點這裡登入");
                myFavoritesOption.setVisible(false);    // 沒有我的最愛選項
                signOutOption.setVisible(false);        // 沒有登出選項
            }
        }

        //contentView.setText("name: "+userName+"\nemail: "+userEmail+"\nid: "+id+"\nuri: "+photoUri);

        // 點這裡登入
        emailOrSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id != null) {} // 若有登入則不會有動作
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
                if(id != null) {} // 若有登入則不會有動作
                else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SignIn.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            }
        });

        // 按鍵後的動作
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.overview:
                        toolbar.setTitle(R.string.overview);
                        overview();
                        break;
                    case R.id.nearby:
                        toolbar.setTitle(R.string.nearby);
                        nearby();
                        break;
                    case R.id.map:
                        toolbar.setTitle(R.string.map);
                        map();
                        break;
                    case R.id.myFavorites:
                        toolbar.setTitle(R.string.myFavorites);
                        myFavorite();
                        break;
                    case R.id.setting:
                        toolbar.setTitle(R.string.setting);
                        setting();
                        break;
                    case R.id.info:
                        toolbar.setTitle(R.string.info);
                        info();
                        break;
                    case R.id.signOut:
                        toolbar.setTitle(R.string.signOut);
                        cleanSetting();
                        signOut();
                        break;
                    case R.id.exit:
                        savingSetting();
                        finish();
                        break;
                    default:
                        break;

                }

                Toast.makeText(MainActivity.this, menuItem.getTitle() + " pressed", Toast.LENGTH_LONG).show();
                //contentView.setText(menuItem.getTitle());

                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });


        // 加上 actionBarDrawerToggle
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar, R.string.openDrawer , R.string.closeDrawer){
            @Override
            // 關起來側邊攔
            public void onDrawerClosed(View drawerView) {
                super .onDrawerClosed(drawerView);
            }

            // 打開側邊攔
            @Override
            public void onDrawerOpened(View drawerView) {
                super .onDrawerOpened(drawerView);
            }
        };

        // 不知道
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if(null != savedInstanceState){
            navItemId = savedInstanceState.getInt("NAV_ITEM_ID", R.id.overview);
        }
        else{
            navItemId = R.id.overview;
        }

        navigateTo(view.getMenu().findItem(navItemId));


    } // [END onCreate]

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("確認視窗")
                    .setMessage("確定要結束應用程式嗎?")
                    .setIcon(R.drawable.icon)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            savingSetting();
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
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
        SearchView searchView = (SearchView) menuSearchItem.getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // 這邊讓icon可以還原到搜尋的icon
        searchView.setIconifiedByDefault(true);

        return true;
    }

    // 點右上角的功能
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
    */

    private void navigateTo(MenuItem menuItem){
        //contentView.setText(menuItem.getTitle());

        navItemId = menuItem.getItemId();
        menuItem.setChecked(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("NAV_ITEM_ID", navItemId);
    }

    private void signOut() {
        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        setting.edit().putBoolean("isFirst", true).commit();
        cleanSetting();

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SignIn.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    private void savingSetting() {
        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        setting.edit().putString("id", id).commit();
        setting.edit().putString("name", userName).commit();
        setting.edit().putString("email", userEmail).commit();
        setting.edit().putString("photoString", photoString).commit();
    }

    private void readSetting() {
        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        id = setting.getString("id", null);
        userName = setting.getString("name", null);
        userEmail = setting.getString("email", null);
        photoString = setting.getString("photoString", null);
    }

    private void cleanSetting() {
        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        setting.edit().putString("id", null).commit();
        setting.edit().putString("name", null).commit();
        setting.edit().putString("email", null).commit();
        setting.edit().putString("photoString", null).commit();
    }

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

    private void display() {

        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

        for(int i = 0; i < dataCount; i++) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("name", name[i]);
            item.put("addr", address[i]);
            list.add(item);
        }
        SimpleAdapter adapter1 = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, new String[] {"name", "addr"}, new int[] {android.R.id.text1, android.R.id.text2});
        storeList.setAdapter(adapter1);

        linearLayout.addView(storeList);
    }

    // ↓↓↓↓↓↓↓↓↓↓↓要寫的↓↓↓↓↓↓↓↓↓↓↓

    // 總覽
    private void overview() {
        // 先把內容清空
        storeList.setEmptyView(empty);

        linearLayout.removeView(storeList); // 必須的
        display();                          // show
    }
    // 附近的店家
    private void nearby() {
        // 先把內容清空
        linearLayout.removeView(storeList);
        storeList.setEmptyView(empty);


    }
    // 地圖
    private void map() {
        // 先把內容清空
        linearLayout.removeView(storeList);
        storeList.setEmptyView(empty);

    }
    private void myFavorite() {
        // 先把內容清空
        linearLayout.removeView(storeList);
        storeList.setEmptyView(empty);

    }
    // 設定
    private void setting() {
        // 先把內容清空
        linearLayout.removeView(storeList);
        storeList.setEmptyView(empty);

    }
    // 說明
    private void info() {
        // 先把內容清空
        linearLayout.removeView(storeList);
        storeList.setEmptyView(empty);

    }
    // ↑↑↑↑↑↑↑↑↑↑↑要寫的↑↑↑↑↑↑↑↑↑↑↑

    // [START inner class] 背景運行
    class JsonParse extends AsyncTask<String , Integer , String> {

        @Override
        protected void onPreExecute() {
            //執行前 設定可以在這邊設定
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "更新中~~", Toast.LENGTH_SHORT).show();
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

                    storeName += jsonObj.getJSONObject(i).get("name")+"\n";
                    storeAddress += jsonObj.getJSONObject(i).get("address")+"\n";
                    storeLongitude += jsonObj.getJSONObject(i).get("longitude")+"\n";
                    storeLatitude += jsonObj.getJSONObject(i).get("latitude")+"\n";
                    storeOpenTime += jsonObj.getJSONObject(i).get("openTime")+"\n";
                    storePhone += jsonObj.getJSONObject(i).get("phone")+"\n";
                    storeEmail += jsonObj.getJSONObject(i).get("email")+"\n";

                    storeWebsite += jsonObj.getJSONObject(i).get("website")+"\n";
                    storeArriveWay += jsonObj.getJSONObject(i).get("arriveWay")+"\n";
                    storeCityName += jsonObj.getJSONObject(i).get("cityName")+"\n";

                    // some json object cannot find "representImage" and "intro" value.
                    /*
                    storeFacebook += jsonObj.getJSONObject(i).get("facebook")+"\n";
                    storeRepresentImage += jsonObj.getJSONObject(i).get("representImage")+"\n";
                    storeIntro += jsonObj.getJSONObject(i).get("intro")+"\n";
                    */

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
            display(); // display
        }

    } // [END inner class]







}
