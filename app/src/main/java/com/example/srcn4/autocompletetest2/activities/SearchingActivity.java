package com.example.srcn4.autocompletetest2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.models.StationDetailVO;
import com.example.srcn4.autocompletetest2.utils.IntentUtil;

import java.util.ArrayList;

/**
 * 検索中画面クラス
 */
public class SearchingActivity extends AppCompatActivity {

    private ArrayList<StationDetailVO> stationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        // 遷移前画面から入力駅情報リストを受け取る
        Intent intent = getIntent();
        stationList =
                (ArrayList<StationDetailVO>) intent.getSerializableExtra("result");
        // 電車を貼ってあるビューを取得
        ImageView searchingTrain = findViewById(R.id.searching_train);
        // 拡大アニメーションの設定(電車を0倍から5倍まで拡大させていく)
        ScaleAnimation scale = new ScaleAnimation(
                0.0f,5.0f, 0.0f, 5.0f);
        // 移動アニメーションの設定(左から右へ移動)
        TranslateAnimation ta = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        // AnimationSetを使いアニメーションを合成する(複数同時に使う時はこれにまとめる)
        AnimationSet animeSet = new AnimationSet(false);
        animeSet.addAnimation(scale);
        animeSet.addAnimation(ta);
        // 3秒かけてアニメーションする
        animeSet.setDuration(3000);
        // animationが終わったそのまま表示にする(元位置に戻らない)
        animeSet.setFillAfter(true);
        //アニメーションの開始
        searchingTrain.startAnimation(animeSet);
        // 3秒遅延させて(アニメ終了時に)Handlerを実行します。
        Handler hdl = new Handler();
        hdl.postDelayed(new Runnable(){
            public void run() {
                // 画面遷移処理で、駅情報のリストを次の画面に送る
                Intent intent = IntentUtil.prepareForResultActivity(SearchingActivity.this, stationList);
                startActivity(intent);
                // SearchingActivityを終了
                SearchingActivity.this.finish();
            }
        }, 3000);
    }
}
