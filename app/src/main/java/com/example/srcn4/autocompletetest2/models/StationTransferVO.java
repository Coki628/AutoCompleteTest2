package com.example.srcn4.autocompletetest2.models;

import java.util.ArrayList;

/**
 * 駅乗り換え情報VO
 *
 * 乗り換え情報を保持する。
 */
public class StationTransferVO extends StationVO {

    // 到着駅名
    private String destName;
    // 到着駅仮名
    private String destKana;
    // 所要時間("分"のみの整数で格納)
    private int time;
    // 料金
    private int cost;
    // 乗り換え回数
    private int transfer;
    // 乗り換え途中駅リスト
    private ArrayList<String> transferList;

    // コンストラクタ
    public StationTransferVO(String name, String destName) {

        super(name);
        this.destName = destName;
        this.time = 0;
        this.cost = 0;
        this.transfer = 0;
        this.transferList = new ArrayList<>();
    }

    public StationTransferVO(String name, String kana, String destName, String destKana,
                             int time, int cost, int transfer) {

        super(name, kana);
        this.destName = destName;
        this.destKana = destKana;
        this.time = time;
        this.cost = cost;
        this.transfer = transfer;
        this.transferList = new ArrayList<>();
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public String getDestKana() {
        return destKana;
    }

    public void setDestKana(String destKana) {
        this.destKana = destKana;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getTransfer() {
        return transfer;
    }

    public void setTransfer(int transfer) {
        this.transfer = transfer;
    }

    public ArrayList<String> getTransferList() {
        return transferList;
    }

    public void setTransferList(ArrayList<String> transferList) {
        this.transferList = transferList;
    }

    @Override
    public String toString() {
        return "StationTransferVO{" +
                "name='" + getName() + '\'' +
                "kana='" + getKana() + '\'' +
                ", destName='" + destName + '\'' +
                ", destKana='" + destKana + '\'' +
                ", time=" + time +
                ", cost=" + cost +
                ", transfer=" + transfer +
                ", transferList=" + transferList +
                '}';
    }
}
