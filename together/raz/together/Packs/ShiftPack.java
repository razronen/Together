package com.together.raz.together.Packs;

import com.together.raz.together.Entities.Shift;

import java.util.HashMap;

/**
 * Created by Raz on 3/16/2017.
 */
public class ShiftPack {
    private static final String TAG = "ShiftPack";
    private Shift shift;
    private String url;

    public String getUrl() {
        return url;
    }

    private String[] keys;

    public ShiftPack(String url, Shift shift, String[] keys) {
        this.shift = shift;
        this.url = url;
        this.keys = keys;
    }

    public HashMap<String, String> Hash() {
        HashMap<String, String> hash = new HashMap<>();
        hash.put(keys[1], shift.getStart());
        hash.put(keys[2], shift.getEnd());
        hash.put(keys[3], shift.getPsycho_id());
        hash.put(keys[4], shift.getPsycho_deviceId());
        hash.put(keys[5], shift.getPsycho_name());
        hash.put(keys[6], shift.getPsycho_mail());
        return hash;
    }
}
