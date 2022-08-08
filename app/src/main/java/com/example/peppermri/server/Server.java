package com.example.peppermri.server;

import com.example.peppermri.MainActivity;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.messages.Message;
import com.example.peppermri.messages.MessageRoles;
import com.example.peppermri.messages.MessageSystem;
import com.example.peppermri.messages.MessageU;
import com.example.peppermri.messages.MessageUser;
import com.example.peppermri.serverclient.ServerClient;
import com.example.peppermri.servermodel.ServerModel;

import java.net.InetAddress;

public class Server {

    boolean isStarted = false;
    ServerModel serverModel;


    /**
     * Constructor for the "Server" class, creating a new ServerModel at the same time passing the variables needed for
     * constuction, as well as sending the conrtoller, to be able to reach function in the controller class.
     *
     * @param port
     * @param controller
     * @param inetAddress
     */
    public Server(int port, Controller controller, InetAddress inetAddress, MainActivity mainActivity) {
            if (!controller.isServerStarted) {
                serverModel = new ServerModel(controller, port, inetAddress, mainActivity);
            }
    }

    public String getIP(){
        return serverModel.getIP();
    }

    public String getPort(){
        return serverModel.getPort();
    }
    /**
     * Clearing arraylist to prepare to recreate a new Server
     * Recreating Server.
     */
    public void allowConnection() {
        if (serverModel != null) {
            serverModel.closeListener();
            serverModel.restartServer();
        }
    }

    /**
     * Sending System Messages
     *
     * @param msgSys
     */
    public void sendBroadcastMessage(MessageSystem msgSys) {
        if (serverModel != null)
            if (serverModel.svClient != null) {
                serverModel.svClient.broadcast(msgSys);
            }
    }

    public boolean sendMessage(MessageSystem msgSys ,int intUserID) {
        if (serverModel != null && msgSys!= null && intUserID >0){
          return serverModel.sendMessage(msgSys,intUserID);
        }
        return false;
    }
    public void sendMessage(MessageUser msgU , int intUserID) {
        if (serverModel != null && msgU!= null && intUserID >0){
            serverModel.sendMessage(msgU,intUserID);
        }
    }

    public void sendMessage(MessageRoles msgR , int intUserID) {
        if (serverModel != null && msgR!= null && intUserID >0){
            serverModel.sendMessage(msgR,intUserID);
        }
    }

    public void clearSpecificClient(int intUserID){
        serverModel.clearSpecificClient(intUserID);
    }

    public void shutDown(){
        serverModel.stopServer();
        serverModel.clearList();
    }
}

