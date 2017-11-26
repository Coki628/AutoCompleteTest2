package com.example.srcn4.autocompletetest2.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.application.MyApplication;
import com.example.srcn4.autocompletetest2.models.StationDetailVO;
import com.example.srcn4.autocompletetest2.models.StationDistanceVO;
import com.example.srcn4.autocompletetest2.models.StationTransferVO;
import com.example.srcn4.autocompletetest2.network.JorudanInfoTask;
import com.example.srcn4.autocompletetest2.storage.StationDAO;
import com.example.srcn4.autocompletetest2.utils.CalculateUtil;
import com.example.srcn4.autocompletetest2.utils.IntentUtil;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 検索結果画面クラス
 */
public class ResultActivity extends AppCompatActivity {

    private ArrayList<StationDetailVO> stationList;
    private StationDetailVO resultStation;
    private LatLng centerLatLng;
    private TextView searchResult;
    private TextView searchResultKana;
    // タスクを実行するタイマー
    private Timer timer;
    Handler handler;
    // 点滅状態を保持する
    private boolean isBlink = false;
    // 全アクティビティで使えるアプリケーションクラス
    private MyApplication ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // アプリケーションクラスのインスタンスを取得
        ma = (MyApplication)this.getApplication();

        // 遷移前画面から入力駅情報リストを受け取る
        Intent intent = getIntent();
        stationList =
                (ArrayList<StationDetailVO>) intent.getSerializableExtra("result");
        // 計算用の緯度と経度のリスト
        ArrayList<Double> latList = new ArrayList<>();
        ArrayList<Double> lngList = new ArrayList<>();
        // 緯度経度を駅情報リストから計算用のリストに格納
        for (StationDetailVO vo : stationList) {

            latList.add(Double.parseDouble(vo.getLat()));
            lngList.add(Double.parseDouble(vo.getLng()));
        }
        // 中間地点座標の取得
        centerLatLng = CalculateUtil.calcCenterLatLng(latList, lngList);
        // 中間地点から近い座標にある駅を調べる
        ArrayList<StationDistanceVO> stationDistanceList
                = CalculateUtil.calcNearStationsList(centerLatLng, getApplicationContext());
        // DB接続のためDAOを生成
        StationDAO dao = new StationDAO(getApplicationContext());
        // 一番先頭にあるVOの駅情報を取得して返却
        resultStation = dao.selectStationByName(stationDistanceList.get(0).getName());
        // 駅名を表示
        searchResult = findViewById(R.id.search_result);
        searchResultKana = findViewById(R.id.search_result_kana);
        searchResult.setText(resultStation.getName());
        searchResultKana.setText(resultStation.getKana());
        // 駅名を点滅させるタイマーを設定する
        timer = new Timer();
        handler = new Handler();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // 処理をUIスレッドに送る
                handler.post(new Runnable() {
                    public void run() {
                        // 点滅フラグがfalseなら点灯させる
                        if (!isBlink) {
                            searchResult.setTextColor(Color.MAGENTA);
                            searchResultKana.setTextColor(Color.MAGENTA);
                            isBlink = true;
                        } else {
                            searchResult.setTextColor(Color.WHITE);
                            searchResultKana.setTextColor(Color.WHITE);
                            isBlink = false;
                        }
                    }
                });
            }
        // 1000ミリ秒後に1000ミリ秒間隔でタスク実行
        }, 1000, 1000);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // アクティビティが落ちる時にタイマーも終了させる
        timer.cancel();
    }

    // 共有ボタンが押された時
    public void callLINE(View v) {

        // LINE共有機能を呼び出す
        Object obj = IntentUtil.prepareForLINE(this, resultStation.getName());
        // Intentが返却されていたら、LINE連携へ遷移する
        if (obj instanceof Intent) {
            // 効果音の再生(ロードしたID, 左音量, 右音量, 優先度, ループ,再生速度)
            ma.getSoundPool().play(ma.getSoundApply(),
                    1.0f, 1.0f, 0, 0, 1);
            Intent intent = (Intent)obj;
            startActivity(intent);
        // AlertDialog.Builderが返却されていたら、遷移せずダイアログを表示
        } else if (obj instanceof AlertDialog.Builder) {
            // 効果音の再生(ロードしたID, 左音量, 右音量, 優先度, ループ,再生速度)
            ma.getSoundPool().play(ma.getSoundSelect(),
                    1.0f, 1.0f, 0, 0, 1);
            AlertDialog.Builder dialog = (AlertDialog.Builder)obj;
            dialog.show();
        }
    }

    // 周辺情報ボタンが押された時
    public void callMapInfo(View v) {
        // 効果音の再生(ロードしたID, 左音量, 右音量, 優先度, ループ,再生速度)
        ma.getSoundPool().play(ma.getSoundSelect(),
                1.0f, 1.0f, 0, 0, 1);
        // 画面遷移処理で、入力されていた駅情報のリストと候補駅を次の画面に送る
        Intent intent = IntentUtil.prepareForMapsActivity(ResultActivity.this,
                stationList, resultStation);
        startActivity(intent);
    }

    // 候補駅ボタンが押された時
    public void callSuggestedStations(View v) {
        // 効果音の再生(ロードしたID, 左音量, 右音量, 優先度, ループ,再生速度)
        ma.getSoundPool().play(ma.getSoundSelect(),
                1.0f, 1.0f, 0, 0, 1);
        // 画面遷移処理で、入力されていた駅情報のリストと中間地点座標を次の画面に送る
        Intent intent = IntentUtil.prepareForStationListActivity(ResultActivity.this,
                stationList, centerLatLng.latitude, centerLatLng.longitude);
        startActivity(intent);
    }

    // ルートボタンが押された時
    public void callRoute(View v) {
        // 効果音の再生(ロードしたID, 左音量, 右音量, 優先度, ループ,再生速度)
        ma.getSoundPool().play(ma.getSoundSelect(),
                1.0f, 1.0f, 0, 0, 1);
        // ジョルダンに経路検索のリクエストを送る
        final JorudanInfoTask jit = new JorudanInfoTask(this, stationList, resultStation.getJorudanName());
        // ここから非同期処理終了後の処理を記述する
        jit.setOnCallBack(new JorudanInfoTask.CallBackTask(){
            @Override
            public void CallBack() {
                super.CallBack();
                // 結果の取得
                ArrayList<StationTransferVO>[] resultInfoLists = jit.getResultInfoLists();
                // 画面遷移処理で、入力されていた駅情報のリストと候補駅を次の画面に送る
                Intent intent = IntentUtil.prepareForRouteActivity(ResultActivity.this,
                        stationList, resultStation, centerLatLng.latitude, centerLatLng.longitude,
                        resultInfoLists);
                startActivity(intent);
            }
        });
        // 非同期処理の実行
        jit.execute();
    }
}
