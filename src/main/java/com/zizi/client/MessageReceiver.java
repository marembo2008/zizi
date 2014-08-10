package com.zizi.client;

import com.zizi.server.TextMessage;

/**
 * It only knows how to display the message.
 */
public interface MessageReceiver {

    void messageReceived(final TextMessage message);
}
