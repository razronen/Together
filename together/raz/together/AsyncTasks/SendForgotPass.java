package com.together.raz.together.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Packs.ForgotPassPack;
import com.together.raz.together.Transmition.POST;

/**
 * Created by Raz on 2/1/2017.
 */
public class SendForgotPass extends AsyncTask<ForgotPassPack, String, String> {
    private AsyncResponse delegate;
    private String key;
    private Cookied cookied;
    private String setCookie;
    private String getCookie;
    private Activity activity;

    public SendForgotPass(AsyncResponse delegate, String key, Cookied cookied, String getCookie, String setCookie,
                           Activity activity){
        this.cookied = cookied;
        this.delegate = delegate;
        this.key = key;
        this.getCookie = getCookie;
        this.setCookie = setCookie;
        this.activity = activity;
    }
    @Override
    protected String doInBackground(ForgotPassPack... packs) {
        POST post = new POST(activity);
        String output = null;
        for(ForgotPassPack pack: packs){
            output =  post.Response(pack.getUrl(), pack.Hash(), cookied, getCookie, setCookie);
        }
        return output;
    }
    protected void onPostExecute(String result) {
        delegate.OnFinished(key + result);
    }
}
