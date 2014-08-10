/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zizi;

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

}
