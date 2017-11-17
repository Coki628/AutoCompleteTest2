package com.example.srcn4.autocompletetest2.utils;

import android.content.Context;

import com.example.srcn4.autocompletetest2.storage.StationDAO;
import com.example.srcn4.autocompletetest2.models.StationDistanceVO;
import com.example.srcn4.autocompletetest2.models.StationVO;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 計算共通クラス
 */
public class CalculateUtil {

    /**
     * 中間地点を計算するメソッド
     *
     * @param latList 計算に使う全駅の緯度リスト
     * @param lngList 計算に使う全駅の経度リスト
     * @return LatLng 中間地点の座標オブジェクト
     */
    public static LatLng calcCenterLatLng(ArrayList<Double> latList, ArrayList<Double> lngList) {

        // 計算に必要な値を定義
        double sumLat = 0;
        double sumLng = 0;

        // 緯度の合計を出す
        for (double lat : latList) {
            sumLat += lat;
        }
        // 経度の合計を出す
        for (double lng : lngList) {
            sumLng += lng;
        }
        // 合計から平均を出す
        double aveLat = sumLat / latList.size();
        double aveLng = sumLng / lngList.size();
        // 平均の座標を返却
        return new LatLng(aveLat, aveLng);
    }

    /**
     * 最大距離を計算するメソッド
     *
     * @param latList 計算に使う全駅の緯度リスト
     * @param lngList 計算に使う全駅の経度リスト
     * @return double[] 最大距離を格納した配列 [0]緯度,[1]経度
     */
    public static double[] calcMaxDistance(ArrayList<Double> latList, ArrayList<Double> lngList) {

        // 昇順ソート
        Collections.sort(latList);
        Collections.sort(lngList);

        // 最大 - 最小で最大距離を出す
        double maxDistanceLat = latList.get(latList.size() - 1) - latList.get(0);
        double maxDistanceLng = lngList.get(lngList.size() - 1) - lngList.get(0);
        // 最大距離を返却
        return new double[] {maxDistanceLat, maxDistanceLng};
    }

    /**
     * 座標から近くにある駅のリストを取得
     *
     * @param latLngFrom 基準となる座標
     * @param context DB接続に使うコンテキスト
     * @return ArrayList<StationDistanceVO> 駅間距離情報VOを基準座標から近い順に並べたリスト
     */
    public static ArrayList<StationDistanceVO> calcNearStationsList(LatLng latLngFrom, Context context) {

        // DB接続のためDAOを生成
        StationDAO dao = new StationDAO(context);
        // 全ての駅情報を取得する
        ArrayList<StationVO> allStationList = dao.selectAllStations();
        // 距離情報格納用リスト
        ArrayList<StationDistanceVO> stationDistanceList = new ArrayList<>();
        // 引数で受け取った座標と各駅の座標の間の距離を順番に割り出していく
        for (StationVO stationVO : allStationList) {
            // 距離計算用のLatLngオブジェクトを生成
            LatLng latLngTo = new LatLng(Double.parseDouble(stationVO.getLat()),
                    Double.parseDouble(stationVO.getLng()));
            // GoogleのUtilクラスを使って座標間の距離を計算
            double distance = SphericalUtil.computeDistanceBetween(latLngFrom, latLngTo);
            // 駅名と距離の情報を持ったVOを生成してリストに詰める
            stationDistanceList.add(new StationDistanceVO(stationVO.getName(), stationVO.getKana(), distance));
        }
        // 距離の昇順でリスト内のVOをソートする
        Collections.sort(stationDistanceList, new Comparator<StationDistanceVO>(){
            @Override
            public int compare(StationDistanceVO a, StationDistanceVO b){
                return Double.compare(a.getDistance(), b.getDistance());
            }
        });
        // ソート済の駅情報リストを返却
        return stationDistanceList;
    }

    /**
     * ○時間○○分を分のみの数値にするメソッド
     *
     * @param _timeStr ○時間○○分の書式の時間文字列
     * @return int 分に合算した数値
     */
    public static int convertTimeToMinutes(String _timeStr) {
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
}