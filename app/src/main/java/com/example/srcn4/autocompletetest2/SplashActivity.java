package com.example.srcn4.autocompletetest2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

/**
 * 起動画面クラス
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // タイトルを非表示にします。
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // splash.xmlをViewに指定します。
        setContentView(R.layout.splash);
        // 500ms遅延させてsplashHandlerを実行します。
        Handler hdl = new Handler();
        hdl.postDelayed(new splashHandler(), 500);
    }
    class splashHandler implements Runnable {
        public void run() {
            // スプラッシュ完了後に実行するActivityを指定します。
            Intent intent = IntentUtils.prepareForMainActivity(SplashActivity.this);
            startActivity(intent);
            // SplashActivityを終了させます。
            SplashActivity.this.finish();
        }
    }
}
