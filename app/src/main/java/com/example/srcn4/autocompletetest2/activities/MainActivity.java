package com.example.srcn4.autocompletetest2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.adapters.MyAdapterForAutoComplete;
import com.example.srcn4.autocompletetest2.models.StationDetailVO;
import com.example.srcn4.autocompletetest2.storage.StationDAO;
import com.example.srcn4.autocompletetest2.utils.IntentUtil;

import java.util.ArrayList;

/**
 * 入力画面クラス
 */
public class MainActivity extends AppCompatActivity {

    // 入力された内容を格納するリスト
    private ArrayList<AutoCompleteTextView> textViewList = new ArrayList<>();
    // 設定画面実行フラグ
    private boolean isSettings = false;

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
        textViewList.add((AutoCompleteTextView)findViewById(R.id.autocomplete_station6));
        textViewList.add((AutoCompleteTextView)findViewById(R.id.autocomplete_station7));
        textViewList.add((AutoCompleteTextView)findViewById(R.id.autocomplete_station8));
        textViewList.add((AutoCompleteTextView)findViewById(R.id.autocomplete_station9));
        textViewList.add((AutoCompleteTextView)findViewById(R.id.autocomplete_station10));

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
    // 検索ボタンが押された時
    public void search(View v) {

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
            Intent intent = IntentUtil.prepareForSearchingActivity(MainActivity.this, stationList);
            startActivity(intent);
        }
    }

    // クリアボタンが押された時呼ばれる
    public void clearAll(View v) {
        // 入力されたテキストを全て削除
        for (AutoCompleteTextView textView : textViewList) {

            textView.setText("");
        }
    }

    // 設定ボタンが押された時呼ばれる
    public void callSettings(final View v) {
        // 動かすボタンを取得
        Button sound = findViewById(R.id.sound);
        Button anime = findViewById(R.id.anime);
        Button lang = findViewById(R.id.lang);
        // 設定中に背景を暗くする準備
        TextView background = findViewById(R.id.background);
        // 設定ボタンを一時的にクリック不可にする
        v.setClickable(false);
        // 設定中フラグによって場合分け
        if (!isSettings) {
            // 設定を開く時の移動
            moveTarget(sound, 100, -200);
            moveTarget(anime, -100, -100);
            moveTarget(lang, -200, 50);
            // 背景を暗くする
            background.setAlpha(0.5f);
            // 設定ボタン以外を一時的に全て無効化する
            findViewById(R.id.autocomplete_station).setEnabled(false);
            findViewById(R.id.autocomplete_station2).setEnabled(false);
            findViewById(R.id.autocomplete_station3).setEnabled(false);
            findViewById(R.id.autocomplete_station4).setEnabled(false);
            findViewById(R.id.autocomplete_station5).setEnabled(false);
            findViewById(R.id.search_button).setEnabled(false);
            findViewById(R.id.clear_button).setEnabled(false);
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
            // 設定を閉じる時の移動
            moveTarget(sound, -100, 200);
            moveTarget(anime, 100, 100);
            moveTarget(lang, 200, -50);
            // 暗い背景を元に戻す
            background.setAlpha(0.0f);
            // 設定ボタン以外の有効化
            findViewById(R.id.autocomplete_station).setEnabled(true);
            findViewById(R.id.autocomplete_station2).setEnabled(true);
            findViewById(R.id.autocomplete_station3).setEnabled(true);
            findViewById(R.id.autocomplete_station4).setEnabled(true);
            findViewById(R.id.autocomplete_station5).setEnabled(true);
            findViewById(R.id.search_button).setEnabled(true);
            findViewById(R.id.clear_button).setEnabled(true);
            // 設定内ボタンの無効化
            sound.setEnabled(false);
            anime.setEnabled(false);
            lang.setEnabled(false);
            sound.setVisibility(View.INVISIBLE);
            anime.setVisibility(View.INVISIBLE);
            lang.setVisibility(View.INVISIBLE);
            // フラグを設定中ではないにセット
            isSettings = false;
        }
        // 移動させる描画が0.3秒で終わるので、0.4秒後動作
        Handler hdl = new Handler();
        hdl.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 設定ボタンのクリックを再度有効化
                v.setClickable(true);

            }
        }, 400);
    }

    /**
     * ターゲットを相対的に移動する
     *
     * TranslateAnimationで移動アニメーションをすると、
     * 見かけ上は移動したように見えるが実際には移動していないらしい。
     * そこで、アニメーション完了時にlayout()を使って物理的にも移動させる。
     *
     * @param v 移動させるビュー
     * @param dx X軸に対する相対移動量
     * @param dy Y軸に対する相対移動量
     */
    protected void moveTarget(final View v, int dx, int dy) {
        if (v.getAnimation() != null) {
            // アニメーション中なら何もしない
            return;
        }
        final int left = v.getLeft();
        final int top = v.getTop();
        final int toX = left + dx;
        final int toY = top + dy;
        TranslateAnimation ta = new TranslateAnimation(left, toX, top, toY);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                // 物理的にも移動
                v.layout(toX, toY, toX + v.getWidth(), toY + v.getHeight());
                // これをしないとアニメーション完了後にチラつく
                v.setAnimation(null);
            }
        });
        // animation時間 msec
        ta.setDuration(300);
        // 繰り返し回数
        ta.setRepeatCount(0);
        // animationが終わったそのまま表示にする
        ta.setFillAfter(true);
        // 初期位置に戻す。これをしないと２度目以降のアニメーションがおかしくなる（チラつく）
        v.layout(0, 0, v.getWidth(), v.getHeight());
        v.startAnimation(ta);
    }

    // ボックス追加ボタン(踏切)が押された時
    public void addInputBox(View v) {

        for (AutoCompleteTextView view : textViewList) {
            // 1～10のテキストビューを確認して、GONEを見つけたらVISIBLEにする
            if (view.getVisibility() == View.GONE) {
                view.setVisibility(View.VISIBLE);
                // ひとつVISIBLEにしたら(表示されるビューがひとつ増える)、すぐに処理終了
                break;
            }
        }
    }
}
