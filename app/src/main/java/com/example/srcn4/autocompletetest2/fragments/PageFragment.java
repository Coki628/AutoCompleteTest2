package com.example.srcn4.autocompletetest2.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.models.StationTransferVO;

import java.util.ArrayList;

/**
 * ルート画面のタブ管理用フラグメント
 */
public class PageFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    // コンストラクタ
    public PageFragment() {
    }

    // フラグメントを生成する時アクティビティから呼ばれる
    public static PageFragment newInstance(int page, ArrayList<StationTransferVO>[] resultInfoLists) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
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
        int page = getArguments().getInt("page", 0) + 1;
        ArrayList<StationTransferVO>[] resultInfoLists =
                (ArrayList<StationTransferVO>[])getArguments().getSerializable("resultInfoLists");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        // 各タブに表示する内容を記述
        TextView textView = view.findViewById(R.id.textView);
        textView.setText("Page" + page);

        // ソート処理ここに入れる

        for (int i = 0; i < resultInfoLists.length; i++) {
            Log.d("test", resultInfoLists[i].get(0).toString());
            // route_info1～5に経路の情報を格納していく
            TextView route_title =
                    view.findViewById(getResources().getIdentifier(
                    "route_info" + String.valueOf(i+1), "id", getActivity().getPackageName()))
                    .findViewById(R.id.route_title);
            route_title.setText((resultInfoLists[i].get(0).getName() + "　～　" + resultInfoLists[i].get(0).getDestName()));
            TextView time = view.findViewById(getResources().getIdentifier(
                    "route_info" + String.valueOf(i+1), "id", getActivity().getPackageName()))
                    .findViewById(R.id.time);
            time.setText(("所要時間" + resultInfoLists[i].get(0).getTime() + "分"));
            TextView cost = view.findViewById(getResources().getIdentifier(
                    "route_info" + String.valueOf(i+1), "id", getActivity().getPackageName()))
                    .findViewById(R.id.cost);
            cost.setText(("料金" + resultInfoLists[i].get(0).getCost() + "円"));
            TextView transfer = view.findViewById(getResources().getIdentifier(
                    "route_info" + String.valueOf(i+1), "id", getActivity().getPackageName()))
                    .findViewById(R.id.transfer);
            transfer.setText(("乗り換え" + resultInfoLists[i].get(0).getTransfer() + "回"));
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
