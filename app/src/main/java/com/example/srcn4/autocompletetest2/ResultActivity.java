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
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private ArrayList<StationVO> stationList;
    private StationVO resultVO;

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
        LatLng centerLatLng = LatLngCalculator.calcCenterLatLng(latList, lngList);
        // 中間地点から一番近い座標にある駅を調べる
        resultVO = LatLngCalculator.calcNearestStation(centerLatLng, getApplicationContext());

        TextView searchResult = findViewById(R.id.search_result);
        searchResult.setText(resultVO.getName());
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
                    + resultVO.getName() + "駅" + LINE_SEPARATOR
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

    // 周辺情報ボタンが押された時
    public void callMapInfo(View v) {

        // 画面遷移処理で、駅情報のリストを次の画面に送る
        Intent intent = new Intent(ResultActivity.this, MapsActivity.class)
                .putExtra("stationList", stationList)
                .putExtra("resultStation", resultVO);
        startActivity(intent);
    }
}
