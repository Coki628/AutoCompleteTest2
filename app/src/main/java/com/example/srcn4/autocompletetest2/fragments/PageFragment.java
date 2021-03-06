package com.example.srcn4.autocompletetest2.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.models.StationTransferVO;
import com.example.srcn4.autocompletetest2.utils.ConvertUtil;
import com.example.srcn4.autocompletetest2.utils.IntentUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * ルート画面のタブ管理用フラグメント
 */
public class PageFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    // コンストラクタ
    public PageFragment() {
    }

    // フラグメントを生成する時アクティビティから呼ばれる
    public static PageFragment newInstance(int position, ArrayList<StationTransferVO>[] resultInfoLists) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putSerializable("resultInfoLists", resultInfoLists);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 必要な値の受け取り
        int position = getArguments().getInt("position", 0);
        ArrayList<StationTransferVO>[] resultInfoLists =
                (ArrayList<StationTransferVO>[])getArguments().getSerializable("resultInfoLists");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        // 各タブに応じたソート処理
        for (int i = 0; i < resultInfoLists.length; i++) {
            switch (position) {
                // 時間順
                case 0:
                    Collections.sort(resultInfoLists[i], new Comparator<StationTransferVO>() {
                        @Override
                        public int compare(StationTransferVO a, StationTransferVO b) {
                            // 時間で比較して、同じなら金額で比較
                            int result = a.getTime() - b.getTime();
                            if (result == 0) {
                                return a.getCost() - b.getCost();
                            } else {
                                return result;
                            }
                        }
                    });
                    break;
                // 金額順
                case 1:
                    Collections.sort(resultInfoLists[i], new Comparator<StationTransferVO>() {
                        @Override
                        public int compare(StationTransferVO a, StationTransferVO b) {
                            // 金額で比較して、同じなら時間で比較
                            int result = a.getCost() - b.getCost();
                            if (result == 0) {
                                return a.getTime() - b.getTime();
                            } else {
                                return result;
                            }
                        }
                    });
                    break;
                // 乗換回数順
                case 2:
                    Collections.sort(resultInfoLists[i], new Comparator<StationTransferVO>() {
                        @Override
                        public int compare(StationTransferVO a, StationTransferVO b) {
                            // 乗換回数で比較して、同じなら時間で比較、時間も同じなら金額で比較
                            int result = a.getTransfer() - b.getTransfer();
                            if (result == 0) {
                                if (a.getTime() - b.getTime() == 0) {
                                    return a.getCost() - b.getCost();
                                } else {
                                    return a.getTime() - b.getTime();
                                }
                            } else {
                                return result;
                            }
                        }
                    });
                    break;
            }
            // ソート確認
            for (StationTransferVO vo : resultInfoLists[i]) {
                Log.d("sorted " + String.valueOf(i), vo.toString());
            }
        }
        // 11個目の空レイアウトのボタンを予め消しておく
        view.findViewById(R.id.route_info11).findViewById(R.id.detail_button).setVisibility(View.GONE);
        // ソート結果に応じて、各出発駅から1位のものだけ表示する(.get(0)がリストの先頭)
        for (int i = 0; i < resultInfoLists.length; i++) {
            // 各数値を表示用の形式に戻す
            String timeStr = ConvertUtil.minutesToTime(resultInfoLists[i].get(0).getTime());
            String costStr = ConvertUtil.addYenAndComma(resultInfoLists[i].get(0).getCost());
            String transferStr = ConvertUtil.addKai(resultInfoLists[i].get(0).getTransfer());
            // 乗り換え駅リストを表示用に整形
            String stations = ConvertUtil.concatTranStations(resultInfoLists[i].get(0).getTransferList());
            // 1～10までの各経路情報のレイアウトを取得
            ConstraintLayout layout = view.findViewById(getResources().getIdentifier(
                    "route_info" + String.valueOf(i+1), "id", getActivity().getPackageName()));
            // 該当のレイアウトを表示する
            layout.setVisibility(View.VISIBLE);
            // route_info1～10に表示用の経路情報を格納していく
            TextView routeTitle = layout.findViewById(R.id.route_title);
            routeTitle.setText((resultInfoLists[i].get(0).getStationFrom() + "　～　" + resultInfoLists[i].get(0).getStationTo()));
            TextView time = layout.findViewById(R.id.time);
            time.setText(("所要時間:" + timeStr));
            TextView cost = layout.findViewById(R.id.cost);
            cost.setText(("料金:" + costStr));
            TextView transfer = layout.findViewById(R.id.transfer);
            transfer.setText(("乗り換え:" + transferStr));
            TextView transferStations = layout.findViewById(R.id.transfer_stations);
            transferStations.setText(stations);
            // 詳細情報をJoudan検索しに行けるボタンの設定
            Button detailButton = layout.findViewById(R.id.detail_button);
            final String searchURL = resultInfoLists[i].get(0).getSearchURL();
            detailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 検索URLからJorudanへ遷移
                    Intent intent = IntentUtil.prepareForJorudanDetailInfo(searchURL);
                    startActivity(intent);
                }
            });
        }
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
