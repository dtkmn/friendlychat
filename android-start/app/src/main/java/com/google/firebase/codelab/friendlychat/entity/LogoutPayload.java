package com.google.firebase.codelab.friendlychat.entity;

public class LogoutPayload {
    //        {
//            "username": "jigar1010@test.com",
//                "pushNotificationToken": "DT-token-00004324",
//                "appInstanceNickname": "Someone"
//        }

    private String username;
    private String pushNotificationtoken;
    private String appInstanceNickname;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPushNotificationtoken() {
        return pushNotificationtoken;
    }

    public void setPushNotificationtoken(String pushNotificationtoken) {
        this.pushNotificationtoken = pushNotificationtoken;
    }

    public String getAppInstanceNickname() {
        return appInstanceNickname;
    }

    public void setAppInstanceNickname(String appInstanceNickname) {
        this.appInstanceNickname = appInstanceNickname;
    }

}
