package com.together.raz.together.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.together.raz.together.R;

import java.util.HashMap;
import java.util.Map;

public class RegistrationTemporaryActivity extends MyActionBarActivity {

    private Map<String, Button> btns = new HashMap<>();
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_temp);

        InitUI();
    }

    private void InitUI() {
        btns.put(getResources().getString(R.string.child),
                (Button) findViewById(R.id.child_registration_btn));
        btns.put(getResources().getString(R.string.psycho),
                (Button) findViewById(R.id.psycho_registration_btn));
        btns.put(getResources().getString(R.string.manager),
                (Button) findViewById(R.id.manager_registration_btn));

        AddIntentToOnClick(btns.get(getResources().getString(R.string.child)),
                new Intent(RegistrationTemporaryActivity.this, ChildActivity.class),
                getResources().getString(R.string.child_acount));
        AddIntentToOnClick(btns.get(getResources().getString(R.string.psycho)),
                new Intent(RegistrationTemporaryActivity.this, PsychoActivity.class),
                getResources().getString(R.string.psycho_acount));
        AddIntentToOnClick(btns.get(getResources().getString(R.string.manager)),
                new Intent(RegistrationTemporaryActivity.this, ManagerActivity.class),
                getResources().getString(R.string.manager_acount));
    }

    private void AddIntentToOnClick(Button btn, final Intent intent, final String prefencesVal){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteToSharedPrefences(getResources().getString(R.string.account_type),prefencesVal);
                startActivity(intent);
            }
        });
    }

    private void WriteToSharedPrefences(String key, String value){
        sharedPreferences = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key,value);
        edit.apply();

    }
}
