package com.example.srcn4.autocompletetest2.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * DB管理用クラス
 */
public class MyOpenHelper extends SQLiteOpenHelper {

    private Context c;

    MyOpenHelper(Context c){
        super(c, "station.db", null, 7);
        this.c = c;
    }

    // データベースがまだなければこれが呼ばれる
    public void onCreate(SQLiteDatabase db) {
        // assetsフォルダ内のsqlファイルから初期データの読み込み
        InputStream is;
        BufferedReader bfReader;
        try {
            is = c.getAssets().open("data.sql");
            bfReader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = bfReader.readLine()) != null) {
                if (!str.equals("")) {
                    sb.append(str);
                    sb.append("\n");
                }
            }
            sb.deleteCharAt(sb.length()-1);
            for (String sql: sb.toString().split(";")) {
                db.execSQL(sql);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // バージョン情報(SQLiteOpenHelperの第4引数)が異なると呼ばれる
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE station;");
        onCreate(db);
    }
}
