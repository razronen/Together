package com.together.raz.together.Fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.together.raz.together.Activities.MyActionBarActivity;
import com.together.raz.together.Adapters.CalendarEventsAdapter;
import com.together.raz.together.AsyncTasks.GetEntity;
import com.together.raz.together.AsyncTasks.SendShift;
import com.together.raz.together.Entities.Shift;
import com.together.raz.together.Entities.ShiftCollection;
import com.together.raz.together.Entities.UserInfo;
import com.together.raz.together.Enums.Account;
import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Interfaces.TabAbleFragment;
import com.together.raz.together.Packs.ShiftPack;
import com.together.raz.together.R;
import com.together.raz.together.Tools.MyForebaseMessagingService;
import com.together.raz.together.Tools.MyGoogleCalendarAPI;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.tyczj.extendedcalendarview.CalendarProvider;
import com.tyczj.extendedcalendarview.Day;
import com.tyczj.extendedcalendarview.Event;
import com.tyczj.extendedcalendarview.ExtendedCalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class Calender extends Fragment implements TabAbleFragment, Cookied, View.OnClickListener, AsyncResponse, Serializable {

    private static final String TAG = "...Calender";
    private SharedPreferences settings;
    private ExtendedCalendarView calendar;
    private ListView listViewCalendar;
    private FloatingActionButton addEventBtn;
    private ScrollView timePickerContainer;
    private Button addShiftBtn;
    private TimePicker startPicker;
    private TimePicker endPicker;
    private UserInfo user;
    private Button delAllEventsBtn;
    private Uri uri;
    private ContentValues values;
    private boolean dayIsClicked;
    private DateTime Month = null;
    private Day clickedDay = null;
    private ArrayList<Event> eventsList;
    private ArrayList itemsList;
    private CalendarEventsAdapter currentAdapter;
    private MyGoogleCalendarAPI googleCalendarAPI;
    private MyActionBarActivity myActionBarActivity;
    private Map<String,Boolean> googleSynced = new HashMap<>();
    private BroadcastReceiver reciever = new Reciever();
    private ShiftCollection approvedShifts = new ShiftCollection();
    private Boolean finisCreatingView = false;
    private Boolean first = false;
    private Boolean shown = false;

    public ExtendedCalendarView getCalendar() {
        return calendar;
    }

    public static Calender newInstance(MyActionBarActivity actionBarActivity){
        Calender fragment = new Calender();
        Bundle bundle = new Bundle();
        bundle.putSerializable(actionBarActivity.getResources().getString(R.string.key_calendar_pass_argument), (Serializable) actionBarActivity);
        fragment.setArguments(bundle);
        return fragment;
    }

    public Calender() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        View view = inflater.inflate(R.layout.fragment_calender, container, false);
        SetSharedPrefences();
        initUI(view);
        InitCal();
        setGoogleAPI(view);
        setFireBase();
        clearChatLabels();
        finisCreatingView = true;
        return view;
    }

    private void checkForNewApprovedShifts() {
        String key = getResources().getString(R.string.key_get_shift);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String getShiftUrl = getResources().getString(R.string.request_get_approved_shift_by_psycho_id_url)  + user.getId();
        getEntity.execute(new String[]{getShiftUrl});
    }

    private void setFireBase() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Device_Token:", token);
    }

    private void setGoogleAPI(View view) {

        TextView textView = (TextView) view.findViewById(R.id.google_calendar_textview);
        ProgressDialog mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage(getActivity().getResources().getString(R.string.calling_google_calendar));
        myActionBarActivity = (MyActionBarActivity) getArguments().
                getSerializable(getResources().getString(R.string.key_calendar_pass_argument));
        if(myActionBarActivity.getmCredential()==null) {
            myActionBarActivity.setmCredential(GoogleAccountCredential.usingOAuth2(
                    getContext(), Arrays.asList(MyGoogleCalendarAPI.SCOPES))
                    .setBackOff(new ExponentialBackOff()));
        }
        //setting acount name if exists:
        SharedPreferences settings =
                getActivity().getPreferences(Context.MODE_PRIVATE);
        myActionBarActivity.getmCredential().setSelectedAccountName(settings.getString(
                getActivity().getResources().getString(R.string.key_account_google_name), null));
        googleCalendarAPI = new MyGoogleCalendarAPI(myActionBarActivity.getmCredential(),textView
                ,mProgress,getActivity(), this);
        if(first) syncMonthWithGoogle();
        calendar.setRunnable(new Runnable() {
            @Override
            public void run() {
                syncMonthWithGoogle();
            }
        });
    }

    private void syncMonthWithGoogle(){
        String month = String.valueOf(calendar.getCal().get(Calendar.YEAR) + "-"
                + String.valueOf(calendar.getCal().get(Calendar.MONTH)));
        if(!googleSynced.containsKey(month) || googleSynced.get(month) == false){
            Log.d(TAG, "Pulling for: " + month);
            googleCalendarAPI.getResultsFromApi();
            googleSynced.put(month,true);
        }
        checkForNewApprovedShifts();
    }

    private void InitCal() {
        values = new ContentValues();
        getActivity().getContentResolver().delete(CalendarProvider.CONTENT_URI, null, null);
        currentAdapter = new CalendarEventsAdapter(getActivity(),null);
        listViewCalendar.setAdapter(currentAdapter);
        SetApprovedShifts();
        putShiftsOnCalendar();
        calendar.setOnDayClickListener(new ExtendedCalendarView.OnDayClickListener() {
            @Override
            public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day) {
                dayIsClicked = true;
                clickedDay = day;

                if (addEventBtn.getVisibility() == View.INVISIBLE)
                    addEventBtn.setVisibility(View.VISIBLE);
                currentAdapter.setList(EraseDuplicatesIfExists(day.getEvents()));
                currentAdapter.notifyDataSetChanged();
            }
        });
    }

    private ArrayList<Event> EraseDuplicatesIfExists(ArrayList<Event> events) {
        ArrayList<Event> result = new ArrayList<>();
        String dateFormat = "HH:mm";
        Boolean skip = false;
        for(Event e1: events){
            for(Event e2: result){
                if(e1.getTitle()==null || e2.getTitle()==null) break;
                if(e1.getStartDate(dateFormat).equals(e2.getStartDate(dateFormat))
                        && e1.getEndDate(dateFormat).equals(e2.getEndDate(dateFormat))
                        && e1.getTitle().equals(e2.getTitle())){
                    skip = true;
                }
            }
            if(skip){
                skip = false;
                continue;
            }
            result.add(e1);
        }
        return result;
    }

    private void initUI(View view) {
        calendar = (ExtendedCalendarView) view.findViewById(R.id.app_calender);
        listViewCalendar = (ListView) view.findViewById(R.id.app_calender_listview_events);
        addEventBtn = (FloatingActionButton) view.findViewById(R.id.app_calendar_add_event);
        timePickerContainer = (ScrollView) view.findViewById(R.id.app_calender_timepicker_container);
        addShiftBtn = (Button) view.findViewById(R.id.app_calendar_add_shift_btn);
        startPicker = (TimePicker) view.findViewById(R.id.app_calender_timepicker_start);
        endPicker = (TimePicker) view.findViewById(R.id.app_calender_timepicker_end);

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timePickerContainer.getVisibility() == View.GONE) {
                    timePickerContainer.setVisibility(View.VISIBLE);
                } else {
                    timePickerContainer.setVisibility(View.GONE);
                }
            }
        });
        addShiftBtn.setOnClickListener(this);
    }

    private void addEventToCal(Date start, Date end,
                          int color, String description, String location, String summary) {

        values = new ContentValues();
        values.put(CalendarProvider.COLOR, color);
        if(description!=null)
            values.put(CalendarProvider.DESCRIPTION, description);
        if(location!=null)
            values.put(CalendarProvider.LOCATION, location);
        if(summary!=null)
            values.put(CalendarProvider.EVENT, summary);
        Calendar cal = Calendar.getInstance();

        int startDayYear = start.getYear() + 1900;  //clickedDay.getYear();
        int startDayMonth = start.getMonth()+1;
        int startDayDay = start.getDay();
        int startTimeHour = start.getHours();
        int startTimeMin = start.getMinutes();
        int julianDay = julianDate(start);// clickedDay.getStartDay();

        int endDayYear = end.getYear() + 1900;
        int endDayMonth = end.getMonth()+1;
        int endDayDay = end.getDay();
        int endTimeHour = end.getHours();
        int endTimeMin = end.getMinutes();

        cal.set(startDayYear, startDayMonth, startDayDay, startTimeHour, startTimeMin);
        Log.d("START", cal.getTime().toString());
        values.put(CalendarProvider.START, cal.getTimeInMillis());

        values.put(CalendarProvider.START_DAY, julianDay);
        TimeZone tz = TimeZone.getDefault();

        cal.set(endDayYear, endDayMonth, endDayDay, endTimeHour, endTimeMin);
        int endDayJulian = julianDate(end); //Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));

        values.put(CalendarProvider.END, cal.getTimeInMillis());
        values.put(CalendarProvider.END_DAY, endDayJulian);
        uri = getActivity().getContentResolver().insert(CalendarProvider.CONTENT_URI, values);
        Log.d(TAG,"ADDED EVENT: " + values.toString());
    }

    public int julianDate(Date lDate) {
        Calendar lCal = Calendar.getInstance();
        lCal.setTime(lDate);
        int lYear = lCal.get(Calendar.YEAR);
        int lMonth = lCal.get(Calendar.MONTH) + 1;
        int lDay = lCal.get(Calendar.DATE);
        int a = (14 - lMonth) / 12;
        int y = lYear + 4800 - a;
        int m = lMonth + 12 * a - 3;
        Integer lJulianDate = lDay + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
        return lJulianDate;
    }

    private void SetSharedPrefences() {
        settings = getActivity().getSharedPreferences(getResources().getString(R.string.data),
                getContext().MODE_PRIVATE);
        settings = getActivity().getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.prefs), 0);
        setCookie(getResources().getString(R.string.setCookie));
        SetUser();
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
    public void onClick(View v) {
        Date start = new Date(clickedDay.getYear() - 1900,
                clickedDay.getMonth(),
                clickedDay.getDay(),
                startPicker.getCurrentHour(),
                startPicker.getCurrentMinute());
        Date end = new Date(clickedDay.getYear() - 1900,
                clickedDay.getMonth(),
                clickedDay.getDay(),
                endPicker.getCurrentHour(),
                endPicker.getCurrentMinute());
        addEventToCal(start, end, 12,
                user.getName() + " " + getResources().getString(R.string.shift),
                null, user.getName() + " " + getResources().getString(R.string.shift));
        //Send prop to server
        sendShiftToServer(start, end, user.getId(),user.getUserToken(), user.getName(), user.getEmail());
        calendar.refreshCalendar();
        timePickerContainer.setVisibility(View.GONE);
    }

    private void sendShiftToServer(Date start, Date end, String id,String deviceId, String name,String email) {
        String[] keys = {
                getResources().getString(R.string.key_calendar_id),
                getResources().getString(R.string.key_calendar_start),
                getResources().getString(R.string.key_calendar_end),
                getResources().getString(R.string.key_calendar_psycho_id),
                getResources().getString(R.string.key_calendar_psycho_deviceId),
                getResources().getString(R.string.key_calendar_psycho_name),
                getResources().getString(R.string.key_calendar_psycho_mail),
        };
        SendShift sendShift = new SendShift(this, getResources().getString(R.string.key_save_message), this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie), getActivity());

        String sendShiftUrl = getResources().getString(R.string.request_save_shift_url).toString();
        Shift shift = new Shift(new DateTime(start.getTime()).toString(),
                new DateTime(end.getTime()).toString()
                ,id,deviceId,name,email);
        sendShift.execute(new ShiftPack[]{new ShiftPack(sendShiftUrl, shift, keys)});
    }

    public void addEvent(com.google.api.services.calendar.model.Event event) {
        if(event!=null){
            Date start = null;
            Date end = null;
            int color = 0;
            String summary = null;
            String description = null;
            String location = null;
            if(event.getDescription()!= null && !event.getDescription().equals("")){
                description = event.getDescription();
            }
            if(event.getColorId()!= null && !event.getColorId().equals("")){
                color = Integer.valueOf(event.getColorId());
            }
            if(event.getSummary()!= null && !event.getSummary().equals("")){
                summary = event.getSummary();
            }
            if(event.getLocation()!= null && !event.getLocation().equals("")){
                location = event.getLocation();
            }
            if(event.getEnd()!= null && !event.getEnd().equals("")){
                if(event.getEnd().getDateTime()!=null){
                    end = new Date(event.getEnd().getDateTime().getValue());
                } else if (event.getEnd().getDate()!=null){
                    end = new Date(event.getStart().getDate().getValue());
                }
            }
            if(event.getStart()!= null && !event.getStart().equals("")){
                if(event.getStart()==null || event.getStart().getDateTime()==null){
                    if(event.getStart().getDate()!=null){
                        start = new Date(event.getStart().getDate().getValue());
                    } else if(event.getEnd().getDateTime()!=null){
                        start = new Date(event.getEnd().getDateTime().getValue());
                    }
                } else {
                    start = new Date(event.getStart().getDateTime().getValue());
                }
            }
            if(start != null && end != null && summary != null){
                addEventToCal(start, end, color, description, location, summary);
            }
        }

    }

    @Override
    public void OnFinished(String result) {
        Log.d("RECIEVED:", result);
        if(getActivity()==null) return;
        try {
            if (result.startsWith(getResources().getString(R.string.key_save_message))) {
                Toast.makeText(getActivity(), getResources().getString(R.string.shift_has_been_sent),
                        Toast.LENGTH_SHORT).show();
            } else if (result.startsWith(getResources().getString(R.string.key_get_shift))) {
                result = result.substring(13);
                if (result.equals(getResources().getString(R.string.empty))) return;
                saveShiftCollection(AddAllShifts(result));
            }
        } catch (JSONException e){
            Log.d(TAG, e.toString());
        }
    }

    private ShiftCollection AddAllShifts(String result) throws JSONException {
        JSONArray array = new JSONArray(result);
        for(int i = 0; i < array.length(); i++){
            JSONObject obj = array.getJSONObject(i);
            approvedShifts.add(DechipherShift(obj));
        }
        return approvedShifts;
    }

    private Shift DechipherShift(JSONObject obj) throws JSONException {
        return new Shift(obj.getString(getResources().getString(R.string.key_calendar_id)),
                obj.getString(getResources().getString(R.string.key_calendar_start)),
                obj.getString(getResources().getString(R.string.key_calendar_end)),
                obj.getString(getResources().getString(R.string.key_calendar_psycho_id)),
                obj.getString(getResources().getString(R.string.key_calendar_psycho_deviceId)),
                obj.getString(getResources().getString(R.string.key_calendar_psycho_name)),
                obj.getString(getResources().getString(R.string.key_calendar_psycho_mail)));
    }

    private void saveShiftCollection(ShiftCollection shiftCollection){
//        Log.d(TAG, shiftCollection.toString());
        SharedPreferences  mPrefs = getActivity().getSharedPreferences(getResources().getString(R.string.data),
                getActivity().MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(shiftCollection);
        prefsEditor.putString(getResources().getString(R.string.my_shifts), json).apply();
    }

    private ShiftCollection getShiftCollection(){
        ShiftCollection shiftCollection = null;
        SharedPreferences  mPrefs = getActivity().getSharedPreferences(getResources().getString(R.string.data),
                getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(getResources().getString(R.string.my_shifts), getResources().getString(R.string.empty));
        if(json.equals(getResources().getString(R.string.empty))) shiftCollection = new ShiftCollection();
        else {
            shiftCollection = gson.fromJson(json, ShiftCollection.class);
        }
        return shiftCollection;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyForebaseMessagingService.BROADCAST_ACTION);
        getActivity().registerReceiver(reciever, filter);
        SetApprovedShifts();
        Log.d(TAG, "onResume()");
    }

    private void SetApprovedShifts(){
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getResources().getString(R.string.data),
                getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(getResources().getString(R.string.my_shifts), getResources().getString(R.string.empty));
        if(json.equals(getResources().getString(R.string.empty))) approvedShifts = new ShiftCollection();
        else {
            approvedShifts = gson.fromJson(json, ShiftCollection.class);
        }
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(reciever);
        super.onPause();
    }
    class Reciever extends BroadcastReceiver implements Serializable{
        /**
         * When serviece broadcast, this method get the updates from server.
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(getResources().getString(R.string.fragment))
                    .equals(getResources().getString(R.string.app_fragment)) && isShown()) {
                if (intent.hasExtra(getResources().getString(R.string.key_help_child_id))
                        && intent.hasExtra(getResources().getString(R.string.key_help_request_num))
                        && intent.hasExtra(getResources().getString(R.string.key_help_child_name))) {
                    if (user.getAccount() == Account.PSYCHOLOGIST || user.getAccount() == Account.MANAGER) {
                        DisplayHelpOption(intent.getStringExtra(getResources().getString(R.string.key_help_request_num)),
                                intent.getStringExtra(getResources().getString(R.string.key_help_child_id)),
                                intent.getStringExtra(getResources().getString(R.string.key_help_child_name)));
                    }
                }
            }
            if(intent.getStringExtra(getResources().getString(R.string.fragment))
                    .equals(getResources().getString(R.string.calendar_fragment))){
                getShift(intent.getStringExtra(getResources().getString(R.string.id)));
            }
        }
    }


    private void DisplayHelpOption(final String num, final String newConversation, final String child_name) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getContext(), "YES", Toast.LENGTH_SHORT);
                        myActionBarActivity.setChatImmediateCall(num, newConversation,child_name);
                        sendAnsweredImmediateHelp(true,user.getId());
                        //notify server for statistics - num
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        sendAnsweredImmediateHelp(false, user.getId());
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

    private void getShift(String id){
        String key = getResources().getString(R.string.key_get_shift);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String getShiftUrl = getResources().getString(R.string.request_get_approved_shift_url)  + id;
        getEntity.execute(new String[]{getShiftUrl});
    }

    public void putShiftsOnCalendar() {
        for(Shift s: approvedShifts.getShifts()){
            Date start = new Date(new DateTime(s.getStart()).getValue());
            Date end = new Date(new DateTime(s.getEnd()).getValue());
            addEventToCal(start, end, 13,
                    getResources().getString(R.string.approved)
                    , null, user.getName() + " " + getResources().getString(R.string.shift));
        }
        calendar.refreshCalendar();
    }

    @Override
    public void Shown(Boolean first) {
        Log.d(TAG, "Shown()");
        if(!first){
            syncMonthWithGoogle();
            clearChatLabels();
        }
        this.first = first;
        shown = true;
    }

    private void clearChatLabels(){
        myActionBarActivity.setPeerStatus(getResources().getString(R.string.empty));
        myActionBarActivity.setPeerName(getResources().getString(R.string.empty));
    }

    @Override
    public void Hidden() {
        Log.d(TAG, "Hidden()");
        shown = false;
    }

    @Override
    public Boolean isShown() {
        return shown;
    }
}
