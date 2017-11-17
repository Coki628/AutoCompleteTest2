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
    private String prefCd;
    private String lat;
    private String lng;
    private String gnaviId;
    private String jorudanName;

    // 全項目設定のコンストラクタ
    public StationVO (String name, String kana, String prefCd, String lat, String lng, String gnaviId, String jorudanName) {

        this.name = name;
        this.kana = kana;
        this.prefCd = prefCd;
        this.lat = lat;
        this.lng = lng;
        this.gnaviId = gnaviId;
        this.jorudanName = jorudanName;
    }

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

    public String getPrefCd() {
        return prefCd;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getGnaviId() {
        return gnaviId;
    }

    public String getJorudanName() {
        return jorudanName;
    }

    @Override
    public String toString() {
        return "StationVO{" +
                "name='" + name + '\'' +
                ", kana='" + kana + '\'' +
                ", prefCd='" + prefCd + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", gnaviId='" + gnaviId + '\'' +
                ", jorudanName='" + jorudanName + '\'' +
                '}';
    }
}
