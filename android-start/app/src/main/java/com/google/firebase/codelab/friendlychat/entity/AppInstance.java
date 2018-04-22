package com.google.firebase.codelab.friendlychat.entity;

/**
 * Created by dtkmn on 20/04/2018.
 */

public class AppInstance {

//    {
//        "pushNotificationToken": "DT-token-00004",
//            "deviceDetails": "iPad",
//            "operationSystemDetails":"IOS 11",
//            "appType": "24x7",
//            "appDetails": "0.1",
//            "pushNotificationPlatformType": "FCM"
//    }

    private String pushNotificationToken;
    private String deviceDetails;
    private String operationSystemDetails;
    private String appType;
    private String appDetails;
    private String pushNotificationPlatformType;

    public String getPushNotificationToken() {
        return pushNotificationToken;
    }

    public void setPushNotificationToken(String pushNotificationToken) {
        this.pushNotificationToken = pushNotificationToken;
    }

    public String getDeviceDetails() {
        return deviceDetails;
    }

    public void setDeviceDetails(String deviceDetails) {
        this.deviceDetails = deviceDetails;
    }

    public String getOperationSystemDetails() {
        return operationSystemDetails;
    }

    public void setOperationSystemDetails(String operationSystemDetails) {
        this.operationSystemDetails = operationSystemDetails;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppDetails() {
        return appDetails;
    }

    public void setAppDetails(String appDetails) {
        this.appDetails = appDetails;
    }

    public String getPushNotificationPlatformType() {
        return pushNotificationPlatformType;
    }

    public void setPushNotificationPlatformType(String pushNotificationPlatformType) {
        this.pushNotificationPlatformType = pushNotificationPlatformType;
    }

}
