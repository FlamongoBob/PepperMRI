package com.example.peppermri.controller;

import android.content.res.Resources;
import android.widget.TextView;

import com.example.peppermri.MainActivity;
import com.example.peppermri.crypto.Decryption;
import com.example.peppermri.messages.Message;
import com.example.peppermri.messages.MessageD;
import com.example.peppermri.messages.MessageI;
import com.example.peppermri.messages.MessageRoles;
import com.example.peppermri.messages.MessageSystem;
import com.example.peppermri.messages.MessageType;
import com.example.peppermri.messages.MessageU;
import com.example.peppermri.messages.MessageUser;
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
    MainActivity mainActivity;
    final private int intPortNr = 10284;
    final private String strIPAdress = "127.10.10.15";
    private ArrayList<User> arrAllUser = new ArrayList<>();

    public Controller(PepperDB pepperDB, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.pepperDB = pepperDB;
        startServer();
    }

    public void startServer() {
        try {

            InetAddress inetAddress = InetAddress.getByName(strIPAdress);
            server = new Server(this.intPortNr, this, inetAddress);

        } catch (Exception ex) {
            String err = ex.getMessage();
            err += "";
        }
    }

    public boolean checkLoginCredential(String strUserName, String strPassword) {
        newestUser = null;
        newestUser = pepperDB.checkLoginCredential(strUserName, strPassword);
        if (newestUser != null) {
            arrLoggedInUsers.add(newestUser);
            return true;
        } else {
            if(arrLoggedInUsers.size() > 0){
                newestUser = arrLoggedInUsers.get(arrLoggedInUsers.size()-1);
            }else {
                newestUser = null;
            }
            return false;
        }
    }

    public void clientDisconnected(int intUserID) {
        for (int i = 0; i < arrLoggedInUsers.size(); i++) {
            User user = arrLoggedInUsers.get(i);
            if (user.getIntUserID() == intUserID) {
                arrLoggedInUsers.remove(user);
                i = arrLoggedInUsers.size() + 1;
            }
        }
    }

    /**
     * TODO Better Logic for choosing who to send it to
     *
     * @param strPatientInfo
     */
    public void sendPatientInformation(String strPatientInfo) {
        MessageSystem msgSys = new MessageSystem(strPatientInfo);
        int intUserID = -1;

        for (int i = 0; i < arrLoggedInUsers.size(); i++) {
            User user = arrLoggedInUsers.get(i);

            if (user.getIntGetsConfidentialInfo() == 1) {
                server.sendMessage(msgSys, user.getIntUserID());
            }
        }

    }
    public void check(TextView tv ){
        TextView tv2 = tv ;
         tv2.setText(pepperDB.Check());

    }

    public User getNewestUser() {
        return newestUser;
    }

    Decryption d = new Decryption();

    public void insertUser(MessageI msgU) {
        try {
            String strTitle = d.decrypt(msgU.getStrTitle());
            String strFirstName = d.decrypt(msgU.getStrFirstName());
            String strLastName = d.decrypt(msgU.getStrLastName());
            String strUsername = d.decrypt(msgU.getStrUserName());
            String strPassword = d.decrypt(msgU.getStrPassword());
            String strPicture = d.decrypt(msgU.getStrPicture());
            int intRoleID = msgU.getIntRoleID();
            int intConfidentialInfoID = msgU.getIntConfidentialInfoID();

            int intUserID = pepperDB.insertNewUser(strUsername, strPassword);
            int intPictureID = -1;
            if (intUserID > 0) {
                if (!strPicture.isEmpty()) {
                    intPictureID = pepperDB.insertNewPicture(strPicture);

                    if (intPictureID < 0) {
                        MessageSystem msgSys = new MessageSystem("Something went wrong on the insert into tblPicture on the Server Database");
                        server.sendMessage(msgSys);
                    }
                }
                pepperDB.insertNewEmployee(strTitle, strFirstName, strLastName, intUserID, intPictureID, intRoleID, intConfidentialInfoID);

                MessageSystem msgSys = new MessageSystem("You have successfully added a new Employee to the database.! Login details are full functional");
                msgSys.setType(MessageType.Suc_IUD);
                server.sendMessage(msgSys);

            } else {
                MessageSystem msgSys = new MessageSystem("Something went wrong on the insert into tblPicture on the Server Database");
                msgSys.setType(MessageType.Error);
                server.sendMessage(msgSys);
            }

        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err += "";

            MessageSystem msgSys = new MessageSystem("Something went wrong.  Please verify everything is completed correctly! Error Message: " + ex.getMessage());
            msgSys.setType(MessageType.Error);
            server.sendMessage(msgSys);
        }
    }

    public void updateUser(MessageU msgU) {
        try {
            String strTitle = d.decrypt(msgU.getStrTitle());
            String strFirstName = d.decrypt(msgU.getStrFirstName());
            String strLastName = d.decrypt(msgU.getStrLastName());
            String strPicture = d.decrypt(msgU.getStrPicture());
            String strUserName = d.decrypt(msgU.getStrUserName());
            String strPassword = d.decrypt(msgU.getStrPassword());

            int intUserID = msgU.getIntUserID();
            int intEmployeeID = msgU.getIntEmployeeID();
            int intPictureID = msgU.getIntPictureID();

            int intRoleID = msgU.getIntRoleID();
            int intConfidentialID = msgU.getIntConfidentialID();

            pepperDB.updateEmployeeData(intEmployeeID
            , strTitle
            , strFirstName
            , strLastName
            , intRoleID
            , intConfidentialID
            , intPictureID
            , strPicture
            , intUserID
            , strUserName
            , strPassword);

            MessageSystem msgSys = new MessageSystem("Data has been successfully updated! Login details are full functional");
            msgSys.setType(MessageType.Suc_IUD);
            server.sendMessage(msgSys);

        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err += "";


            MessageSystem msgSys = new MessageSystem("Something went wrong while updating wrong. Error Message: " + ex.getMessage());
            msgSys.setType(MessageType.Error);
            server.sendMessage(msgSys);
        }
    }

    public void deleteUser(MessageD msdD) {
        try {
            int intUserID = msdD.getIntUserID();
            int intEmployeeID = msdD.getIntEmployeeID();
            int intPictureID = msdD.getIntPictureID();

            MessageSystem msgSys = new MessageSystem("Data has been successfully deleted");
            msgSys.setType(MessageType.Suc_IUD);
            server.sendMessage(msgSys);

            pepperDB.deleteEmployeeData(intEmployeeID,intPictureID,intUserID);
        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err += "";


            MessageSystem msgSys = new MessageSystem("Something went wrong. Error Message: " + ex.getMessage());
            msgSys.setType(MessageType.Error);
            server.sendMessage(msgSys);
        }
    }
    public void collectAllUser(User user){
        arrAllUser.add(user);
    }


    public void getAllEmployeeData(int intUserID) {
        pepperDB.selectAllEmployeeData();
        for(int i = 0; i<arrAllUser.size(); i++){
            sendUser(arrAllUser.get(i),intUserID, MessageType.AllUser);
        }

    }

    public void sendUser(User user, int intUserID, MessageType msgType) {
        MessageUser msgU = new MessageUser(user.getIntEmployeeID()
                , user.getStrTitle()
                , user.getStrFirstname()
                , user.getStrLastname()

                , user.getIntPictureID()
                , user.getStrPicture()

                , user.getIntRoleID()
                , user.getStrRole()

                , user.getIntUserID()
                , user.getStrUserName()
                , user.getStrPassword()


                , user.getIntConfidentialID()
                , user.getIntGetsConfidentialInfo());
        msgU.setType(msgType);
        server.sendMessage(msgU,intUserID);

    }

    public void prepareRoles(int intUserID){
        pepperDB.getAllRoles(intUserID);
    }


    public void sendRoles(int intRoleID, String strRole, int intUserID){
        MessageRoles msgR = new MessageRoles(intRoleID, strRole);
        server.sendMessage(msgR, intUserID);

    }























}
