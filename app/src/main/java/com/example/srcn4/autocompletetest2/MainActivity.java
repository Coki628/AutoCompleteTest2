package com.example.srcn4.autocompletetest2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AutoCompleteTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_station);
        // 自分で定義したアダプターをビューに設定する
        MyAdapter myadapter = new MyAdapter(getApplicationContext());
        textView.setAdapter(myadapter);
        // 何文字目から予測変換を出すかを設定
        textView.setThreshold(1);
    }
}
