package com.example.trainer;

import android.content.Context;
import android.content.SharedPreferences;

public class Sharedprefence {

    public static final String PREFERENCES_NAME="rebuild_preference";// 저장되는 파일명

    private static  final String DEFAULT_VALUE_STRING="";//기본값
    private static  final int DEFAULT_VALUE_INT=0;//기본값
    private  static  final boolean DEFAULT_VALUE_BOOLEAN=false;// 블리언 기본값

    private static SharedPreferences getPreference(Context context){// 객체들 처음 세팅시 입력하는값 단축을 위한것
        return context.getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE);

    }


    public static  void setBoolean(Context context, String key, boolean value){
        SharedPreferences prefs = getPreference(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key,value);// 해당 액티비티의 context,저장시 키값, 저장할 값을 입력
        editor.commit();


    }

    public static Boolean getBoolean(Context context,String key){// 불러올때 해당 액티비티의 context, 키값입력
        SharedPreferences prefs = getPreference(context);
        boolean value = prefs.getBoolean(key,DEFAULT_VALUE_BOOLEAN);
        return value;
    }


    public  static  void setString(Context context, String key, String value){// 저장할때 사용
        SharedPreferences prefs = getPreference(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key,value);// 해당 액티비티의 context,저장시 키값, 저장할 값을 입력
        editor.commit();
    }

    public  static  String getString(Context context,String key){// 값 가져올때 사용
        SharedPreferences prefs = getPreference(context);

//        Gson gson = new Gson();
        String value = prefs.getString(key,DEFAULT_VALUE_STRING);
//        Object object = gson.fromJson(json,Object.class);
//        UserData userlist = new Gson().fromJson(object.toString(), UserData.class);

        return value;
    }

    public  static  void setInt(Context context, String key, int value){// 저장할때 사용
        SharedPreferences prefs = getPreference(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(key,value);// 해당 액티비티의 context,저장시 키값, 저장할 값을 입력
        editor.commit();
    }

    public  static  int getInt(Context context,String key){// 값 가져올때 사용
        SharedPreferences prefs = getPreference(context);

        int value = prefs.getInt(key,DEFAULT_VALUE_INT);

        return value;
    }

    public  static  void removeKey(Context context, String key) {// 키값 지울때 사용
        SharedPreferences prefs = getPreference(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(key);
        edit.apply();

    }

    public static void clear(Context context){// 모두다 지울때 사용
        SharedPreferences prefs = getPreference(context);
        SharedPreferences.Editor editor =prefs.edit();

        editor.clear();
        editor.commit();
    }
}
