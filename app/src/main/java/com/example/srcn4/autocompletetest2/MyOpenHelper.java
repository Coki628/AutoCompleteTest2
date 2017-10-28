package com.example.srcn4.autocompletetest2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MyOpenHelper extends SQLiteOpenHelper {

    MyOpenHelper(Context c){
        super(c, "station.db", null, 3);
    }

    // データベースがまだなければこれが呼ばれる
    public void onCreate(SQLiteDatabase db) {

        // 駅名テーブルの作成
        db.execSQL(
                "create table station ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT, kana TEXT);"
        );
        // DB作成時に駅データをまとめて取り込む(最終的にはここに数1000個の駅を書き込む)
        ArrayList<StationVO> stationVOList = new ArrayList<>();
        stationVOList.add(new StationVO("新橋", "しんばし"));
        stationVOList.add(new StationVO("品川", "しながわ"));
        stationVOList.add(new StationVO("新宿", "しんじゅく"));
        stationVOList.add(new StationVO("汐留", "しおどめ"));
        stationVOList.add(new StationVO("品川シーサイド", "しながわしーさいど"));
        stationVOList.add(new StationVO("赤坂", "あかさか"));
        stationVOList.add(new StationVO("新横浜", "しんよこはま"));
        stationVOList.add(new StationVO("汐入", "しおいり"));
        // VOに取り込んだ駅データをテーブルに全て挿入
        for (StationVO vo : stationVOList) {
            db.execSQL("insert into station(name, kana) " +
                    "values('" + vo.getName() + "', '" + vo.getKana() + "');"
            );
        }
    }

    // バージョン情報(SQLiteOpenHelperの第4引数)が異なると呼ばれる
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table station;");
        onCreate(db);
    }
}
