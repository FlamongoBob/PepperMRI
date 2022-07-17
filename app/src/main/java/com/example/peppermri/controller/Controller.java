package com.example.peppermri.controller;

import android.content.res.Resources;

import com.example.peppermri.messages.MessageSystem;
import com.example.peppermri.model.User;
import com.example.peppermri.pepperDB.PepperDB;
import com.example.peppermri.server.Server;

import java.net.InetAddress;
import java.util.ArrayList;

public class Controller {

    Resources resources = Resources.getSystem();
    User newestUser;
    Server server;
    public volatile boolean isServerStarted = false;
    public boolean hasClientJoined = false;
    private ArrayList<User> arrLoggedInUsers = new ArrayList<>();
    PepperDB pepperDB;

    final private int intPortNr = 10284;
    final private String strIPAdress="127.10.10.15";

    public Controller(PepperDB pepperDB) {
        this.pepperDB = pepperDB;
        startServer();
    }

    public void startServer() {
        try {

            InetAddress inetAddress = InetAddress.getByName(strIPAdress);
            server = new Server(this.intPortNr, this, inetAddress);

        } catch (Exception ex) {
            String err = ex.getMessage();
            err+="";
        }
    }

    public boolean checkLoginCredential(String strUserName, String strPassword) {
        newestUser = null;
        newestUser = pepperDB.checkLoginCredential(strUserName, strPassword);
        if(newestUser != null) {
            arrLoggedInUsers.add(newestUser);
            return true;
        }
        return false;
    }

    public void clientDisconnected(int intUserID){
        for (int i = 0; i < arrLoggedInUsers.size(); i++) {
            User user = arrLoggedInUsers.get(i);
            if(user.getIntUserID() == intUserID){
                arrLoggedInUsers.remove(user);
                i = arrLoggedInUsers.size() +1;
            }
        }
    }

    /** TODO Better Logic for choosing who to send it to
     *
     * @param strPatientInfo
     */
    public void sendPatientInformation(String strPatientInfo){
        MessageSystem  msgSys = new MessageSystem(strPatientInfo);
        int intUserID=-1;

        for(int i = 0; i<arrLoggedInUsers.size(); i++) {
            User user = arrLoggedInUsers.get(i);

            if (user.getIntRoleID() == 1) {
                server.sendMessage(msgSys, intUserID);
            }
        }

    }

    public User getNewestUser() {
        return newestUser;
    }
}
