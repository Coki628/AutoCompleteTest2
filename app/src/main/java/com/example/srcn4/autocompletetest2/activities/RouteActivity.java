package com.example.srcn4.autocompletetest2.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.fragments.PageFragment;
import com.example.srcn4.autocompletetest2.models.StationDetailVO;
import com.example.srcn4.autocompletetest2.models.StationTransferVO;

import java.util.ArrayList;

/**
 * ルート画面クラス
 */
public class RouteActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        PageFragment.OnFragmentInteractionListener {

    private ArrayList<StationDetailVO> stationList;
    private StationDetailVO resultStation;
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
}
