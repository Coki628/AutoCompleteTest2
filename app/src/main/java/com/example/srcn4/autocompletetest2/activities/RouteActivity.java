package com.example.srcn4.autocompletetest2.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.fragments.PageFragment;
import com.example.srcn4.autocompletetest2.models.StationDetailVO;
import com.example.srcn4.autocompletetest2.models.StationTransferVO;
import com.example.srcn4.autocompletetest2.utils.IntentUtil;

import java.util.ArrayList;

/**
 * ルート画面クラス
 */
public class RouteActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        PageFragment.OnFragmentInteractionListener {

    private ArrayList<StationDetailVO> stationList;
    private StationDetailVO resultStation;
    private double centerLat;
    private double centerLng;
    private ArrayList<StationTransferVO>[] resultInfoLists;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        // 遷移前画面から駅情報リストと候補駅を受け取る
        Intent intent = getIntent();
        stationList =
                (ArrayList<StationDetailVO>)intent.getSerializableExtra("stationList");
        resultStation = (StationDetailVO)intent.getSerializableExtra("resultStation");
        centerLat = intent.getDoubleExtra("centerLat", 0);
        centerLng = intent.getDoubleExtra("centerLng", 0);
        resultInfoLists = (ArrayList<StationTransferVO>[])intent.getSerializableExtra("resultInfoLists");

        // xmlからTabLayoutの取得
        tabLayout = findViewById(R.id.tabs);
        // xmlからViewPagerを取得
        ViewPager viewPager = findViewById(R.id.pager);
        // ページタイトル配列
        final String[] pageTitle = {"時間", "料金", "乗換"};

        // 表示Pageに必要な項目を設定
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                // 各タブに設定するフラグメントを渡す
                return PageFragment.newInstance(position, resultInfoLists);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return pageTitle[position];
            }

            @Override
            public int getCount() {
                return pageTitle.length;
            }
        };

        // ViewPagerにページを設定
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        // ViewPagerをTabLayoutを設定
        tabLayout.setupWithViewPager(viewPager);
        // タブをタップ可能にするためにviewPagerより前面に移動
        tabLayout.bringToFront();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    // 共有ボタンが押された時
    public void callLINE(View v) {
        // LINE共有機能を呼び出す
        Object obj = IntentUtil.prepareForLINE(this, resultStation.getName());
        // Intentが返却されていたら、LINE連携へ遷移する
        if (obj instanceof Intent) {
            Intent intent = (Intent)obj;
            startActivity(intent);
            // AlertDialog.Builderが返却されていたら、遷移せずダイアログを表示
        } else if (obj instanceof AlertDialog.Builder) {
            AlertDialog.Builder dialog = (AlertDialog.Builder)obj;
            dialog.show();
        }
    }

    // 周辺情報ボタンが押された時
    public void callMapInfo(View v) {

        // 画面遷移処理で、入力されていた駅情報のリストと候補駅を次の画面に送る
        Intent intent = IntentUtil.prepareForMapsActivity(RouteActivity.this,
                stationList, resultStation);
        startActivity(intent);
    }

    // 候補駅ボタンが押された時
    public void callSuggestedStations(View v) {

        // 画面遷移処理で、入力されていた駅情報のリストと中間地点座標を次の画面に送る
        Intent intent = IntentUtil.prepareForStationListActivity(RouteActivity.this,
                stationList, centerLat, centerLng);
        startActivity(intent);
    }

    // ジャンルボタンが押された時
    public void callGenre(View v) {
        // 各ジャンルを配列に格納
        final String[] items = {"レストラン", "居酒屋", "カフェ", "コンビニ", "カラオケ"};
        // デフォルトでチェックされているアイテム
        int defaultItem = 0;
        final ArrayList<Integer> checkedItems = new ArrayList<>();
        // 最初にデフォルトをリストに追加しておく
        checkedItems.add(defaultItem);
        new AlertDialog.Builder(this)
                .setTitle("ジャンル選択")
                //ラジオボタンの設定
                .setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // クリックされたら、今ある番号をクリアして新しい番号を格納
                        checkedItems.clear();
                        checkedItems.add(which);
                    }
                })
                // OKボタンの設定
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!checkedItems.isEmpty()) {
                            // ジャンルが決定されたら、外部連携のURL情報を取得
                            Intent intent = IntentUtil.prepareForExternalInfo(
                                    resultStation, checkedItems.get(0));
                            // ブラウザを起動する
                            startActivity(intent);
                        }
                    }
                })
                // キャンセルボタンの設定
                .setNegativeButton("Cancel", null)
                .show();
    }
}
