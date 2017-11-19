package com.example.srcn4.autocompletetest2.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.srcn4.autocompletetest2.models.StationDetailVO;
import com.example.srcn4.autocompletetest2.models.StationTransferVO;
import com.example.srcn4.autocompletetest2.network.JorudanInfoTask;
import com.example.srcn4.autocompletetest2.utils.CalculateUtil;
import com.example.srcn4.autocompletetest2.utils.IntentUtil;
import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.storage.StationDAO;
import com.example.srcn4.autocompletetest2.models.StationDistanceVO;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * 検索結果画面クラス
 */
public class ResultActivity extends AppCompatActivity {

    private ArrayList<StationDetailVO> stationList;
    private StationDetailVO resultVO;
    private LatLng centerLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // MainActivityから駅情報リストを受け取る
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
        resultVO = dao.selectStationByName(stationDistanceList.get(0).getName());
        // 駅名を表示
        TextView searchResult = findViewById(R.id.search_result);
        TextView searchResultKana = findViewById(R.id.search_result_kana);
        searchResult.setText(resultVO.getName());
        searchResultKana.setText(resultVO.getKana());
    }

    // 共有ボタンが押された時
    public void callLINE(View v) {
        // LINE共有機能を呼び出す
        Object obj = IntentUtil.prepareForLINE(this, resultVO.getName());
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
        Intent intent = IntentUtil.prepareForMapsActivity(ResultActivity.this, stationList, resultVO);
        startActivity(intent);
    }

    // 候補駅ボタンが押された時
    public void callSuggestedStations(View v) {

        // 画面遷移処理で、入力されていた駅情報のリストと中間地点座標を次の画面に送る
        Intent intent = IntentUtil.prepareForStationListActivity(ResultActivity.this, stationList,
                centerLatLng.latitude, centerLatLng.longitude);
        startActivity(intent);
    }

    // ルートボタンが押された時
    public void callRoute(View v) {

        // ジョルダンに経路検索のリクエストを送る
        final JorudanInfoTask jit = new JorudanInfoTask(this, stationList, resultVO.getJorudanName());
        // ここから非同期処理終了後の処理を記述する
        jit.setOnCallBack(new JorudanInfoTask.CallBackTask(){
            @Override
            public void CallBack() {
                super.CallBack();
                // 結果の取得
                ArrayList<StationTransferVO>[] resultInfoLists = jit.getResultInfoLists();
                // 画面遷移処理で、入力されていた駅情報のリストと候補駅を次の画面に送る
                Intent intent = IntentUtil.prepareForRouteActivity(ResultActivity.this,
                        stationList, resultVO, resultInfoLists);
                startActivity(intent);
            }
        });
        // 非同期処理の実行
        jit.execute();
    }
}
