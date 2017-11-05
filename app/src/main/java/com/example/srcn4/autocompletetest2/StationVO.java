package com.example.srcn4.autocompletetest2;

import java.io.Serializable;

public class StationVO implements Serializable {

    private String name;
    private String kana;
    private String prefCd;
    private String gnaviId;
    private String lat;
    private String lng;

    // 全項目設定のコンストラクタ
    public StationVO (String name, String kana, String prefCd, String gnaviId, String lat, String lng) {

        this.name = name;
        this.kana = kana;
        this.prefCd = prefCd;
        this.gnaviId = gnaviId;
        this.lat = lat;
        this.lng = lng;
    }

    // 駅名と仮名だけ使う時のコンストラクタ
    public StationVO (String name, String kana) {

        this.name = name;
        this.kana = kana;
        this.prefCd = null;
        this.gnaviId = null;
        this.lat = null;
        this.lng = null;
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

    public String getGnaviId() {
        return gnaviId;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    @Override
    public String toString() {
        return "StationVO{" +
                "name='" + name + '\'' +
                ", kana='" + kana + '\'' +
                ", prefCd='" + prefCd + '\'' +
                ", gnaviId='" + gnaviId + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }
}
