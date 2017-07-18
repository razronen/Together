package com.together.raz.together.AsyncTasks;

import android.os.AsyncTask;

import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Transmition.GET;

/**
 * Created by Raz on 2/5/2017.
 */
public class DeleteComment extends AsyncTask<String, String, String> {

    private AsyncResponse delegate;
    private boolean append;
    private String key;
    private Cookied cookied;
    private String setCookie;
    private String getCookie;
    /**
     * Constructor
     * @param delegate - whom to notify when done.
     * @param key - string to notify.
     * @param cookied - used for GET.
     */
    public DeleteComment(AsyncResponse delegate, String key, Cookied cookied
            , String getCookie, String setCookie){
        this.cookied = cookied;
        this.delegate = delegate;
        this.append = append;
        this.key = key;
        this.getCookie = getCookie;
        this.setCookie = setCookie;
    }

    /**
     * The async task in background -
     * @param urls
     * @return output from server
     */
    @Override
    protected String doInBackground(String... urls) {
        GET post = new GET();
        String output = null;
        for(String url: urls){
            output =  post.getOutputFromURL(url, cookied, getCookie, setCookie);
        }
        return output;
    }
    /**
     * Notifying delegate async done.
     * @param result output from server.
     */
    @Override
    protected void onPostExecute(String result) {
        delegate.OnFinished(key + result);
    }
}
