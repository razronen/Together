package com.together.raz.together.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.together.raz.together.Activities.MyActionBarActivity;
import com.together.raz.together.AsyncTasks.GetEntity;
import com.together.raz.together.Entities.Updates.Update;
import com.together.raz.together.Entities.UserInfo;
import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Callback;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Interfaces.TabAbleFragment;
import com.together.raz.together.Painters.UpdatePainter;
import com.together.raz.together.R;
import com.together.raz.together.Sensors.ShakeDetector;
import com.together.raz.together.Sensors.SwipeGestureDetector;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Updates extends Fragment implements TabAbleFragment, AsyncResponse, Cookied, Callback {

    private static final String TAG = "Updates.Fragment";
    private static final Integer maxUpdatesInRequest = 10;
    private SharedPreferences settings;
    private MyActionBarActivity myActionBarActivity = null;
    private List<Update> updates = new ArrayList<>();
    private UpdatePainter painter = null;
    private UserInfo user = null;
    private LinearLayout updatesLayout = null;
    private ScrollView scroll = null;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private boolean swipeRefresh = false;
    private Boolean shown = false;

    public static Updates newInstance(MyActionBarActivity actionBarActivity){
        Updates fragment = new Updates();
        Bundle bundle = new Bundle();
        bundle.putSerializable(actionBarActivity.getResources().getString(R.string.key_updates_pass_argument), (Serializable) actionBarActivity);
        fragment.setArguments(bundle);
        return fragment;
    }

    public Updates() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_updates, container, false);
        SetSharedPrefences();
        initUI(view, inflater);
        GetUpdates(-1,10);
        // Inflate the layout for this fragment
        return view;
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
        scroll.setOnTouchListener(gl);
    }

    private void handleShakeEvent(int count) {
        Toast.makeText(getActivity(), getResources().getString(R.string.loading_new_updates),
                Toast.LENGTH_SHORT).show();
        GetUpdates(-1, maxUpdatesInRequest);
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

    private void initUI(View view, LayoutInflater inflater) {
        painter = new UpdatePainter(getActivity(),this,this,inflater,user);
        updatesLayout = (LinearLayout) view.findViewById(R.id.updates_container);
        myActionBarActivity = (MyActionBarActivity) getArguments().
                getSerializable(getResources().getString(R.string.key_updates_pass_argument));
        myActionBarActivity.setMenuLabel(getActivity().getResources().getString(R.string.empty));
        scroll = (ScrollView) view.findViewById(R.id.update_scrollview);
        scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scroll.getScrollY(); //for verticalScrollView
                if (scrollY == 0)
                    swipeRefresh = true;
                else
                    swipeRefresh = false;
            }
        });
    }

    private void GetUpdates(int num, int amount) {
        String key = getResources().getString(R.string.key_get_updates);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String getUpdatesUrl = getResources().getString(R.string.request_get_updates_url)
                + String.valueOf(num) + getResources().getString(R.string.slash)
                + String.valueOf(amount);
        getEntity.execute(new String[]{getUpdatesUrl});
    }

    private void SetSharedPrefences() {
        settings = getActivity().getSharedPreferences(getResources().getString(R.string.data),
                getContext().MODE_PRIVATE);
        settings = getActivity().getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.prefs), 0);
        setCookie(getResources().getString(R.string.setCookie));
        SetUser();
    }

    @Override
    public void OnFinished(String result) {
        Log.d("RECIEVED:", result);
        if(getActivity()==null) return;
        try {
            if (result.startsWith(getResources().getString(R.string.key_get_updates))) {
                result = result.substring(15);
                if (result.equals(getResources().getString(R.string.empty))) return;
                ReArrangeUpdates(result);
                painter.paint(updatesLayout, updates);
            } else if(result.startsWith(getResources().getString(R.string.key_approve_shift))){
                GetUpdates(-1,10);
            }
            scroll.fullScroll(View.FOCUS_DOWN);
        } catch (JSONException e){
            e.printStackTrace();
            Log.d("JSON","Failed to parse JSON");
        }
    }

    private void ReArrangeUpdates(String result) throws JSONException {
        List<Update> downloadedUpdates = PullUpdates(result);
        for(Update update: downloadedUpdates){
            Boolean replaced = false;
            for(Update update1: updates){
                if(update.getNum()==update1.getNum()){
                    update1.setJson(update.getJson());
                    update1.setTime(update.getTime());
                    update1.setUpdate(update.getUpdate());
                    replaced = true;
                }
            }
            if(!replaced){
                updates.add(update);
            }
        }

        Collections.sort(updates, new Update());
    }

    private List<Update> PullUpdates(String result) throws JSONException {
        List<Update> newUpdates = new ArrayList<>();
        JSONArray array = new JSONArray(result);
        for(int i = array.length() -1; i >= 0; i--){
            newUpdates.add(DechiperUpdate(array.getJSONObject(i)));
        }
        return newUpdates;
    }

    private Update DechiperUpdate(JSONObject obj) throws JSONException {
        return new Update(obj.getString(getResources().getString(R.string.type)),
                obj.getString(getResources().getString(R.string.time)),
                obj.getInt(getResources().getString(R.string.num)),
                new JSONObject(obj.getString(getResources().getString(R.string.json))));
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

    @Override
    public void Shown(Boolean first) {
        if(!first) {
            if(myActionBarActivity!=null) {
                myActionBarActivity.setPeerStatus(getResources().getString(R.string.empty));
                myActionBarActivity.setPeerName(getResources().getString(R.string.empty));
            }
            if(mSensorManager==null || mShakeDetector==null || mAccelerometer==null) return;
            mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
            myActionBarActivity.setPeerStatus(getResources().getString(R.string.empty));
            myActionBarActivity.setPeerName(getResources().getString(R.string.empty));
        }
        shown = true;
    }

    @Override
    public void Hidden() {
        if(mSensorManager!=null)
            mSensorManager.unregisterListener(mShakeDetector);
        shown = false;
    }

    @Override
    public Boolean isShown() {
        return shown;
    }

    @Override
    public void Callback() {
        if(swipeRefresh) {
            Toast.makeText(getActivity(), (String) getResources().getString(R.string.loading_updates),
                    Toast.LENGTH_SHORT).show();
            if (updates.isEmpty())
                GetUpdates(-1, maxUpdatesInRequest);
            else
                GetUpdates(updates.get(0).getNum(), maxUpdatesInRequest);
        }
    }
}
