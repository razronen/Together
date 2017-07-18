package com.together.raz.together.Packs;

import com.together.raz.together.Entities.HelpRequest;

import java.util.HashMap;

/**
 * Created by Raz on 2/1/2017.
 */
public class HelpRequestPack {
    private HelpRequest helpRequest;
    private String url;
    private String[] keys;

    public HelpRequestPack(String url, HelpRequest helpRequest, String[] keys) {
        this.helpRequest = helpRequest;
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
        hash.put(keys[0], helpRequest.getTime());
        hash.put(keys[1], helpRequest.getChild_id());
        hash.put(keys[2], helpRequest.getChild_name());
        hash.put(keys[3], helpRequest.getMessage());
        return hash;
    }
}
