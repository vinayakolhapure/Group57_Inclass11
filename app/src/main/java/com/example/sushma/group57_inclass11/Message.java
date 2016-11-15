package com.example.sushma.group57_inclass11;

import java.util.Date;

/**
 * Created by Vinayak on 11/14/2016.
 */
public class Message {

    String messageText;
    Date chatDate;
    String fname;
    String lname;
    String thumbNail;

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Date getChatDate() {
        return chatDate;
    }

    public void setChatDate(Date chatDate) {
        this.chatDate = chatDate;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public void setThumbNail(String thumbNail) {
        this.thumbNail = thumbNail;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageText='" + messageText + '\'' +
                ", chatDate=" + chatDate +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", thumbNail='" + thumbNail + '\'' +
                '}';
    }
}
