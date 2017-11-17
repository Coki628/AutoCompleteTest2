package com.example.srcn4.autocompletetest2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.models.StationDistanceVO;

import java.util.ArrayList;

/**
 * リストビュー管理用アダプター
 *
 * リストビューの1行の内容を定義する。
 */
public class MyAdapterForListView extends BaseAdapter {

    private Context context;
    private LayoutInflater myInflater;
    private ArrayList<StationDistanceVO> list;

    // コンストラクタでnew時に呼ばれる処理を書いとく
    public MyAdapterForListView(Context context, ArrayList<StationDistanceVO> list) {
        this.context = context;
        this.myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;   // 引数で受け取ったlistを入れる
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // リストビュー1行ごとに呼ばれる
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // ビューがあれば再利用する処理
        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.adapter_list_view, parent, false);
        }
        // 画面の各要素を取得
        TextView listViewName = convertView.findViewById(R.id.list_view_station);
        TextView listViewKana = convertView.findViewById(R.id.list_view_kana);
        Button shareButton = convertView.findViewById(R.id.button12);
        Button mapInfoButton = convertView.findViewById(R.id.button13);
        // 近い順になっているリストから順番に駅名をセットしていく
        listViewName.setText(list.get(position).getName());
        listViewKana.setText(list.get(position).getKana());
        // 共有ボタン用のクリック準備
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view, position, R.id.button12);
            }
        });
        // 周辺情報用のクリック準備
        mapInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view, position, R.id.button13);
            }
        });
        return convertView;
    }
}
