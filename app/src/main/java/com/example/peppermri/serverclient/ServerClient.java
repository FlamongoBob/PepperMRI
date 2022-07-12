package com.example.peppermri.serverclient;

package server_client;

import com.example.peppermri.controller.Controller;
import com.example.peppermri.messages.Message;
import com.example.peppermri.messages.MessageLogin;
import com.example.peppermri.messages.MessageSystem;
import com.example.peppermri.messages.MessageType;
import com.example.peppermri.servermodel.ServerModel;

import java.net.Socket;
import java.util.logging.Logger;

public class ServerClient {

    ServerModel serverModel;
    Socket socket;
    Controller controller;
    public String name = "";
    public static Logger logger = Logger.getLogger("");
    Thread t;


    /**
     * Constructor for the ServerClient class, that receive parameter to set global variable, so other classes
     * can be accessed when needed, to run functions.
     * Creates a new thread running
     *
     * @param model
     * @param socket
     * @param controller
     */
    public ServerClient(ServerModel model, Socket socket, Controller controller) {
        this.serverModel = model;
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

                                /**TODO CHECK PASSWORD
                                 * isCorrect = controller.checkPWDstatus(((MessageLogin) msg).getPassword(), ((MessageLogin) msg).getName());
                                 */

                                if (isCorrect) {
                                    ServerClient.this.name = ((MessageLogin) msg).getName();

                                    controller.hasClientJoined = true;
                                    /**TODO USER CREATION & SEND USER
                                     *controller.ServerPlayerCreation(((MessageLogin) msg).getName());
                                     */

                                    //view.textArea.appendText("System: " + ((JoinMsg) msg).getName() + " joined the Game" + "\n");
                                    //controller.appendTextToChat( ((JoinMsg) msg).getName() + " joined the Game" , Controller.eTextType.System);

                                    MessageSystem messageSystem = new MessageSystem("Successful Login");
                                    messageSystem.setType(MessageType.Successful_LogIn);
                                    messageSystem.send(socket);

                                } else {
                                    MessageSystem messageSystem = new MessageSystem("Unsuccessful Login");
                                    messageSystem.setType(MessageType.Unsuccessful_LogIn);
                                    messageSystem.send(socket);
                                }


                            } else if (msg instanceof MessageSystem) {
                                if (msg.getType().equals(MessageType.Disconnect)) {
                                    /**TODO Client Has Disconnected
                                     * controller.ServerClientHasDisconnected();
                                     */

                                    //controller.appendTextToChat(((DisconnectMessage) msg).getStrUsername() + " has disconnected from the Server", Controller.eTextType.System);
                                    //controller.appendTextToChat("Server has reopened for next connection to a client", Controller.eTextType.System);
                                } else if (msg.getType().equals(MessageType.Test)) {


                                } else if (msg.getType().equals(MessageType.LogOut)) {


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

    @Override
    public String toString() {
        return name + ": " + socket.toString();
    }
}

