package com.zizi.client;

import com.zizi.server.User;

/**
 * The sender knows whom the message is meant for, and from whom it is coming from.
 */
public interface MessageSender {

    void sendMessage(final String message);

    /**
     * The current logged in user.
     *
     * @return
     */
    User getCurrentUser();
}
