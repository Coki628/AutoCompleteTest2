package com.example.srcn4.autocompletetest2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.utils.IntentUtil;

/**
 * 起動画面クラス
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // タイトルを非表示にします。
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // activity_splash.xmlをViewに指定します。
        setContentView(R.layout.activity_splash);
        // ロゴを貼ってあるビューを取得
        LinearLayout titleLogo = findViewById(R.id.title_logo);
        // アニメーションの設定(画面左から移動してくる)
        TranslateAnimation ta = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -2.5f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        // animation時間 msec
        ta.setDuration(1000);
        // 繰り返し回数
        ta.setRepeatCount(0);
        // animationが終わったそのまま表示にする
        ta.setFillAfter(true);
        //アニメーションの開始
        titleLogo.startAnimation(ta);
        // 1.5秒遅延させてHandlerを実行します。
        Handler hdl = new Handler();
        hdl.postDelayed(new Runnable(){
            public void run() {
                // スプラッシュ完了後に実行するActivityを指定します。
                Intent intent = IntentUtil.prepareForMainActivity(SplashActivity.this);
                startActivity(intent);
                // SplashActivityを終了させます。
                SplashActivity.this.finish();
            }
        }, 1500);
    }
}
