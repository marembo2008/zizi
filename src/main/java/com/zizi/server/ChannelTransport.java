/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zizi.server;

import java.io.Serializable;

/**
 * This is like the traffic polic. It knows how and where to route a message.
 *
 * A ChannelTransport typically connects two users who are in a chat, and periodically sends messages to the chat app on
 * either side, informing them of states.
 */
public class ChannelTransport implements Serializable {

    /**
     * The two users who are connected in this chat.
     */
    private final UserConnection connectedUser1;
    private final UserConnection connectedUser2;

    public ChannelTransport(UserConnection connectedUser1, UserConnection connectedUser2) {
        this.connectedUser1 = connectedUser1;
        this.connectedUser2 = connectedUser2;
    }

    public UserConnection getConnectedUser2() {
        return connectedUser2;
    }

    public UserConnection getConnectedUser1() {
        return connectedUser1;
    }

}
