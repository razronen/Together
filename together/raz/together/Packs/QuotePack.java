package com.together.raz.together.Packs;

import com.together.raz.together.Entities.Quote;

import java.util.HashMap;

/**
 * Created by Raz on 2/1/2017.
 */
public class QuotePack {
    private Quote quote;
    private String url;
    private String[] keys;

    public QuotePack(String url, Quote quote, String[] keys) {
        this.quote = quote;
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
        hash.put(keys[0], quote.getNum());
        hash.put(keys[1], quote.getContent());
        hash.put(keys[2], quote.getCreator());
        return hash;
    }
}
