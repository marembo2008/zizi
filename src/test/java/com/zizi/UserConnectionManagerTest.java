package com.zizi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.zizi.UserConnectionManager.getUserConnectionManager;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for the UserConnection logic.
 */
public class UserConnectionManagerTest {

    private class UserSocket {

        private final User user;
        private final Socket socket;

        public UserSocket(User user, Socket socket) {
            this.user = user;
            this.socket = socket;
        }

    }

    public UserConnectionManagerTest() {
    }

    @Before
    public void setUp() {
        UserConnectionManager userConnectionManager = getUserConnectionManager();
        userConnectionManager.start();
    }

    @After
    public void tearDown() {
        UserConnectionManager userConnectionManager = getUserConnectionManager();
        userConnectionManager.stop();
    }

    private UserSocket connectUser(final String userName) {
        try {
            User user = new User(userName);
            LoginMessage message = new LoginMessage(user);
            Socket socket = new Socket("localhost", UserConnectionManager.LISTENING_PORT);
            ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
            objOut.writeObject(message);
            return new UserSocket(user, socket);
        } catch (IOException ex) {
            Logger.getLogger(UserConnectionManagerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private <T extends Message> T readMessage(final Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream objInn = new ObjectInputStream(socket.getInputStream());
        return (T) objInn.readObject();
    }

    private void sendMessage(final Socket socket, final Message message) throws IOException {
        ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
        objOut.writeObject(message);
    }

    @Test
    public void testUserConnectionManagerStartedSuccessfully() {
        UserConnectionManager ucm = getUserConnectionManager();
        assertThat(ucm.isStarted(), is(true));
    }

    @Test
    public void testUserLoggedInSuccessfully() throws InterruptedException {
        UserSocket user0 = connectUser("Test User000");
        UserConnectionManager ucm = getUserConnectionManager();
        Thread.sleep(5000);
        assertThat(ucm.countLoggedInUsers(), is(1));
        UserConnection uc = ucm.findUserConnection(user0.user);
        assertThat(uc.getUser(), is(user0.user));
    }

    @Test
    public void testServerReceivesMessage() throws InterruptedException, IOException {
        UserSocket user0 = connectUser("Test User000");
        Thread.sleep(1000); //Avoid the ONLINE statues message to user1.
        UserSocket user1 = connectUser("Test User111");
        UserConnectionManager ucm = getUserConnectionManager();
        Thread.sleep(5000);
        assertThat(ucm.countLoggedInUsers(), is(2));
        UserConnection uc = ucm.findUserConnection(user0.user);
        TextMessage txtMessage = new TextMessage("This is from user0 to user1", user0.user, user1.user);
        sendMessage(user0.socket, txtMessage);
        Thread.sleep(1000); //wait for send messaging
        assertThat((TextMessage) uc.getRecentMessageReceived(), is(txtMessage));
    }

    @Test
    public void testUserReceivesMessage() throws InterruptedException, IOException, ClassNotFoundException {
        UserSocket user0 = connectUser("Test User000");
        Thread.sleep(1000); //Avoid the ONLINE statues message to user1.
        UserSocket user1 = connectUser("Test User111");
        UserConnectionManager ucm = getUserConnectionManager();
        Thread.sleep(5000);
        assertThat(ucm.countLoggedInUsers(), is(2));
        TextMessage txtMessage = new TextMessage("This is from user0 to user1", user0.user, user1.user);
        sendMessage(user0.socket, txtMessage);
        Thread.sleep(5000); //wait for send to complete
        TextMessage recTxtMessage = readMessage(user1.socket);
        assertThat((TextMessage) recTxtMessage, is(txtMessage));
    }

    @Test
    public void testUserLogsOut() throws InterruptedException, IOException, ClassNotFoundException {
        UserSocket user0 = connectUser("Test User000");
        Thread.sleep(1000); //lets no send the ONLINE status message to user1.
        UserSocket user1 = connectUser("Test User111");
        UserConnectionManager ucm = getUserConnectionManager();
        Thread.sleep(1000);
        assertThat(ucm.countLoggedInUsers(), is(2));

        //Allow connection to be established between the two users.
        TextMessage txtMessage = new TextMessage("This is from user0 to user1", user0.user, user1.user);
        sendMessage(user0.socket, txtMessage);
        Thread.sleep(1000); //wait for send to complete
        UserConnection uc = ucm.findUserConnection(user0.user);
        UserConnection uc1 = uc.findUserConnection(user1.user);
        readMessage(user1.socket); //read the message so that it clears the buffer for the status message.
        assertThat(uc1, notNullValue());

        //Logout the current user
        LogoutMessage logoutMessage = new LogoutMessage(user0.user);
        sendMessage(user0.socket, logoutMessage);
        Thread.sleep(1000); //wait for send to complete
        uc = ucm.findUserConnection(user0.user);
        uc1 = ucm.findUserConnection(user1.user);
        assertThat(uc, nullValue()); //Deregistered.
        assertThat(uc1.findUserConnection(user0.user), nullValue()); //nolonger part of the chat group

        //we receive status message with OFFLINE.
        StatusMessage statusMessage = readMessage(user1.socket);
        assertThat(statusMessage.getStatus(), is(StatusMessage.Status.OFFLINE));
    }

}
