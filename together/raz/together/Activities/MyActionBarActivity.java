package com.together.raz.together.Activities;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.together.raz.together.AsyncTasks.GetEntity;
import com.together.raz.together.AsyncTasks.GetEntityCheck;
import com.together.raz.together.Entities.UserInfo;
import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.R;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.gson.Gson;

import java.util.Calendar;

import pub.devrel.easypermissions.EasyPermissions;

public class MyActionBarActivity extends AppCompatActivity implements AsyncResponse, Cookied {

    private static final String TAG = "MyActionBarActivity";
    private SharedPreferences settings = null;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private SharedPreferences sharedPreferences;
    private GoogleAccountCredential mCredential = null;

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }

    private String conversation = null;
    private Boolean answer = false;

    private TextView title = null;
    private TextView chatPeerName = null;
    private TextView chatPeerStatus = null;

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public void setTitle(String _title){
        if(title!=null){
            title.setText(_title);

        }
    }

    public void setChatImmediateCall(String num, String id, String name){} // let Child and Psycho Override method and there send to Chat and setConversation

    public void setPeerName(String _name){
        if(chatPeerName!=null){
            chatPeerName.setText(_name);
        }
    }

    public void setPeerStatus(String _status){
        if(chatPeerStatus!=null){
            chatPeerStatus.setText(_status);
        }
    }

    public GoogleAccountCredential getmCredential() {
        return mCredential;
    }

    public void setmCredential(GoogleAccountCredential mCredential) {
        this.mCredential = mCredential;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_action_bar);
        SetSharedPrefences();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.color_activity_bar)));
        getSupportActionBar().setElevation(0);
        sharedPreferences = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        setupActionBar();

        //check();
    }

    private void check() {
            String key = getResources().getString(R.string.key_answered_help);
            GetEntityCheck getEntity = new GetEntityCheck(this,key,this,
                    getResources().getString(R.string.getcookie),
                    getResources().getString(R.string.setCookie), this);
            String getPeerStatus = getResources().getString(R.string.request_answered_help_url);
            getEntity.execute(new String[]{getPeerStatus});
            // Instantiate the custom HttpClient
    }

    private void setupActionBar() {
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.menu_main_user_item, null);

//        title = (TextView) v.findViewById(R.id.main_menu_title);
        chatPeerName = (TextView) v.findViewById(R.id.chat_menu_peer_name);
        chatPeerStatus = (TextView) v.findViewById(R.id.chat_menu_peer_status);

//        if(title!=null){
//            Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/DAVIDNEW.TTF");
//            title.setTypeface(myTypeface);
//
//            title.setText(getResources().getString(R.string.actionbar_title));
//        }

        ab.setCustomView(v);

//        ab.setHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        String key = getResources().getString(R.string.key_set_last_login_time);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String destroyAppUrl = getResources().getString(R.string.request_destroy_app_url);
        getEntity.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new String[]{destroyAppUrl});
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLoginTime(getResources().getString(R.string.logged_in));
    }

    @Override
    protected void onPause() {
        setLoginTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        super.onPause();
    }

    private void setLoginTime(String time){
        SetSharedPrefences();
        UserInfo userInfo = null;
        Gson gson = new Gson();
        if(settings==null) return;
        String json = settings.getString(getResources().getString(R.string.key_user), "");
        if(json.equals("")){
        } else {
            userInfo = gson.fromJson(json, UserInfo.class);
        }
        if(userInfo==null) return;
        String key = getResources().getString(R.string.key_set_last_login_time);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String setLastloginTime = getResources().getString(R.string.request_set_last_login_time_url)
                + userInfo.getId() + getResources().getString(R.string.slash) + time;
        getEntity.execute(new String[]{setLastloginTime});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.reset_account_btn:
                sharedPreferences.edit().remove(getResources().getString(R.string.account_type))
                        .apply();
                SharedPreferences settings = getApplicationContext().getSharedPreferences(
                        getResources().getString(R.string.prefs), 0);
                settings.edit().remove(getResources().getString(R.string.key_user))
                        .apply();
                settings.edit().remove(getResources().getString(R.string.key_user_approve))
                        .apply();
                Intent intent = new Intent(MyActionBarActivity.this,LoginActivity.class);
                intent.putExtra(getResources().getString(R.string.check_approve), false);
                startActivity(intent);
                return true;
            case R.id.menu_choose_account:
                setGoogleAccount();
                if(mCredential!=null){
                    startActivityForResult(mCredential.newChooseAccountIntent(),
                            REQUEST_ACCOUNT_PICKER);
                    return true;
                } else {
                    return false;
                }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setGoogleAccount() {
        if (EasyPermissions.hasPermissions(
                this, new String[]{android.Manifest.permission.GET_ACCOUNTS})) {
            if (mCredential != null) {
                startActivityForResult(mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    new String[]{android.Manifest.permission.GET_ACCOUNTS});
        }
    }

    /**
     //     * Called when an activity launched here (specifically, AccountPicker
     //     * and authorization) exits, giving you the requestCode you started it with,
     //     * the resultCode it returned, and any additional data from it.
     //     * @param requestCode code indicating which activity result is incoming.
     //     * @param resultCode code indicating the result of the incoming
     //     *     activity result.
     //     * @param data Intent (containing result data) returned by incoming
     //     *     activity result.
     //     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
//            case REQUEST_GOOGLE_PLAY_SERVICES:
//                if (resultCode != activity.RESULT_OK) {
//                    mOutputText.setText(
//                            "This app requires Google Play Services. Please install " +
//                                    "Google Play Services on your device and relaunch this app.");
//                } else {
//                    getResultsFromApi();
//                }
//                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(getResources().getString(R.string.key_account_google_name), accountName);
                        editor.apply();
                        Log.d(TAG,"saved: " + accountName);
                    }
                }
                break;
//            case REQUEST_AUTHORIZATION:
//                if (resultCode == activity.RESULT_OK) {
//                    getResultsFromApi();
//                }
//                break;
        }
    }

    public void setMenuLabel(String menuLabel) {
        if(getActionBar()!=null) getActionBar().setTitle(menuLabel);
        if(getSupportActionBar()!=null) getSupportActionBar().setTitle(menuLabel);
    }

    @Override
    public void OnFinished(String result) {

    }

    private void SetSharedPrefences() {
        settings = getSharedPreferences(getResources().getString(R.string.data), MODE_PRIVATE);
        settings =  getApplicationContext().getSharedPreferences(getResources().getString(R.string.prefs), 0);
    }

    @Override
    public void setCookie(String cookie) {
        SharedPreferences.Editor edit = settings.edit();
        edit.putString(getResources().getString(R.string.cookiekey), cookie);
        edit.apply();
    }

    @Override
    public String getCookie() {
        return settings.getString(getResources().getString(R.string.cookiekey),
                getResources().getString(R.string.empty));
    }
}
