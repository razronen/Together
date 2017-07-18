package com.together.raz.together.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.together.raz.together.R;
import com.tyczj.extendedcalendarview.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raz on 4/1/2017.
 */
public class CalendarEventsAdapter extends BaseAdapter{
    private static final String TAG = "CalendarEventsAdapter";
    private final LayoutInflater inflator;
    private Activity activity;
    private List<Event> eventList = new ArrayList<>();

    public CalendarEventsAdapter(Activity activity, ArrayList eventsList) {
        this.eventList = eventList;
        this.activity = activity;
        this.inflator = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
            convertView = this.inflator.inflate(R.layout.calendar_event_item_list, null);
        Event event = eventList.get(position);
        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.calendar_item_container);
        TextView summary = (TextView) convertView.findViewById(R.id.calendar_item_summary);
        TextView time = (TextView) convertView.findViewById(R.id.calendar_item_time);
        TextView location = (TextView) convertView.findViewById(R.id.calendar_item_location);
        TextView description = (TextView) convertView.findViewById(R.id.calendar_item_description);
        LinearLayout containerSum = (LinearLayout) convertView.findViewById(R.id.calendar_item_summary_container);
        LinearLayout containerTim = (LinearLayout) convertView.findViewById(R.id.calendar_item_time_container);
        LinearLayout containerLoc = (LinearLayout) convertView.findViewById(R.id.calendar_item_location_container);
        LinearLayout containerDes = (LinearLayout) convertView.findViewById(R.id.calendar_item_description_container);




        if(summary != null && event.getTitle()!=null) {
            summary.setText(event.getTitle().toString());
        } else if(containerSum!= null){
            containerSum.setVisibility(View.GONE);
        }
        if(location != null && event.getLocation()!=null) {
            location.setText(event.getLocation().toString());
        } else if(containerLoc != null){
            location.setText("");
//            containerLoc.setVisibility(View.INVISIBLE);
        }
        if(description != null && event.getDescription()!=null){
            description.setText(event.getDescription().toString());
        } else if(containerDes != null){
            containerDes.setVisibility(View.GONE);
        }
        if(time != null){
            String dateFormat = "HH:mm";
            if(event.getStartDate(dateFormat)!=null && event.getEndDate(dateFormat)!=null){
                time.setText(event.getStartDate(dateFormat) + "-" + event.getEndDate(dateFormat));
            } else if (event.getStartDate(dateFormat)!=null){
                time.setText(event.getStartDate(dateFormat) + "..");
            } else if (event.getEndDate(dateFormat)!=null){
                time.setText(".." + event.getEndDate(dateFormat));
            }
        } else if(containerTim != null) {
            containerTim.setVisibility(View.GONE);
        }
        switch(event.getColor()){
            case 0:
            case 6:
                break;
            case 1:
            case 7:
                if(container!=null)
                    container.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.calendar_item_1));
                break;
            case 2:
            case 8:
                if(container!=null)
                    container.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.calendar_item_2));
                break;
            case 3:
            case 9:
                if(container!=null)
                    container.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.calendar_item_3));
                break;
            case 4:
            case 10:
                if(container!=null)
                    container.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.calendar_item_4));
                break;
            case 5:
            case 11:
                if(container!=null)
                    container.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.calendar_item_5));
                break;
            case 12:
                if(container!=null)
                    container.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.calendar_item_6));
                break;
            case 13:
                if(container!=null)
                    container.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.calendar_item_7));
                break;
            default:;
        }
        return convertView;
    }

    public void clear() {
        eventList.clear();
    }

    public void setList(ArrayList<Event> list) {
        this.eventList = list;
    }
}
