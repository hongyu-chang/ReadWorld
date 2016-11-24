package com.example.user.readworld;

/**
 * Created by USER on 2016/11/24.
 */

public class ShopInfo {

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

    public ShopInfo(String[] name, String[] representImage, String[] intro, String[] cityName, String[] address,
                    String[] longitude, String[] latitude, String[] openTime , String[] phone,
                    String[] email, String[] facebook, String[] website, String[] arriveWay) {

        this.name = name;
        this.representImage = representImage;
        this.intro = intro;
        this.cityName = cityName;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.openTime = openTime;
        this.phone = phone;
        this.email = email;
        this.facebook = facebook;
        this.website = website;
        this.arriveWay = arriveWay;
    }






}
