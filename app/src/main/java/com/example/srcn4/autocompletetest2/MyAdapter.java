package com.example.srcn4.autocompletetest2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 自作アダプター
 *
 * カスタムしたアダプターで予測変換の1行の内容を定義する。
 */
public class MyAdapter extends BaseAdapter implements Filterable {

    private LayoutInflater myInflater;
    private ArrayList<StationVO> resultVOList = new ArrayList<>();
    private SQLiteDatabase db;

    // コンストラクタ
    MyAdapter(Context context) {
        this.myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //DBの準備
        MyOpenHelper helper = new MyOpenHelper(context);
        db = helper.getWritableDatabase();
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
            convertView = myInflater.inflate(R.layout.list_item, parent, false);
        }
        // 1行に表示するビューを設定
        TextView listViewKana = convertView.findViewById(R.id.list_item_kana);
        TextView listViewName = convertView.findViewById(R.id.list_item_name);
        listViewKana.setText(resultVOList.get(position).getKana());
        listViewName.setText(resultVOList.get(position).getName());

        return convertView;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            //ここでフィルタリングした値を選択したときに返す値を実装
            @Override
            public String convertResultToString(Object resultValue) {
                // 選択された駅オブジェクトをキャストして取得
                StationVO vo = (StationVO) resultValue;
                // 駅名を返す(表示する)
                return vo.getName();
            }

            // ここで独自のフィルタリングルールを実装
            @Override
            protected FilterResults performFiltering(CharSequence str) {
                // 新しい値を格納するリスト
                ArrayList<StationVO> newValues = new ArrayList<>();

                if (str == null) {
                    // 文字列がnullの時は何もせず終了
                    return new FilterResults();
                }
                // DBから全駅情報を取得
                Cursor cursor = db.rawQuery("select name, kana from station", null);
                // 取得した数だけ繰り返す
                while (cursor.moveToNext()) {
                    // カーソルから駅名と仮名を取得
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String kana = cursor.getString(cursor.getColumnIndex("kana"));

                    // 前方一致で駅名かカナに当てはまれば候補リストに入れる
                    if (name.startsWith(str.toString())
                            || kana.startsWith(str.toString())) {
                        newValues.add(new StationVO(name, kana));
                    }
                }
                cursor.close();

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