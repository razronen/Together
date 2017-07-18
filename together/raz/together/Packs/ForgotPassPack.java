package com.together.raz.together.Packs;

import com.together.raz.together.Entities.ForgotPass;

import java.util.HashMap;

/**
 * Created by Raz on 2/1/2017.
 */
public class ForgotPassPack {
    private ForgotPass forgotPass;
    private String url;
    private String[] keys;

    public ForgotPassPack(String url, ForgotPass forgotPass, String[] keys) {
        this.forgotPass = forgotPass;
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
        hash.put(keys[0], forgotPass.getEmail());
        hash.put(keys[1], forgotPass.getCode());
        return hash;
    }
}
