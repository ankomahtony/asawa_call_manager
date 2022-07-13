package com.codein.asawacallmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.codein.asawacallmanager.model.MessageData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PrefConfig {

    private static final String LIST_KEY = "list_key100";
    private static final String SHARED_PREFS = "sharedPrefs";

    public static void writeListInPref(Context context, List<MessageData> list) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);

        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFS,
                AppCompatActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(LIST_KEY, jsonString);
        editor.apply();
    }

    public static ArrayList<MessageData> readListFromPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFS, AppCompatActivity.MODE_PRIVATE);
        String jsonString = pref.getString(LIST_KEY, "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<MessageData>>() {}.getType();
        return gson.fromJson(jsonString, type);
    }

}
