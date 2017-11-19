package com.example.srcn4.autocompletetest2.models;

import java.io.Serializable;

/**
 * 駅情報VO
 *
 * 駅情報を格納するのに用いる。
 */
public class StationVO implements Serializable {

    private String name;
    private String kana;

    // 駅名だけ使う時のコンストラクタ
    public StationVO (String name) {

        this.name = name;
    }

    // 駅名と仮名だけ使う時のコンストラクタ
    public StationVO (String name, String kana) {

        this.name = name;
        this.kana = kana;
    }

    public String getName() {
        return name;
    }

    public String getKana() {
        return kana;
    }

    @Override
    public String toString() {
        return "StationVO{" +
                "name='" + name + '\'' +
                ", kana='" + kana + '\'' +
                '}';
    }
}
