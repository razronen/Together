package com.together.raz.together.Entities;

import com.together.raz.together.Enums.Account;
import com.together.raz.together.Enums.CAccount;

/**
 * Created by Raz on 2/1/2017.
 */
public class UserInfo {
    private String id;
    private CAccount account;
    private String name;
    private String userToken;

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    private String pass;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setAccount(CAccount account) {
        this.account = account;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public UserInfo(String id, String account, String name, String icon, String userToken, String email, String password) {

        this.id = id;
        this.account = new CAccount(account);
        this.name = name;
        this.icon = icon;
        this.userToken = userToken;
        this.email = email;
        this.pass = password;
    }

    private String icon;

    public Account getAccount() {

        return account.getAcount();
    }

    public void setAccount(Account account) {
        this.account.setAcount(account);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
