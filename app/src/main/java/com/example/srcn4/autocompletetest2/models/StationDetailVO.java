package com.example.srcn4.autocompletetest2.models;

/**
 * 駅詳細情報VO
 *
 * 駅詳細情報を格納するのに用いる。
 */
public class StationDetailVO extends StationVO {

    private String prefCd;
    private String lat;
    private String lng;
    private String gnaviId;
    private String jorudanName;
    private String romaji;

    // 全項目設定のコンストラクタ
    public StationDetailVO(String name, String kana, String prefCd, String lat, String lng, String gnaviId, String jorudanName, String romaji) {

        super(name, kana);
        this.prefCd = prefCd;
        this.lat = lat;
        this.lng = lng;
        this.gnaviId = gnaviId;
        this.jorudanName = jorudanName;
        this.romaji = romaji;
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

    public String getRomaji() {
        return romaji;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "StationDetailVO{" +
                "prefCd='" + prefCd + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", gnaviId='" + gnaviId + '\'' +
                ", jorudanName='" + jorudanName + '\'' +
                ", romaji='" + romaji + '\'' +
                '}';
    }
}
