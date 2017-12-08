package com.example.srcn4.autocompletetest2.storage;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * プリファレンス管理クラス
 *
 * SharedPreferencesの機能をラップする
 */
public class MyPreferenceManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public MyPreferenceManager(Context context) {

        this.pref = context.getSharedPreferences("pref", MODE_PRIVATE);
    }

    public boolean getSoundFlag() {

        return pref.getBoolean("soundFlag", true);
    }

    public void setSoundFlag(boolean soundFlag) {

        editor = pref.edit();
        editor.putBoolean("soundFlag", soundFlag);
        editor.apply();
    }

    public boolean getAnimeFlag() {

        return pref.getBoolean("animeFlag", true);
    }

    public void setAnimeFlag(boolean animeFlag) {

        editor = pref.edit();
        editor.putBoolean("animeFlag", animeFlag);
        editor.apply();
    }

    public String getSelectLang() {

        return pref.getString("selectLang", "");
    }

    public void setSelectLang(String selectLang) {

        editor = pref.edit();
        editor.putString("selectLang", selectLang);
        editor.apply();
    }
}
