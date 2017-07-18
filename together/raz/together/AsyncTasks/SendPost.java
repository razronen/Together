package com.together.raz.together.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Packs.PostPack;
import com.together.raz.together.Transmition.POST;

/**
 * Created by Raz on 1/28/2017.
 */
public class SendPost extends AsyncTask<PostPack, String, String> {
    private AsyncResponse delegate;
    private String key;
    private Cookied cookied;
    private String setCookie;
    private String getCookie;
    private Activity activity;
    /**
     * Constructor
     * @param delegate - whom to notify.
     * @param key - how to notify.
     * @param cookied - used for POST
     */
    public SendPost(AsyncResponse delegate, String key, Cookied cookied, String getCookie, String setCookie,
                    Activity activity){
        this.cookied = cookied;
        this.delegate = delegate;
        this.key = key;
        this.getCookie = getCookie;
        this.setCookie = setCookie;
        this.activity = activity;
    }
    /**
     * The async task in background -
     * @param packs
     * @return output from server
     */
    @Override
    protected String doInBackground(PostPack... packs) {
        POST post = new POST(activity);
        String output = null;
        for(PostPack pack: packs){
            output =  post.Response(pack.getUrl(), pack.Hash(), cookied, getCookie, setCookie);
        }
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
