package com.example.srcn4.autocompletetest2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * 候補駅画面クラス
 */
public class StationListActivity extends AppCompatActivity {

    private ArrayList<StationVO> stationList = new ArrayList<>();
    private ArrayList<StationDistanceVO> stationDistanceList = new ArrayList<>();
    private ListView listView;
    private MyAdapterForListView myadapter;
    public static int FVP = 0;
    public static int y = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // 前画面から駅情報リストを受け取る
        Intent intent = getIntent();
        stationList = (ArrayList<StationVO>) intent.getSerializableExtra("stationList");
        double centerLat = intent.getDoubleExtra("centerLat", 0);
        double centerLng = intent.getDoubleExtra("centerLng", 0);
        LatLng centerLatLng = new LatLng(centerLat, centerLng);

        // ここではソートされた駅情報をリストごと取得
        stationDistanceList
                = LatLngCalculator.calcNearStationsList(centerLatLng, getApplicationContext());
        // 1000件以上もいらないので上位10件以外削除
        stationDistanceList.subList(10, stationDistanceList.size()).clear();

        //AdapterでListをListViewと紐付ける
        listView = findViewById(R.id.list_view);
        myadapter = new MyAdapterForListView(this, stationDistanceList);
        listView.setAdapter(myadapter);
        // 記憶してあったリストの位置を取得
        listView.setSelectionFromTop(FVP, y);
        // リストビュー内のボタンが押された時の処理
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (view.getId()) {
                    case R.id.button12:
                        // LINE連携処理を呼び出す
                        callLINE(stationDistanceList.get(position).getName());
                        break;
                    case R.id.button13:
                        // MAPへ画面遷移させる
                        callMapInfo(stationDistanceList.get(position).getName());
                        break;
                }
            }
        });
    }

    // 共有ボタンが押された時
    public void callLINE(String stationName) {
        // LINE共有機能を呼び出す
        Object obj = IntentUtils.prepareForLINE(this, stationName);
        // Intentが返却されていたら、LINE連携へ遷移する
        if (obj instanceof Intent) {
            Intent intent = (Intent)obj;
            startActivity(intent);
            // AlertDialog.Builderが返却されていたら、遷移せずダイアログを表示
        } else if (obj instanceof AlertDialog.Builder) {
            AlertDialog.Builder dialog = (AlertDialog.Builder)obj;
            dialog.show();
        }
    }

    // 周辺情報ボタンが押された時
    public void callMapInfo(String stationName) {

        // DB接続のためDAOを生成
        StationDAO dao = new StationDAO(getApplicationContext());
        // 駅情報を取得する
        StationVO vo = dao.selectStationByName(stationName);
        // 周辺情報(MAP)の画面に遷移
        Intent intent = IntentUtils.prepareForMapsActivity(
                StationListActivity.this, stationList, vo);
        startActivity(intent);
    }
}


