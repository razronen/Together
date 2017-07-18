package com.together.raz.together.Activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.together.raz.together.AsyncTasks.GetEntity;
import com.together.raz.together.AsyncTasks.SendUser;
import com.together.raz.together.Entities.UserInfo;
import com.together.raz.together.Enums.Account;
import com.together.raz.together.Fragments.CharChooser;
import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Packs.UserPack;
import com.together.raz.together.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements AsyncResponse, Cookied, View.OnClickListener {

    private static final String TAG = "RegistrationActivity";
    private SharedPreferences settings;
    private EditText firstNameEditText = null;
    private EditText lastNameEditText = null;
    private EditText emailEditText = null;
    private EditText passEditText = null;
    private Boolean validEmail = false;
    private Button submitBtn = null;
    private String entity = null;
    private String token = null;
    private TextView exitText = null;

    private View.OnFocusChangeListener emailListener = null;
    private View.OnFocusChangeListener passsListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initSharedRefences();
        initListeners();
        initUI();


    }

    private void initListeners() {
        emailListener = new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) return;
                CheckIfEmailExists(emailEditText.getText().toString());
            }
        };
        passsListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) return;
                if(passEditText.getText().toString().length()<8){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.regist_pass_more_than),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };


    }

    private void CheckIfEmailExists(String email) {
        String key = getResources().getString(R.string.key_get_exists);
        GetEntity getEntity = new GetEntity(this,key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie));
        String emailExistsUrl = getResources().getString(R.string.request_get_if_email_exists_url)
                + email;
        getEntity.execute(new String[]{emailExistsUrl});
    }

    private void initSharedRefences() {
        settings = getSharedPreferences(getResources().getString(R.string.data),MODE_PRIVATE);
        settings = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.prefs), 0);
        setCookie(getResources().getString(R.string.setCookie));

        entity = getIntent().getStringExtra(getResources().getString(R.string.key_user_entity));
        token = getIntent().getStringExtra(getResources().getString(R.string.key_user_code));
    }


    private void initUI() {
        SetCharFragment();
        String firstName = getIntent().getStringExtra(getResources().getString(R.string.key_first_name));
        String lastName = getIntent().getStringExtra(getResources().getString(R.string.key_last_name));
        firstNameEditText = (EditText) findViewById(R.id.regist_first_name);
        lastNameEditText = (EditText) findViewById(R.id.regist_last_name);
        emailEditText = (EditText) findViewById(R.id.regist_email);
        passEditText = (EditText) findViewById(R.id.regist_password);
        submitBtn = (Button) findViewById(R.id.regist_submit_btn);
        exitText = (TextView) findViewById(R.id.regist_exit);

        exitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                intent.putExtra(getResources().getString(R.string.check_approve), false);
                startActivity(intent);
            }
        });


        pullDataFromIntent();

        if(firstNameEditText!=null && firstName !=null) firstNameEditText.setText(firstName);
        if(lastNameEditText!=null && lastName !=null) lastNameEditText.setText(lastName);
        emailEditText.setOnFocusChangeListener(emailListener);
        passEditText.setOnFocusChangeListener(passsListener);
        submitBtn.setOnClickListener(this);
    }

    private void pullDataFromIntent() {
        Intent intent = getIntent();
        if(intent.hasExtra(getResources().getString(R.string.key_user_first_name))){
            firstNameEditText.setText(intent.getStringExtra(getResources().getString(R.string.key_user_first_name)));
        }
        if(intent.hasExtra(getResources().getString(R.string.key_user_last_name))){
            lastNameEditText.setText(intent.getStringExtra(getResources().getString(R.string.key_user_last_name)));
        }
        if(intent.hasExtra(getResources().getString(R.string.key_entity))){
            entity = intent.getStringExtra(getResources().getString(R.string.key_entity));
        }
        if(intent.hasExtra(getResources().getString(R.string.key_user_code))){
            token = intent.getStringExtra(getResources().getString(R.string.key_user_code));
        }
    }

    private void SetCharFragment(){
        // Create new fragment and transaction
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.registration_char_chooser_frame, new CharChooser());
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void OnFinished(String result) {
        Log.d(TAG, "RESULT:" + result);
        try {
            if(result.startsWith(getResources().getString(R.string.key_get_exists))){
                result = result.substring(14);
                if(result.equals(getResources().getString(R.string.regist_exits))){
                    validEmail = false;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.regist_this_email_is_regist),
                            Toast.LENGTH_SHORT).show();
                } else if(result.equals(getResources().getString(R.string.regists_not_exists))){
                    validEmail = true;
                }
            } else if(result.startsWith(getResources().getString(R.string.key_send_user))){
                result = result.substring(13);
                if(result.equals(getResources().getString(R.string.empty))) return;
                Account account = SaveToSharedPrefences(result);
                if(account==Account.CHILD){
                    startActivity(new Intent(this, ChildActivity.class));
                } else if(account==Account.PSYCHOLOGIST){
                    startActivity(new Intent(this, PsychoActivity.class));
                } else if(account==Account.MANAGER || account==Account.DEVELOPER){
                    startActivity(new Intent(this, ManagerActivity.class));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Account SaveToSharedPrefences(String result) throws JSONException {
        JSONObject obj = new JSONObject(result);
        UserInfo userInfo= DecihperUserInfo(obj);
        Gson gson = new Gson();
        String user_json = gson.toJson(userInfo);
        settings.edit().putString(getResources().getString(R.string.key_user), user_json).apply();
        settings.edit().putString(getResources().getString(R.string.account_type),userInfo.getAccount().toString()).apply();
        Log.d(TAG, "SAVED USER: " + userInfo.toString());
        return userInfo.getAccount();
    }

    private UserInfo DecihperUserInfo(JSONObject obj) throws JSONException {
        return new UserInfo(obj.getString(getResources().getString(R.string.key_id)),
                obj.getString(getResources().getString(R.string.key_entity)),
                obj.getString(getResources().getString(R.string.key_name)),
                obj.getString(getResources().getString(R.string.key_icon)),
                obj.getString(getResources().getString(R.string.key_user_deviceId)),
                obj.getString(getResources().getString(R.string.key_user_email)),
                obj.getString(getResources().getString(R.string.key_user_pass)));
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
        if(!validEmail){
            if(!isEmailValid(emailEditText.getText().toString())){
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.regist_insert_valid_email),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.regist_this_email_is_regist),
                        Toast.LENGTH_SHORT).show();
            }
            return;
        } else if(passEditText.getText().toString().length()<=7){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.regist_pass_more_than),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        CreateUser();
    }

    private void CreateUser() {
        String keys[] = {
                getResources().getString(R.string.key_user_id),
                getResources().getString(R.string.key_user_entity),
                getResources().getString(R.string.key_user_name),
                getResources().getString(R.string.key_user_icon),
                getResources().getString(R.string.key_user_deviceId),
                getResources().getString(R.string.key_user_email),
                getResources().getString(R.string.key_user_pass)
        };
        String name = firstNameEditText.getText().toString() + " " + lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String image = settings.getString(getResources().getString(R.string.key_user_character), null);
        String password = passEditText.getText().toString();
        settings.edit().putString(getResources().getString(R.string.user_pass), password);
        Log.d(TAG,entity);
        UserInfo user = new UserInfo("",entity,name,image, token, email, password);
        String key = getResources().getString(R.string.key_send_user);
        SendUser sendUser = new SendUser(this, key,this,
                getResources().getString(R.string.getcookie),
                getResources().getString(R.string.setCookie), this);
        String sendUserUrl = getResources().getString(R.string.request_send_user_url);
        sendUser.execute(new UserPack[]{new UserPack(sendUserUrl,user,keys)});

    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
