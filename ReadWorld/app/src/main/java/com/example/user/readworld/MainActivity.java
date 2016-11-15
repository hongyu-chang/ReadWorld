package com.example.user.readworld;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TextView contentView;

    private int navItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtil.setColor(MainActivity.this, 0x394B54);

        // 將 ToolBar設為 ActionBar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contentView = (TextView) findViewById(R.id.content_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);

        // 按鍵後的動作
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.mainPage:
                        toolbar.setTitle(R.string.mainPage);
                        break;
                    case R.id.myFavorites:
                        toolbar.setTitle(R.string.myFavorites);
                        break;
                    case R.id.overview:
                        toolbar.setTitle(R.string.overview);
                        break;
                    case R.id.map:
                        toolbar.setTitle(R.string.map);
                        break;
                    case R.id.nearby:
                        toolbar.setTitle(R.string.nearby);
                        break;
                    case R.id.setting:
                        toolbar.setTitle(R.string.setting);
                        break;
                    case R.id.exit:
                        toolbar.setTitle(R.string.exit);
                        break;
                    case R.id.signOut:
                        toolbar.setTitle(R.string.signOut);
                        signOut();
                        break;
                    case R.id.signIn:
                        toolbar.setTitle(R.string.signIn);
                        break;
                    default:
                        break;

                }

                Toast.makeText(MainActivity.this, menuItem.getTitle() + " pressed", Toast.LENGTH_LONG).show();
                contentView.setText(menuItem.getTitle());

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
                StatusBarUtil.setColor(MainActivity.this, 0x394B54);
            }

            // 打開側邊攔
            @Override
            public void onDrawerOpened(View drawerView) {
                super .onDrawerOpened(drawerView);
                StatusBarUtil.setColor(MainActivity.this, 0x5A7A88);
            }
        };

        // 不知道
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if(null != savedInstanceState){
            navItemId = savedInstanceState.getInt("NAV_ITEM_ID", R.id.mainPage);
        }
        else{
            navItemId = R.id.mainPage;
        }

        navigateTo(view.getMenu().findItem(navItemId));

    } // [END onCreate]


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void navigateTo(MenuItem menuItem){
        contentView.setText(menuItem.getTitle());

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










}
