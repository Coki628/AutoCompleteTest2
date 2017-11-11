package com.example.srcn4.autocompletetest2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * 駅情報DAO
 *
 * データベースの駅情報テーブルにアクセスする時に使用する。
 */
public class StationDAO {

    private MyOpenHelper helper;
    private SQLiteDatabase db;

    // コンストラクタ(初期化時に呼ばれる)
    public StationDAO (Context context) {

        // DBの準備
        this.helper = new MyOpenHelper(context);
        this.db = helper.getWritableDatabase();
    }

    // 文字列と前方一致する駅名と仮名を取得するメソッド
    public ArrayList<StationVO> selectNamesByStr(String str) {

        // 結果返却用リスト
        ArrayList<StationVO> stationList = new ArrayList<>();
        // DBから前方一致で駅名かカナに当てはまる駅情報を取得
        Cursor cursor = db.rawQuery("SELECT name, kana FROM station WHERE name LIKE ? || '%' OR kana LIKE ? || '%'",
                new String[]{str, str});
        // 取得した数だけ繰り返す
        while (cursor.moveToNext()) {
            // 取得した全駅名と仮名をVOに格納
            stationList.add(new StationVO(cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("kana"))));
        }
        // 使用済カーソルはクローズする
        cursor.close();
        // 結果値を格納したリストを返却
        return stationList;
    }

    // 駅名から駅情報を取得するメソッド
    public StationVO selectStationByName(String name) {

        // 結果返却用VO
        StationVO vo = null;
        // DBから駅情報を取得
        Cursor cursor = db.rawQuery("SELECT kana, pref_cd, gnavi_id, lat, lng FROM station WHERE name = ?",
                new String[]{name});

        if (cursor.moveToNext()) {
            // カーソルから各項目を取得
            String kana = cursor.getString(cursor.getColumnIndex("kana"));
            String prefCd = cursor.getString(cursor.getColumnIndex("pref_cd"));
            String gnaviId = cursor.getString(cursor.getColumnIndex("gnavi_id"));
            String lat = cursor.getString(cursor.getColumnIndex("lat"));
            String lng = cursor.getString(cursor.getColumnIndex("lng"));
            // DBから取得した値を格納したVOを生成
            vo = new StationVO(name, kana, prefCd, gnaviId, lat, lng);
        }
        // 使用済カーソルはクローズする
        cursor.close();
        // 結果値VOを返却
        return vo;
    }

    // 全ての駅情報を取得するメソッド
    public ArrayList<StationVO> selectAllStations() {

        // 結果返却用リスト
        ArrayList<StationVO> stationList = new ArrayList<>();
        // DBから全情報を取得
        Cursor cursor = db.rawQuery("SELECT name, kana, pref_cd, gnavi_id, lat, lng FROM station", null);

        while (cursor.moveToNext()) {
            // カーソルから各項目を取得
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String kana = cursor.getString(cursor.getColumnIndex("kana"));
            String prefCd = cursor.getString(cursor.getColumnIndex("pref_cd"));
            String gnaviId = cursor.getString(cursor.getColumnIndex("gnavi_id"));
            String lat = cursor.getString(cursor.getColumnIndex("lat"));
            String lng = cursor.getString(cursor.getColumnIndex("lng"));
            // DBから取得した値を格納したVOを生成
            StationVO vo = new StationVO(name, kana, prefCd, gnaviId, lat, lng);
            // VOをリストに格納
            stationList.add(vo);
        }
        // 使用済カーソルはクローズする
        cursor.close();
        // 結果値を格納したリストを返却
        return stationList;
    }
}
