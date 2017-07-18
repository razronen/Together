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
public class Tabs4_Container extends Fragment implements View.OnClickListener {
    private static final String TAG = "...Tabs4_Container";
    private SectionsPagerAdapter mySectionsPagerAdapter;
    private static ViewPager viewPager;
    private Map<Integer,TextView> labels;
    private Map<Integer,LinearLayout> lines;
    private FragmentsCreator creator;
    private SharedPreferences settings;

    public Tabs4_Container() {
        // Required empty public constructor
    }

    public static Tabs4_Container newInstance(String key, FragmentsCreator creator){
        Tabs4_Container fragment = new Tabs4_Container();
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, (Serializable) creator);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onPause() {
        SharedPreferences.Editor edit = settings.edit();
        edit.putInt(getResources().getString(R.string.key_last_fragment_4container),
                viewPager.getCurrentItem()).apply();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentsCreator creator = (FragmentsCreator) getArguments().
                getSerializable(getResources().getString(R.string.fragment_creator_key));
        SetSharedPrefences();
        this.creator = creator;
        View view = inflater.inflate(R.layout.fragment_tab4__container, container, false);
        InitUI(view);
        mySectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), labels,
                lines,getContext(),creator);
        viewPager = (ViewPager) view.findViewById(R.id.tab4_cfp_container);
        viewPager.setAdapter(mySectionsPagerAdapter);

        setLastFragmentAppear();
        return view;
    }

    private void SetSharedPrefences() {
        settings = getActivity().getSharedPreferences(getResources().getString(R.string.data),
                getContext().MODE_PRIVATE);
        settings = getActivity().getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.prefs), 0);
    }

    private void setLastFragmentAppear() {
        switch(settings.getInt(getResources().getString(R.string.key_last_fragment_4container), 0)){
            case 0:
                viewPager.setCurrentItem(0, false);
                break;
            case 1:
                viewPager.setCurrentItem(1, false);
                break;
            case 2:
                viewPager.setCurrentItem(2, false);
                break;
            case 3:
                viewPager.setCurrentItem(3, false);
                break;
            default:;
        }
    }

    private void InitUI(View view) {
        labels = new HashMap<>();
        labels.put(0, (TextView) view.findViewById(R.id.tab4_label1_fragment_label));
        labels.put(1, (TextView) view.findViewById(R.id.tab4_label2_fragment_label));
        labels.put(2, (TextView) view.findViewById(R.id.tab4_label3_fragment_label));
        labels.put(3, (TextView) view.findViewById(R.id.tab4_label4_fragment_label));
        labels.get(0).setOnClickListener(this);
        labels.get(1).setOnClickListener(this);
        labels.get(2).setOnClickListener(this);
        labels.get(2).setOnClickListener(this);
        creator.putFragmentsNames(labels);
        lines = new HashMap<>();
        lines.put(0, (LinearLayout) view.findViewById(R.id.tab4_label1_line_label));
        lines.put(1, (LinearLayout) view.findViewById(R.id.tab4_label2_line_label));
        lines.put(2, (LinearLayout) view.findViewById(R.id.tab4_label3_line_label));
        lines.put(3, (LinearLayout) view.findViewById(R.id.tab4_label4_line_label));
    }

    @Override
    public void onClick(View v) {
        for(Map.Entry<Integer,TextView> entry: labels.entrySet()){
            if(entry.getValue() == v){
                viewPager.setCurrentItem(entry.getKey());
            }
        }
    }
}
