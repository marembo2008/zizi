/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zizi;

/**
 * The actual message sent to the user. This is the message actually typed by the user on the screen.
 */
public class TextMessage extends CommunicationMessage {

    private final String text;

    public TextMessage(String text, User fromUser, User toUser) {
        super(fromUser, toUser);
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
