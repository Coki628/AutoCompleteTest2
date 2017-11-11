package com.example.srcn4.autocompletetest2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<AutoCompleteTextView> textViewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // XMLとの紐付け
        textViewList.add((AutoCompleteTextView)findViewById(R.id.autocomplete_station));
        textViewList.add((AutoCompleteTextView)findViewById(R.id.autocomplete_station2));
        textViewList.add((AutoCompleteTextView)findViewById(R.id.autocomplete_station3));
        textViewList.add((AutoCompleteTextView)findViewById(R.id.autocomplete_station4));
        textViewList.add((AutoCompleteTextView)findViewById(R.id.autocomplete_station5));

        for (AutoCompleteTextView textView : textViewList) {
            // 自分で定義したアダプターをビューに設定する
            MyAdapter myadapter = new MyAdapter(getApplicationContext());
            textView.setAdapter(myadapter);
            // 何文字目から予測変換を出すかを設定
            textView.setThreshold(1);
        }
    }
    // 検索ボタンが押された時
    public void search(View v) {

        // 駅情報格納用VOのリスト
        ArrayList<StationVO> stationList = new ArrayList<>();

        for (AutoCompleteTextView textView : textViewList) {

            // テキストが空だったら何もしない
            if (textView.getText().toString().isEmpty()) {
                // continueは今の処理は終了するけどfor文自体は継続
                continue;
            }
            // 入力された駅名を取得
            String station = textView.getText().toString();
            // DB接続のためDAOを生成
            StationDAO dao = new StationDAO(getApplicationContext());
            // 駅情報を取得する
            StationVO vo = dao.selectStationByName(station);
            // レコードが取得できなかった時は処理中断
            if (vo == null) {
                Toast.makeText(getApplicationContext(), station + "：駅情報が取得できません", Toast.LENGTH_SHORT).show();
                return;
            }
            // 駅情報を格納したVOをリストに格納
            stationList.add(vo);
        }
        // 駅が入力されていなければ遷移せずに処理終了
        if (stationList.isEmpty()) {

            Toast.makeText(getApplicationContext(), "駅名を入力して下さい", Toast.LENGTH_SHORT).show();
        } else {
            // 画面遷移処理で、駅情報のリストを次の画面に送る
            Intent intent = new Intent(MainActivity.this, ResultActivity.class)
                    .putExtra("result", stationList);
            startActivity(intent);
        }
    }
}
