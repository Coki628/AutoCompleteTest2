package com.example.srcn4.autocompletetest2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.ArrayList;

public class IntentUtils {

    public static Intent prepareForMainActivity(Context context) {

        // メイン画面には何も送らないのでそのまま返却
        return new Intent(context, MainActivity.class);
    }

    public static Intent prepareForResultActivity(Context context, ArrayList<StationVO> stationList) {

        // 入力されていた駅情報のリストを次の画面に送る準備
        Intent intent = new Intent(context, ResultActivity.class)
                .putExtra("result", stationList);
        return intent;
    }

    public static Intent prepareForMapsActivity(Context context, ArrayList<StationVO> stationList, StationVO resultVO) {

        // 入力されていた駅情報のリストと候補駅を次の画面に送る準備
        Intent intent = new Intent(context, MapsActivity.class)
                .putExtra("stationList", stationList)
                .putExtra("resultStation", resultVO);
        return intent;
    }

    public static Intent prepareForStationListActivity(Context context, ArrayList<StationVO> stationList,
                                                double centerLat, double centerLng) {

        // 入力されていた駅情報のリストと中間地点座標を次の画面に送る準備
        Intent intent = new Intent(context, StationListActivity.class)
                .putExtra("stationList", stationList)
                .putExtra("centerLat", centerLat)
                .putExtra("centerLng", centerLng);
        return intent;
    }

    public static Intent prepareForExternalInfo(StationVO resultStation, int genre) {

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
        return intent;
    }

    public static Object prepareForLINE(Context context, String stationName) {

        // LINEのアプリID
        String LINE_APP_ID = "jp.naver.line.android";
        // LINEで送る用の改行コード
        String LINE_SEPARATOR = "%0D%0A";

        try {
            // パッケージ情報の取得
            PackageManager pm = context.getPackageManager();
            // LINEがインストールされているかの確認
            ApplicationInfo appInfo = pm.getApplicationInfo(LINE_APP_ID, PackageManager.GET_META_DATA);
            // インストールされてたら、LINEへ
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("line://msg/text/" + "中間地点は…" + LINE_SEPARATOR
                    + stationName + "駅" + LINE_SEPARATOR
                    + "だよ！" + LINE_SEPARATOR
                    + "by 中間地点アプリ"
            ));
            return intent;

        } catch(PackageManager.NameNotFoundException e) {
            //インストールされてなかったら、インストールを要求する
            AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                    .setTitle("LINEが見つかりません。")
                    .setMessage("LINEをインストールしてやり直して下さい。")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 特に何もしない
                        }
                    })
                    .setCancelable(false);
            return dialog;
        }
    }
}
