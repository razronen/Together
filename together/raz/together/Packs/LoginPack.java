package com.together.raz.together.Packs;

import com.together.raz.together.Entities.Login;

import java.util.HashMap;

/**
 * Created by Raz on 2/1/2017.
 */
public class LoginPack {
    private Login login;
    private String url;
    private String[] keys;

    public LoginPack(String url, Login login, String[] keys) {
        this.login = login;
        this.url = url;
        this.keys = keys;
    }

    /**
     * get method
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Creating hashmap from properties
     * @return the hashmap
     */
    public HashMap<String, String> Hash() {
        HashMap<String, String> hash = new HashMap<>();
        hash.put(keys[0], login.getEmail());
        hash.put(keys[1], login.getPass());
        return hash;
    }
}
