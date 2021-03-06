package com.example.srcn4.autocompletetest2.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.application.MyApplication;
import com.example.srcn4.autocompletetest2.models.StationDetailVO;
import com.example.srcn4.autocompletetest2.models.StationTransferVO;
import com.example.srcn4.autocompletetest2.network.JorudanInfoTask;
import com.example.srcn4.autocompletetest2.network.MyNetworkManager;
import com.example.srcn4.autocompletetest2.utils.CalculateUtil;
import com.example.srcn4.autocompletetest2.utils.IntentUtil;
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

    private ArrayList<StationDetailVO> stationList;
    private StationDetailVO resultStation;
    private LatLng centerLatLng;
    private ArrayList<Double> latList = new ArrayList<>();
    private ArrayList<Double> lngList = new ArrayList<>();
    private ArrayList<LatLng> latLngList = new ArrayList<>();
    // 全アクティビティで使えるアプリケーションクラス
    private MyApplication ma;
    // BGMを停止させる時使いたいので宣言しておく
    private int streamId1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // アプリケーションクラスのインスタンスを取得
        ma = (MyApplication)this.getApplication();

        // 遷移前画面から駅情報リストと候補駅を受け取る
        Intent intent = getIntent();
        stationList =
                (ArrayList<StationDetailVO>)intent.getSerializableExtra("stationList");
        resultStation = (StationDetailVO)intent.getSerializableExtra("resultStation");

        // 緯度経度を計算用のリストに格納
        for (StationDetailVO vo : stationList) {
            latList.add(Double.parseDouble(vo.getLat()));
            lngList.add(Double.parseDouble(vo.getLng()));
        }
        // マップ使用準備
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // BGMの再生
        streamId1 = ma.getMySoundManager().playLoop(ma.getMySoundManager().getTrainMusic());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // BGMの停止
        ma.getMySoundManager().stop(streamId1);
    }

    // マップ使用準備が完了したら呼ばれる
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // マップオブジェクトを受け取る
        GoogleMap map = googleMap;

        for (int i = 0; i < stationList.size(); i++) {
            // 取得した座標の数だけピンをセットする
            latLngList.add(new LatLng(latList.get(i), lngList.get(i)));
            map.addMarker(new MarkerOptions().position(latLngList.get(i))
                    .title(stationList.get(i).getName() + "駅"));
        }
        // 中間地点座標の取得
        centerLatLng = CalculateUtil.calcCenterLatLng(latList, lngList);
        // 候補駅の座標を取得
        LatLng resultStationLatLng = new LatLng(Double.parseDouble(resultStation.getLat()),
                Double.parseDouble(resultStation.getLng()));
        // 最大距離の取得
        double[] maxDistance = CalculateUtil.calcMaxDistance(latList, lngList);
        double maxDistanceLat = maxDistance[0];
        double maxDistanceLng = maxDistance[1];
        // 最大距離に応じてズーム具合を調整する
        int zoomLevel = 0;
        Log.d("lat", String.valueOf(maxDistanceLat));
        Log.d("lng", String.valueOf(maxDistanceLng));
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
        } else if (maxDistanceLat <= 0.8 && maxDistanceLng <= 0.8) {
            zoomLevel = 9;
        } else {
            zoomLevel = 8;
        }
        // 候補駅に色違いのピンをセットして情報ウインドウも設定する
        Marker stationMarker = map.addMarker(new MarkerOptions().position(resultStationLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title(resultStation.getName() + "駅！"));
        // 情報ウインドウを表示
        stationMarker.showInfoWindow();
        // 中間地点に色違いのピンをセットして情報ウインドウも設定する
        map.addMarker(new MarkerOptions().position(centerLatLng)
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
    }

    // 共有ボタンが押された時
    public void callLINE(View v) {
        // LINE共有機能を呼び出す
        Object obj = IntentUtil.prepareForLINE(this, stationList, resultStation);
        // Intentが返却されていたら、LINE連携へ遷移する
        if (obj instanceof Intent) {
            // 効果音の再生
            ma.getMySoundManager().play(ma.getMySoundManager().getSoundApply());
            Intent intent = (Intent)obj;
            startActivity(intent);
        // AlertDialog.Builderが返却されていたら、遷移せずダイアログを表示
        } else if (obj instanceof AlertDialog.Builder) {
            // 効果音の再生
            ma.getMySoundManager().play(ma.getMySoundManager().getSoundSelect());
            AlertDialog.Builder dialog = (AlertDialog.Builder)obj;
            dialog.show();
        }
    }

    // ジャンルボタンが押された時
    public void callGenre(View v) {
        // 効果音の再生
        ma.getMySoundManager().play(ma.getMySoundManager().getSoundSelect());
        // 各ジャンルを配列に格納
        final String[] items = {"レストラン", "居酒屋", "カフェ", "コンビニ", "カラオケ"};
        // デフォルトでチェックされているアイテム
        int defaultItem = 0;
        final ArrayList<Integer> checkedItems = new ArrayList<>();
        // 最初にデフォルトをリストに追加しておく
        checkedItems.add(defaultItem);
        new AlertDialog.Builder(this)
                .setTitle("ジャンル選択")
                //ラジオボタンの設定
                .setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 効果音の再生
                        ma.getMySoundManager().play(ma.getMySoundManager().getSoundSelect());
                        // クリックされたら、今ある番号をクリアして新しい番号を格納
                        checkedItems.clear();
                        checkedItems.add(which);
                    }
                })
                // OKボタンの設定
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!checkedItems.isEmpty()) {
                            // 効果音の再生
                            ma.getMySoundManager().play(ma.getMySoundManager().getSoundApply());
                            // ジャンルが決定されたら、外部連携のURL情報を取得
                            Intent intent = IntentUtil.prepareForExternalInfo(
                                    resultStation, checkedItems.get(0));
                            // ブラウザを起動する
                            startActivity(intent);
                        }
                    }
                })
                // キャンセルボタンの設定
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!checkedItems.isEmpty()) {
                            // 効果音の再生
                            ma.getMySoundManager().play(ma.getMySoundManager().getSoundSelect());
                        }
                    }
                })
                .show();
    }

    // 候補駅ボタンが押された時
    public void callSuggestedStations(View v) {
        // 効果音の再生
        ma.getMySoundManager().play(ma.getMySoundManager().getSoundSelect());
        // 画面遷移処理で、入力されていた駅情報のリストと中間地点座標を次の画面に送る
        Intent intent = IntentUtil.prepareForStationListActivity(MapsActivity.this, stationList,
                centerLatLng.latitude, centerLatLng.longitude);
        startActivity(intent);
    }

    // ルートボタンが押された時
    public void callRoute(View v) {
        // 効果音の再生
        ma.getMySoundManager().play(ma.getMySoundManager().getSoundSelect());
        //　ネットワークの接続状態を確認
        AlertDialog.Builder dialog = MyNetworkManager.checkConnection(this);
        if (dialog == null) {
            // ジョルダンに経路検索のリクエストを送る
            final JorudanInfoTask jit = new JorudanInfoTask(this, stationList,
                    resultStation.getJorudanName());
            // ここから非同期処理終了後の処理を記述する
            jit.setOnCallBack(new JorudanInfoTask.CallBackTask(){
                @Override
                public void CallBack() {
                    super.CallBack();
                    // 結果の取得
                    ArrayList<StationTransferVO>[] resultInfoLists = jit.getResultInfoLists();
                    // 画面遷移処理で、入力されていた駅情報のリストと候補駅を次の画面に送る
                    Intent intent = IntentUtil.prepareForRouteActivity(MapsActivity.this,
                            stationList, resultStation,
                            centerLatLng.latitude, centerLatLng.longitude, resultInfoLists);
                    startActivity(intent);
                }
            });
            // 非同期処理の実行
            jit.execute();
        } else {
            // 電波なかったらダイアログ出す
            dialog.show();
        }
    }
}
