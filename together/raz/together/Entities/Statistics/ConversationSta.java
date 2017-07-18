package com.together.raz.together.Entities.Statistics;

/**
 * Created by Raz on 4/20/2017.
 */
public class ConversationSta {
    private String peer = null;
    private String msg = null;

    public ConversationSta(String peer, String msg) {
        this.peer = peer;
        this.msg = msg;
    }

    public String getPeer() {
        return peer;
    }

    public void setPeer(String peer) {
        this.peer = peer;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
