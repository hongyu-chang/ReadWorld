package com.example.user.readworld;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

/**
 * Created by USER on 2016/11/18.
 */

public class ReadJson {

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

    // [START inner class]
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

                    storeName += jsonObj.getJSONObject(i).get("name")+"\n";
                    storeAddress += jsonObj.getJSONObject(i).get("address")+"\n";
                    storeLongitude += jsonObj.getJSONObject(i).get("longitude")+"\n";
                    storeLatitude += jsonObj.getJSONObject(i).get("latitude")+"\n";
                    storeOpenTime += jsonObj.getJSONObject(i).get("openTime")+"\n";
                    storePhone += jsonObj.getJSONObject(i).get("phone")+"\n";
                    storeEmail += jsonObj.getJSONObject(i).get("email")+"\n";
                    storeFacebook += jsonObj.getJSONObject(i).get("facebook")+"\n";
                    storeWebsite += jsonObj.getJSONObject(i).get("website")+"\n";
                    storeArriveWay += jsonObj.getJSONObject(i).get("arriveWay")+"\n";
                    storeCityName += jsonObj.getJSONObject(i).get("cityName")+"\n";

                    // some json object cannot find "representImage" and "intro" value.
                    /*
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
            //Toast.makeText(getApplicationContext(), "更新完成", Toast.LENGTH_SHORT).show();
            //display();
        }

    } // [END inner class]




}
