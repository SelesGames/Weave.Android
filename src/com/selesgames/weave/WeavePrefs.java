package com.selesgames.weave;

import android.content.Context;
import android.content.SharedPreferences;

public class WeavePrefs {

    private static final String PREFS = "prefs";

    private static final String KEY_USER_ID = "userId";
    
    private static final String KEY_THEME_ID = "themeId";
    
    private static final String KEY_READING_FONT_SIZE = "readingFontSize";

    private SharedPreferences mSharedPreferences;

    public WeavePrefs(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void setUserId(String userId) {
        mSharedPreferences.edit().putString(KEY_USER_ID, userId).apply();
    }

    public String getUserId() {
        return mSharedPreferences.getString(KEY_USER_ID, null);
    }
    
    public void setThemeId(int id) {
        mSharedPreferences.edit().putInt(KEY_THEME_ID, id).apply();
    }
    
    public int getThemeId() {
        return mSharedPreferences.getInt(KEY_THEME_ID, 1);
    }
    
    public void setReadingFontSize(int size) {
        mSharedPreferences.edit().putInt(KEY_READING_FONT_SIZE, size).apply();
    }
    
    public int getReadingFontSize() {
        return mSharedPreferences.getInt(KEY_READING_FONT_SIZE, 18);
    }

}
