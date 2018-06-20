package com.google.firebase.codelab.friendlychat.entity;

public class DeliveryStatus {

//    {
//        "uuid": "sdsdsdsdfsdfsdf",
//            "notificationStatus": "DISPLAY"
//    }

    private String uuid;
    private String notificationStatus;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

}
