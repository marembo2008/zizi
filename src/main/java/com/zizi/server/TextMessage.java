/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zizi.server;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(this.text);
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
        final TextMessage other = (TextMessage) obj;
        return super.equals(obj) && Objects.equals(this.text, other.text);
    }

    @Override
    public String toString() {
        return super.toString() + ", text=" + text;
    }

}
