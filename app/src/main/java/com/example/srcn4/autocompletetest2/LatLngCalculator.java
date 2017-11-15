package com.example.srcn4.autocompletetest2;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 緯度経度計算クラス
 */
public class LatLngCalculator {

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
     * 座標から一番近くにある駅を取得
     *
     * @param latLng 基準となる座標
     * @param context DB接続に使うコンテキスト
     * @return StationVO 基準座標から一番近い駅の駅情報VO
     */
    public static StationVO calcNearestStation(LatLng latLng, Context context) {

        // DB接続のためDAOを生成
        StationDAO dao = new StationDAO(context);
        // 全ての駅情報を取得する
        ArrayList<StationVO> allStationList = dao.selectAllStations();
        // 距離情報格納用リスト
        ArrayList<StationDistanceVO> stationDistanceList = new ArrayList<>();
        // 引数で受け取った座標と各駅の座標の間の距離を順番に割り出していく
        for (StationVO stationVO : allStationList) {
            // 2点間の緯度と経度の距離を出す(どちらが大きくてもいいようにA-Bを絶対値にする)
            double distanceLat = Math.abs(Double.parseDouble(stationVO.getLat()) - latLng.latitude);
            double distanceLng = Math.abs(Double.parseDouble(stationVO.getLng()) - latLng.longitude);
            // 緯度と経度をタテヨコの線として、三平方の定理で直線距離(ナナメの線)を出す
            double distance = Math.sqrt(Math.pow(distanceLat, 2) + Math.pow(distanceLng, 2));
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
        // 一番先頭にあるVOの駅情報を取得して返却
        return dao.selectStationByName(stationDistanceList.get(0).getName());
    }

    /**
     * 座標から近くにある駅のリストを取得
     *
     * @param latLng 基準となる座標
     * @param context DB接続に使うコンテキスト
     * @return ArrayList<StationDistanceVO> 駅間距離情報VOを基準座標から近い順に並べたリスト
     */
    public static ArrayList<StationDistanceVO> calcNearStationsList(LatLng latLng, Context context) {

        // DB接続のためDAOを生成
        StationDAO dao = new StationDAO(context);
        // 全ての駅情報を取得する
        ArrayList<StationVO> allStationList = dao.selectAllStations();
        // 距離情報格納用リスト
        ArrayList<StationDistanceVO> stationDistanceList = new ArrayList<>();
        // 引数で受け取った座標と各駅の座標の間の距離を順番に割り出していく
        for (StationVO stationVO : allStationList) {
            // 2点間の緯度と経度の距離を出す(どちらが大きくてもいいようにA-Bを絶対値にする)
            double distanceLat = Math.abs(Double.parseDouble(stationVO.getLat()) - latLng.latitude);
            double distanceLng = Math.abs(Double.parseDouble(stationVO.getLng()) - latLng.longitude);
            // 緯度と経度をタテヨコの線として、三平方の定理で直線距離(ナナメの線)を出す
            double distance = Math.sqrt(Math.pow(distanceLat, 2) + Math.pow(distanceLng, 2));
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
}
