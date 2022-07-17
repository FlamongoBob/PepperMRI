package com.example.peppermri.servermodel;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.example.peppermri.controller.Controller;
import com.example.peppermri.messages.MessageSystem;
import com.example.peppermri.serverclient.ServerClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;

public class ServerModel {

    private ServerSocket listener;
    private Controller controller;
    private volatile boolean isStopped = false;
    public ServerClient svClient;
    private int intPort;
    private InetAddress inetAddress;
    Thread t;

    public ObservableList<ServerClient> srvClient = new ObservableArrayList<>();

    /**
     * Constructor for the ServerModel Class,
     * preparing the global variables
     *
     * @param controller
     * @param intPort
     * @param inetAddress
     */
    public ServerModel(Controller controller, int intPort, InetAddress inetAddress) {
        this.controller = controller;
        this.intPort = intPort;
        this.inetAddress = inetAddress;
        Runnable r = prepareServer();//prepareServer(intPort, inetAddress);

        t = new Thread(r, "ServerSocket");
        t.start();
    }

    /**
     * Preparing variable for so that the application can create servers as needed.
     * //@param port
     * //@param inetAddress
     *
     * @return
     */
    public Runnable prepareServer() {
        Runnable r;
        try {
            listener = new ServerSocket(intPort, 10, inetAddress);

        } catch (IOException e) {
            String err = e.getMessage();
            err += "";
        }
        r = createServer();
        return r;
    }

    /**
     * Creating the Server, and closing the socket after the 1 Client has connected to the Server.
     *
     * @return
     */
    public Runnable createServer() {
        Runnable r;
        try {
            if (listener == null | listener.isClosed()) {
                listener = new ServerSocket(this.intPort, 10, this.inetAddress);
            }
            controller.isServerStarted = true;
        } catch (IOException e) {
            String err = e.getMessage();
            err += "";
        }

        r = new Runnable() {
            @Override
            public void run() {
                while (srvClient.size() < 1) {//!isStopped) {
                    try {
                        Socket socket = listener.accept();
                        svClient = new ServerClient(ServerModel.this, socket, controller);
                        srvClient.add(svClient);
                        if (srvClient.size() >= 1 && !listener.isClosed()) {
                            closeListener();
                        }
                    } catch (Exception e) {
                        String err = e.getMessage();
                        err += "";
                    }
                }
            }
        };

        return r;
    }

    /**
     * Recreate the Server after, after the Client has disconnected.
     */
    public void restartServer() {
        t = new Thread(createServer(), "ServerSocket");
        t.start();
    }

    /**
     * Close the Serversocket afte a client has conected.
     */
    public void closeListener() {
        if (listener != null) {
            try {
                listener.close();
            } catch (IOException e) {
                // Uninteresting
            }
        }
    }

    /**
     * Clear arraylist with the clients before recreating the server
     */
    public void clearList() {
        if (srvClient.size() > 0) {
            srvClient.clear();
        }

    }

    public void clearSpecificClient(String strName) {
        for (int i = 0; i < srvClient.size(); i++) {
            ServerClient svClient = srvClient.get(i);
            if(svClient.getName().equals(strName)){
                svClient.stop();
                controller.clientDisconnected(svClient.getIntUserId());
                srvClient.remove(svClient);
                i = srvClient.size() +1;
            }
        }
    }

    public void clearSpecificClient(int intUserID) {
        for (int i = 0; i < srvClient.size(); i++) {
            ServerClient svClient = srvClient.get(i);
            if(svClient.getIntUserId() == intUserID){
                svClient.stop();
                controller.clientDisconnected(intUserID);
                srvClient.remove(svClient);
                i = srvClient.size() +1;
            }
        }
    }


    public void sendMessage(MessageSystem msgSys, int intUserID){
        for (int i = 0; i < srvClient.size(); i++) {
            ServerClient svClient = srvClient.get(i);
            if(svClient.getIntUserId() == intUserID){
                    svClient.send(msgSys);
                i = srvClient.size() +1;
            }
        }
    }

    /**
     * Stop the Server for every Client
     */
    public void stopServer() {

        for (ServerClient c : srvClient)
            c.stop();
        isStopped = true;
        closeListener();
    }
}

