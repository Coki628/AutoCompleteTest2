package com.example.srcn4.autocompletetest2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.srcn4.autocompletetest2.R;
import com.example.srcn4.autocompletetest2.models.StationDetailVO;
import com.example.srcn4.autocompletetest2.storage.StationDAO;
import com.example.srcn4.autocompletetest2.models.StationVO;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 予測変換機能管理アダプター
 *
 * 予測変換の1行の内容を定義する。
 */
public class MyAdapterForAutoComplete extends BaseAdapter implements Filterable {

    private LayoutInflater myInflater;
    private ArrayList<StationVO> resultVOList = new ArrayList<>();
    private Context context;

    // コンストラクタ
    public MyAdapterForAutoComplete(Context context) {

        this.myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getCount() {
        return resultVOList.size();
    }

    @Override
    public Object getItem(int position) {
        return resultVOList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 予測変換1行ごとに呼ばれる
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ビューがあれば再利用する処理
        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.adapter_auto_complete, parent, false);
        }
        // 1行に表示するビューを設定
        TextView listViewKana = convertView.findViewById(R.id.list_item_kana);
        TextView listViewName = convertView.findViewById(R.id.list_item_name);
        // 言語設定が英語の時
        if (Locale.getDefault().getLanguage().equals("en")) {
            // VOをキャストしてローマ字名を設定する
            StationDetailVO vo = (StationDetailVO) resultVOList.get(position);
            listViewName.setText(vo.getRomaji());
        } else {
            listViewKana.setText(resultVOList.get(position).getKana());
            listViewName.setText(resultVOList.get(position).getName());
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            //ここでフィルタリングした値を選択したときに返す値を実装
            @Override
            public String convertResultToString(Object resultValue) {
                // 言語設定が英語の時
                if (Locale.getDefault().getLanguage().equals("en")) {
                    StationDetailVO vo = (StationDetailVO) resultValue;
                    return vo.getRomaji();
                } else {
                    // 選択された駅オブジェクトをキャストして取得
                    StationVO vo = (StationVO) resultValue;
                    // 駅名を返す(表示する)
                    return vo.getName();
                }
            }

            // ここで独自のフィルタリングルールを実装
            @Override
            protected FilterResults performFiltering(CharSequence str) {

                if (str == null) {
                    // 文字列がnullの時は何もせず終了
                    return new FilterResults();
                }
                // DB接続のためDAOを生成
                StationDAO dao = new StationDAO(context);
                ArrayList<StationVO> newValues = null;
                // 言語設定が英語の時
                if (Locale.getDefault().getLanguage().equals("en")) {
                    // ローマ字名を取得する
                    newValues = dao.selectRomajisByStr(str.toString());
                } else {
                    // 入力文字列と前方一致する駅名と仮名を取得する
                    newValues = dao.selectNamesByStr(str.toString());
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = newValues;            // フィルタリング結果オブジェクト
                filterResults.count = newValues.size();      // フィルタリング結果件数
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    // performFilteringから受け取ったnewValuesをVOリストに入れる
                    resultVOList = (ArrayList<StationVO>) results.values;
                    // 再描画させる
                    notifyDataSetChanged();

                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}