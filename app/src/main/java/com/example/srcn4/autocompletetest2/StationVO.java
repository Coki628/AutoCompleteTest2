package com.example.srcn4.autocompletetest2;

import java.io.Serializable;

public class StationVO implements Serializable {

    private String name;
    private String kana;
    private String prefCd;
    private String gnaviId;

    public StationVO (String name, String kana, String prefCd, String gnaviId) {

        this.name = name;
        this.kana = kana;
        this.prefCd = prefCd;
        this.gnaviId = gnaviId;
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
}
