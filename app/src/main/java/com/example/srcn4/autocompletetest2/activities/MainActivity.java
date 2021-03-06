package com.example.srcn4.autocompletetest2.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.adapters.MyAdapterForAutoComplete;
import com.example.srcn4.autocompletetest2.application.MyApplication;
import com.example.srcn4.autocompletetest2.models.StationDetailVO;
import com.example.srcn4.autocompletetest2.storage.MyPreferenceManager;
import com.example.srcn4.autocompletetest2.storage.StationDAO;
import com.example.srcn4.autocompletetest2.utils.IntentUtil;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 入力画面クラス
 */
public class MainActivity extends AppCompatActivity {

    // 1～10の入力ボックスを格納するリスト
    private ArrayList<AutoCompleteTextView> textViewList = new ArrayList<>();
    // 入力ボックス左右の線を格納するリスト
    private ArrayList<TextView> strokeList = new ArrayList<>();
    // 上記左右の線の反対側を隠すカバーを格納するリスト
    private ArrayList<TextView> strokeCoverList = new ArrayList<>();
    // 設定画面実行フラグ
    private boolean isSettings = false;
    // 全アクティビティで使えるアプリケーションクラス
    private MyApplication ma;
    // プリファレンス管理クラス
    private MyPreferenceManager mpm;
    // 言語設定を保持する変数
    private String lang;
    // BGMを停止させる時使いたいので宣言しておく
    private int streamId1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // アプリケーションクラスのインスタンスを取得
        ma = (MyApplication)this.getApplication();
        // プリファレンス管理クラスのインスタンスを取得
        mpm = new MyPreferenceManager(getApplicationContext());

        // XMLとの紐付け：1～10の入力ボックス
        for (int i = 1; i <= 10; i++) {
            textViewList.add((AutoCompleteTextView)findViewById(getResources().getIdentifier(
                    "autocomplete_station" + String.valueOf(i), "id", getPackageName())));
        }
        // 1～9の左右線とカバー
        for (int i = 1; i <= 9; i++) {
            strokeList.add((TextView)findViewById(getResources().getIdentifier(
                    "stroke" + String.valueOf(i), "id", getPackageName())));
            strokeCoverList.add((TextView)findViewById(getResources().getIdentifier(
                    "stroke" + String.valueOf(i) + "_cover", "id", getPackageName())));
        }
        for (AutoCompleteTextView textView : textViewList) {
            // 自分で定義したアダプターをビューに設定する
            MyAdapterForAutoComplete myAdapter = new MyAdapterForAutoComplete(getApplicationContext());
            textView.setAdapter(myAdapter);
            // 何文字目から予測変換を出すかを設定
            textView.setThreshold(1);
            // 改行ボタンでキーボードを閉じる設定(これやらないとキーボードから次のテキストに進めない)
            textView.setOnEditorActionListener(new TextView.OnEditorActionListener(){
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                    return false;
                }
            });
        }
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

    // 検索ボタンが押された時
    public void search(View v) {
        // 効果音の再生
        ma.getMySoundManager().play(ma.getMySoundManager().getSoundApply());
        // 駅情報格納用VOのリスト
        ArrayList<StationDetailVO> stationList = new ArrayList<>();

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
            StationDetailVO vo = dao.selectStationByName(station);
            // レコードが取得できなかった時は処理中断
            if (vo == null) {
                Toast.makeText(getApplicationContext(), station + getString(R.string.main_toast1), Toast.LENGTH_SHORT).show();
                return;
            }
            // 駅情報を格納したVOをリストに格納
            stationList.add(vo);
        }
        // 駅名が入力されていれば画面遷移へ
        if (!stationList.isEmpty()) {
            // プリファレンスからアニメの設定値を取得
            boolean animeFlag = mpm.getAnimeFlag();
            // アニメが有効なら検索中画面、無効なら直接検索結果画面へ遷移
            if (animeFlag) {
                // 画面遷移処理で、駅情報のリストを次の画面に送る
                Intent intent = IntentUtil.prepareForSearchingActivity(MainActivity.this, stationList);
                startActivity(intent);
            } else {
                // 画面遷移処理で、駅情報のリストを次の画面に送る
                Intent intent = IntentUtil.prepareForResultActivity(MainActivity.this, stationList);
                startActivity(intent);
            }
        } else {
            // 駅が入力されていなければ遷移せずに処理終了
            Toast.makeText(getApplicationContext(), getString(R.string.main_toast2), Toast.LENGTH_SHORT).show();
        }
    }

    // クリアボタンが押された時呼ばれる
    public void clearAll(View v) {
        // 効果音の再生
        ma.getMySoundManager().play(ma.getMySoundManager().getSoundSelect());
        // 入力されたテキストを全て削除
        for (AutoCompleteTextView textView : textViewList) {

            textView.setText("");
        }
    }

    // 設定ボタンが押された時呼ばれる
    public void callSettings(final View v) {
        // 効果音の再生
        ma.getMySoundManager().play(ma.getMySoundManager().getSoundSelect());
        // 動かすボタンを取得
        final Button sound = findViewById(R.id.sound);
        final Button anime = findViewById(R.id.anime);
        final Button lang = findViewById(R.id.lang);
        // プリファレンスからサウンドとアニメの設定値を取得
        boolean soundFlag = mpm.getSoundFlag();
        boolean animeFlag = mpm.getAnimeFlag();
        // サウンドが無効なら、該当のボタンを半透明
        if (!soundFlag) {
            sound.setAlpha(0.2f);
        }
        // アニメが無効なら、該当のボタンを半透明
        if (!animeFlag) {
            anime.setAlpha(0.2f);
        }
        // 設定中に背景を暗くする準備
        TextView background = findViewById(R.id.background);
        // 設定ボタンを一時的にクリック不可にする
        v.setClickable(false);
        // 設定中フラグによって場合分け
        if (!isSettings) {
            // 設定を開く時の移動
            moveTarget(sound, 0.0f, 50.0f, 0.0f, -300.0f);
            moveTarget(anime, 0.0f, -200.0f, 0.0f, -200.0f);
            moveTarget(lang, 0.0f, -300.0f, 0.0f, 50.0f);
            // 背景を暗くする
            background.setAlpha(0.5f);
            // 設定ボタン以外を一時的に全て無効化する
            for (AutoCompleteTextView textView : textViewList) {
                // 表示されている入力ボックスを無効化
                if (textView.getVisibility() != View.GONE) {
                    textView.setEnabled(false);
                }
            }
            findViewById(R.id.search_button).setEnabled(false);
            findViewById(R.id.clear_button).setEnabled(false);
            findViewById(R.id.add_button).setEnabled(false);
            // 設定内ボタンの有効化
            sound.setEnabled(true);
            anime.setEnabled(true);
            lang.setEnabled(true);
            sound.setVisibility(View.VISIBLE);
            anime.setVisibility(View.VISIBLE);
            lang.setVisibility(View.VISIBLE);
            // フラグを設定中にセット
            isSettings = true;
        } else {
            // 設定を閉じる時の移動(基準の0は最初に動かす前のView位置)
            moveTarget(sound, 50.0f, 0.0f, -300.0f, 0.0f);
            moveTarget(anime, -200.0f, 0.0f, -200.0f, 0.0f);
            moveTarget(lang, -300.0f, 0.0f, 50.0f, 0.0f);
            // 暗い背景を元に戻す
            background.setAlpha(0.0f);
            // 設定ボタン以外を有効に戻す
            for (AutoCompleteTextView textView : textViewList) {
                // 表示されている入力ボックスを有効化
                if (textView.getVisibility() != View.GONE) {
                    textView.setEnabled(true);
                }
            }
            findViewById(R.id.search_button).setEnabled(true);
            findViewById(R.id.clear_button).setEnabled(true);
            findViewById(R.id.add_button).setEnabled(true);
            // 設定内ボタンの無効化
            sound.setEnabled(false);
            anime.setEnabled(false);
            lang.setEnabled(false);
            // フラグを設定中ではないにセット
            isSettings = false;
        }
        // 移動させる描画が0.3秒で終わるので、0.32秒後動作
        Handler hdl = new Handler();
        hdl.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 設定ボタンのクリックを再度有効化
                v.setClickable(true);
                // 設定を閉じる時には設定内ボタンを見えなくする
                if (!isSettings) {
                    sound.setVisibility(View.INVISIBLE);
                    anime.setVisibility(View.INVISIBLE);
                    lang.setVisibility(View.INVISIBLE);
                }
            }
        }, 320);
    }

    /**
     * 引数に与えたXY座標の位置にターゲットを移動させる
     * ※基準の0は最初に動かす前のView位置
     *
     * @param target 移動対象のView
     * @param fromX 移動前X座標
     * @param toX 移動後X座標
     * @param fromY 移動前Y座標
     * @param toY 移動後Y座標
     */
    private void moveTarget(View target, float fromX, float toX, float fromY, float toY) {

        // translationXプロパティを0fからtoXに変化させます
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("translationX", fromX, toX);
        // translationYプロパティを0fからtoYに変化させます
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("translationY", fromY, toY);
        // targetに対してholderX, holderYを同時に実行させます
        ObjectAnimator objAnimator = ObjectAnimator.ofPropertyValuesHolder(
                target, holderX, holderY);
        // animation時間 msec
        objAnimator.setDuration(300);
        objAnimator.start();
    }

    // ボックス追加ボタン(踏切)が押された時
    public void addInputBox(View v) {
        // 効果音の再生
        ma.getMySoundManager().play(ma.getMySoundManager().getSoundSelect());

        for (AutoCompleteTextView view : textViewList) {
            // 1～10の入力ボックスを確認して、GONEを見つけたらVISIBLEにする
            if (view.getVisibility() == View.GONE) {
                view.setVisibility(View.VISIBLE);
                // ひとつVISIBLEにしたら(表示されるビューがひとつ増える)、すぐにfor文のループ終了
                break;
            }
        }
        for (TextView view : strokeList) {
            // 1～9の左右の線を確認して、GONEを見つけたらVISIBLEにする
            if (view.getVisibility() == View.GONE) {
                view.setVisibility(View.VISIBLE);
                // ひとつVISIBLEにしたら(表示されるビューがひとつ増える)、すぐにfor文のループ終了
                break;
            }
        }
        for (TextView view : strokeCoverList) {
            // 1～9の左右線のカバーを確認して、GONEを見つけたらVISIBLEにする
            if (view.getVisibility() == View.GONE) {
                view.setVisibility(View.VISIBLE);
                // ひとつVISIBLEにしたら(表示されるビューがひとつ増える)、すぐにfor文のループ終了
                break;
            }
        }
    }

    // 音楽on/offボタン
    public void setSound(View v) {
        // 効果音の再生
        ma.getMySoundManager().play(ma.getMySoundManager().getSoundSelect());
        // プリファレンスからサウンドの設定値を取得
        boolean soundFlag = mpm.getSoundFlag();
        // サウンドが有効なら無効にして、ボタンも半透明
        if (soundFlag) {
            // BGMの停止
            ma.getMySoundManager().stop(streamId1);
            // プリファレンスに設定を保存
            mpm.setSoundFlag(false);
            // 即時適用させるので、mySoundManagerオブジェクトにも設定を反映
            ma.getMySoundManager().setSoundFlag(false);
            // ボタン透明度の変更
            v.setAlpha(0.2f);
        // 無効なら有効にして、透明度も戻す
        } else {
            mpm.setSoundFlag(true);
            ma.getMySoundManager().setSoundFlag(true);
            v.setAlpha(1.0f);
            // BGMの再生
            streamId1 = ma.getMySoundManager().playLoop(ma.getMySoundManager().getTrainMusic());
        }
    }

    // アニメーションon/offボタン
    public void setAnimation(View v) {
        // 効果音の再生
        ma.getMySoundManager().play(ma.getMySoundManager().getSoundSelect());
        // プリファレンスからアニメの設定値を取得
        boolean animeFlag = mpm.getAnimeFlag();
        // アニメが有効なら、無効にしてボタンも半透明
        if (animeFlag) {
            mpm.setAnimeFlag(false);
            v.setAlpha(0.2f);
            // 無効なら有効にして透明度も戻す
        } else {
            mpm.setAnimeFlag(true);
            v.setAlpha(1.0f);
        }
    }

    // 言語設定切り替えボタン
    public void setLanguage(View v) {
        // 効果音の再生
        ma.getMySoundManager().play(ma.getMySoundManager().getSoundSelect());
        // プリファレンスから言語の設定値を取得
        lang = mpm.getSelectLang();
        if (lang.equals("")) {
            // 設定値がない(今回が最初)の場合は端末設定から読み取る
            lang = Locale.getDefault().getLanguage();
        }
        // 言語設定変更のダイアログを表示
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.main_dialog1_title))
                .setMessage(getString(R.string.main_dialog1_message))
                .setPositiveButton(getString(R.string.main_dialog1_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 即時反映させるかを聞くダイアログを表示
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(getString(R.string.main_dialog2_title))
                                .setMessage(getString(R.string.main_dialog2_message))
                                .setPositiveButton(getString(R.string.main_dialog2_positive), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // アクティビティ再読み込み
                                        finish();
                                        startActivity(getIntent());
                                    }
                                })
                                .setNegativeButton(getString(R.string.main_dialog2_negative), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 何もしない
                                    }
                                })
                                .setCancelable(false)       //画面外タッチによるキャンセル阻止
                                .show();
                        // ひとつめのダイアログをOKしたら端末の言語設定を切り替え
                        if (lang.equals("en")) {
                            ma.setLocale("ja");
                            // プリファレンスに言語設定を登録
                            mpm.setSelectLang("ja");
                        } else {
                            ma.setLocale("en");
                            // プリファレンスに言語設定を登録
                            mpm.setSelectLang("en");
                        }
                    }
                })
                .setNegativeButton(getString(R.string.main_dialog1_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 何もしない
                    }
                })
                //画面外タッチによるキャンセル阻止
                .setCancelable(false)
                .show();
    }
}
