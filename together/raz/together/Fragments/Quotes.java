package com.together.raz.together.Fragments;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.together.raz.together.Activities.MyActionBarActivity;
import com.together.raz.together.AsyncTasks.GetEntity;
import com.together.raz.together.AsyncTasks.SendHelpRequest;
import com.together.raz.together.AsyncTasks.SendQuote;
import com.together.raz.together.Entities.HelpRequest;
import com.together.raz.together.Entities.Quote;
import com.together.raz.together.Entities.UserInfo;
import com.together.raz.together.Enums.Account;
import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Interfaces.TabAbleFragment;
import com.together.raz.together.Packs.HelpRequestPack;
import com.together.raz.together.Packs.QuotePack;
import com.together.raz.together.Painters.QuotesPainter;
import com.together.raz.together.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Quotes extends Fragment implements TabAbleFragment, AsyncResponse, Cookied, View.OnClickListener {

    private String TAG= "Quotes";
    private LinearLayout quotesContainer;
    private Button addQuote;
    private LinearLayout quoteCreationContainer;
    private LinearLayout helpContainer;
    private EditText createAuthorTextBox;
    private EditText createContentTextBox;
    private Button submitNewQuoteBtn;
    private FloatingActionButton helpSignBtn;
    private QuotesPainter painter;
    private SharedPreferences settings;
    private UserInfo user;
    private List<Quote> quotes = new ArrayList<>();
    private MyActionBarActivity myActionBarActivity = null;
    private EditText helpTextBox;
    private Button helpSubmitBtn;
    private boolean exitedTimeout = false;
    private Boolean shown = false;

    public Quotes() {
        // Required empty public constructor
    }

    public static Quotes newInstance(MyActionBarActivity actionBarActivity){
        Quotes fragment = new Quotes();
        Bundle bundle = new Bundle();
        bundle.putSerializable(actionBarActivity.getResources().getString(R.string.key_quotes_pass_argument), (Serializable) actionBarActivity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qoutes, container, false);
        SetSharedPrefences();
        InitUI(view);
        InitPainter(inflater);
        GetQuotes();
        clearChatLabels();
        // Inflate the layout for this fragment
        return view;
    }

    private void GetQuotes() {
        String key = getResources().getString(R.string.key_get_quote);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String getQuotesUrl = getResources().getString(R.string.request_get_quotes_url);
        getEntity.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new String[]{getQuotesUrl});
    }

    private void SetSharedPrefences() {
        settings = getActivity().getSharedPreferences(getResources().getString(R.string.data),
                getContext().MODE_PRIVATE);
        settings = getActivity().getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.prefs), 0);
        setCookie(getResources().getString(R.string.setCookie));
        SetUser();
        Log.d(TAG, user.toString());
    }

    private void SetUser() {
        Gson gson = new Gson();
        String json = settings.getString(getResources().getString(R.string.key_user), "");
        if(json.equals("")){
            Log.d(TAG, "No User defined");
        } else {
            user = gson.fromJson(json, UserInfo.class);
        }
    }

    private void InitUI(View view) {
        quotesContainer = (LinearLayout) view.findViewById(R.id.quotes_container);
        addQuote = (Button) view.findViewById(R.id.quote_add_quote_btn);
        quoteCreationContainer = (LinearLayout) view.findViewById(R.id.quote_create_container);;
        createAuthorTextBox = (EditText) view.findViewById(R.id.quote_create_quote_author_textbox);
        createContentTextBox = (EditText) view.findViewById(R.id.quote_create_quote_content_textbox);;
        submitNewQuoteBtn = (Button) view.findViewById(R.id.quote_submit_new_quote);
        helpSignBtn = (FloatingActionButton) view.findViewById(R.id.quotes_help_sign);
        helpContainer = (LinearLayout) view.findViewById(R.id.quote_help_container);
        helpTextBox = (EditText) view.findViewById(R.id.quote_help_textbox);
        helpSubmitBtn = (Button) view.findViewById(R.id.quote_help_submit);

        myActionBarActivity = (MyActionBarActivity) getArguments().
                getSerializable(getResources().getString(R.string.key_quotes_pass_argument));
        addQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quoteCreationContainer.getVisibility() == View.INVISIBLE) {
                    quoteCreationContainer.setVisibility(View.VISIBLE);
                } else {
                    quoteCreationContainer.setVisibility(View.INVISIBLE);
                    createAuthorTextBox.setText(getResources().getString(R.string.empty));
                    createContentTextBox.setText(getResources().getString(R.string.empty));
                }
            }
        });
        if(user.getAccount()!=Account.CHILD) helpSignBtn.setVisibility(View.GONE);
        helpSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpContainer.getVisibility() == View.INVISIBLE) {
                    helpContainer.setVisibility(View.VISIBLE);
                } else {
                    helpContainer.setVisibility(View.INVISIBLE);
                    helpTextBox.setText(getResources().getString(R.string.empty));
                }
            }
        });
        submitNewQuoteBtn.setOnClickListener(this);
        if(user.getAccount()== Account.CHILD){
            addQuote.setVisibility(View.GONE);
        }
        helpSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendHelpRequest();
            }
        });
    }

    private void SendHelpRequest() {
        String[] keys = {
              getResources().getString(R.string.key_help_time),
              getResources().getString(R.string.key_help_child_id),
              getResources().getString(R.string.key_help_child_name),
              getResources().getString(R.string.key_help_message),
        };
        SendHelpRequest sendHelpRequest = new SendHelpRequest(this,
                getResources().getString(R.string.key_send_help), this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie), getActivity());
        HelpRequest helpRequest = new HelpRequest(String.valueOf(Calendar.getInstance().getTimeInMillis()),
                user.getId(),helpTextBox.getText().toString(),user.getName());
        String url = getResources().getString(R.string.request_send_help_request_url);
        sendHelpRequest.execute(new HelpRequestPack[]{new HelpRequestPack(url,helpRequest,keys)});
        helpTextBox.setText(getResources().getString(R.string.empty));
        helpContainer.setVisibility(View.INVISIBLE);
    }

    private void InitPainter(LayoutInflater inflater) {
        painter = new QuotesPainter(getActivity(),this,this,inflater,user);
    }


    @Override
    public void Shown(Boolean first) {
        setHasOptionsMenu(false);
        shown = true;
        if(!first){
            myActionBarActivity.setPeerStatus(getResources().getString(R.string.empty));
            myActionBarActivity.setPeerName(getResources().getString(R.string.empty));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    exitedTimeout = false;
                    Thread.sleep(2000);
                    if(!exitedTimeout){
                        SendReadingQoutes();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void SendReadingQoutes() {
        String key = getResources().getString(R.string.key_reading_quotes);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String readingQuotesUrl = getResources().getString(R.string.request_reading_qoutes_url);
        getEntity.execute(new String[]{readingQuotesUrl});
    }

    private void clearChatLabels(){
        myActionBarActivity.setPeerStatus(getResources().getString(R.string.empty));
        myActionBarActivity.setPeerName(getResources().getString(R.string.empty));
    }

    @Override
    public void Hidden() {
        exitedTimeout = true;
        shown = false;
    }

    @Override
    public Boolean isShown() {
        return shown;
    }

    @Override
    public void OnFinished(String result) {
        Log.d("RECIEVED:", result);
        if(getActivity()==null) return;
        try {
            if (result.startsWith(getResources().getString(R.string.key_get_quote))) {
                result = result.substring(13);
                if (result.equals(getResources().getString(R.string.empty))) return;
                BuildQuotes(result);
                painter.paint(quotesContainer, quotes);
            } else if (result.startsWith(getResources().getString(R.string.key_edit_quote))){
                result = result.substring(14);
                if (result.equals(getResources().getString(R.string.empty))) return;
                EditQuote(result);
                painter.paint(quotesContainer, quotes);
            } else if (result.startsWith(getResources().getString(R.string.key_del_quote))){
                result = result.substring(13);
                if (result.equals(getResources().getString(R.string.empty))) return;
                GetQuotes();
            } else if(result.startsWith(getResources().getString(R.string.key_create_quote))){
                result = result.substring(16);
                quotes.add(DecihperQuote(new JSONObject(result)));
                painter.paint(quotesContainer, quotes);
            }
        } catch (JSONException e){
            Log.d(TAG,"JSONException");
        }
    }

    private void EditQuote(String result) throws JSONException {
        Quote editedQuote = DecihperQuote(new JSONObject(result));
        List<Quote> newQuotes = new ArrayList<>();
        for(Quote q: quotes){
            if(q.getNum().equals(editedQuote.getNum())){
                newQuotes.add(editedQuote);
            } else {
                newQuotes.add(q);
            }
        }
        quotes = newQuotes;
    }

    private void BuildQuotes(String result) throws JSONException {
        quotes.clear();
        JSONArray array = new JSONArray(result);
        for(int i = 0; i < array.length(); i++){
            JSONObject obj = array.getJSONObject(i);
            quotes.add(DecihperQuote(obj));
        }
    }

    private Quote DecihperQuote(JSONObject obj) throws JSONException {
        return new Quote(obj.getString(getResources().getString(R.string.key_quote_num)),
                obj.getString(getResources().getString(R.string.key_quote_content)),
                obj.getString(getResources().getString(R.string.key_quote_creator)));
    }

    @Override
    public void setCookie(String cookie) {
        //set Cookie to Shared references.
        SharedPreferences.Editor edit = settings.edit();
        edit.putString(getResources().getString(R.string.cookiekey), cookie);
        edit.apply();
    }

    @Override
    public String getCookie() {
        return settings.getString(getResources().getString(R.string.cookiekey),
                getResources().getString(R.string.empty));
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        SendQuote editQuote = new SendQuote(this, getResources().getString(R.string.key_create_quote),
                this, getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie), getActivity());
        String[] keys = {
                getResources().getString(R.string.key_quote_num),
                getResources().getString(R.string.key_quote_content),
                getResources().getString(R.string.key_quote_creator),
        };
        String content = createContentTextBox.getText().toString();
        String author = createAuthorTextBox.getText().toString();
        String editQuoteUrl = getResources().getString(R.string.request_create_quote_url);
        editQuote.execute(new QuotePack[]{new QuotePack(editQuoteUrl, new Quote(content, author), keys)});
        quoteCreationContainer.setVisibility(View.INVISIBLE);
        createContentTextBox.setText(getResources().getString(R.string.empty));
        createAuthorTextBox.setText(getResources().getString(R.string.empty));
    }
}
