package com.together.raz.together.Fragments;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.together.raz.together.Activities.MyActionBarActivity;
import com.together.raz.together.Adapters.MessageAdapter;
import com.together.raz.together.AsyncTasks.GetEntity;
import com.together.raz.together.AsyncTasks.SendMessage;
import com.together.raz.together.AsyncTasks.SendGeneric;
import com.together.raz.together.Entities.Message;
import com.together.raz.together.Entities.UserInfo;
import com.together.raz.together.Enums.Account;
import com.together.raz.together.Enums.Location;
import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Callback;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Interfaces.TabAbleFragment;
import com.together.raz.together.Packs.MessagePack;
import com.together.raz.together.R;
import com.together.raz.together.Sensors.ShakeDetector;
import com.together.raz.together.Sensors.SwipeGestureDetector;
import com.together.raz.together.Tools.BitmapHelper;
import com.together.raz.together.Tools.ImageBase64;
import com.together.raz.together.Tools.MyForebaseMessagingService;
import com.together.raz.together.Tools.Time;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment implements TabAbleFragment, Callback, View.OnClickListener, AsyncResponse, Cookied {

    private static final String TAG = "Chat.Fragment";
    private static final String MAX_MESSAGE_NUM = "999999999";
    private static final long BACKGROUND_THREAD_SLEEP = 10000;
    private List<Message> msgs    = new ArrayList<>();
    private MessageAdapter adapter;
    private ListView messages;
    private Button addFileBtn;
    private EditText userInput;
    private Button submitBtn;
    private LinearLayout additionPanel;
    private Button addLinkBtn;
    private Button addImageBtn;
    private LinearLayout addLinkPanel;
    private EditText addLinkEditText;
    private TextView loadingLabel;
    private Button chooseLinkBtn;
    private SharedPreferences settings;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private String lastImageChoosen = "";
    private String imageDimX = "";
    private String imageDimY = "";
    private String lastLinkChoosen = "";
    private int PICK_IMAGE_REQUEST = 1;
    private Integer lastMessage = -1;
    private UserInfo userInfo;
    //relavent for Accountes besides CHILD
    private String currentConversation = "0";
    private Integer UPDATED_MESSAGES_NUM = 10;
    private FloatingActionButton searchChildBtn;
    private LinearLayout searchChildContainer;
    private Button getChildrenByChildLastCommentBtn;
    private Button getChildrenByPsychoLastCommentBtn;
    private Button getSpecificChildBtn;
    private LinearLayout searchChildrenLayout;
    private LinearLayout searchSpecificEditTextLayout;
    private EditText searchSpecificEditText;
    private Button searchSpecificBtn;
    private LayoutInflater inflater;
    private Boolean typeing;
    private BroadcastReceiver reciever = new Reciever();
    private MyActionBarActivity myActionBarActivity;
    private Boolean shown = false;

    public Chat() {
        // Required empty public constructor
    }


    public static Chat newInstance(MyActionBarActivity actionBarActivity){
        Chat fragment = new Chat();
        Bundle bundle = new Bundle();
        bundle.putSerializable(actionBarActivity.getResources().getString(R.string.key_chat_pass_argument), (Serializable) actionBarActivity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Log.d(TAG,"onCreateView()");
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        this.inflater = inflater;
        SetSharedPrefences();
        setFireBase();
        InitUI(view);
        InitSensors();
        InitList();
        InitMessages();
        setConversationIfExists();
//        OpenBackgroundThread();
        return view;
    }

    private void setConversationIfExists() {
        if(myActionBarActivity.getConversation()!=null){
            setNewConversation(myActionBarActivity.getConversation());
//            myActionBarActivity.setConversation(null);
        }
    }

    /*
    In case google cloud messaging takes time,
    this thread, every 5 seconds, initiate a get status request -
    get status only let let us know the status of our peer chat -
    typeing/online/away
     */
    private void OpenBackgroundThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(BACKGROUND_THREAD_SLEEP);
                        Log.d(TAG, "Getting Peer Status");
                        SetPeerStatus();
                    }
                } catch (InterruptedException e){
                    Log.d(TAG, "InterruptedException Background Thread.");
                }
            }
        }).start();
    }

    private void SetUser() {
        Gson gson = new Gson();
        String json = settings.getString(getResources().getString(R.string.key_user), "");
        if(json.equals("")){
            Log.d(TAG, "No User defined");
        } else {
            userInfo = gson.fromJson(json, UserInfo.class);
        }
    }

    private void setFireBase() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Device_Token:", token);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;//super.onOptionsItemSelected(item);
    }

    private void InitMessages() {
        if(userInfo.getAccount()==Account.CHILD){
            currentConversation = userInfo.getId();
            GetMessages(userInfo,currentConversation,"-1",10);
        } else {
            GetMessages(userInfo,"1", "-1", 10);
        }
    }

    private void InitList() {
        adapter = new MessageAdapter(getActivity(),msgs,userInfo, this,this);
        messages.setAdapter(adapter);
    }

    private void SetSharedPrefences() {
        settings = getActivity().getSharedPreferences(getResources().getString(R.string.data),
                getContext().MODE_PRIVATE);
        settings = getActivity().getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.prefs), 0);
        setCookie(getResources().getString(R.string.setCookie));
        SetUser();
    }

    private void InitUI(View view) {
        messages = (ListView) view.findViewById(R.id.chat_list);
        addFileBtn = (Button) view.findViewById(R.id.chat__add_file);
        userInput = (EditText) view.findViewById(R.id.chat_user_input_textbox);
        submitBtn = (Button) view.findViewById(R.id.chat_user_submit);
        additionPanel = (LinearLayout) view.findViewById(R.id.chat_addition_panel);
        addLinkBtn = (Button) view.findViewById(R.id.chat_add_link);
        addImageBtn = (Button) view.findViewById(R.id.chat_add_image);
        addLinkPanel = (LinearLayout) view.findViewById(R.id.chat_add_link_layout);
        addLinkEditText = (EditText) view.findViewById(R.id.chat_add_link_edittext);
        chooseLinkBtn = (Button) view.findViewById(R.id.chat_add_link_btn);
        loadingLabel = (TextView) view.findViewById(R.id.chat_pending_texview);
        searchChildBtn = (FloatingActionButton) view.findViewById(R.id.search_chat_icon);
        searchChildContainer = (LinearLayout) view.findViewById(R.id.chat_search_child_container);
        getChildrenByChildLastCommentBtn = (Button) view.findViewById(R.id.chat_sort_children_by_child_btn);
        getChildrenByPsychoLastCommentBtn = (Button) view.findViewById(R.id.chat_sort_children_by_psycho_btn);
        getSpecificChildBtn = (Button) view.findViewById(R.id.chat_get_specific_child_btn);
        searchChildrenLayout = (LinearLayout) view.findViewById(R.id.chat_children_layout);
        searchSpecificEditTextLayout = (LinearLayout) view.findViewById(R.id.chat_search_specific_layout);
        searchSpecificEditText = (EditText) view.findViewById(R.id.chat_search_child_edittext);
        searchSpecificBtn = (Button) view.findViewById(R.id.chat_search_specific_btn);
//        chatPeerName = (TextView) view.findViewById(R.id.chat_peer_name);
//        chatPeerStatus = (TextView) view.findViewById(R.id.chat_peer_status);
        myActionBarActivity = (MyActionBarActivity) getArguments().
                getSerializable(getResources().getString(R.string.key_chat_pass_argument));
        myActionBarActivity.setMenuLabel(getActivity().getResources().getString(R.string.empty));
        if(userInfo.getAccount()==Account.CHILD) searchChildBtn.setVisibility(View.GONE);
        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (additionPanel.getVisibility() == View.INVISIBLE) {
                    additionPanel.setVisibility(View.VISIBLE);
                } else {
                    additionPanel.setVisibility(View.INVISIBLE);
                }
            }
        });
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetImage();
                additionPanel.setVisibility(View.INVISIBLE);
            }
        });
        addLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLinkPanel.setVisibility(View.VISIBLE);
                additionPanel.setVisibility(View.INVISIBLE);
            }
        });
        chooseLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastLinkChoosen = addLinkEditText.getText().toString();
                Toast.makeText(getActivity(), getResources().getString(R.string.link_uploaded),
                        Toast.LENGTH_SHORT).show();
                addLinkPanel.setVisibility(View.INVISIBLE);
            }
        });
        searchChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchChildContainer.getVisibility()==View.INVISIBLE){
                    searchChildContainer.setVisibility(View.VISIBLE);
                } else {
                    searchChildContainer.setVisibility(View.INVISIBLE);
                    userInput.setVisibility(View.VISIBLE);
                }
            }
        });
        getChildrenByChildLastCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetChildrenByChildLastComment();
            }
        });
        getChildrenByPsychoLastCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetChildrenByPsychoLastComment();
            }
        });
        getSpecificChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSpecificEditTextLayout.setVisibility(View.VISIBLE);
            }
        });
        searchSpecificBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = searchSpecificEditText.getText().toString();
                if(name!=null && !name.equals(getResources().getString(R.string.empty)))
                    GetSpecificChild(name);
            }
        });
        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "Text: |" + userInput.getText().toString() + "|    typeing: " + typeing.toString());
                if(userInput.getText().toString().equals(getResources().getString(R.string.empty)) && typeing){
                    SendStatus(getResources().getString(R.string.key_online_text));
                    typeing = false;
                } else if(!userInput.getText().toString().equals(getResources().getString(R.string.empty)) && !typeing){
                    SendStatus(getResources().getString(R.string.key_typeing_text));
                    typeing = true;
                }
            }
        });
        submitBtn.setOnClickListener(this);
    }

    private void SendStatus(String status) {
        Log.d(TAG, "Sending " + status);
        if(userInfo==null) return;
        String key = getResources().getString(R.string.key_chat_set_status);
        HashMap<String, String> hash = new HashMap<>();
        hash.put(getResources().getString(R.string.status_conversation_id),currentConversation);
        hash.put(getResources().getString(R.string.status_entity), userInfo.getAccount().toString());
        hash.put(getResources().getString(R.string.status_id), userInfo.getId());
        hash.put(getResources().getString(R.string.status_name),userInfo.getName());
        hash.put(getResources().getString(R.string.status_status),status);
        hash.put(getResources().getString(R.string.status_deviceId),userInfo.getUserToken());
        String updateStatusUrl = getResources().getString(R.string.request_update_status_url);
        new SendGeneric(this,getResources().getString(R.string.key_login_req), this,
                getResources().getString(R.string.getcookie), getResources().getString(R.string.setCookie),
                updateStatusUrl, hash, getActivity()).execute();
    }

    private void GetSpecificChild(String name) {
        String key = getResources().getString(R.string.key_get_specific_child);
        String getChildrenUrl = getResources().getString(R.string.request_user_by_name_url);
        HashMap<String, String> hash = new HashMap<>();
        hash.put(getResources().getString(R.string.key_name),name);
        new SendGeneric(this,key, this,
                getResources().getString(R.string.getcookie), getResources().getString(R.string.setCookie),
                getChildrenUrl, hash, getActivity()).execute();
        userInput.setVisibility(View.INVISIBLE);
    }

    private void GetChildrenByPsychoLastComment() {
        String key = getResources().getString(R.string.key_get_children);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String getChildrenUrl = getResources().getString(R.string.request_get_children_by_psycho_last_comment_url);
        getEntity.execute(new String[]{getChildrenUrl});
    }

    private void GetChildrenByChildLastComment() {
        String key = getResources().getString(R.string.key_get_children);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String getChildrenUrl = getResources().getString(R.string.request_get_children_by_child_last_comment_url);
        getEntity.execute(new String[]{getChildrenUrl});
    }

    public void GetImage() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST &&  data != null && data.getData() != null) {

            Uri uri = data.getData();

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                Bitmap b = BitmapHelper.getScaledRectangleBitmap(bitmap, size.x, size.y/2);
                if(bitmap!=null) {
                    String background = ImageBase64.encodeTobase64(b);
                    lastImageChoosen = background;
                    imageDimX = String.valueOf(b.getWidth());
                    imageDimY = String.valueOf(b.getHeight());
                    Toast.makeText(getActivity(), getResources().getString(R.string.image_uploaded),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void InitSensors() {
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                handleShakeEvent(count);
            }
        });
        final GestureDetector gd = new GestureDetector(getActivity(), new SwipeGestureDetector(this,
                getResources().getString(R.string.duration)));
        View.OnTouchListener gl = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gd.onTouchEvent(event);
            }
        };
        messages.setOnTouchListener(gl);
    }

    private void handleShakeEvent(int count) {
        Toast.makeText(getActivity(), getResources().getString(R.string.loading_new_messages),
                Toast.LENGTH_SHORT).show();
        GetMessages(userInfo, currentConversation, "-1", UPDATED_MESSAGES_NUM);
    }

    @Override
    public void Callback() {
        if (messages.getChildCount()==0 ||  (messages.getFirstVisiblePosition() == 0 &&
                messages.getChildAt(0).getTop() >= 0)) {
            Toast.makeText(getActivity(), (String) getResources().getString(R.string.loading_new_messages),
                    Toast.LENGTH_SHORT).show();
            if(msgs.isEmpty()){
                GetMessages(userInfo,currentConversation,MAX_MESSAGE_NUM,UPDATED_MESSAGES_NUM);
            } else {
                GetMessages(userInfo,currentConversation,String.valueOf(msgs.get(0).getNum()),UPDATED_MESSAGES_NUM);
            }
        }
    }

    @Override
    public void onClick(View v) {
        String[] keys = {
                getResources().getString(R.string.key_chat_childID),
                getResources().getString(R.string.key_chat_num),
                getResources().getString(R.string.key_chat_time),
                getResources().getString(R.string.key_chat_message),
                getResources().getString(R.string.key_chat_image),
                getResources().getString(R.string.key_chat_link),
                getResources().getString(R.string.key_chat_author_id),
                getResources().getString(R.string.key_chat_author_name),
                getResources().getString(R.string.key_chat_author_entity),
                getResources().getString(R.string.key_image_dim_x),
                getResources().getString(R.string.key_image_dim_y)
        };
        //Get last message id.
        SendMessage sendMessage = new SendMessage(this, getResources().getString(R.string.key_save_message), this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie), getActivity());
        String childID = (userInfo.getAccount()==Account.CHILD)?userInfo.getId():currentConversation;
        Integer num = lastMessage;
        String time = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String message = userInput.getText().toString();
        String image = lastImageChoosen;
        String imageX = imageDimX;
        String imageY = imageDimY;
        String link = lastLinkChoosen;
        String authorID = userInfo.getId();
        String authorName = userInfo.getName();
        String authorEntity = userInfo.getAccount().toString();
        String sendMessageUrl = getResources().getString(R.string.request_save__message_url).toString();
        Message msg = new Message(childID, num, time, message, image,
                link, authorID, authorName, authorEntity, imageX, imageY);
        Log.d("Message:", msg.toString());
        sendMessage.execute(new MessagePack[]{new MessagePack(sendMessageUrl, msg, keys)});
        lastLinkChoosen = "";
        lastImageChoosen = "";
        imageDimX = "";
        imageDimY = "";
        lastMessage++;
        userInput.setText(getResources().getString(R.string.empty));
    }

    private void GetMessages(UserInfo user,String conversation, String before, int amount){
        String key = (before.equals("-1"))?
                getResources().getString(R.string.getfront)
                :getResources().getString(R.string.getback);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String getPostsUrl = getResources().getString(R.string.request_get_messages_url)
                + getResources().getString(R.string.slash) + user.getId()
                + getResources().getString(R.string.slash) + conversation
                + getResources().getString(R.string.slash) + before
                + getResources().getString(R.string.slash) + String.valueOf(amount);
        Log.d(TAG,"GetMessages()");
        getEntity.execute(new String[]{getPostsUrl});
    }

    @Override
    public void OnFinished(String result) {
        Log.d("RECIEVED:", result);
        if(getActivity()==null) return;
        try {
            if (result.startsWith(getResources().getString(R.string.key_save_message))) {
                result = result.substring(4);
                if (result.equals(getResources().getString(R.string.empty))) return;
                ReadMessages(result, Location.FRONT);
                adapter.notifyDataSetChanged();
                messages.setSelection(adapter.getCount() - 1);
            } else if (result.startsWith(getResources().getString(R.string.getfront))) {
                loadingLabel.setVisibility(View.INVISIBLE);
                result = result.substring(8);
                if (result.equals(getResources().getString(R.string.empty))) return;
                ReadMessages(result, Location.FRONT);
                adapter.notifyDataSetChanged();
                messages.setSelection(adapter.getCount() - 1);
            } else if(result.startsWith(getResources().getString(R.string.getback))){
                result = result.substring(7);
                if(result.equals(getResources().getString(R.string.empty)) ||
                        result.equals(getResources().getString(R.string.empty_array))){
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_more_messages),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                ReadMessages(result, Location.BACK);
                adapter.notifyDataSetChanged();;
            } else if (result.startsWith(getResources().getString(R.string.key_get_children))){
                result = result.substring(16);
                if (result.equals(getResources().getString(R.string.empty))) return;
                AddChildrenToLayout(result);
            } else if(result.startsWith(getResources().getString(R.string.key_get_specific_child))){
                result = result.substring(22);
                if (result.equals(getResources().getString(R.string.empty))) return;
                AddSpecificChildrenToLayout(result);
            } else if(result.startsWith(getResources().getString(R.string.key_chat_get_peers_status))){
                result = result.substring(25);
                if (result.equals(getResources().getString(R.string.empty))) return;
                SetPeerTextViewStatus(result);
            }
        } catch (JSONException e){
            Log.d("JSON","Failed to parse JSON");
        }
    }

    private void SetPeerTextViewStatus(String result) throws JSONException {
        boolean set = false;
        JSONObject obj = new JSONObject(result);
        if(userInfo.getAccount()==Account.CHILD){
            JSONArray array = obj.getJSONArray(getResources().getString(R.string.key_psycho_text));
            for(int i = 0; i < array.length(); i++){
                JSONObject psycho = array.getJSONObject(i);
                set = true;
                myActionBarActivity.setPeerName(psycho.getString(getResources().getString(R.string.key_chat_psycho_name)));
                myActionBarActivity.setPeerStatus(adjustText(psycho.getString(getResources().getString(R.string.key_chat_psycho_status))));
            }
            if(!set){
                myActionBarActivity.setPeerName(getResources().getString(R.string.empty));
                myActionBarActivity.setPeerStatus(getResources().getString(R.string.empty));
            }
        } else {
            myActionBarActivity.setPeerName(obj.getString(getResources().getString(R.string.key_chat_child_name)));
            myActionBarActivity.setPeerStatus(adjustText(obj.getString(getResources().getString(R.string.key_chat_child_status))));
        }
    }

    private String adjustText(String string) {
        if(string.equals(getResources().getString(R.string.key_away_text))){
            return getResources().getString(R.string.away);
        } else if (string.equals(getResources().getString(R.string.key_online_text))){
            return getResources().getString(R.string.online);
        } else if (string.equals(getResources().getString(R.string.key_typeing_text))){
            return getResources().getString(R.string.typeing);
        }
        return getResources().getString(R.string.empty);
    }

    private void AddSpecificChildrenToLayout(String result) throws JSONException {
        searchSpecificEditText.setText(getResources().getString(R.string.empty));
        searchSpecificEditTextLayout.setVisibility(View.GONE);
        searchChildrenLayout.removeAllViews();
        JSONArray array = new JSONArray(result);
        for(int i =0;i<array.length();i++){
            JSONObject obj = array.getJSONObject(i);
            if(obj!=null) {
                UserInfo representation = DecihperUserInfo(obj);
                View child = inflater.inflate(R.layout.chat_user_rep, null);
                setUserView(child, representation);
                searchChildrenLayout.addView(child);
            }
        }
    }

    private void setUserView(View child, UserInfo representation) {
        TextView name = (TextView) child.findViewById(R.id.chat_user_rep);
        TextView email = (TextView) child.findViewById(R.id.chat_user_email);

        if(name!=null && representation.getName()!=null) name.setText(representation.getName());
        if(email!=null && representation.getEmail()!=null) email.setText(representation.getEmail());
        final String conversationID = representation.getId();
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewConversation(conversationID);
            }
        });
    }

    private void setNewConversation(String conversationID) {
        if(!currentConversation.equals("0")) SendStatus(getResources().getString(R.string.key_away_text));
        currentConversation = conversationID;
        SendStatus(getResources().getString(R.string.key_online_text));
        msgs.clear();
        myActionBarActivity.setPeerName(getResources().getString(R.string.empty));
        myActionBarActivity.setPeerStatus(getResources().getString(R.string.empty));
        SetPeerStatus();
        GetMessages(userInfo, conversationID, "-1", 10);
        searchChildrenLayout.removeAllViews();
        searchChildContainer.setVisibility(View.INVISIBLE);
        userInput.setVisibility(View.VISIBLE);
    }

    private UserInfo DecihperUserInfo(JSONObject obj) throws JSONException {
        return new UserInfo(obj.getString(getResources().getString(R.string.key_id)),
                obj.getString(getResources().getString(R.string.key_entity)),
                obj.getString(getResources().getString(R.string.key_name)),
                obj.getString(getResources().getString(R.string.key_icon)),
                obj.getString(getResources().getString(R.string.key_user_deviceId)),
                obj.getString(getResources().getString(R.string.key_user_email)),
                obj.getString(getResources().getString(R.string.key_user_pass)));
    }

    private void AddChildrenToLayout(String result) throws JSONException {
        searchChildrenLayout.removeAllViews();
        JSONArray array = new JSONArray(result);
        for(int i =0;i<array.length();i++){
            JSONObject obj = array.getJSONObject(i);
            if(obj!=null) {
                EntityRepresentation representation = DecihperRepresentation(obj);
                View child = inflater.inflate(R.layout.chat_child_rep, null);
                setChild(child, representation);
                searchChildrenLayout.addView(child);
            }
        }
    }

    private void setChild(View child, final EntityRepresentation representation) {
        TextView name = (TextView) child.findViewById(R.id.chat_child_rep);
        TextView time = (TextView) child.findViewById(R.id.chat_child_time);

        if(name!=null && representation.getName()!=null) name.setText(representation.getName());
        if(time!=null && representation.getTime()!=null){
            Time.setTime(getActivity(), time, Calendar.getInstance().getTimeInMillis(),
                    representation.getTime(), getActivity().getResources().getString(R.string.date_ago), "");
        }
        final String conversationID = representation.conversationID;
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewConversation(conversationID);
            }
        });
    }

    private EntityRepresentation DecihperRepresentation(JSONObject obj) throws JSONException {
        return new EntityRepresentation(obj.get(getResources().getString(R.string.user_id)).toString(),
                obj.get(getResources().getString(R.string.time)).toString(),
                obj.get(getResources().getString(R.string.conversation_id)).toString(),
                obj.get(getResources().getString(R.string.name)).toString());
    }


    private void ReadMessages(String result, Location location) throws JSONException {
        if(result.equals(getResources().getString(R.string.empty))) return;
        else if(result.startsWith(getResources().getString(R.string.left_bracket))){
            JSONObject json = new JSONObject(result);
            if(json!=null) {
                if(location == Location.FRONT){
                    addOrReplace(DecihperMessage(json),Location.FRONT);
                } else {
                    addOrReplace(DecihperMessage(json), Location.BACK);
                }
            }
        } else {
            JSONArray array = new JSONArray(result);
            if(location==Location.FRONT){
                for(int i =0;i<array.length();i++){
                    JSONObject obj = array.getJSONObject(i);
                    if(obj!=null) {
                        addOrReplace(DecihperMessage(obj), Location.FRONT);
                    }
                }
            } else {
                for(int i =array.length()-1;i>=0;i--){
                    JSONObject obj = array.getJSONObject(i);
                    if(obj!=null) {
                        addOrReplace(DecihperMessage(obj), Location.BACK);
                    }
                }
            }
        }
        Log.d("Messages:",msgs.toString());
    }

    private void addOrReplace(Message msg, Location location){
        Boolean replaced = false;
        List<Message> newList = new ArrayList<>();
        for(Message message: msgs){
            if (message.getNum().equals(msg.getNum())){
                replaced = true;
                newList.add(msg);
            } else {
                newList.add(message);
            }
        }
        if(!replaced) {
            if (location == Location.BACK) {
                newList.add(0, msg);
            } else {
                newList.add(msg);
            }
        }
        msgs = newList;
        adapter.setList(msgs);
    }

    private Message DecihperMessage(JSONObject obj) throws JSONException {
        return new Message(obj.get(getResources().getString(R.string.key_chat_childID)).toString(),
                Integer.parseInt(obj.get(getResources().getString(R.string.key_chat_num)).toString()),
                obj.get(getResources().getString(R.string.key_chat_time)).toString(),
                obj.get(getResources().getString(R.string.key_chat_message)).toString(),
                obj.get(getResources().getString(R.string.key_chat_image)).toString(),
                obj.get(getResources().getString(R.string.key_chat_link)).toString(),
                obj.get(getResources().getString(R.string.key_chat_author_id)).toString(),
                obj.get(getResources().getString(R.string.key_chat_author_name)).toString(),
                obj.get(getResources().getString(R.string.key_chat_author_entity)).toString(),
                obj.get(getResources().getString(R.string.key_image_dim_x)).toString(),
                obj.get(getResources().getString(R.string.key_image_dim_y)).toString());
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
        SendStatus(getResources().getString(R.string.key_online_text));
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyForebaseMessagingService.BROADCAST_ACTION);
        getActivity().registerReceiver(reciever, filter);
        // Add the following line to register the Session Manager Listener onResume
        typeing = settings.getBoolean(getResources().getString(R.string.key_typeing_text), false);
        Log.d(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        SendStatus(getResources().getString(R.string.key_away_text));
        getActivity().unregisterReceiver(reciever);
        SharedPreferences.Editor edit = settings.edit();
        edit.putBoolean(getResources().getString(R.string.key_typeing_text),typeing).apply();
        super.onPause();
    }

    @Override
    public void Shown(Boolean first) {
        setHasOptionsMenu(false);
        SendStatus(getResources().getString(R.string.key_online_text));
        if(!first){
            if(mSensorManager==null || mShakeDetector==null || mAccelerometer==null) return;
            mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
            if(myActionBarActivity!=null){
                myActionBarActivity.setTitle(getResources().getString(R.string.empty));
            }
        }
        shown = true;
    }

    @Override
    public void Hidden() {
        Log.d(TAG,"Hidden()");
        if(myActionBarActivity!=null){
            myActionBarActivity.setTitle(getResources().getString(R.string.actionbar_title));
        }
        mSensorManager.unregisterListener(mShakeDetector);
        SendStatus(getResources().getString(R.string.key_away_text));
        shown = false;
    }

    @Override
    public Boolean isShown() {
        return shown;
    }

    private class EntityRepresentation {
        private String entity_id;
        private String time;
        private String conversationID;
        private String name;

        public String getEntity_id() {
            return entity_id;
        }

        public String getTime() {
            return time;
        }

        public String getConversationID() {
            return conversationID;
        }

        public String getName() {
            return name;
        }

        public EntityRepresentation(String entity_id, String time, String conversationID, String name) {
            this.entity_id = entity_id;
            this.time = time;
            this.conversationID = conversationID;
            this.name = name;
        }


    }

    class Reciever extends BroadcastReceiver {
        /**
         * When serviece broadcast, this method get the updates from server.
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(getResources().getString(R.string.fragment))
                    .equals(getResources().getString(R.string.app_fragment)) && isShown()){
                if(intent.hasExtra(getResources().getString(R.string.key_help_child_id))
                        && intent.hasExtra(getResources().getString(R.string.key_help_request_num))
                        && intent.hasExtra(getResources().getString(R.string.key_help_child_name))){
                    if(userInfo.getAccount()==Account.PSYCHOLOGIST || userInfo.getAccount()==Account.MANAGER){
                        DisplayHelpOption(intent.getStringExtra(getResources().getString(R.string.key_help_request_num)),
                                intent.getStringExtra(getResources().getString(R.string.key_help_child_id)),
                                intent.getStringExtra(getResources().getString(R.string.key_help_child_name)));
                        }
                }
            } else if(intent.getStringExtra(getResources().getString(R.string.fragment))
                    .equals(getResources().getString(R.string.chat_fragment)) && isShown()){
                if(intent.hasExtra(getResources().getString(R.string.my_key))
                        && intent.getStringExtra(getResources().getString(R.string.my_key))
                    .equals(getResources().getString(R.string.status_update))){
                    GetMessages(userInfo, currentConversation, "-1", UPDATED_MESSAGES_NUM);
                    SetPeerStatus();
                }
            }
        }
    }

    private void DisplayHelpOption(String num, final String newConversation, final String child_name) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getContext(),"YES", Toast.LENGTH_SHORT);
                        setNewConversation(newConversation);
                        sendAnsweredImmediateHelp(true,userInfo.getId());
                        //notify server for statistics - num
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        sendAnsweredImmediateHelp(false, userInfo.getId());
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(child_name + " " + getResources().getString(R.string.asks_for_immediate_help_can_you_help))
                .setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.no),dialogClickListener).show();


    }

    private void sendAnsweredImmediateHelp(boolean b, String id) {
        String key = getResources().getString(R.string.key_answered_help);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String getPeerStatus = getResources().getString(R.string.request_answered_help_url)
                + id + getResources().getString(R.string.slash) + String.valueOf(b);
        getEntity.execute(new String[]{getPeerStatus});
    }

    private void SetPeerStatus() {
        String key = getResources().getString(R.string.key_chat_get_peers_status);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String getPeerStatus = getResources().getString(R.string.request_chat_peers_status_url)
                + currentConversation;
        getEntity.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{getPeerStatus});
    }
}
