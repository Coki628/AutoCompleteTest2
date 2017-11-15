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
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * 検索結果画面クラス
 */
public class ResultActivity extends AppCompatActivity {

    private ArrayList<StationVO> stationList;
    private StationVO resultVO;
    private LatLng centerLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // MainActivityから駅情報リストを受け取る
        Intent intent = getIntent();
        stationList =
                (ArrayList<StationVO>) intent.getSerializableExtra("result");

        // 計算用の緯度と経度のリスト
        ArrayList<Double> latList = new ArrayList<>();
        ArrayList<Double> lngList = new ArrayList<>();
        // 緯度経度を駅情報リストから計算用のリストに格納
        for (StationVO vo : stationList) {

            latList.add(Double.parseDouble(vo.getLat()));
            lngList.add(Double.parseDouble(vo.getLng()));
        }
        // 中間地点座標の取得
        centerLatLng = LatLngCalculator.calcCenterLatLng(latList, lngList);
        // 中間地点から一番近い座標にある駅を調べる
        resultVO = LatLngCalculator.calcNearestStation(centerLatLng, getApplicationContext());
        // 駅名を表示
        TextView searchResult = findViewById(R.id.search_result);
        TextView searchResultKana = findViewById(R.id.search_result_kana);
        searchResult.setText(resultVO.getName());
        searchResultKana.setText(resultVO.getKana());
    }

    // 共有ボタンが押された時
    public void callLINE(View v) {
        // LINE共有機能を呼び出す
        Object obj = IntentUtils.prepareForLINE(this, resultVO.getName());
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
    public void callMapInfo(View v) {

        // 画面遷移処理で、入力されていた駅情報のリストと候補駅を次の画面に送る
        Intent intent = IntentUtils.prepareForMapsActivity(ResultActivity.this, stationList, resultVO);
        startActivity(intent);
    }

    // 候補駅ボタンが押された時
    public void callSuggestedStations(View v) {

        // 画面遷移処理で、入力されていた駅情報のリストと中間地点座標を次の画面に送る
        Intent intent = IntentUtils.prepareForStationListActivity(ResultActivity.this, stationList,
                centerLatLng.latitude, centerLatLng.longitude);
        startActivity(intent);
    }
}
