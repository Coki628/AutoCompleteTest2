package com.example.srcn4.autocompletetest2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * 周辺情報画面クラス
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener {

    private ArrayList<StationVO> stationList;
    private StationVO resultStation;
    private LatLng centerLatLng;
    private ArrayList<Double> latList = new ArrayList<>();
    private ArrayList<Double> lngList = new ArrayList<>();
    private ArrayList<LatLng> latLngList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // ResultActivityから駅情報リストと候補駅を受け取る
        Intent intent = getIntent();
        stationList =
                (ArrayList<StationVO>)intent.getSerializableExtra("stationList");
        resultStation = (StationVO)intent.getSerializableExtra("resultStation");

        // 緯度経度を計算用のリストに格納
        for (StationVO vo : stationList) {
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

        for (int i = 0; i < latList.size(); i++) {
            // 取得した座標の数だけピンをセットする
            latLngList.add(new LatLng(latList.get(i), lngList.get(i)));
            map.addMarker(new MarkerOptions().position(latLngList.get(i)));
        }
        // 中間地点座標の取得
        centerLatLng = LatLngCalculator.calcCenterLatLng(latList, lngList);
        // 候補駅の座標を取得
        LatLng resultStationLatLng = new LatLng(Double.parseDouble(resultStation.getLat()),
                Double.parseDouble(resultStation.getLng()));
        // 最大距離の取得
        double[] maxDistance = LatLngCalculator.calcMaxDistance(latList, lngList);
        double maxDistanceLat = maxDistance[0];
        double maxDistanceLng = maxDistance[1];
        // 最大距離に応じてズーム具合を調整する
        int zoomLevel = 0;
        Log.d("debug", String.valueOf(maxDistanceLat));
        Log.d("debug", String.valueOf(maxDistanceLng));
        // 2～21で大きいほどズーム
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
        // 候補駅に色違いのピンをセットして情報ウインドウも設定する
        Marker stationMarker = map.addMarker(new MarkerOptions().position(resultStationLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title(resultStation.getName() + "駅！"));
        // 情報ウインドウを表示
        stationMarker.showInfoWindow();
        // 中間地点に色違いのピンをセットして情報ウインドウも設定する
        Marker centerMarker = map.addMarker(new MarkerOptions().position(centerLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .draggable(true)
                .title("中間地点！"));
        // フォーカスを当てるマーカーを設定
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

    // 共有ボタンが押された時
    public void callLINE(View v) {

        // LINEのアプリID
        final String LINE_APP_ID = "jp.naver.line.android";
        // LINEで送る用の改行コード
        final String LINE_SEPARATOR = "%0D%0A";

        try {
            // パッケージ情報の取得
            PackageManager pm = getPackageManager();
            // LINEがインストールされているかの確認
            ApplicationInfo appInfo = pm.getApplicationInfo(LINE_APP_ID, PackageManager.GET_META_DATA);
            // インストールされてたら、LINEへ
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("line://msg/text/" + "中間地点は…" + LINE_SEPARATOR
                    + resultStation.getName() + "駅" + LINE_SEPARATOR
                    + "だよ！" + LINE_SEPARATOR
                    + "from 中間地点アプリ"
            ));
            startActivity(intent);

        } catch(PackageManager.NameNotFoundException e) {
            //インストールされてなかったら、インストールを要求する
            new AlertDialog.Builder(this)
                    .setTitle("LINEが見つかりません。")
                    .setMessage("LINEをインストールしてやり直して下さい。")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 特に何もしない
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    // ジャンルボタンが押された時
    public void callGenre(View v) {

        final String[] items = {"レストラン", "居酒屋", "カフェ", "コンビニ", "カラオケ"};
        int defaultItem = 0; // デフォルトでチェックされているアイテム
        final ArrayList<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);
        new AlertDialog.Builder(this)
                .setTitle("ジャンル選択")
                .setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItems.clear();
                        checkedItems.add(which);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!checkedItems.isEmpty()) {
                            Log.d("checkedItem:", "" + checkedItems.get(0));
                            // ジャンルが決定されたら次の画面へ遷移
                            callBrowser(checkedItems.get(0));
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ブラウザ呼び出し用メソッド
    private void callBrowser(int genre) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        // ジャンル別で接続先を分ける
        switch (genre){
            // レストラン系はぐるナビ
            case 0:
                intent.setData(Uri.parse("https://r.gnavi.co.jp/eki/"
                        + resultStation.getGnaviId() + "/rs/"));
                break;
            case 1:
                intent.setData(Uri.parse("https://r.gnavi.co.jp/eki/"
                        + resultStation.getGnaviId() + "/izakaya/rs/"));
                break;
            case 2:
                intent.setData(Uri.parse("https://r.gnavi.co.jp/eki/"
                        + resultStation.getGnaviId() + "/cafe/rs/"));
                break;
            // コンビニとカラオケはグーグルマップ
            case 3:
                intent.setData(Uri.parse("https://www.google.co.jp/maps/search/コンビニ/@"
                        + resultStation.getLat() + "," + resultStation.getLng() + "," + "15z")); // 15zはズーム具合
                break;
            case 4:
                intent.setData(Uri.parse("https://www.google.co.jp/maps/search/カラオケ/@"
                        + resultStation.getLat() + "," + resultStation.getLng() + "," + "15z"));
                break;
        }
        // 上記URLでブラウザを起動する
        startActivity(intent);
    }

    // 候補駅ボタンが押された時
    public void callSuggestedStations(View v) {

        // 画面遷移処理で、入力されていた駅情報のリストと中間地点座標を次の画面に送る
        Intent intent = new Intent(MapsActivity.this, StationListActivity.class)
                .putExtra("stationList", stationList)
                .putExtra("centerLat", centerLatLng.latitude)
                .putExtra("centerLng", centerLatLng.longitude);
        startActivity(intent);
    }
}
