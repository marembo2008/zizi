package com.zizi;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Calendar.getInstance;

/**
 * User's are always connected to the server.
 */
public class UserConnection implements Serializable, OnMessageListener {

    private static final Logger LOG = Logger.getLogger(UserConnection.class.getName());

    private final User user;
    private final Socket connection;
    private final Calendar loginTime;
    private final Set<OnMessageListener> onMessageListeners = new HashSet<>();
    private volatile boolean running;

    //The user this user is chatting with.
    private final Map<User, UserConnection> connectedUser = new HashMap<>();
    //A cache of the most recent message that has been sent.
    //We use this to avoid sending the message twice, especially when the connection is done between two users by the connection manager.
    private Message recentMessageSent;
    private Message recentMessageReceived;

    public UserConnection(User user, Socket connection, Calendar loginTime) {
        this.user = user;
        this.connection = connection;
        this.loginTime = loginTime;
        this.running = true;
        listenForMessages();
    }

    //Registers an on Message Listener to this user connection.
    public void addOnMessageListener(final OnMessageListener onMessageListener) {
        this.onMessageListeners.add(onMessageListener);
    }

    public void removeOnMessageListener(final OnMessageListener onMessageListener) {
        this.onMessageListeners.remove(onMessageListener);
    }

    //Visble for testing
    Message getRecentMessageSent() {
        return recentMessageSent;
    }

    //Visible for testing
    Message getRecentMessageReceived() {
        return recentMessageReceived;
    }

    private void listenForMessages() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                processInCommingMessages();
            }
        }).start();
    }

    private void processInCommingMessages() {
        try {
            while (running) {
                InputStream inn = connection.getInputStream();
                ObjectInputStream objInn = new ObjectInputStream(inn);
                Message message = recentMessageReceived = (Message) objInn.readObject();
                LOG.log(Level.INFO, "Received message: {0}='{'{1}'}'", new Object[]{message.getClass().getName(),
                                                                                    message});

                for (OnMessageListener onMessageListener : onMessageListeners) {
                    onMessageListener.onMessageReceived(message);
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(UserConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            running = false;
        }
    }

    public UserConnection findUserConnection(final User user) {
        return connectedUser.get(user);
    }

    public void addUserConnection(final UserConnection userConnection) {
        connectedUser.put(userConnection.getUser(), userConnection);
    }

    public void removeUserConnection(final User user) {
        connectedUser.remove(user);
    }

    /**
     * Returns all users the current connection is aware of.
     *
     * @return
     */
    public Iterable<UserConnection> findAllUserConnections() {
        return connectedUser.values();
    }

    @Override
    public void onMessageReceived(final Message message) {
        //We have received message from one of our connected peers, we simply forward it to the user.
        //We only forward automatically, text and status messages.
        if (message instanceof CommunicationMessage) {
            CommunicationMessage commMessage = (CommunicationMessage) message;
            //We could be forwarded someones else messages. Simply ignore them.
            if (commMessage.getToUser().equals(this.getUser())) {
                sendMessage(message);
            }
        }
    }

    public void sendMessage(final Message message) {
        if (message == recentMessageSent) { //We use simple equality here since we want to determine if it is the same object.
            return;
        }
        try {
            //sends this message to the other user.
            OutputStream out = connection.getOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(message);
            recentMessageSent = message;
        } catch (IOException ex) {
            Logger.getLogger(UserConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public User getUser() {
        return user;
    }

    public Calendar getLoginTime() {
        return loginTime;
    }

    public Long getSecondsSinceLogin() {
        Calendar now = getInstance();
        long nowMillis = now.getTimeInMillis();
        long loginMillis = loginTime.getTimeInMillis();
        return (nowMillis - loginMillis) / 1000;
    }

    public Long getMinutesSinceLogin() {
        return getSecondsSinceLogin() / 60;
    }

    public Long getHoursSinceLogin() {
        return getMinutesSinceLogin() / 60;
    }

    public void close() throws IOException {
        this.running = false;
        this.connection.close();
        this.connectedUser.clear();
        this.onMessageListeners.clear();
        this.recentMessageSent = null;
    }
}
