package com.example.peppermri.serverclient;


import android.content.res.Resources;

import com.example.peppermri.MainActivity;
import com.example.peppermri.R;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.crypto.Decryption;
import com.example.peppermri.crypto.Encryption;
import com.example.peppermri.messages.Message;
import com.example.peppermri.messages.MessageD;
import com.example.peppermri.messages.MessageI;
import com.example.peppermri.messages.MessageLogin;
import com.example.peppermri.messages.MessageSystem;
import com.example.peppermri.messages.MessageType;
import com.example.peppermri.messages.MessageU;
import com.example.peppermri.messages.MessageUser;
import com.example.peppermri.model.User;
import com.example.peppermri.servermodel.ServerModel;

import java.net.Socket;
import java.util.logging.Logger;

public class ServerClient {

    ServerModel serverModel;
    Socket socket;
    Controller controller;
    public String name = "";
    private int intUserID = -1;
    public static Logger logger = Logger.getLogger("");
    Thread t;
    User user;
    Encryption e = new Encryption();
    Decryption d = new Decryption();

    MainActivity mainActivity;

    /**
     * Constructor for the ServerClient class, that receive parameter to set global variable, so other classes
     * can be accessed when needed, to run functions.
     * Creates a new thread running
     *
     * @param serverModel
     * @param socket
     * @param controller
     */
    public ServerClient(ServerModel serverModel, Socket socket, Controller controller, MainActivity mainActivity) {
        this.serverModel = serverModel;
        this.socket = socket;
        this.controller = controller;
        this.mainActivity = mainActivity;
        Runnable r = messageThread();
        t = new Thread(r);
        startThread();
    }

    /**
     * Creates a thread specifically just to receive incoming messages from connected clients, and then based on the
     * message run different functions.
     *
     * @return
     */
    private Runnable messageThread() {
        Runnable r = new Runnable() {

            @Override
            public void run() {


                try {
                    while (controller.isServerStarted) {
                        try {
                            Message msg = Message.receive(socket);


                            if (msg instanceof MessageLogin) {
                                boolean isCorrect = false;

                                isCorrect = controller.checkLoginCredential(
                                        d.decrypt(((MessageLogin) msg).getPassword())
                                        , d.decrypt(((MessageLogin) msg).getName())
                                );

                                if (isCorrect) {
                                    ServerClient.this.name = d.decrypt(
                                            ((MessageLogin) msg).getName()
                                    );

                                    controller.hasClientJoined = true;

                                    user = controller.getNewestUser();

                                    MessageSystem messageSystem = new MessageSystem("You have successfully Connected to Pepper!");
                                    messageSystem.setType(MessageType.Successful_LogIn);
                                    messageSystem.send(socket);

                                    if (user != null) {
                                        MessageUser msgU = new MessageUser(user.getIntEmployeeID()
                                                , e.encrypt(user.getStrTitle())
                                                , e.encrypt(user.getStrFirstname())
                                                , e.encrypt(user.getStrLastname())

                                                , user.getIntPictureID()
                                                , e.encrypt(user.getStrPicture())

                                                , user.getIntRoleID()
                                                , e.encrypt(user.getStrRole())


                                                , user.getIntUserID()
                                                , e.encrypt(user.getStrUserName())
                                                , e.encrypt(user.getStrPassword())

                                                , user.getIntConfidentialID()
                                                , user.getIntGetsConfidentialInfo()
                                        );

                                        ServerClient.this.intUserID = user.getIntUserID();

                                        msgU.send(socket);

                                        controller.prepareRoles(intUserID);
                                    }

                                } else {
                                    MessageSystem messageSystem = new MessageSystem(mainActivity.getString(R.string.msg_UnSucLogin));
                                    messageSystem.setType(MessageType.Unsuccessful_LogIn);
                                    messageSystem.send(socket);
                                    serverModel.clearSpecificClient(ServerClient.this.name);
                                }


                            } else if (msg instanceof MessageSystem) {
                                if (msg.getType().equals(MessageType.Disconnect)) {

                                    controller.clientDisconnected(intUserID);

                                } else if (msg.getType().equals(MessageType.Test)) {
                                    MessageSystem msgSys = new MessageSystem(mainActivity.getString(R.string.msg_Test));
                                    msgSys.setType(MessageType.Test);
                                    msgSys.send(socket);

                                    controller.clientDisconnected(intUserID);

                                } else if (msg.getType().equals(MessageType.LogOut)) {

                                    MessageSystem msgSys = new MessageSystem(mainActivity.getString(R.string.msg_Disconnect));
                                    msgSys.setType(MessageType.Disconnect);
                                    msgSys.send(socket);

                                } else if (msg.getType().equals(MessageType.AllUser )) {

                                    controller.getAllEmployeeData(intUserID);

                                }
                            } else if (msg instanceof MessageI) {

                                controller.insertUser((MessageI) msg);

                            } else if (msg instanceof MessageU) {

                                controller.updateUser((MessageU) msg);

                            } else if (msg instanceof MessageD) {

                                controller.deleteUser((MessageD) msg);

                            }


                        } catch (Exception ex) {
                            String err = "";
                            err = ex.getMessage();
                            err += "";
                            logger.warning(ex.toString());
                        }
                    }

                } catch (Exception e) {
                    String err = e.getMessage();
                    err += "";
                }

            }
        };
        return r;
    }


    /**
     * Start a thread with a given runnable
     */
    public void startThread() {

        t.start();
    }

    /**
     * Stops/ interrupts a thread.
     */
    public void stopThread() {
        t.interrupt();
    }

    /**
     * Sending Message to all clients saved in the observableArraylist of the ServerModel,
     * by using a for loop working through every client entry and then passing the message along to the function 'send'
     */
    public void broadcast(Message outMsg) {
        // logger.info("Broadcasting message to clients");
        for (ServerClient c : serverModel.srvClient) {
            c.send(outMsg);

        }
    }

    /**
     * Passes the message along to the Message method send, to send the message to the client
     *
     * @param msg
     */
    public void send(Message msg) {
        msg.send(socket);
    }


    /**
     * Stops the ServerClient from receiving any messages from anyone, by closing the socket.
     */
    public void stop() {
        try {
            //socket.close();
            stopThread();
        } catch (Exception ex) {
            //(IOException e) {
            String err = ex.getMessage();
            err += "";
        }
    }

    public User getUser() {
        return user;
    }

    public int getIntUserID() {
        return intUserID;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ": " + socket.toString();
    }
}

