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

    private AutoCompleteTextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_station);
        // 自分で定義したアダプターをビューに設定する
        MyAdapter myadapter = new MyAdapter(getApplicationContext());
        textView.setAdapter(myadapter);
        // 何文字目から予測変換を出すかを設定
        textView.setThreshold(1);
    }
    // 検索ボタンが押された時
    public void search(View v) {

        if (textView.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "駅名を入力して下さい", Toast.LENGTH_SHORT).show();
            // テキストが空だったら何もしない
            return;
        }
        // 入力された駅名を取得
        String station1 = textView.getText().toString();
        // 駅情報格納用VOのリスト
        ArrayList<StationVO> stationList = new ArrayList<>();
        // DBの準備
        MyOpenHelper helper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        // DBから駅情報を取得
        Cursor cursor = db.rawQuery("select kana, pref_cd, gnavi_id, lat, lng from station where name = ?",
                new String[]{station1});
        // 取得レコードが一意でなかった時は処理中断
        if (cursor.getCount() > 1) {
            Toast.makeText(getApplicationContext(), station1 + "：駅情報に重複があります", Toast.LENGTH_SHORT).show();
            return;
        } else if (cursor.getCount() == 0) {
            Toast.makeText(getApplicationContext(), station1 + "：駅情報が取得できません", Toast.LENGTH_SHORT).show();
            return;
        }
        // カーソル位置を動かす
        while (cursor.moveToNext()) {
            // カーソルから各項目を取得
            String kana = cursor.getString(cursor.getColumnIndex("kana"));
            String prefCd = cursor.getString(cursor.getColumnIndex("pref_cd"));
            String gnaviId = cursor.getString(cursor.getColumnIndex("gnavi_id"));
            String lat = cursor.getString(cursor.getColumnIndex("lat"));
            String lng = cursor.getString(cursor.getColumnIndex("lng"));
            // DBから取得した値を格納したVOを生成
            StationVO vo = new StationVO(station1, kana, prefCd, gnaviId, lat, lng);
            Log.d("test", vo.toString());
            // 駅情報を格納したVOをリストに格納
            stationList.add(vo);
        }
        // 使用したカーソルはクローズすること
        cursor.close();
        // 画面遷移処理で、駅情報のリストを次の画面に送る
        Intent intent = new Intent(MainActivity.this, MapsActivity.class)
                .putExtra("result", stationList);
        startActivity(intent);
    }
}
