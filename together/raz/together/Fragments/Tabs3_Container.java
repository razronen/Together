package com.together.raz.together.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.together.raz.together.Adapters.SectionsPagerAdapter;
import com.together.raz.together.Interfaces.FragmentsCreator;
import com.together.raz.together.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tabs3_Container extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener, Serializable {
    private static final String TAG = "...Tabs3_Container";
    private SectionsPagerAdapter mySectionsPagerAdapter;
    private static ViewPager viewPager;
    private Map<Integer,TextView> labels;
    private Map<Integer,LinearLayout> lines;
    private FragmentsCreator creator;
    private SharedPreferences settings;
    private Integer shown = null;

    @Override
    public void onPause() {
        SharedPreferences.Editor edit = settings.edit();
        edit.putInt(getResources().getString(R.string.key_last_fragment_3container),
                viewPager.getCurrentItem()).apply();
        super.onPause();
    }

    public Tabs3_Container() {
        // Required empty public constructor
    }

    private void SetSharedPrefences() {
        settings = getActivity().getSharedPreferences(getResources().getString(R.string.data),
                getContext().MODE_PRIVATE);
        settings = getActivity().getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.prefs), 0);
    }

    public static Tabs3_Container newInstance(String key, FragmentsCreator creator){
        Tabs3_Container fragment = new Tabs3_Container();
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, (Serializable) creator);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentsCreator creator = (FragmentsCreator) getArguments().
                getSerializable(getResources().getString(R.string.fragment_creator_key));
        SetSharedPrefences();
        this.creator = creator;
        View view = inflater.inflate(R.layout.fragment_tab3__container, container, false);
        InitUI(view);
        mySectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), labels,
                lines,getContext(),creator);
        viewPager = (ViewPager) view.findViewById(R.id.tab3_cfp_container);
        viewPager.setAdapter(mySectionsPagerAdapter);
        viewPager.setOnPageChangeListener(this);
        setLastFragmentAppear();
        return view;
    }

    private void setLastFragmentAppear() {
        switch(settings.getInt(getResources().getString(R.string.key_last_fragment_3container), 0)){
            case 0:
                viewPager.setCurrentItem(0, false);
                break;
            case 1:
                viewPager.setCurrentItem(1, false);
                break;
            case 2:
                viewPager.setCurrentItem(2, false);
                break;
            default:;
        }
    }

    private void InitUI(View view) {
        labels = new HashMap<>();
        labels.put(0, (TextView) view.findViewById(R.id.tab3_label1_fragment_label));
        labels.put(1, (TextView) view.findViewById(R.id.tab3_label2_fragment_label));
        labels.put(2, (TextView) view.findViewById(R.id.tab3_label3_fragment_label));
        labels.get(0).setOnClickListener(this);
        labels.get(1).setOnClickListener(this);
        labels.get(2).setOnClickListener(this);
        creator.putFragmentsNames(labels);
        lines = new HashMap<>();
        lines.put(0, (LinearLayout) view.findViewById(R.id.tab3_label1_line_label));
        lines.put(1, (LinearLayout) view.findViewById(R.id.tab3_label2_line_label));
        lines.put(2, (LinearLayout) view.findViewById(R.id.tab3_label3_line_label));
    }

    @Override
    public void onClick(View v) {
        for(Map.Entry<Integer,TextView> entry: labels.entrySet()){
            if(entry.getValue() == v){
                viewPager.setCurrentItem(entry.getKey());
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(shown!=null) mySectionsPagerAdapter.Hidden(shown);
        shown = position;
        mySectionsPagerAdapter.Shown(position);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setChatImmediatCall(String num, String id, String name) {
        viewPager.setCurrentItem(0);
    }
}
