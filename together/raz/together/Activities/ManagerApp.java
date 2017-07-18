package com.together.raz.together.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.TextView;

import com.together.raz.together.Fragments.Calender;
import com.together.raz.together.Fragments.Chat;
import com.together.raz.together.Fragments.Posts;
import com.together.raz.together.Fragments.Tabs3_Container;
import com.together.raz.together.Interfaces.FragmentsCreator;
import com.together.raz.together.R;

import java.io.Serializable;
import java.util.Map;

public class ManagerApp extends MyActionBarActivity implements FragmentsCreator, Serializable{

    private transient Tabs3_Container container = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_app);
        setFragment();
    }

    private void setFragment(){
        container = Tabs3_Container.newInstance(getResources().getString(R.string.fragment_creator_key),
                (FragmentsCreator) this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.manager_app_cfp_coontainer, container);
        fragmentTransaction.commit();
    }

    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return Chat.newInstance(this);
            case 1:
                return Calender.newInstance(this);
            case 2:
                return Posts.newInstance(this);
            default:
                return new Chat();
        }
    }

    @Override
    public void setChatImmediateCall(String num, String id, String name){
        container.setChatImmediatCall(num,id,name);
        setConversation(id);
    }

    @Override
    public void putFragmentsNames(Map<Integer, TextView> labels) {
        labels.get(0).setText(getResources().getString(R.string.chat_label));
        labels.get(1).setText(getResources().getString(R.string.calender_label));
        labels.get(2).setText(getResources().getString(R.string.posts_label));
    }
}
