package com.together.raz.together.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.together.raz.together.Entities.Post;
import com.together.raz.together.R;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 */
public class Comments extends Fragment {

    private View view;
    private ListView list;
    private Post post;

    public static Comments newInstance(String key, Post p){
        Comments fragment = new Comments();
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, (Serializable) p);
        fragment.setArguments(bundle);
        return fragment;
    }

    public Comments() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.post = (Post) getArguments().
                getSerializable(getResources().getString(R.string.fragment_creator_key));
        view = inflater.inflate(R.layout.fragment_comments, container, false);

        initUI(view);

        return view;
    }

    private void initUI(View view) {
    }
}
