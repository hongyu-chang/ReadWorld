package com.example.user.readworld;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TextView contentView;
    TextView nameOrGuest;
    TextView emailOrSignIn;
    ImageView profilePic;

    String name;
    String email;
    String id;
    Uri photoUri;
    String photoString;

    private int navItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 將 ToolBar設為 ActionBar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contentView = (TextView) findViewById(R.id.content_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        View header = view.inflateHeaderView(R.layout.drawer_header);
        view.setItemIconTintList(null);

        MenuItem signOutOption = view.getMenu().findItem(R.id.signOut);
        MenuItem myFavoritesOption = view.getMenu().findItem(R.id.myFavorites);

        nameOrGuest = (TextView) header.findViewById(R.id.NameOrGuest);
        emailOrSignIn = (TextView) header.findViewById(R.id.EmailOrSignIn);
        profilePic = (ImageView) header.findViewById(R.id.ProfilePic);

        // 接收google帳戶資訊
        Intent intent = this.getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        id = intent.getStringExtra("id");
        photoUri = intent.getParcelableExtra("photoUri");

        // 若google登入
        if(id != null) {
            nameOrGuest.setText(name);
            emailOrSignIn.setText(email);
            if(photoUri != null) { // 有照片
                Picasso.with(this.getApplicationContext()).load(photoUri).into(profilePic);
            }
        }
        // 從SharedPreferences找
        else {
            readSetting();
            if (id != null) {
                nameOrGuest.setText(name);
                emailOrSignIn.setText(email);
                photoUri = Uri.parse(photoString);

                if (photoUri != null) { // 有照片
                    Picasso.with(this.getApplicationContext()).load(photoUri).into(profilePic);
                }
            }
            else { // 訪客登入
                nameOrGuest.setText("訪客");
                emailOrSignIn.setText("點這裡登入");
                myFavoritesOption.setVisible(false);    // 沒有我的最愛選項
                signOutOption.setVisible(false);        // 沒有登出選項
            }
        }

        contentView.setText("name: "+name+"\nemail: "+email+"\nid: "+id+"\nuri: "+photoUri);

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
                        break;
                    case R.id.nearby:
                        toolbar.setTitle(R.string.nearby);
                        break;
                    case R.id.map:
                        toolbar.setTitle(R.string.map);
                        break;
                    case R.id.myFavorites:
                        toolbar.setTitle(R.string.myFavorites);
                        break;
                    case R.id.setting:
                        toolbar.setTitle(R.string.setting);
                        break;
                    case R.id.info:
                        toolbar.setTitle(R.string.info);
                        break;
                    case R.id.signOut:
                        toolbar.setTitle(R.string.signOut);
                        clearSetting();
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

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SignIn.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    private void savingSetting() {
        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        setting.edit().putString("id", id).commit();
        setting.edit().putString("name", name).commit();
        setting.edit().putString("email", email).commit();
        setting.edit().putString("photoUri", photoUri.toString()).commit();
    }

    private void readSetting() {
        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        id = setting.getString("id", null);
        name = setting.getString("name", null);
        email = setting.getString("email", null);
        photoString = setting.getString("photoUri", null);
    }

    private void clearSetting() {
        SharedPreferences setting = getSharedPreferences("profile_info", 0);
        setting.edit().putString("id", null).commit();
        setting.edit().putString("name", null).commit();
        setting.edit().putString("email", null).commit();
        setting.edit().putString("photoUri", null).commit();
    }

}
