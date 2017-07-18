/*************************************************************
 * Raz Ronen
 * 201410669
 * 89-211-05
 ************************************************************/
package com.together.raz.together.Packs;

import com.together.raz.together.Entities.UserInfo;

import java.util.HashMap;

/**
 * holds data for post async task.
 */
public class UserPack {
    private UserInfo user;
    private String url;
    private String[] keys;

    public UserPack(String url, UserInfo user, String[] keys) {
        this.user = user;
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
        hash.put(keys[0], user.getId());
        hash.put(keys[1], user.getAccount().toString());
        hash.put(keys[2], user.getName());
        hash.put(keys[3], user.getIcon());
        hash.put(keys[4], user.getUserToken());
        hash.put(keys[5], user.getEmail());
        hash.put(keys[6], user.getPass());
        return hash;
    }
}
