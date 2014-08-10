/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zizi.server;

/**
 * Notified when a new message has been received.
 *
 * The listener must check if it is capable of handling the message.
 */
public interface OnMessageListener {

    void onMessageReceived(final Message message);
}
