package com.example.peppermri.serverclient;


import android.content.res.Resources;

import com.example.peppermri.R;
import com.example.peppermri.controller.Controller;
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
    private int intUserId =-1;
    public static Logger logger = Logger.getLogger("");
    Thread t;
    User user;

    Resources resources = Resources.getSystem();

    /**
     * Constructor for the ServerClient class, that receive parameter to set global variable, so other classes
     * can be accessed when needed, to run functions.
     * Creates a new thread running
     *
     * @param serverModel
     * @param socket
     * @param controller
     */
    public ServerClient(ServerModel serverModel, Socket socket, Controller controller) {
        this.serverModel = serverModel;
        this.socket = socket;
        this.controller = controller;

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


                                isCorrect = controller.checkLoginCredential(((MessageLogin) msg).getPassword(), ((MessageLogin) msg).getName());


                                if (isCorrect) {
                                    ServerClient.this.name = ((MessageLogin) msg).getName();

                                    controller.hasClientJoined = true;

                                    user = controller.getNewestUser();
                                    if(user  != null) {
                                        MessageUser msgU = new MessageUser(user.getIntUserID()
                                                , user.getStrTitle()
                                                , user.getStrFirstname()
                                                , user.getStrLastname()
                                                , user.getStrPicture()
                                                , user.getIntRoleID()
                                        );

                                        ServerClient.this.intUserId = user.getIntUserID();

                                        msgU.send(socket);
                                    }

                                    MessageSystem messageSystem = new MessageSystem(resources.getString(R.string.msg_SucLogin));
                                    messageSystem.setType(MessageType.Successful_LogIn);
                                    messageSystem.send(socket);

                                } else {
                                    MessageSystem messageSystem = new MessageSystem(resources.getString(R.string.msg_UnSucLogin));
                                    messageSystem.setType(MessageType.Unsuccessful_LogIn);
                                    messageSystem.send(socket);
                                    serverModel.clearSpecificClient(ServerClient.this.name);
                                }


                            } else if (msg instanceof MessageSystem) {
                                if (msg.getType().equals(MessageType.Disconnect)) {
                                    controller.clientDisconnected(intUserId);
                                    MessageSystem msgSys = new MessageSystem(resources.getString(R.string.msg_Disconnect));
                                    msgSys.setType(MessageType.Disconnect);
                                    msgSys.send(socket);

                                    controller.clientDisconnected(intUserId);

                                } else if (msg.getType().equals(MessageType.Test)) {
                                    MessageSystem msgSys = new MessageSystem(resources.getString(R.string.msg_Test));
                                    msgSys.setType(MessageType.Test);
                                    msgSys.send(socket);

                                } else if (msg.getType().equals(MessageType.LogOut)) {

                                    controller.clientDisconnected(intUserId);
                                    MessageSystem msgSys = new MessageSystem(resources.getString(R.string.msg_Disconnect));
                                    msgSys.setType(MessageType.Disconnect);
                                    msgSys.send(socket);

                                    controller.clientDisconnected(intUserId);

                                }else if (msg.getType().equals(MessageType.InsertUser)) {

                                   controller.insertUser((MessageI) msg);

/*


                                    controller.clientDisconnected(intUserId);
*/
                                }else if (msg.getType().equals(MessageType.UpdateUser)) {

                                    controller.updateUser((MessageU) msg);

/*
                                    MessageSystem msgSys = new MessageSystem(resources.getString(R.string.msg_Disconnect));
                                    msgSys.setType(MessageType.Disconnect);
                                    msgSys.send(socket);

                                    controller.clientDisconnected(intUserId);
*/
                                }else if (msg.getType().equals(MessageType.DeleteUser)) {

                                    controller.deleteUser((MessageD) msg);

/*
                                    MessageSystem msgSys = new MessageSystem(resources.getString(R.string.msg_Disconnect));
                                    msgSys.setType(MessageType.Disconnect);
                                    msgSys.send(socket);

                                    controller.clientDisconnected(intUserId);
*/
                                }
                            }

                        } catch (Exception ex) {
                            logger.warning(ex.toString());
                            break;
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
    public int getIntUserId(){
        return intUserId;
    }
    public String getName(){
        return name;
    }

    @Override
    public String toString() {
        return name + ": " + socket.toString();
    }
}

