package com.example.srcn4.autocompletetest2.media;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.storage.MyPreferenceManager;

/**
 * 効果音管理クラス
 *
 * SoundPoolの機能をラップする
 */
public class MySoundManager {

    // 効果音を取り扱うSoundPoolオブジェクト
    private SoundPool soundPool;
    // 各効果音のID
    private int soundSelect;
    private int soundApply;
    private int soundTrain1;
    private int soundTrain2;
    // サウンドon/offフラグ
    private boolean soundFlag;

    // コンストラクタ：ここで効果音の初期化処理を行う
    public MySoundManager(Context context) {

        // プリファレンスからサウンドの設定値を取得
        MyPreferenceManager mpm = new MyPreferenceManager(context);
        this.soundFlag = mpm.getSoundFlag();

        // SoundPoolの初期化
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            // ロリポップより前のバージョンに対応するコード
            this.soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        } else {
            // 音を出すための手続き１　※音の出し方を設定している
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();
            // 音を出すための手続き２　※１の設定を利用してsoundPoolを設定
            this.soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(10).build();
        }
        // 音のロード処理を行う(※使用直前にロードしても間に合わないので早めに)
        soundSelect = this.soundPool.load(context, R.raw.button01a, 1);
        soundApply = this.soundPool.load(context, R.raw.decision7, 1);
        soundTrain1 = this.soundPool.load(context, R.raw.train_pass2, 1);
        soundTrain2 = this.soundPool.load(context, R.raw.train_horn2, 1);
    }

    // 音の再生
    public int play(int soundID) {
        // 再生はサウンドがonの時のみ行う
        if (soundFlag) {
            // 効果音の再生(ロードしたID, 左音量, 右音量, 優先度, ループ,再生速度)
            return soundPool.play(soundID, 1.0f, 1.0f, 0, 0, 1);
        } else {
            // 返却値はストリームID、音がなければ0
            return 0;
        }
    }

    // 個別に音量調節したい場面はこっちが使える
    public int play(int soundID, float leftVolume, float rightVolume) {
        // 再生はサウンドがonの時のみ行う
        if (soundFlag) {
            // 効果音の再生(ロードしたID, 左音量, 右音量, 優先度, ループ,再生速度)
            return soundPool.play(soundID, leftVolume, rightVolume, 0, 0, 1);
        } else {
            // 返却値はストリームID、音がなければ0
            return 0;
        }
    }

    // 音の停止
    public void stop(int soundID) {

        if (soundFlag) {
            soundPool.stop(soundID);
        }
    }

    // SoundPoolの開放
    public void release() {

        soundPool.release();
    }

    // 各SoundIDは外部から設定することはないのでGetterのみ定義
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

    // on/offのフラグは逆に外部からは設定しかしないのでSetterのみ定義
    public void setSoundFlag(boolean soundFlag) {
        this.soundFlag = soundFlag;
    }
}
