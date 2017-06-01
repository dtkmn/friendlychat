package com.google.firebase.codelab.friendlychat.entity;

import java.io.Serializable;

/**
 * Created by d623267 on 31/05/2017.
 */

public class ReceivedMessage implements Serializable {

//    private String from;
//    private String title;
//    private String body;

    private String ticketNo;
    private String jobType;
    private String address;
    private String description;

    public ReceivedMessage(String ticketNo,
                           String jobType, String address, String description) {
//        this.from = from;
//        this.title = title;
//        this.body = body;
        this.ticketNo = ticketNo;
        this.jobType = jobType;
        this.address = address;
        this.description = description;
    }

//    public String getFrom() {
//        return from;
//    }
//
//    public void setFrom(String from) {
//        this.from = from;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getBody() {
//        return body;
//    }
//
//    public void setBody(String body) {
//        this.body = body;
//    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
