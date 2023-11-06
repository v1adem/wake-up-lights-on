package com.v1adem.wakeup;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageManager {
    private final Context context;
    private final SharedPreferences sharedPreferences;

    public LanguageManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("LANG", Context.MODE_PRIVATE);
    }

    public void updateResource(String langCode){
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        setLang(langCode);
    }

    public String getLang(){
        return sharedPreferences.getString("lang", "en-US");
    }

    public void setLang(String langCode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lang", langCode);
        editor.commit();
    }

}
