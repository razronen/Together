package com.together.raz.together.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.together.raz.together.Activities.MyActionBarActivity;
import com.together.raz.together.AsyncTasks.GetEntity;
import com.together.raz.together.AsyncTasks.SendPostRequest;
import com.together.raz.together.Entities.Statistics.CommentSta;
import com.together.raz.together.Entities.Statistics.ConversationSta;
import com.together.raz.together.Entities.Statistics.PostSta;
import com.together.raz.together.Enums.Account;
import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Interfaces.TabAbleFragment;
import com.together.raz.together.R;
import com.together.raz.together.Tools.Time;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Statistics extends Fragment implements TabAbleFragment, AsyncResponse, Cookied {

    private static final String TAG = "Statistics.Fragment";
    private SharedPreferences settings;
    private MyActionBarActivity myActionBarActivity = null;
    private Button childrenSearchBtn = null;
    private Button psychologistsSearchBtn = null;
    private Button daysSearchBtn = null;
    private LinearLayout childrenSearchContainer = null;
    private LinearLayout psychologistsSearchContainer = null;
    private LinearLayout daysSearchContainer = null;
    private LinearLayout selectionContainer = null;
    //inside containers UI:
    private EditText childrenSearchEditText = null;
    private EditText psychologistSearchEditText = null;
    private FloatingActionButton childrenSearchSubmitBtn = null;
    private FloatingActionButton psychologistsSearchSubmitBtn = null;
    private FloatingActionButton daysSearchSubmitBtn = null;
    private LinearLayout childrenLayout = null;
    private LinearLayout psychologistsLayout = null;
    private LayoutInflater inflator = null;
    private View userLayout = null;
    private CalendarView fromCalendar = null;
    private CalendarView toCalendar = null;
    private ViewSwitcher daysViewSwitcher = null;
    private LinearLayout daysLayout = null;
    private ScrollView daysScrollView= null;
    private long from = Calendar.getInstance().getTimeInMillis();
    private long to = Calendar.getInstance().getTimeInMillis();
    private Boolean shown = false;

    public Statistics() {
        // Required empty public constructor
    }

    public static Statistics newInstance(MyActionBarActivity actionBarActivity){
        Statistics fragment = new Statistics();
        Bundle bundle = new Bundle();
        bundle.putSerializable(actionBarActivity.getResources().getString(R.string.key_statistics_pass_argument), (Serializable) actionBarActivity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        this.inflator = inflater;
        SetSharedPrefences();
        initUI(view);

        return view;
    }

    private void initUI(View view) {
        myActionBarActivity = (MyActionBarActivity) getArguments().
                getSerializable(getResources().getString(R.string.key_statistics_pass_argument));
        childrenSearchBtn = (Button) view.findViewById(R.id.statistics_children_button);
        psychologistsSearchBtn = (Button) view.findViewById(R.id.statistics_psychologists_button);
        daysSearchBtn = (Button) view.findViewById(R.id.statistics_days_button);
        childrenSearchContainer = (LinearLayout) view.findViewById(R.id.statistic_children_search_container);
        psychologistsSearchContainer = (LinearLayout) view.findViewById(R.id.statistic_psychologists_search_container);
        daysSearchContainer = (LinearLayout) view.findViewById(R.id.statistic_days_search_container);
        selectionContainer = (LinearLayout) view.findViewById(R.id.statistic_selection_container);
        initInsideContainersUI(view);

        childrenSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionContainer.setVisibility(View.INVISIBLE);
                childrenSearchContainer.setVisibility(View.VISIBLE);
            }
        });
        psychologistsSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionContainer.setVisibility(View.INVISIBLE);
                psychologistsSearchContainer.setVisibility(View.VISIBLE);
            }
        });
        daysSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionContainer.setVisibility(View.INVISIBLE);
                daysSearchContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initInsideContainersUI(final View view) {
       childrenSearchEditText = (EditText) view.findViewById(R.id.statistic_children_editbox);
       psychologistSearchEditText = (EditText) view.findViewById(R.id.statistic_psychologists_editbox);
       childrenSearchSubmitBtn = (FloatingActionButton) view.findViewById(R.id.statstics_search_children);
       psychologistsSearchSubmitBtn = (FloatingActionButton) view.findViewById(R.id.statstics_search_psychologist);
       daysSearchSubmitBtn = (FloatingActionButton) view.findViewById(R.id.statstics_search_day);
       fromCalendar = (CalendarView) view.findViewById(R.id.sta_from_calendar_view);
       toCalendar = (CalendarView) view.findViewById(R.id.sta_to_calendar_view);
       childrenLayout = (LinearLayout) view.findViewById(R.id.statistics_children_layout);
       psychologistsLayout = (LinearLayout) view.findViewById(R.id.statistics_psychologists_layout);
        daysLayout = (LinearLayout) view.findViewById(R.id.sta_day_layout);
        daysViewSwitcher = (ViewSwitcher) view.findViewById(R.id.sta_day_switcher);
        daysScrollView = (ScrollView) view.findViewById(R.id.sta_days_scroll_view);


        childrenSearchSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!childrenSearchEditText.getText().equals(getResources().getString(R.string.empty))) {
                    getChildren(childrenSearchEditText.getText().toString());
                }
            }
        });
        psychologistsSearchSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!psychologistSearchEditText.getText().equals(getResources().getString(R.string.empty))) {
                    getPsychologists(psychologistSearchEditText.getText().toString());
                }
            }
        });
        daysSearchSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(daysViewSwitcher.getCurrentView()==(LinearLayout)view.findViewById(R.id.sta_days_choose_limits_layout)){
                    getDayStatistics();
                }
                daysViewSwitcher.showNext();
            }
        });
        fromCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                from = (new Date(year - 1900, month, dayOfMonth)).getTime();
            }
        });
        toCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                to = (new Date(year - 1900, month, dayOfMonth)).getTime();

            }
        });
    }

    private void getChildren(String name) {
        String key = getResources().getString(R.string.key_sta_get_children);
        HashMap<String, String> hash = new HashMap<>();
        hash.put(getResources().getString(R.string.statistics_name), name);
        String Url = getResources().getString(R.string.request_get_children_by_name_url);
        new SendPostRequest(this,key, this,
                getResources().getString(R.string.getcookie), getResources().getString(R.string.setCookie),
                Url, hash, getActivity()).execute();
    }

    private void getPsychologists(String name) {
        String key = getResources().getString(R.string.key_sta_get_psychologists);
        HashMap<String, String> hash = new HashMap<>();
        hash.put(getResources().getString(R.string.statistics_name), name);
        String Url = getResources().getString(R.string.request_get_psychologists_by_name_url);
        new SendPostRequest(this,key, this,
                getResources().getString(R.string.getcookie), getResources().getString(R.string.setCookie),
                Url, hash, getActivity()).execute();
    }

    private void SetSharedPrefences() {
        settings = getActivity().getSharedPreferences(getResources().getString(R.string.data),
                getContext().MODE_PRIVATE);
        settings = getActivity().getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.prefs), 0);
    }

    @Override
    public void OnFinished(String result) {
        Log.d("RECIEVED:", result);
        if(getActivity()==null) return;
        try {
            if (result.startsWith(getResources().getString(R.string.key_sta_get_children))) {
                result = result.substring(20);
                if (result.equals(getResources().getString(R.string.empty))) return;
                AddToLayout(childrenLayout, result);
            } else if (result.startsWith(getResources().getString(R.string.key_sta_get_psychologists))) {
                result = result.substring(25);
                if (result.equals(getResources().getString(R.string.empty))) return;
                AddToLayout(psychologistsLayout, result);
            } else if(result.startsWith(getResources().getString(R.string.key_sta_user_general_info))) {
                result = result.substring(25);
                if (result.equals(getResources().getString(R.string.empty))) return;
                setDetailes(result);
            } else if(result.startsWith(getResources().getString(R.string.key_sta_day))) {
                result = result.substring(11);
                if (result.equals(getResources().getString(R.string.empty))) return;
                setDaysDetailes(result);
            }
        } catch (JSONException e){
            Log.d(TAG,"JSONException " + e.toString());
        }
    }

    private void setDaysDetailes(String result) throws JSONException {
        JSONObject json = new JSONObject(result);
        String public_post = (json.has(getResources().getString(R.string._public_post)))?
                json.getString(getResources().getString(R.string._public_post)):null;
        String private_post = (json.has(getResources().getString(R.string._private_post)))?
                json.getString(getResources().getString(R.string._private_post)):null;
        String response  = (json.has(getResources().getString(R.string._response)))?
                json.getString(getResources().getString(R.string._response)):null;
        String chat_started  = (json.has(getResources().getString(R.string._chat_started)))?
                json.getString(getResources().getString(R.string._chat_started)):null;
        String chat_responses  = (json.has(getResources().getString(R.string._chat_responses)))?
                json.getString(getResources().getString(R.string._chat_responses)):null;
        String inserted_to_quotes  = (json.has(getResources().getString(R.string._inserted_to_quote)))?
                json.getString(getResources().getString(R.string._inserted_to_quote)):null;
        String user_sign_up  = (json.has(getResources().getString(R.string._user_sign_up)))?
                json.getString(getResources().getString(R.string._user_sign_up)):null;
        String user_delete  = (json.has(getResources().getString(R.string._user_delete)))?
                json.getString(getResources().getString(R.string._user_delete)):null;
        setDaysInfo(public_post, private_post, response, chat_started, chat_responses, inserted_to_quotes,
                user_sign_up, user_delete);
    }

    private void setDaysInfo(String public_post, String private_post, String response,
                             String chat_started, String chat_responses, String inserted_to_quotes,
                             String user_sign_up, String user_delete) {
        daysLayout.removeAllViews();
        View view = inflator.inflate(R.layout.day_statistics,null);
        if(view==null) return;
        TextView Tpublic_post = (TextView) view.findViewById(R.id.sta_public_post);
        TextView Tprivate_post = (TextView) view.findViewById(R.id.sta_private_post);
        TextView Tresponse = (TextView) view.findViewById(R.id.sta_response);
        TextView Tchat_started = (TextView) view.findViewById(R.id.sta_chat_started);
        TextView Tchat_responses = (TextView) view.findViewById(R.id.sta_chat_responses);
        TextView Tinserted_to_quotes = (TextView) view.findViewById(R.id.sta_inserted_to_quotes);
        TextView Tuser_sign_up = (TextView) view.findViewById(R.id.sta_user_sign_up);
        TextView Tuser_delete = (TextView) view.findViewById(R.id.sta_user_delete);

        if(Tpublic_post!=null && public_post!=null)
            Tpublic_post.setText(getResources().getString(R.string.sta_public_post) + public_post);
        if(Tprivate_post!=null && private_post!=null)
            Tprivate_post.setText(getResources().getString(R.string.sta_private_post) + private_post);
        if(Tresponse!=null && response!=null)
            Tresponse.setText(getResources().getString(R.string.sta_response) + response);
        if(Tchat_started!=null && chat_started!=null)
            Tchat_started.setText(getResources().getString(R.string.sta_chat_started) + chat_started);
        if(Tchat_responses!=null && chat_responses!=null)
            Tchat_responses.setText(getResources().getString(R.string.sta_chat_responses) + chat_responses);
        if(Tinserted_to_quotes!=null && inserted_to_quotes!=null)
            Tinserted_to_quotes.setText(getResources().getString(R.string.sta_inserted_to_quotes) + inserted_to_quotes);
        if(Tuser_sign_up!=null && user_sign_up!=null)
            Tuser_sign_up.setText(getResources().getString(R.string.sta_user_sign_up) + user_sign_up);
        if(Tuser_delete!=null && user_delete!=null)
            Tuser_delete.setText(getResources().getString(R.string.sta_user_delete) + "-");

        daysLayout.addView(view);
        daysScrollView.fullScroll(View.FOCUS_UP);
    }

    private void setDetailes(String result) throws JSONException {
        JSONObject json = new JSONObject(result);
        String name = (json.has(getResources().getString(R.string.name_text)))?
                json.getString(getResources().getString(R.string.name_text)):null;
        String email  = (json.has(getResources().getString(R.string.email_key)))?
                json.getString(getResources().getString(R.string.email_key)):null;
        String lastLogin  = (json.has(getResources().getString(R.string.lastlogin_text)))?
                json.getString(getResources().getString(R.string.lastlogin_text)):null;
        String entity  = (json.has(getResources().getString(R.string.entity_text)))?
                json.getString(getResources().getString(R.string.entity_text)):null;
        String public_posts_published  = (json.has(getResources().getString(R.string.public_posts_published)))?
                json.getString(getResources().getString(R.string.public_posts_published)):null;
        String private_posts_published  = (json.has(getResources().getString(R.string.private_posts_published)))?
                json.getString(getResources().getString(R.string.private_posts_published)):null;
        String avg_login_time_per_day  = (json.has(getResources().getString(R.string.avg_login_time_per_day)))?
                json.getString(getResources().getString(R.string.avg_login_time_per_day)):null;
        String post_erased_by_manager  = (json.has(getResources().getString(R.string.post_erased_by_manager)))?
                json.getString(getResources().getString(R.string.post_erased_by_manager)):null;
        String missed_post_by_psycho = (json.has(getResources().getString(R.string.missed_post_by_psycho)))?
                json.getString(getResources().getString(R.string.missed_post_by_psycho)):null;
        JSONArray posts_publised = (json.has(getResources().getString(R.string.posts_publised)))?
                json.getJSONArray(getResources().getString(R.string.posts_publised)):null;
        JSONArray comments_published = (json.has(getResources().getString(R.string.comments_published)))?
                json.getJSONArray(getResources().getString(R.string.comments_published)):null;
        JSONArray immediate_help_messages = (json.has(getResources().getString(R.string.immediate_help_messages)))?
                json.getJSONArray(getResources().getString(R.string.immediate_help_messages)):null;
        JSONArray conversations = (json.has(getResources().getString(R.string.conversations)))?
                json.getJSONArray(getResources().getString(R.string.conversations)):null;
        String help_answered = (json.has(getResources().getString(R.string.immediate_help_messages_answered)))?
                json.getString(getResources().getString(R.string.immediate_help_messages_answered)):null;
        String help_unanswered = (json.has(getResources().getString(R.string.immediate_help_messages_not_answered)))?
                json.getString(getResources().getString(R.string.immediate_help_messages_not_answered)):null;

        setGeneralInfo(name, email, lastLogin, entity, avg_login_time_per_day);
        setPostsInfo(public_posts_published, private_posts_published, post_erased_by_manager, missed_post_by_psycho,
                DechiperPosts(posts_publised));
        setCommenstsInfo(DechipgerComments(comments_published));
        setImmediateHelpInfo(DechiperImmediateHelpMsgs(immediate_help_messages), help_answered, help_unanswered, entity);
        setChatInfo(DecihperConversation(conversations));
    }

    private void setChatInfo(List<ConversationSta> list) {
        TextView chatNum = (TextView) userLayout.findViewById(R.id.sta_user_chat_num);
        LinearLayout chatLayout = (LinearLayout) userLayout.findViewById(R.id.sta_user_char_layut);

        Log.d(TAG, list.toString());
        if(chatNum!=null && list!=null)
            chatNum.setText(getResources().getString(R.string.chat_communication) + String.valueOf(list.size()));
        if(chatLayout!=null && list!=null){
            chatLayout.removeAllViews();
            if(list.size()==0) return;
            for(int i = list.size()-1; i >=0;i--){
                View view = inflator.inflate(R.layout.statistics_chat_rep,null);
                if(view==null) continue;
                TextView peer = (TextView) view.findViewById(R.id.sta_chat_peer);
                TextView msg = (TextView) view.findViewById(R.id.sta_chat_msg);
                if(peer!=null && list.get(i).getPeer()!=null) peer.setText(list.get(i).getPeer());
                if(msg!=null && list.get(i).getMsg()!=null) msg.setText(list.get(i).getMsg());
                chatLayout.addView(view);
            }
        }
    }

    private List<ConversationSta> DecihperConversation(JSONArray conversations) throws JSONException {
        List<ConversationSta> list = new ArrayList<>();
        if(conversations.length()==0) return list;
        for(int i = conversations.length()-1; i>=0; i--){
            JSONObject obj = conversations.getJSONObject(i);
            list.add(new ConversationSta(obj.getString(getResources().getString(R.string.peer_text)),
                    obj.getString(getResources().getString(R.string.msg_text))));
        }
        return list;
    }

    private void setImmediateHelpInfo(List<String> msgs, String help_answered, String help_unanswered, String entity) {
        TextView msgsNum = (TextView) userLayout.findViewById(R.id.sta_usr_help_msgs_num);
        LinearLayout msgsLayout = (LinearLayout) userLayout.findViewById(R.id.sta_user_help_msg_layout);

        if(!entity.equals(Account.CHILD.toString())){
            if(msgsNum!=null){
                msgsNum.setText(getResources().getString(R.string.help_msgs_label)
                        + help_unanswered + getResources().getString(R.string.slash) +
                        help_answered);
            }
            return;
        }
        if(msgsNum!=null && msgs!=null)
            msgsNum.setText(getResources().getString(R.string.calls_for_immediate_help) + String.valueOf(msgs.size()));
        if(msgsLayout!=null && msgs!=null){
            msgsLayout.removeAllViews();
            for(int i = 0; i < msgs.size();i++){
                View view = inflator.inflate(R.layout.statistics_help_req_rep,null);
                if(view==null) continue;
                TextView msg = (TextView) view.findViewById(R.id.sta_help_req_rep);
                if(msg!=null && msgs.get(i)!=null) msg.setText(msgs.get(i));
                msgsLayout.addView(view);
            }
        }
    }

    private List<String> DechiperImmediateHelpMsgs(JSONArray immediate_help_messages) throws JSONException {
        List<String> list = new ArrayList<>();
        for(int i = 0; i < immediate_help_messages.length(); i++){
            JSONObject obj = immediate_help_messages.getJSONObject(i);
            list.add(obj.getString(getResources().getString(R.string.msg_text)));
        }
        return list;

    }

    private void setCommenstsInfo(List<CommentSta> commentStas) {
        TextView commentNum = (TextView) userLayout.findViewById(R.id.sta_user_comment_num);
        LinearLayout commentsLayout = (LinearLayout) userLayout.findViewById(R.id.sta_user_comment_layout);

        if(commentNum!=null && commentStas!=null)
            commentNum.setText(getResources().getString(R.string.comments_to_posts) + String.valueOf(commentStas.size()));
        if(commentsLayout!=null && commentStas!=null){
            commentsLayout.removeAllViews();
            if(commentStas.size()==0) return;;
            for(int i = commentStas.size()-1; i >= 0;i--){
                View view = inflator.inflate(R.layout.statistics_comment_rep,null);
                if(view==null) continue;
                TextView title = (TextView) view.findViewById(R.id.sta_post_rep_title);
                TextView msg = (TextView) view.findViewById(R.id.sta_comment_rep_msg);
                if(title!=null && commentStas.get(i).getTitlePost()!=null) title.setText(commentStas.get(i).getTitlePost());
                if(msg!=null && commentStas.get(i).getMsg()!=null) msg.setText(commentStas.get(i).getMsg());
                commentsLayout.addView(view);
            }
        }
    }

    private List<CommentSta> DechipgerComments(JSONArray comments_published) throws JSONException {
        List<CommentSta> list = new ArrayList<>();
        for(int i = 0; i < comments_published.length(); i++){
            JSONObject obj = comments_published.getJSONObject(i);
            list.add(new CommentSta(obj.getString(getResources().getString(R.string.post_title)),
                    obj.getString(getResources().getString(R.string.comment_msg))));
        }
        return list;
    }

    private void setPostsInfo(String public_posts_published, String private_posts_published, String post_erased_by_manager,
                              String missed_post_by_psycho, List<PostSta> list) {
        TextView generalT = (TextView) userLayout.findViewById(R.id.sta_user_post_reg);
        TextView eraseT = (TextView) userLayout.findViewById(R.id.sta_user_post_erase);
        LinearLayout postsLayout = (LinearLayout) userLayout.findViewById(R.id.sta_posts_layout);

        if(generalT!=null && public_posts_published!=null && private_posts_published!= null){
            generalT.setText(getResources().getString(R.string.posts_statics_label) +
                    public_posts_published + getResources().getString(R.string.slash) +
                    private_posts_published);
        }
        Log.d(TAG,post_erased_by_manager);
        if(eraseT!= null && post_erased_by_manager!=null){
            eraseT.setText(getResources().getString(R.string.post_erase) +
                            post_erased_by_manager);
        }
        if(postsLayout!=null && list!=null){
            postsLayout.removeAllViews();
            for(int i = 0; i < list.size();i++){
                View view = inflator.inflate(R.layout.statistics_post_rep,null);
                if(view==null) continue;
                TextView title = (TextView) view.findViewById(R.id.sta_post_rep_title);
                TextView msg = (TextView) view.findViewById(R.id.sta_post_rep_msg);
                if(title!=null && list.get(i).getTitle()!=null) title.setText(list.get(i).getTitle());
                if(msg!=null && list.get(i).getMsg()!=null) msg.setText(list.get(i).getMsg());
                postsLayout.addView(view);
            }
        }

    }

    private List<PostSta> DechiperPosts(JSONArray posts_publised) throws JSONException {
        List<PostSta> list = new ArrayList<>();
        for(int i = 0; i < posts_publised.length(); i++){
            JSONObject obj = posts_publised.getJSONObject(i);
            list.add(new PostSta(obj.getString(getResources().getString(R.string.post_title)),
                    obj.getString(getResources().getString(R.string.post_msg))));
        }
        return list;
    }

    private void setGeneralInfo(String name, String email, String lastLogin, String entity, String avg_login_time_per_day) {
        TextView nameT = (TextView) userLayout.findViewById(R.id.sta_user_name);
        TextView emailT = (TextView) userLayout.findViewById(R.id.sta_user_email);
        TextView lastLoginT = (TextView) userLayout.findViewById(R.id.sta_user_last_login);
        TextView phoneT = (TextView) userLayout.findViewById(R.id.sta_user_phone);

        if(nameT!= null && name != null) nameT.setText(name);
        if(emailT!= null && email != null) emailT.setText(email);
        if(lastLoginT!= null && lastLogin != null) {
            if(lastLogin.equals(getResources().getString(R.string.logged_in))){
                lastLoginT.setText(lastLogin);
            } else {
                Time.setTime(getActivity(), lastLoginT, Calendar.getInstance().getTimeInMillis(),
                        lastLogin, getResources().getString(R.string.last_login_date_ago), "");
            }
        }
    }

    private void AddToLayout(LinearLayout layout, String result) throws JSONException {
        layout.removeAllViews();
        JSONArray array = new JSONArray(result);
        for(int i =array.length()-1;i>=0;i--){
            JSONObject obj = array.getJSONObject(i);
            if(obj!=null) {
                EntityRepresentation representation = DecihperRepresentation(obj);
                View repLayout = inflator.inflate(R.layout.statistics_entity_rep, null);
                setRepLayout(repLayout, representation);
                layout.addView(repLayout);
            }
        }
    }

    private void setRepLayout(View repLayout, final EntityRepresentation representation) {
        TextView name = (TextView) repLayout.findViewById(R.id.sta_entity_rep);
        TextView email = (TextView) repLayout.findViewById(R.id.sta_entity_email);

        if(name != null && representation.getName()!=null) name.setText(representation.getName());
        if(email != null && representation.getEmail()!=null) email.setText(representation.getEmail());

        repLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetUserDetail(representation.getEntity(), representation.getId());
            }
        });
    }

    private void GetUserDetail(String entity, String id) {
        if(entity.equals(Account.CHILD.toString())){
            childrenLayout.removeAllViews();
            userLayout = inflator.inflate(R.layout.user_statistics,null);
            GetInfo(getResources().getString(R.string.key_sta_user_general_info), id);
            childrenLayout.addView(userLayout);
        } else {
            psychologistsLayout.removeAllViews();
            userLayout = inflator.inflate(R.layout.user_statistics,null);
            GetInfo(getResources().getString(R.string.key_sta_user_general_info), id);
            psychologistsLayout.addView(userLayout);
        }
    }

    private void GetInfo(String key, String id) {
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String searchChildrenUrl = getResources().getString(R.string.request_user_general_info_url)
                + id;
        getEntity.execute(new String[]{searchChildrenUrl});
    }

    private EntityRepresentation DecihperRepresentation(JSONObject obj) throws JSONException {
        return new EntityRepresentation(obj.get(getResources().getString(R.string.name_text)).toString(),
                obj.get(getResources().getString(R.string.id_text)).toString(),
                obj.get(getResources().getString(R.string.email_key)).toString(),
                obj.get(getResources().getString(R.string.entity_key)).toString());
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
    public void Shown(Boolean first) {
        if(!first && myActionBarActivity!=null) {
            myActionBarActivity.setPeerStatus(getResources().getString(R.string.empty));
            myActionBarActivity.setPeerName(getResources().getString(R.string.empty));
            childrenSearchEditText.setText(getResources().getString(R.string.empty));
            psychologistSearchEditText.setText(getResources().getString(R.string.empty));
            childrenSearchContainer.setVisibility(View.INVISIBLE);
            psychologistsSearchContainer.setVisibility(View.INVISIBLE);
            daysSearchContainer.setVisibility(View.INVISIBLE);
            selectionContainer.setVisibility(View.VISIBLE);
            childrenLayout.removeAllViews();
            psychologistsLayout.removeAllViews();
        }
        shown = true;
    }

    @Override
    public void Hidden() {
        shown = false;
    }

    @Override
    public Boolean isShown() {
        return shown;
    }

    public void getDayStatistics() {
        String key = getResources().getString(R.string.key_sta_day);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String timeStatisticsUrl = getResources().getString(R.string.request_time_statistics_url)
                + String.valueOf(from) + getResources().getString(R.string.slash) + String.valueOf(to);
        getEntity.execute(new String[]{timeStatisticsUrl});


    }

    public class EntityRepresentation{
        private String name;
        private String id;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        private String email;
        private String entity;

        public EntityRepresentation(String name, String id, String email, String entity) {
            this.name = name;
            this.id = id;
            this.email = email;
            this.entity = entity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }


        public String getEntity() {
            return entity;
        }

        public void setEntity(String entity) {
            this.entity = entity;
        }
    }
}
