package com.example.srcn4.autocompletetest2.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.models.StationTransferVO;
import com.example.srcn4.autocompletetest2.utils.ConvertUtil;

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
        // ソート結果に応じて、各出発駅から1位のものだけ表示する(.get(0)がリストの先頭)
        for (int i = 0; i < resultInfoLists.length; i++) {
            // 各数値を表示用の形式に戻す
            String timeStr = ConvertUtil.minutesToTime(resultInfoLists[i].get(0).getTime());
            String costStr = ConvertUtil.addYenAndComma(resultInfoLists[i].get(0).getCost());
            String transferStr = ConvertUtil.addKai(resultInfoLists[i].get(0).getTransfer());
            // 乗り換え駅リストを表示用に整形
            String stations = ConvertUtil.concatTranStations(resultInfoLists[i].get(0).getTransferList());
            // route_info1～5に表示用の経路情報を格納していく
            TextView routeTitle =
                    view.findViewById(getResources().getIdentifier(
                    "route_info" + String.valueOf(i+1), "id", getActivity().getPackageName()))
                    .findViewById(R.id.route_title);
            routeTitle.setText((resultInfoLists[i].get(0).getName() + "　～　" + resultInfoLists[i].get(0).getDestName()));
            TextView time = view.findViewById(getResources().getIdentifier(
                    "route_info" + String.valueOf(i+1), "id", getActivity().getPackageName()))
                    .findViewById(R.id.time);
            time.setText(("所要時間:" + timeStr));
            TextView cost = view.findViewById(getResources().getIdentifier(
                    "route_info" + String.valueOf(i+1), "id", getActivity().getPackageName()))
                    .findViewById(R.id.cost);
            cost.setText(("料金:" + costStr));
            TextView transfer = view.findViewById(getResources().getIdentifier(
                    "route_info" + String.valueOf(i+1), "id", getActivity().getPackageName()))
                    .findViewById(R.id.transfer);
            transfer.setText(("乗り換え:" + transferStr));
            TextView transferStations = view.findViewById(getResources().getIdentifier(
                    "route_info" + String.valueOf(i+1), "id", getActivity().getPackageName()))
                    .findViewById(R.id.transfer_stations);
            transferStations.setText(stations);
        }
        // 余分なレイアウトを削除する処理
        for (int i = 0; i < 10; i++) {
            // 1～10までの各経路情報のレイアウトを取得
            ConstraintLayout layout = view.findViewById(getResources().getIdentifier(
                            "route_info" + String.valueOf(i+1), "id", getActivity().getPackageName()));
            // レイアウト内のビューを取得
            TextView routeTitle = layout.findViewById(R.id.route_title);
            // ビューが空だったら、該当するレイアウトは削除
            if (routeTitle.getText().toString().isEmpty()) {
                ViewGroup p = (ViewGroup)layout.getParent();
                p.removeView(layout);
            }
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
