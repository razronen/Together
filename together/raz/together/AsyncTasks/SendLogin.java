package com.together.raz.together.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Packs.LoginPack;
import com.together.raz.together.Transmition.POST;

/**
 * Created by Raz on 2/1/2017.
 */
public class SendLogin extends AsyncTask<LoginPack, String, String> {
    private AsyncResponse delegate;
    private String key;
    private Cookied cookied;
    private String setCookie;
    private String getCookie;
    private Activity activity;

    public SendLogin(AsyncResponse delegate, String key, Cookied cookied, String getCookie, String setCookie,
                     Activity activity){
        this.cookied = cookied;
        this.delegate = delegate;
        this.key = key;
        this.getCookie = getCookie;
        this.setCookie = setCookie;
        this.activity = activity;
    }
    @Override
    protected String doInBackground(LoginPack... packs) {
        POST post = new POST(activity);
        String output = null;
        for(LoginPack pack: packs){
            output =  post.Response(pack.getUrl(), pack.Hash(), cookied, getCookie, setCookie);
        }
        return output;
    }
    protected void onPostExecute(String result) {
        delegate.OnFinished(key + result);
    }
}
