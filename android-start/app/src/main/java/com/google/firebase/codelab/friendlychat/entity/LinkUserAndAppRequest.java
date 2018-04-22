package com.google.firebase.codelab.friendlychat.entity;

/**
 * Created by dtkmn on 20/04/2018.
 */

public class LinkUserAndAppRequest {

    //{
    //  "username": "jigar1010@test.com",
    //  "pushNotificationToken": "DT-token-00005",
    //  "appInstanceNickname": "Someone",
    //  "active": true
    //}

    private String pushNotificationToken;
    private String username;
    private String appInstanceNickname;
    private boolean active;

    public String getPushNotificationToken() {
        return pushNotificationToken;
    }

    public void setPushNotificationToken(String pushNotificationToken) {
        this.pushNotificationToken = pushNotificationToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAppInstanceNickname() {
        return appInstanceNickname;
    }

    public void setAppInstanceNickname(String appInstanceNickname) {
        this.appInstanceNickname = appInstanceNickname;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
