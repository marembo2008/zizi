/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zizi.server;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import javax.swing.text.DateFormatter;

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

    @Override
    public String toString() {
        try {
            return "fromUser=" + fromUser + ", "
                    + "time=" + new DateFormatter(new SimpleDateFormat("yyyy, MM dd HH:mm:ss")).valueToString(time
                            .getTime());
        } catch (ParseException ex) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.fromUser);
        hash = 97 * hash + Objects.hashCode(this.time);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Message other = (Message) obj;
        if (!Objects.equals(this.fromUser, other.fromUser)) {
            return false;
        }
        return Objects.equals(this.time, other.time);
    }

}
