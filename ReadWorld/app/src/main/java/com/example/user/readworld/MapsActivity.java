package com.example.user.readworld;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String[] name = new String[]{};
    String[] cityName = new String[]{};
    String[] address = new String[]{};
    String[] longitude = new String[]{};
    String[] latitude = new String[]{};
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        Intent intent = this.getIntent();
        name = intent.getStringArrayExtra("name");
        cityName = intent.getStringArrayExtra("cityName");
        address = intent.getStringArrayExtra("address");
        longitude = intent.getStringArrayExtra("longitude");
        latitude = intent.getStringArrayExtra("latitude");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestLocationPermission();
            return;
        }
        else {
            mMap.setMyLocationEnabled(true);
            mark();
        }


    }

    private void requestLocationPermission() {
        // 如果裝置版本是6.0（包含）以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 取得授權狀態，參數是請求授權的名稱
            int hasPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);

            // 如果未授權
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                // 請求授權
                // 第一個參數是請求授權的名稱
                // 第二個參數是請求代碼
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
            }
            else {
                mMap.setMyLocationEnabled(true);
                mark();
                // 啟動地圖與定位元件
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_FINE_LOCATION_PERMISSION) {
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                mMap.setMyLocationEnabled(true);
                mark();
            }
            else {
                // Permission Denied
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void mark() {

        //mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getFocusedBuilding();

        // Add  markers
        List<LatLng> points = new ArrayList<LatLng>();

        for(int i = 0; i < longitude.length; i++) {
            if(latitude[i].isEmpty() || longitude[i].isEmpty()) {
                points.add(new LatLng(0, 0));
            }
            else {
                points.add(new LatLng(Double.valueOf(latitude[i]), Double.valueOf(longitude[i])));
                mMap.addMarker(new MarkerOptions().position(points.get(i)).draggable(true).title(name[i]).snippet(address[i]));
            }
        }

        // 把沒有經緯度的手動加進去
        LatLng a001 = new LatLng(22.9753003,120.22234);
        mMap.addMarker(new MarkerOptions().title("吾家書店").snippet("崇明路367號").position(a001));

        LatLng a002 = new LatLng(25.0546477,121.5078449);
        mMap.addMarker(new MarkerOptions().title("Bookstore 1920s").snippet("迪化街一段34號").position(a002));

        LatLng a003 = new LatLng(25.0533778,121.5130723);
        mMap.addMarker(new MarkerOptions().title("偵探書屋").snippet("南京西路262巷11號").position(a003));

        LatLng a004 = new LatLng(25.024562,121.5441024);
        mMap.addMarker(new MarkerOptions().title("陸連島書店").snippet("和平東路二段134-3號").position(a004));

        LatLng a005 = new LatLng(25.0310517,121.5453103);
        mMap.addMarker(new MarkerOptions().title("雅痞書店").snippet("四維路154巷7號").position(a005));

        LatLng a006 = new LatLng(25.0318982,121.5560196);
        mMap.addMarker(new MarkerOptions().title("鹿途中旅遊書店").snippet("嘉興街28號").position(a006));

        LatLng a007 = new LatLng(25.0065191,121.4483365);
        mMap.addMarker(new MarkerOptions().title("書店").snippet("大觀路一段29巷81號").position(a007));

        LatLng a008 = new LatLng(24.999124,121.5142467);
        mMap.addMarker(new MarkerOptions().title("綠書店").snippet("中正路151巷8弄18號").position(a008));

        LatLng a009 = new LatLng(25.0778548,121.3841686);
        mMap.addMarker(new MarkerOptions().title("書房味道").snippet("中正路356號").position(a009));

        LatLng a10 = new LatLng(24.7553796,121.7557175);
        mMap.addMarker(new MarkerOptions().title("舊書櫃").snippet("宜興路一段280號").position(a10));

        LatLng a11 = new LatLng(24.7478429,121.7433767);
        mMap.addMarker(new MarkerOptions().title("旅二手概念書店").snippet("神農路一段1號").position(a11));

        LatLng a12 = new LatLng(24.9562428,121.2025993);
        mMap.addMarker(new MarkerOptions().title("小兔子書坊").snippet("民族路二段193巷15弄61").position(a12));

        LatLng a13 = new LatLng(24.8962699,121.2234372);
        mMap.addMarker(new MarkerOptions().title("晴耕雨讀小書院").snippet("福龍路一段560巷12號").position(a13));

        LatLng a14 = new LatLng(25.001419,121.3060771);
        mMap.addMarker(new MarkerOptions().title("荒野夢二").snippet("中正二街28號").position(a14));

        LatLng a15 = new LatLng(24.7890462,121.174144);
        mMap.addMarker(new MarkerOptions().title("石店子69有機書店").snippet("中正路69號").position(a15));

        LatLng a16 = new LatLng(24.7304104,121.0869421);
        mMap.addMarker(new MarkerOptions().title("瓦當人文書屋").snippet("大林路104號").position(a16));

        LatLng a17 = new LatLng(24.1375341,120.6783147);
        mMap.addMarker(new MarkerOptions().title("想想人文空間 Thinkers' Corner").snippet("民權路78號").position(a17));

        LatLng a18 = new LatLng(24.1478048,120.6609475);
        mMap.addMarker(new MarkerOptions().title("新手書店").snippet("向上北路129號").position(a18));

        LatLng a19 = new LatLng(24.1612649,120.6533643);
        mMap.addMarker(new MarkerOptions().title("梓書房").snippet("四川路87巷28號").position(a19));

        LatLng a20 = new LatLng(24.0360326,120.6411218);
        mMap.addMarker(new MarkerOptions().title("羅布森書蟲房").snippet("溪岸路8-3號").position(a20));

        LatLng a21 = new LatLng(23.8527579,120.4935543);
        mMap.addMarker(new MarkerOptions().title("成功旅社農用書店").snippet("復興路50號").position(a21));

        LatLng a22 = new LatLng(23.3056399,120.3179353);
        mMap.addMarker(new MarkerOptions().title("曬書店×新營市民學堂").snippet("中山路93-2號").position(a22));

        LatLng a23 = new LatLng(22.6305967,120.3133186);
        mMap.addMarker(new MarkerOptions().title("三餘書店 TaKaoBooks").snippet("中正二路214號").position(a23));

        LatLng a24 = new LatLng(22.6201128,120.3018509);
        mMap.addMarker(new MarkerOptions().title("小房子書舖").snippet("文橫二路115巷15號").position(a24));

        LatLng a25 = new LatLng(22.702517,120.6065455);
        mMap.addMarker(new MarkerOptions().title("蕃藝書屋").snippet("三和村玉泉巷65之1號").position(a25));

        LatLng a26 = new LatLng(22.7071639,120.6501783);
        mMap.addMarker(new MarkerOptions().title("小陶壺書坊").snippet("北葉村風景104號").position(a26));

        LatLng a27 = new LatLng(23.1993029,119.4259173);
        mMap.addMarker(new MarkerOptions().title("鶵鳥藝文空間").snippet("南港村9鄰尖山腳4號").position(a27));

        LatLng a28 = new LatLng(24.1015792,121.6049287);
        mMap.addMarker(new MarkerOptions().title("雨果部落書坊").snippet("景美村三棧38之1號").position(a28));

        mMap.animateCamera(CameraUpdateFactory.newLatLng(points.get(0)));
    }


}
