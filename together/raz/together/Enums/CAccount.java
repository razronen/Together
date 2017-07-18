package com.together.raz.together.Enums;

/**
 * Created by Raz on 3/23/2017.
 */
public class CAccount {
    Account acount;

    public CAccount(String val) {
        switch(val){
            case "Child": acount = Account.CHILD;
                break;
            case "Psychologist": acount = Account.PSYCHOLOGIST;
                break;
            case "Manager": acount = Account.MANAGER;
                break;
            case "Developer": acount = Account.DEVELOPER;
                break;
            default: acount = Account.CHILD;
        }
    }

    @Override
    public String toString() {
        return acount.toString();
    }

    public Account getAcount() {
        return acount;
    }

    public void setAcount(Account acount) {
        this.acount = acount;
    }
}
