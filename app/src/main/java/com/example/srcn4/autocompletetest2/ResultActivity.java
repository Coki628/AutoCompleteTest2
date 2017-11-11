package com.example.srcn4.autocompletetest2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private ArrayList<StationVO> stationList;
    private LatLng centerLatLng;
    private ArrayList<Double> latList = new ArrayList<>();
    private ArrayList<Double> lngList = new ArrayList<>();
    private TextView searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // MainActivityから駅情報リストを受け取る
        Intent intent = getIntent();
        stationList =
                (ArrayList<StationVO>) intent.getSerializableExtra("result");

        for (StationVO vo : stationList) {
            // 受け取った駅情報の確認
            Log.d("map", vo.toString());
            // 緯度経度を計算用のリストに格納
            latList.add(Double.parseDouble(vo.getLat()));
            lngList.add(Double.parseDouble(vo.getLng()));
        }
        // 中間地点座標の取得
        centerLatLng = LatLngCalculator.calcCenterLatLng(latList, lngList);
        // 最寄り駅を調べる
        StationVO resultVO = LatLngCalculator.calcNearestStation(centerLatLng, getApplicationContext());

        searchResult = findViewById(R.id.search_result);
        searchResult.setText(resultVO.getName());
    }

    // 共有ボタンが押された時
    public void callLINE(View v) {

    }
}
