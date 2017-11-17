package com.example.srcn4.autocompletetest2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.example.srcn4.autocompletetest2.utils.IntentUtil;
import com.example.srcn4.autocompletetest2.R;

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
        // 500ms遅延させてsplashHandlerを実行します。
        Handler hdl = new Handler();
        hdl.postDelayed(new splashHandler(), 500);
    }
    class splashHandler implements Runnable {
        public void run() {
            // スプラッシュ完了後に実行するActivityを指定します。
            Intent intent = IntentUtil.prepareForMainActivity(SplashActivity.this);
            startActivity(intent);
            // SplashActivityを終了させます。
            SplashActivity.this.finish();
        }
    }
}
