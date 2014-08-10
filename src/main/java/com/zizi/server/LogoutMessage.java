/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zizi.server;

/**
 * A Message sent when a user logs out of the application. This can be through session expired event, or user manually
 * logging out.
 */
public class LogoutMessage extends LoginMessage {

    public LogoutMessage(User fromUser) {
        super(fromUser);
    }

}
