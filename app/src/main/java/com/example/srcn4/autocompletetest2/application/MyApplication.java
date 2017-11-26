package com.example.srcn4.autocompletetest2.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.example.srcn4.autocompletetest2.R;

import java.util.Locale;

/**
 * アプリケーションクラス
 *
 * 複数のアクティビティで横断的に保持したい値などをここで扱う
 */
public class MyApplication extends Application {

    // 各効果音のID
    private int soundSelect;
    private int soundApply;
    private int soundTrain1;
    private int soundTrain2;
    // サウンドプール
    private SoundPool soundPool;

    @Override
    public void onCreate() {
        super.onCreate();
        // プリファレンスからサウンドの設定値を取得
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        boolean soundFlag = pref.getBoolean("soundFlag", true);

        // 効果音の初期化処理
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            // ロリポップより前のバージョンに対応するコード
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        } else {
            // 音を出すための手続き１　※音の出し方を設定している
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();
            // 音を出すための手続き２　※１の設定を利用してsoundPoolを設定
            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(10).build();
        }
        // サウンドが有効なら音のロード処理を行う(※直前にロードしても間に合わないので早めに)
        if (soundFlag) {
            soundSelect = soundPool.load(getBaseContext(), R.raw.button01a, 1);
            soundApply = soundPool.load(getBaseContext(), R.raw.decision7, 1);
            soundTrain1 = soundPool.load(getBaseContext(), R.raw.train_pass2, 1);
            soundTrain2 = soundPool.load(getBaseContext(), R.raw.train_horn2, 1);


        }

        // アプリ内言語設定の確認
        String lang = pref.getString("selectLang", "");
        setLocale(lang);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // サウンドプールをクリア
        soundPool.release();
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

    public int getSoundSelect() {
        return soundSelect;
    }

    public int getSoundApply() {
        return soundApply;
    }

    public int getSoundTrain1() {
        return soundTrain1;
    }

    public int getSoundTrain2() {
        return soundTrain2;
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }
}
