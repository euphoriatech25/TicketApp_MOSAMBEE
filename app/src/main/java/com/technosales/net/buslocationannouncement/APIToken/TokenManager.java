package com.technosales.net.buslocationannouncement.APIToken;
import android.content.SharedPreferences;

import com.technosales.net.buslocationannouncement.pojo.HelperModel;

public class TokenManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static com.technosales.net.buslocationannouncement.APIToken.TokenManager INSTANCE = null;

    private TokenManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    public static synchronized com.technosales.net.buslocationannouncement.APIToken.TokenManager getInstance(SharedPreferences prefs){
        if(INSTANCE == null){
            INSTANCE = new com.technosales.net.buslocationannouncement.APIToken.TokenManager(prefs);
        }
        return INSTANCE;
    }

    public void saveToken( HelperModel.Token token){
        editor.putString("ACCESS_TOKEN", token.getAccessToken()).commit();
        editor.putString("REFRESH_TOKEN", token.getRefreshToken()).commit();
    }


    public void deleteToken(){
        editor.remove("ACCESS_TOKEN").commit();
        editor.remove("REFRESH_TOKEN").commit();
    }

    public  HelperModel.Token getToken(){
        HelperModel.Token token = new  HelperModel.Token();
        token.setAccessToken(prefs.getString("ACCESS_TOKEN", null));
        token.setRefreshToken(prefs.getString("REFRESH_TOKEN", null));
        return token;
    }



}
