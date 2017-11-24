package com.example.srcn4.autocompletetest2.utils;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * 文字列変換共通クラス
 */
public class ConvertUtil {

    /**
     * ○時間○○分を分のみの数値にするメソッド
     *
     * @param _timeStr ○時間○○分の書式の時間文字列
     * @return int 分に合算した数値
     */
    public static int timeToMinutes(String _timeStr) {
        // 返却用
        int minutes = 0;
        // 引数の受け取り
        String timeStr = _timeStr;
        // "時間"がない場合
        if (!timeStr.contains("時間")) {
            // "分"より手前を切り出す
            timeStr = timeStr.substring(0, timeStr.indexOf("分"));
            // 数値にして格納
            minutes = Integer.parseInt(timeStr);
            // "時間"がある場合
        } else {
            // "時間"より手前を切り出す
            String hours = timeStr.substring(0, timeStr.indexOf("時間"));
            // "時間"のあと"分"より手前を切り出す
            timeStr = timeStr.substring(timeStr.indexOf("時間") + 2, timeStr.indexOf("分"));
            // 時間と分を足し合わせる
            minutes = Integer.parseInt(hours) * 60 + Integer.parseInt(timeStr);
        }
        // 結果値を返却
        return minutes;
    }

    /**
     * 分の数値を○時間○○分の書式に戻すメソッド
     *
     * @param value 分に合算した数値
     * @return String ○時間○○分の書式の文字列
     */
    public static String minutesToTime(int value) {
        // 60で割った商が時間になる
        int hours = value / 60;
        // 60で割った余りが分になる
        int minutes = value % 60;
        // 数値を文字列にして単位をつけて返却
        if (hours == 0) {
            // 1時間未満なら分だけで返却
            return (String.valueOf(minutes) + "分");
        } else {
            // 1時間以上なら時間付きで返却
            return (String.valueOf(hours) + "時間" + String.valueOf(minutes) + "分");
        }
    }

    /**
     * 円とコンマを削除して数値に変換するメソッド
     *
     * @param str "1,000円"の書式の文字列
     * @return int 円とコンマを削除した数値
     */
    public static int removeYenAndComma(String str) {
        // 円とコンマを削除
        String newStr = str.substring(0, str.indexOf("円"))
                .replaceAll(",", "");
        // 文字列を数値にして返却
        return Integer.parseInt(newStr);
    }

    /**
     * 数値に円とコンマを追加するメソッド
     *
     * @param value 円とコンマを削除した数値
     * @return "1,000円"の書式の文字列
     */
    public static String addYenAndComma(int value) {
        // NumberFormatインスタンス(コンマが簡単に足せる)を生成
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 円をつけて返却
        return (nf.format(value) + "円");
    }

    /**
     * "乗換 "と"回"を削除して数値に変換するメソッド
     *
     * @param str "乗換 2回"の書式の文字列
     * @return int "乗換 "と"回"を削除した数値
     */
    public static int removeNorikaeAndKai(String str) {
        // "乗換 "と"回"を削除
        String newStr = str.substring(3, str.indexOf("回"));
        // 文字列を数値にして返却
        return Integer.parseInt(newStr);
    }

    /**
     * 数値に"回"をつけるメソッド
     *
     * @param value 数値
     * @return String "回"をつけた文字列
     */
    public static String addKai(int value) {
        // 単位を足して変換して返却
        return (String.valueOf(value) + "回");
    }

    /**
     * 乗り換え駅リストを表示用の文字列に整形するメソッド
     *
     * @param transferList 乗り換え駅リスト
     * @return String 表示用文字列
     */
    public static String concatTranStations(ArrayList<String> transferList) {
        // 駅が何もない(同一駅で移動がない等)場合、"経路なし"を返却
        if (transferList.isEmpty()) {
            return "経路なし";
        } else {
            // 駅リストを文字列として連結させていく
            StringBuilder stations = new StringBuilder();
            for (String station : transferList) {
                stations.append(station);
                stations.append(" - ");
            }
            // 最後の余分な" - "の削除
            stations.delete(stations.length() - 3, stations.length());
            // 表示用文字列の返却(StringBuilderからStringに変換してる)
            return stations.toString();
        }
    }
}
