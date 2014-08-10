/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zizi;

import java.io.Serializable;
import java.util.Calendar;

import static java.util.Calendar.getInstance;

/**
 * The message sent between users in a chat.
 */
public abstract class Message implements Serializable {

    private final User fromUser;
    private final Calendar time;

    public Message(User fromUser) {
        this.fromUser = fromUser;
        this.time = getInstance();
    }

    public User getFromUser() {
        return fromUser;
    }

    public Calendar getTime() {
        return time;
    }

}
