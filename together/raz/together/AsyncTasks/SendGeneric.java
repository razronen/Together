package com.together.raz.together.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Transmition.POST;

import java.util.HashMap;

/**
 * Created by Raz on 1/28/2017.
 */
public class SendGeneric extends AsyncTask<Void, String, String> {
    private AsyncResponse delegate;
    private String key;
    private Cookied cookied;
    private String setCookie;
    private String getCookie;
    private String url;
    private HashMap<String, String> hash = null;
    private Activity activity;
    /**
     * Constructor
     * @param delegate - whom to notify.
     * @param key - how to notify.
     * @param cookied - used for POST
     */
    public SendGeneric(AsyncResponse delegate, String key, Cookied cookied, String getCookie, String setCookie,
                       String url, HashMap<String, String> hash, Activity activity){
        this.cookied = cookied;
        this.delegate = delegate;
        this.key = key;
        this.getCookie = getCookie;
        this.setCookie = setCookie;
        this.url = url;
        this.hash = hash;
        this.activity = activity;
    }
    /**
     * The async task in background -
     * @return output from server
     */
    @Override
    protected String doInBackground(Void... params) {
        POST post = new POST(activity);
        String output = post.Response(url, hash, cookied, getCookie, setCookie);
        return output;
    }

    /**
     * Notify delegate async task is done.
     * @param result from server.
     */
    protected void onPostExecute(String result) {
        delegate.OnFinished(key + result);
    }
}
