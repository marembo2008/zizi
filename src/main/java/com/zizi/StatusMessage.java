/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zizi;

/**
 * This message is a channel communication. It indicates the current status of the channel.
 */
public class StatusMessage extends CommunicationMessage {

    public static enum Status {

        ONLINE,
        OFFLINE,
        TYPING,
        MESSAGE_SENT,
        MESSAGE_SEEN,
        MESSAGE_DELETED;
    }
    private final Status status;

    public StatusMessage(Status status, User fromUser, User toUser) {
        super(fromUser, toUser);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

}
