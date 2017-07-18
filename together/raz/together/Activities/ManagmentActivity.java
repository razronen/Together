package com.together.raz.together.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.TextView;

import com.together.raz.together.Fragments.Quotes;
import com.together.raz.together.Fragments.Statistics;
import com.together.raz.together.Fragments.Tabs3_Container;
import com.together.raz.together.Fragments.Updates;
import com.together.raz.together.Interfaces.FragmentsCreator;
import com.together.raz.together.R;

import java.io.Serializable;
import java.util.Map;

public class ManagmentActivity extends MyActionBarActivity implements FragmentsCreator, Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managment);

        setFragment();
    }

    private void setFragment(){
        Fragment container = Tabs3_Container.newInstance(getResources().getString(R.string.fragment_creator_key),
                (FragmentsCreator) this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.manager_managment_cfp_coontainer, container);
        fragmentTransaction.commit();
    }

    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return Statistics.newInstance(this);
            case 1:
                return Quotes.newInstance(this);
            case 2:
                return Updates.newInstance(this);
            default:
                return Statistics.newInstance(this);
        }
    }

    @Override
    public void putFragmentsNames(Map<Integer, TextView> labels) {
        labels.get(0).setText(getResources().getString(R.string.statistics_fragment_label));
        labels.get(1).setText(getResources().getString(R.string.quotes_fragment_label));
        labels.get(2).setText(getResources().getString(R.string.update_fragment_label));
    }
}
