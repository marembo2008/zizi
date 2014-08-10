/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zizi;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Responsible for managing connections between users.
 *
 * A user must always be connected to the server.
 */
public class UserConnectionManager implements Serializable, OnMessageListener {

    private static final Logger LOG = Logger.getLogger(UserConnectionManager.class.getName());

    //List of connected users
    private final Map<User, UserConnection> CONNECTIONS = new HashMap<>();
    //The server socket where users connect to.
    private ServerSocket serverListener;
    /**
     * True if we are to continue listening for requests.
     */
    private volatile boolean running;

    /**
     * Current instance of user connection.
     */
    private static UserConnectionManager userConnectionManager;
    private static final int listeningPort = 35000;

    private UserConnectionManager() {
    }

    public static UserConnectionManager getUserConnectionManager() {
        return userConnectionManager == null ? (userConnectionManager = new UserConnectionManager()) : userConnectionManager;
    }

    public void start() {
        running = true;
        try {
            serverListener = new ServerSocket(listeningPort);
            while (running) {
                final Socket socket = serverListener.accept();
                handleUserConnection(socket);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Connection Failed to Start", ex);
        }
    }

    public void stop() {
        try {
            running = false;
            for (UserConnection uc : CONNECTIONS.values()) {
                uc.close();
            }
            serverListener.close();
        } catch (IOException ex) {
            Logger.getLogger(UserConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns null if there is no connection to the specified user.
     *
     * @param user the user to find its connection
     * @return the current user connection, if any.
     */
    public UserConnection findUserConnection(final User user) {
        return CONNECTIONS.get(user);
    }

    private void handleUserConnection(final Socket socket) {
        //Since we need the server to continue listening, do it a thread.
        new Thread(new NewUserRegistrationProcessor(socket)).start();
    }

    @Override
    public void onMessageReceived(final Message message) {
        User user = message.getFromUser();
        UserConnection userConnection = findUserConnection(user);
        if (message instanceof LogoutMessage) {
            //Oh oh, we are logging out
            if (userConnection == null) {
                LOG.warning("Received message from a user without a connection. This should not be happening!!");
                return;
            }
            try {
                //Stop the user connection
                userConnection.close();
                //Deregister the user.
                CONNECTIONS.remove(user);
                //Find all the connected channels and inform them of a logout status message
                for (UserConnection users : userConnection.findAllUserConnections()) {
                    users.removeOnMessageListener(userConnection); //Stop automatic forwarding to logged out user.
                    users.removeUserConnection(user); //Remove from a list of connected users.
                    StatusMessage statusMessage = new StatusMessage(StatusMessage.Status.OFFLINE, user, users.getUser());
                    users.sendMessage(statusMessage);
                }
            } catch (IOException ex) {
                Logger.getLogger(UserConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (message instanceof TextMessage) {
            //We need to check if the two users have already been connected to communicate. otherwise, we add the connections.
            TextMessage textMessage = (TextMessage) message;
            User toUser = textMessage.getToUser();
            UserConnection toUserConnection = userConnection.findUserConnection(toUser);
            //If we are not null, we are going to get automatic forwarding, since we have registered as message listeners
            if (toUserConnection == null) {
                //We are not registered to chat with the sender. hence we are not going to get automatic forwarding.
                toUserConnection = findUserConnection(toUser);
                if (toUserConnection == null) {
                    //Disastor, we are not aroung
                    StatusMessage statusMessage = new StatusMessage(StatusMessage.Status.OFFLINE, toUser, user);
                    userConnection.sendMessage(statusMessage);
                    LOG.log(Level.WARNING,
                            "User {0} tried to send message to user {1} who is offline.",
                            new Object[]{user, toUser});
                } else {
                    //The two users are not connected at all.
                    //We connect the, so that they can start communicating.
                    userConnection.addOnMessageListener(toUserConnection);
                    userConnection.addUserConnection(toUserConnection);
                    toUserConnection.addOnMessageListener(userConnection);
                    toUserConnection.addUserConnection(userConnection);
                    //forward the message on behalf of user connection.
                    toUserConnection.sendMessage(message);
                }
            }
        }
    }

    private void loginUserConnection(final Socket socket) {
        try {
            //get the input stream. Do not close it!! Otherwise the socket will be closed.
            InputStream inn = socket.getInputStream();
            ObjectInputStream objInn = new ObjectInputStream(inn);
            LoginMessage message = (LoginMessage) objInn.readObject();
            //We however knows this is the first connection, so a status message is expected.
            //Register the user.
            User user = message.getFromUser();
            //To user currently will be null, since the current user is simply register
            UserConnection userConnection = new UserConnection(user, socket, Calendar.getInstance());

            //We register ourselves to listen to messages from the connection.
            userConnection.addOnMessageListener(this);
            CONNECTIONS.put(user, userConnection);
            onUserLoggedIn(userConnection);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Notifies all currently logged in users, that a new user has registered.
     *
     * @param userConnection
     */
    private void onUserLoggedIn(final UserConnection userConnection) {
        for (UserConnection uc : CONNECTIONS.values()) {
            //We do not connect the two users to chat here.
            //We simply inform him, that that a new user has just logged in.
            if (uc != userConnection) { //Dont notify yourself.
                StatusMessage message = new StatusMessage(StatusMessage.Status.ONLINE, userConnection.getUser(), uc
                                                          .getUser());
                uc.sendMessage(message);
            }
        }
    }

    private class NewUserRegistrationProcessor implements Runnable {

        private final Socket socket;

        public NewUserRegistrationProcessor(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            loginUserConnection(socket);
        }

    }
}
