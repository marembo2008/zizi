/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zizi;

/**
 * A message sent when a user logs into the chat application.
 */
public class LoginMessage extends Message {

    public LoginMessage(User fromUser) {
        super(fromUser);
    }

}
