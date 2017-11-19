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
import com.example.srcn4.autocompletetest2.models.StationDetailVO;
import com.example.srcn4.autocompletetest2.models.StationTransferVO;
import com.example.srcn4.autocompletetest2.models.StationVO;

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
        // Inflate the layout for this fragment
        int page = getArguments().getInt("page", 0) + 1;
        ArrayList<StationTransferVO>[] resultInfoLists =
                (ArrayList<StationTransferVO>[])getArguments().getSerializable("resultInfoLists");
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        // 各タブに表示する内容を記述
        TextView textView = view.findViewById(R.id.textView);
        textView.setText("Page" + page);
        // とりあえずログで必要な値がちゃんと送られてきてる確認
        Log.d("test", String.valueOf(page));
        for (int i = 0; i < resultInfoLists.length; i++) {
            Log.d("test", resultInfoLists[i].get(0).toString());
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
