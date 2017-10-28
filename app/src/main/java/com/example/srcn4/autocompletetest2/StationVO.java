package com.example.srcn4.autocompletetest2;

import java.io.Serializable;

public class StationVO implements Serializable {

    private String name;
    private String kana;

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
}
