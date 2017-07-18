package com.together.raz.together.Tools;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.together.raz.together.AsyncTasks.SendUser;
import com.together.raz.together.Entities.UserInfo;
import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Packs.UserPack;
import com.together.raz.together.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

/**
 * Created by Raz on 3/25/2017.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService implements AsyncResponse, Cookied{
    private static final String TAG = "MyFirebaseInsIDService";

    @Override
    public void onTokenRefresh() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        setUserInApp(refreshToken);
        notifyServer();
        Log.d(TAG, "New Token: " + refreshToken);
    }

    private void notifyServer() {
        String[] keys = {
                getResources().getString(R.string.key_user_id),
                getResources().getString(R.string.key_user_entity),
                getResources().getString(R.string.key_user_name),
                getResources().getString(R.string.key_user_phone),
                getResources().getString(R.string.key_user_icon),
                getResources().getString(R.string.key_user_deviceId),
                getResources().getString(R.string.key_user_email)
        };
        SendUser sendUser = new SendUser(this, getResources().getString(R.string.key_save_user), this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie),getApplicationContext());

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getResources().getString(R.string.key_user), "");
        if(json.equals("")) return;
        UserInfo newUser = gson.fromJson(json, UserInfo.class);


        String sendUserUrl = getResources().getString(R.string.request_save__user_url).toString() +
                newUser.getId();
        sendUser.execute(new UserPack[]{new UserPack(sendUserUrl, newUser, keys)});
    }

    private void setUserInApp(String refreshToken) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        //get userInfo
        String json = mPrefs.getString(getResources().getString(R.string.key_user),"");
        if(json.equals("")) return;
        UserInfo newUser = gson.fromJson(json, UserInfo.class);

        //updating
        newUser.setUserToken(refreshToken);

        //saving user.
        String updatedjson = gson.toJson(newUser);
        prefsEditor.putString(getResources().getString(R.string.key_user),updatedjson);
        prefsEditor.commit();
    }

    @Override
    public void OnFinished(String result) {
        Log.d(TAG, "OnFinish method");
    }

    @Override
    public void setCookie(String cookie) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.prefs), 0);
        SharedPreferences.Editor edit = settings.edit();
        edit.putString(getResources().getString(R.string.cookiekey), cookie);
        edit.apply();
        Log.d(TAG, "setCookie method");
    }

    @Override
    public String getCookie() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.prefs), 0);
        return settings.getString(getResources().getString(R.string.cookiekey),
                getResources().getString(R.string.empty));
    }
}
