package com.example.srcn4.autocompletetest2.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.example.srcn4.autocompletetest2.media.MySoundManager;

import java.util.Locale;

/**
 * アプリケーションクラス
 *
 * 複数のアクティビティで横断的に保持したい値などをここで扱う
 */
public class MyApplication extends Application {

    private MySoundManager mySoundManager;

    // アプリ開始時に行う処理
    @Override
    public void onCreate() {
        super.onCreate();

        // 効果音の初期化処理を行う
        mySoundManager = new MySoundManager(getApplicationContext());

        // アプリ内言語設定の確認
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String lang = pref.getString("selectLang", "");
        setLocale(lang);
    }

    // アプリ終了時に行う処理
    @Override
    public void onTerminate() {
        super.onTerminate();
        // サウンドプールをクリア
        mySoundManager.release();
    }

    // アプリ内言語設定の変更
    public void setLocale(String lang) {
        // アプリ内言語設定があったら適用、なければ端末の設定のまま(何もしない)
        if (!lang.equals("")) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config, null);
        }
    }

    public MySoundManager getMySoundManager() {
        return mySoundManager;
    }

    public void setMySoundManager(MySoundManager mySoundManager) {
        this.mySoundManager = mySoundManager;
    }
}
