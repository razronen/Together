package com.together.raz.together.Interfaces;

import android.support.v4.app.Fragment;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Raz on 1/10/2017.
 */
public interface FragmentsCreator extends Serializable{
    Fragment createFragment(int position);

    void putFragmentsNames(Map<Integer, TextView> labels);
}
