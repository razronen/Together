package com.together.raz.together.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Packs.QuotePack;
import com.together.raz.together.Transmition.POST;

/**
 * Created by Raz on 1/28/2017.
 */
public class EditQuote extends AsyncTask<QuotePack, String, String> {
    private AsyncResponse delegate;
    private String key;
    private Cookied cookied;
    private String setCookie;
    private String getCookie;
    private Context context;
    /**
     * Constructor
     * @param delegate - whom to notify.
     * @param key - how to notify.
     * @param cookied - used for POST
     */
    public EditQuote(AsyncResponse delegate, String key, Cookied cookied, String getCookie, String setCookie,
                     Context context){
        this.cookied = cookied;
        this.delegate = delegate;
        this.key = key;
        this.getCookie = getCookie;
        this.setCookie = setCookie;
        this.context = context;
    }
    /**
     * The async task in background -
     * @param packs
     * @return output from server
     */
    @Override
    protected String doInBackground(QuotePack... packs) {
        POST post = new POST(context);
        String output = null;
        for(QuotePack pack: packs){
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
