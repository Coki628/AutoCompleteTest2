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

import java.util.ArrayList;

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
        stationDistanceList =
                (ArrayList<StationDistanceVO>) intent.getSerializableExtra("stationDistanceList");

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
    public void callLINE(String station) {

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
                    + station + "駅" + LINE_SEPARATOR
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
    public void callMapInfo(String station) {

        // DB接続のためDAOを生成
        StationDAO dao = new StationDAO(getApplicationContext());
        // 駅情報を取得する
        StationVO vo = dao.selectStationByName(station);
        // 周辺情報(MAP)の画面に遷移
        Intent intent = new Intent(StationListActivity.this, MapsActivity.class)
                .putExtra("stationList", stationList)
                .putExtra("resultStation", vo);
        startActivity(intent);
    }
}


