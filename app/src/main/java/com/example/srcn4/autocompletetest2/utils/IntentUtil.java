package com.example.srcn4.autocompletetest2.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.example.srcn4.autocompletetest2.activities.RouteActivity;
import com.example.srcn4.autocompletetest2.models.StationDetailVO;
import com.example.srcn4.autocompletetest2.models.StationTransferVO;
import com.example.srcn4.autocompletetest2.models.StationVO;
import com.example.srcn4.autocompletetest2.activities.MainActivity;
import com.example.srcn4.autocompletetest2.activities.MapsActivity;
import com.example.srcn4.autocompletetest2.activities.ResultActivity;
import com.example.srcn4.autocompletetest2.activities.StationListActivity;

import java.util.ArrayList;

/**
 * 画面遷移準備共通クラス
 */
public class IntentUtil {

    /**
     * 入力画面への遷移準備
     *
     * @param context コンテキスト(実行中ActivityのthisでOK)
     * @return Intent 画面遷移に必要な情報を保持したIntent
     */
    public static Intent prepareForMainActivity(Context context) {

        // メイン画面には何も送らないのでそのまま返却
        return new Intent(context, MainActivity.class);
    }

    /**
     * 検索結果画面への遷移準備
     *
     * @param context コンテキスト(実行中ActivityのthisでOK)
     * @param stationList 入力されていた駅情報のリスト
     * @return Intent 画面遷移に必要な情報を保持したIntent
     */
    public static Intent prepareForResultActivity(Context context, ArrayList<StationDetailVO> stationList) {

        // 入力されていた駅情報のリストを次の画面に送る準備
        Intent intent = new Intent(context, ResultActivity.class)
                .putExtra("result", stationList);
        return intent;
    }

    /**
     * 周辺情報画面への遷移準備
     *
     * @param context コンテキスト(実行中ActivityのthisでOK)
     * @param stationList 入力されていた駅情報のリスト
     * @param resultVO 検索結果として選ばれた候補の駅
     * @return Intent 画面遷移に必要な情報を保持したIntent
     */
    public static Intent prepareForMapsActivity(Context context, ArrayList<StationDetailVO> stationList, StationDetailVO resultVO) {

        // 入力されていた駅情報のリストと候補駅を次の画面に送る準備
        Intent intent = new Intent(context, MapsActivity.class)
                .putExtra("stationList", stationList)
                .putExtra("resultStation", resultVO);
        return intent;
    }

    /**
     * 候補駅画面への遷移準備
     *
     * @param context コンテキスト(実行中ActivityのthisでOK)
     * @param stationList 入力されていた駅情報のリスト
     * @param centerLat 計算された中間地点の緯度
     * @param centerLng 計算された中間地点の経度
     * @return Intent 画面遷移に必要な情報を保持したIntent
     */
    public static Intent prepareForStationListActivity(Context context, ArrayList<StationDetailVO> stationList,
                                                double centerLat, double centerLng) {

        // 入力されていた駅情報のリストと中間地点座標を次の画面に送る準備
        Intent intent = new Intent(context, StationListActivity.class)
                .putExtra("stationList", stationList)
                .putExtra("centerLat", centerLat)
                .putExtra("centerLng", centerLng);
        return intent;
    }

    /**
     * ルート画面への遷移準備
     *
     * @param context コンテキスト(実行中ActivityのthisでOK)
     * @param stationList 入力されていた駅情報のリスト
     * @param resultStation 検索結果として選ばれた候補の駅
     * @param centerLat 計算された中間地点の緯度
     * @param centerLng 計算された中間地点の経度
     * @param resultInfoLists Jorudan検索結果の経路を格納したVOのリストの配列
     * @return Intent 画面遷移に必要な情報を保持したIntent
     */
    public static Intent prepareForRouteActivity(Context context, ArrayList<StationDetailVO> stationList,
                StationDetailVO resultStation, double centerLat, double centerLng,
                ArrayList<StationTransferVO>[] resultInfoLists) {

        // 入力されていた駅情報のリストと候補駅とJorudan情報を次の画面に送る準備
        Intent intent = new Intent(context, RouteActivity.class)
                .putExtra("stationList", stationList)
                .putExtra("resultStation", resultStation)
                .putExtra("centerLat", centerLat)
                .putExtra("centerLng", centerLng)
                .putExtra("resultInfoLists", resultInfoLists);
        return intent;
    }

    /**
     * 外部連携(ブラウザ)への遷移準備
     *
     * @param resultStation 検索結果として選ばれた候補の駅
     * @param genre 選択されたジャンル
     * @return Intent 画面遷移(ブラウザ起動)に必要な情報を保持したIntent
     */
    public static Intent prepareForExternalInfo(StationDetailVO resultStation, int genre) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        // ジャンル別で接続先を分ける
        switch (genre){
            // レストラン→ぐるナビ
            case 0:
                intent.setData(Uri.parse("https://r.gnavi.co.jp/eki/"
                        + resultStation.getGnaviId() + "/rs/"));
                break;
            // 居酒屋→ぐるナビ
            case 1:
                intent.setData(Uri.parse("https://r.gnavi.co.jp/eki/"
                        + resultStation.getGnaviId() + "/izakaya/rs/"));
                break;
            // カフェ→ぐるナビ
            case 2:
                intent.setData(Uri.parse("https://r.gnavi.co.jp/eki/"
                        + resultStation.getGnaviId() + "/cafe/rs/"));
                break;
            // コンビニ→グーグルマップ
            case 3:
                intent.setData(Uri.parse("https://www.google.co.jp/maps/search/コンビニ/@"
                        + resultStation.getLat() + "," + resultStation.getLng() + "," + "15z")); // 15zはズーム具合
                break;
            // カラオケ→グーグルマップ
            case 4:
                intent.setData(Uri.parse("https://www.google.co.jp/maps/search/カラオケ/@"
                        + resultStation.getLat() + "," + resultStation.getLng() + "," + "15z"));
                break;
        }
        return intent;
    }

    /**
     * 共有(LINE)への遷移準備
     *
     * @param context コンテキスト(実行中ActivityのthisでOK)
     * @param stationName 検索結果として選ばれた候補駅の駅名
     * @return Intent 画面遷移(LINE起動)に必要な情報を保持したIntent
     */
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
