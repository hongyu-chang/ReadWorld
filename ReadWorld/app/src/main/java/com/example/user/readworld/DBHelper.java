package com.example.user.readworld;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.sql.SQLClientInfoException;

/**
 * Created by Terry on 2016/11/20.
 */

public class DBHelper extends SQLiteOpenHelper {

    private final static int DBVersion = 1;                             // 版本
    private final static String DBName = "ReadWorld.db";                // db name
    public final static String profileTableName = "profile";            // 存個人資訊
    public final static String myFavoritesTableName = "myFavorites";    // 存我的最愛資訊

    public DBHelper(Context context) {
        super(context, DBName, null, DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 建立個人資訊Table
        final String profileSQL = "CREATE TABLE IF NOT EXISTS " + profileTableName +
                "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId VARCHAR(100), " +
                "name VARCHAR(50), " +
                "mail VARCHAR(50), " +
                "profilePic VARCHAR(200) );";

        // 建立我的最愛Table
        final String myFavoritesSQL = "CREATE TABLE IF NOT EXISTS " + myFavoritesTableName +
                "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId VARCHAR(100), " +
                "myFavorites VARCHAR(3) );";

        // 執行語法
        db.execSQL(profileSQL);
        db.execSQL(myFavoritesSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addInProfile(String id, String name, String mail, String pic, SQLiteDatabase db) {

        ContentValues values = new ContentValues(4);
        values.put("userId", id);
        values.put("name", name);
        values.put("mail", mail);
        values.put("profilePic", pic);

        db.insert(profileTableName, null, values);
    }

    public void addInMyFavorites(String id, int index, SQLiteDatabase db) {

        ContentValues values = new ContentValues(2);
        values.put("userId", id);
        values.put("myFavorites", index);

        db.insert(myFavoritesTableName, null, values);
    }

    public void deleteFromMyFavorites(String id, int index, SQLiteDatabase db) {

        ContentValues values = new ContentValues(2);

        db.delete(myFavoritesTableName, "myFavorites = " + index, null);
    }




}
