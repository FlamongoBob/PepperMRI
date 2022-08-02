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
        try {
            if (!controller.isServerStarted) {
                //new
                serverModel = new ServerModel(controller, port, inetAddress, mainActivity);
            }

        } catch (Exception e) {
            String err = "";
            err = e.getMessage();
        }
    }

    /**
     * Clearing arraylist to prepare to recreate a new Server
     * Recreating Server.
     */
    public void allowConnection() {
        if (serverModel != null) {
            serverModel.closeListener();
            //serverModel.clearList();
            serverModel.restartServer();
            //serverModel.openListener();
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

    public void sendMessage(MessageSystem msgSys ,int intUserID) {
        if (serverModel != null && msgSys!= null && intUserID >0){
            serverModel.sendMessage(msgSys,intUserID);
        }
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
    /*
    public void SendMessage(DisconnectMessage disconnectMessage) {
        if (serverModel != null) {
            if (serverModel.svClient != null) {
                serverModel.svClient.broadcast(disconnectMessage);
            }
        }
    }

    public void SendMessage(IncorrectPwdMessage incorrectPwdMessage) {
        if (serverModel != null) {
            if (serverModel.svClient != null) {
                serverModel.svClient.broadcast(incorrectPwdMessage);
            }
        }
    }

    /**
     * Sending Start / End messages of the Game
     * @param startEndGameMessage

    public void SendMessage(StartEndGameMessage startEndGameMessage) {
        if (serverModel != null)
            serverModel.svClient.broadcast(startEndGameMessage);
    }

    /**
     * Sending Gamemessages
     * @param gameMessage

    public void SendMessage(GameMessage gameMessage) {
        if (serverModel != null)
            serverModel.svClient.broadcast(gameMessage);
    }
    /**
     * Sending Button messages
     * @param buttonMessage

    public void SendMessage(ButtonMessage buttonMessage) {
        if (serverModel != null)
            serverModel.svClient.broadcast(buttonMessage);
    }

    /**
     * Sending Player messages
     * @param playerMsg

    public void SendMessage(PlayerMsg playerMsg) {
        if (serverModel != null)
            serverModel.svClient.broadcast(playerMsg);
    }

    /**
     * Stopping the Server from

    public void stopServer() {
        if (serverModel != null)
            serverModel.stopServer();
    }
    public void SendMessage(CardsMessage cardsMessage) {
        serverModel.svClient.broadcast(cardsMessage);
//    GameMessage gameMessage = new GameMessage(MessageType.DeBlock);
//    serverModel.svClient.broadcast(gameMessage);
    }

*/
}

