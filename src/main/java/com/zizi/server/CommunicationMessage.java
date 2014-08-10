/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zizi.server;

import java.util.Objects;

/**
 * This message is intended for two users.
 *
 * There must be a recipient and a sender.
 */
public abstract class CommunicationMessage extends Message {

    private final User toUser;

    public CommunicationMessage(User fromUser, User toUser) {
        super(fromUser);
        this.toUser = toUser;
    }

    public User getToUser() {
        return toUser;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(this.toUser);
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
        final CommunicationMessage other = (CommunicationMessage) obj;
        return super.equals(obj) && Objects.equals(this.toUser, other.toUser);
    }

    @Override
    public String toString() {
        return super.toString() + ", toUser=" + toUser;
    }
}
