package com.example.srcn4.autocompletetest2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener {

    private ArrayList<StationVO> stationList;
    private LatLng centerLatLng;
    private ArrayList<Double> latList = new ArrayList<>();
    private ArrayList<Double> lngList = new ArrayList<>();
    private ArrayList<LatLng> latLngList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // MainActivityから駅情報リストを受け取る
        Intent intent = getIntent();
        stationList =
                (ArrayList<StationVO>)intent.getSerializableExtra("result");

        for (StationVO vo : stationList) {
            // 受け取った駅情報の確認
            Log.d("map", vo.toString());
            // 緯度経度を計算用のリストに格納
            latList.add(Double.parseDouble(vo.getLat()));
            lngList.add(Double.parseDouble(vo.getLng()));
        }
        // マップ使用準備
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // マップ使用準備が完了したら呼ばれる
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // マップオブジェクトを受け取る
        GoogleMap map = googleMap;
        // 中間地点の計算に必要な値の定義
        double sumLat = 0;
        double sumLng = 0;
        double aveLat = 0;
        double aveLng = 0;
        Double maxLat = null;
        Double minLat = null;
        Double maxLng = null;
        Double minLng = null;
        double maxDistanceLat = 0;
        double maxDistanceLng = 0;

        for (int i = 0; i < latList.size(); i++) {
            // 取得した座標の数だけピンをセットする
            latLngList.add(new LatLng(latList.get(i), lngList.get(i)));
            map.addMarker(new MarkerOptions().position(latLngList.get(i)));
        }
        // 座標の合計・最大・最小を出す
        for (double lat : latList) {
            sumLat += lat;
            if (maxLat == null) {
                maxLat = lat;
            } else {
                maxLat = Math.max(maxLat, lat);
            }
            if (minLat == null) {
                minLat = lat;
            } else {
                minLat = Math.min(minLat, lat);
            }
        }
        for (double lng : lngList) {
            sumLng += lng;
            if (maxLng == null) {
                maxLng = lng;
            } else {
                maxLng = Math.max(maxLng, lng);
            }
            if (minLng == null) {
                minLng = lng;
            } else {
                minLng = Math.min(minLng, lng);
            }
        }
        // 合計から平均、最大・最少から最大距離を出す
        aveLat = sumLat / latList.size();
        aveLng = sumLng / lngList.size();
        centerLatLng = new LatLng(aveLat, aveLng);
        maxDistanceLat = maxLat - minLat;
        maxDistanceLng = maxLng - minLng;
        // 最大距離に応じてズーム具合を調整する
        int zoomLevel = 0;
        Log.d("debug", String.valueOf(maxDistanceLat));
        Log.d("debug", String.valueOf(maxDistanceLng));
        //2～21で大きいほどズーム
        if (maxDistanceLat <= 0.03 && maxDistanceLng <= 0.03) {
            zoomLevel = 14;
        } else if (maxDistanceLat <= 0.06 && maxDistanceLng <= 0.06) {
            zoomLevel = 13;
        } else if (maxDistanceLat <= 0.1 && maxDistanceLng <= 0.1) {
            zoomLevel = 12;
        } else if (maxDistanceLat <= 0.2 && maxDistanceLng <= 0.2) {
            zoomLevel = 11;
        } else if (maxDistanceLat <= 0.4 && maxDistanceLng <= 0.4) {
            zoomLevel = 10;
        } else {
            zoomLevel = 9;
        }
        // 中間地点に色違いのピンをセットして情報ウインドウも表示
        Marker centerMarker = map.addMarker(new MarkerOptions().position(centerLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .draggable(true)
                .title("中間地点！"));
        centerMarker.showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, zoomLevel));
        // 情報ウインドウのリスナーをセット
        map.setOnInfoWindowClickListener(this);
        // ドラッグのリスナーをセット
        map.setOnMarkerDragListener(this);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // ドラッグが終わったら中間地点の座標をずらす
        centerLatLng = marker.getPosition();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // 情報ウインドウがタップされた時の処理
        new AlertDialog.Builder(this)
                .setTitle("周辺駅検索")
                .setMessage("この地点の周辺駅情報を表示しますか？")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 周辺駅表示
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 何もしない
                    }
                })
                .show();
    }
}
