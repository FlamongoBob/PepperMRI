package com.example.peppermri.controller;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.peppermri.MainActivity;
import com.example.peppermri.R;
import com.example.peppermri.crypto.Decryption;
import com.example.peppermri.crypto.Encryption;
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
import java.util.List;

public class Controller {

    Resources resources = Resources.getSystem();
    User newestUser;
    Server server;
    public volatile boolean isServerStarted = false;
    public boolean hasClientJoined = false;
    private ArrayList<User> arrLoggedInUsers = new ArrayList<>();
    PepperDB pepperDB;
    MainActivity mainActivity;
    final private int intPortNr = 6666;//80; //= 10284;
    final private String strIPAdress  = "127.10.10.15"; //= "10.0.2.15";

    private ArrayList<User> clientArrAllUser = new ArrayList<>();
    Encryption e = new Encryption();

    public Controller(PepperDB pepperDB, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.pepperDB = pepperDB;
        try {

            //InetAddress inetAddress = InetAddress.getByName(strIPAdress);
            //startServer(intPortNr, this,inetAddress,mainActivity);

        } catch (Exception ex) {
            isServerStarted = false;
            String err = ex.getMessage();
            err += "";
        }
    }

    public Server startServer(int intPortNr, Controller controller, InetAddress inetAddress, MainActivity mainActivity) {
        try {

            server = new Server(intPortNr, controller, inetAddress, mainActivity);


        } catch (Exception ex) {
            isServerStarted = false;
            String err = ex.getMessage();
            err += "";
        }
        return server;
    }

    public boolean clientCheckLoginCredential(String strUserName, String strPassword) {
        newestUser = null;
        newestUser = pepperDB.checkLoginCredential(strUserName, strPassword);

        if (newestUser != null) {
            arrLoggedInUsers.add(newestUser);
            return true;
        } else {

            if (arrLoggedInUsers.size() > 0) {
                newestUser = arrLoggedInUsers.get(arrLoggedInUsers.size() - 1);
            } else {
                newestUser = null;
            }
            return false;
        }
    }

    public void serverShutDown(MessageSystem msg) {
        server.sendBroadcastMessage(msg);
        server.shutDown();
        isServerStarted = false;
    }

    public void clientDisconnected(int intUserID) {
        for (int i = 0; i < arrLoggedInUsers.size(); i++) {
            User user = arrLoggedInUsers.get(i);
            if (user.getIntUserID() == intUserID) {
                arrLoggedInUsers.remove(user);
                server.clearSpecificClient(intUserID);
                server.allowConnection();
                //i = arrLoggedInUsers.size() + 1;
            }
        }
    }

    public void sendPatientInformation(String strPatientInfo) {
        MessageSystem msgSys = new MessageSystem(strPatientInfo);
        msgSys.setType(MessageType.Patient);

        int intUserID = -1;

        for (int i = 0; i < arrLoggedInUsers.size(); i++) {
            User user = arrLoggedInUsers.get(i);

            if (user.getIntGetsConfidentialInfo() == 1) {
                server.sendMessage(msgSys, user.getIntUserID());
            }
        }

    }

    public void adminTestMessage(TextView tv) {
        //TextView tv2 = tv ;
        ///tv2.setText(pepperDB.Check());
        try {
            MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Admin_Test).toString());
            msgSys.setType(MessageType.Patient);
            server.sendBroadcastMessage(msgSys);
        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err = "";
        }

    }

    public void adminDisconnectMessage() {
        try {
            MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Admin_Test).toString());
            msgSys.setType(MessageType.Disconnect);
            server.sendBroadcastMessage(msgSys);
        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err = "";
        }

    }


    public User getNewestUser() {
        return newestUser;
    }

    Decryption d = new Decryption();

    public void clientInsertUser(MessageI msgU, int intSenderUserID) {
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
                if (!strPicture.isEmpty() && !strPicture.equals("NoPicture")) {
                    intPictureID = pepperDB.insertNewPicture(strPicture);

                    if (intPictureID < 0) {
                        MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Error_tblPicture).toString());
                        server.sendMessage(msgSys, intSenderUserID);
                    }
                } else {
                    intPictureID = pepperDB.insertNewPicture(strPicture);

                    if (intPictureID < 0) {
                        MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Error_tblPicture).toString());
                        server.sendMessage(msgSys, intSenderUserID);
                    }
                }
                pepperDB.insertNewEmployee(strTitle, strFirstName, strLastName, intUserID, intPictureID, intRoleID, intConfidentialInfoID);

                MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Suc_Employee).toString());
                msgSys.setType(MessageType.Suc_IUD);
                server.sendMessage(msgSys, intSenderUserID);

            } else {
                MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Error_tblUser).toString());
                msgSys.setType(MessageType.Error);
                server.sendMessage(msgSys, intSenderUserID);
            }

        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err += "";

            MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Error_Exception).toString() + ex.getMessage());
            msgSys.setType(MessageType.Error);
            server.sendMessage(msgSys, intSenderUserID);
        }
    }

    public void clientUpdateUser(MessageU msgU, int intSenderUserID) {
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

            MessageSystem msgSys = new MessageSystem( mainActivity.getText(R.string.Suc_Update).toString());
            msgSys.setType(MessageType.Suc_IUD);
            server.sendMessage(msgSys, intUserID);

        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err += "";


            MessageSystem msgSys = new MessageSystem( mainActivity.getText(R.string.Error_UpdateUser).toString() + ex.getMessage());
            msgSys.setType(MessageType.Error);
            server.sendMessage(msgSys, intSenderUserID);
        }
    }

    public void clientDeleteUser(MessageD msdD, int intSenderUserID) {
        try {
            int intUserID = msdD.getIntUserID();
            int intEmployeeID = msdD.getIntEmployeeID();
            int intPictureID = msdD.getIntPictureID();

            MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Suc_Delete).toString());
            msgSys.setType(MessageType.Suc_IUD);
            server.sendMessage(msgSys, intSenderUserID);

            pepperDB.deleteEmployeeData(intEmployeeID, intPictureID, intUserID);
        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err += "";


            MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Error_Delete).toString() + ex.getMessage());
            msgSys.setType(MessageType.Error);
            server.sendMessage(msgSys, intSenderUserID);
        }
    }

    public void clientCollectAllUser(User user) {
        clientArrAllUser.add(user);
    }


    public void clientGetAllEmployeeData(int intUserID) {
        pepperDB.selectAllEmployeeData(true, intUserID);
       /* for (int i = 0; i < clientArrAllUser.size(); i++) {
            clientSendUser(clientArrAllUser.get(i), intUserID, MessageType.AllUser);
        }*/

    }

    public void clientSendUser(User user, int intUserID, MessageType msgType) {
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
                , user.getIntGetsConfidentialInfo());
        msgU.setType(msgType);
        server.sendMessage(msgU, intUserID);

    }

    public void clientPrepareRoles(int intUserID) {
        pepperDB.getAllRoles(intUserID);
    }


    public void clientSendRoles(int intRoleID, String strRole, int intUserID) {
        MessageRoles msgR = new MessageRoles(intRoleID, strRole);
        msgR.setType(MessageType.Roles);
        server.sendMessage(msgR, intUserID);

    }

    /**
     * User Management
     */
    ArrayList<User> allEmployees = new ArrayList<>();
    EditText etUMFirstName, etUMTitle, etUMLastName, etUMPassword, etUMUserName;
    String strUMPicture;
    Spinner spUMRole;
    int intUMRoleID, intUserID, intEmployeeID, intPictureID;
    ImageButton ibUMPicture;
    RadioGroup rgConfidentialUM;
    RadioButton rb_RConfidentialUM, rb_NConfidentialUM;
    User userCurrentSelectedUm;

    private ArrayList<User> serverArrAllUser = new ArrayList<>();
    ArrayList<String> arrRoles = new ArrayList<>();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;


    public void serverCollectAllUser(User user) {
        serverArrAllUser.add(user);
    }


    public void serverGetAllEmployeeData() {
        pepperDB.selectAllEmployeeData(false, currentUser.getIntUserID());

    }


    public void setEtUMFirstName(EditText etUMFirstName) {
        if (etUMFirstName != null) {
            this.etUMFirstName = etUMFirstName;
        }
    }

    //User Management Setters

    public void setEtUMTitle(EditText etUMTitle) {
        if (etUMTitle != null) {
            this.etUMTitle = etUMTitle;
        }
    }

    public void setEtUMLastName(EditText etUMLastName) {
        if (etUMLastName != null) {
            this.etUMLastName = etUMLastName;
        }
    }

    public void setEtUMPassword(EditText etUMPassword) {
        if (etUMPassword != null) {
            this.etUMPassword = etUMPassword;
        }
    }

    public void setEtUMUserName(EditText etUMUserName) {
        if (etUMUserName != null) {
            this.etUMUserName = etUMUserName;
        }
    }

    public void setSpUMRole(Spinner spUMRole) {
        if (spUMRole != null) {
            this.spUMRole = spUMRole;
        }
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context
                , Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int cameraPermission = ContextCompat.checkSelfPermission(context
                , Manifest.permission.CAMERA);

        int intNotificationPermission = ContextCompat.checkSelfPermission(context
                , Manifest.permission.POST_NOTIFICATIONS);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (intNotificationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.POST_NOTIFICATIONS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void setUMNewPicture(ImageButton ibUMPicture) {
        if (ibUMPicture != null) {
            this.ibUMPicture = ibUMPicture;
        }
    }


    public int starFillUserManagement(int intPos) {
        if (intPos >= allEmployees.size()) {
            intPos = 0;
        }

        if (intPos < 0) {

            intPos = 0;
        }

        userCurrentSelectedUm = allEmployees.get(intPos);
        populateUserManagementControlls(allEmployees.get(intPos));
        return intPos;
    }

    private void populateUserManagementControlls(User user) {
        etUMTitle.setText(user.getStrTitle());
        etUMFirstName.setText(user.getStrFirstname());
        etUMLastName.setText(user.getStrLastname());
        String strPicture = user.getStrPicture();


       /* setIBNewPicture(
                StringToBitMap(strPicture)
                , ibUMPicture
        );*/

        etUMPassword.setText(user.getStrPassword());
        etUMUserName.setText(user.getStrUserName());

        if (user.getIntRoleID() == 2) {
            spUMRole.setSelection(arrRoles.indexOf("User"));
        } else {

            spUMRole.setSelection(arrRoles.indexOf("Admin"));
        }

        if (user.getIntConfidentialID() == 1) {
            rb_RConfidentialUM.setChecked(true);
        } else {

            rb_NConfidentialUM.setChecked(true);
        }

    }

    /**
     * pepperInfo
     */

    public int getIntRoleID() {
        return loggedInUser.getIntRoleID();
    }


    /**
     * Login
     */

    EditText etLoginPassword, etLoginUserName;
    TextView tvLoginInformation;
    User loggedInUser;
    public boolean isLoggedIn = true;

    public boolean serverCheckLoginCredential(String strUserName, String strPassword) {
        loggedInUser = null;
        loggedInUser = pepperDB.checkLoginCredential(strUserName, strPassword);

        if (loggedInUser != null) {
            isLoggedIn = true;
            return isLoggedIn;
        }
        return isLoggedIn;
    }

    public void setEtLoginUsername(EditText etLoginUserName) {
        if (etLoginUserName != null) {
            this.etLoginUserName = etLoginUserName;
        }
    }

    public void setEtLoginPassword(EditText etPassword) {
        if (etPassword != null) {
            this.etLoginPassword = etPassword;
        }
    }

    public void setTvLoginInformation(TextView tvLoginInformation) {
        if (tvLoginInformation != null) {
            this.tvLoginInformation = tvLoginInformation;
        }
    }


    /**
     * Generel
     */
    User currentUser;
    private AlertDialog.Builder alertDialogBuilder;

    public void serverUpdateUser(int intPos) {

        User user = clientArrAllUser.get(intPos);

        try {
            String strTitle = etUMTitle.getText().toString();
            String strFirstName = etUMFirstName.getText().toString();
            String strLastName = etUMLastName.getText().toString();
            String strPicture = etUMTitle.getText().toString();
            String strUserName = etUMUserName.getText().toString();
            String strPassword = etUMPassword.getText().toString();

            int intUserID = user.getIntUserID();
            int intEmployeeID = user.getIntEmployeeID();
            int intPictureID = user.getIntPictureID();

            int intRoleID = user.getIntRoleID();
            int intConfidentialID = user.getIntConfidentialID();

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

        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err += "";


            MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Error_UpdateUser).toString() + ex.getMessage());
            msgSys.setType(MessageType.Error);
        }
    }


    public void deleteEmployee() {

        if (currentUser.getIntEmployeeID() == userCurrentSelectedUm.getIntEmployeeID()) {
            alertDialogBuilder.setTitle(mainActivity.getText(R.string.Delete_Yourself_Title).toString());
            alertDialogBuilder.setMessage(mainActivity.getText(R.string.Delete_Yourself_Text).toString());
            alertDialogBuilder.setPositiveButton(mainActivity.getText(R.string.alertD_YES), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Toast.makeText(mainActivity, mainActivity.getText(R.string.Delete_Yourself_Text2).toString(), Toast.LENGTH_SHORT).show();
                }
            });

            alertDialogBuilder.setNegativeButton(mainActivity.getText(R.string.alertD_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Toast.makeText(mainActivity, mainActivity.getText(R.string.Delete_Yourself_Text3).toString(), Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            alertDialogBuilder.setTitle(mainActivity.getText(R.string.Delete_User_Title).toString());
            alertDialogBuilder.setMessage(mainActivity.getText(R.string.Delete_User_Text).toString());
            alertDialogBuilder.setPositiveButton(mainActivity.getText(R.string.alertD_YES), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {


                    serverDeleteUser(userCurrentSelectedUm.getIntEmployeeID()
                            , userCurrentSelectedUm.getIntUserID()
                            , userCurrentSelectedUm.getIntPictureID());
                }
            });
            alertDialogBuilder.setNegativeButton(mainActivity.getText(R.string.alertD_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                    Toast.makeText(mainActivity, mainActivity.getText(R.string.Suc_Delete_User).toString(), Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


            allEmployees.remove(userCurrentSelectedUm);
        }

    }

    public void serverInsertUser(String strTitle, String strFirstName, String strLastName, String strUsername, String strPassword, String strPicture, int intRoleID, int intConfidentialInfoID) {
        try {

            int intUserID = pepperDB.insertNewUser(strUsername, strPassword);
            int intPictureID = -1;
            if (intUserID > 0) {
                intPictureID = pepperDB.insertNewPicture(strPicture);

                if (intPictureID < 0) {
                    MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Error_tblPicture).toString());
                    showInformation(msgSys);
                }

                pepperDB.insertNewEmployee(strTitle, strFirstName, strLastName, intUserID, intPictureID, intRoleID, intConfidentialInfoID);

                MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Suc_Employee).toString());
                msgSys.setType(MessageType.Suc_IUD);
                showInformation(msgSys);


            } else {
                MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Error_tblPicture).toString());

                showInformation(msgSys);
            }

        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err += "";

            MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Error_Something).toString() + ex.getMessage());
            msgSys.setType(MessageType.Error);

            showInformation(msgSys);
        }
    }


    public void serverDeleteUser(int intUserID, int intEmployeeID, int intPictureID) {
        try {

            MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Suc_Delete).toString());
            msgSys.setType(MessageType.Suc_IUD);

            showInformation(msgSys);
            pepperDB.deleteEmployeeData(intEmployeeID, intPictureID, intUserID);
        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err += "";



            MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Error_Delete).toString() + ex.getMessage());
            msgSys.setType(MessageType.Error);

            showInformation(msgSys);
        }
    }

    /**
     * New User
     */

    //New User Controlls
    EditText etNuFirstName, etNuTitle, etNuLastName, etNuPassword, etNuUserName;
    String strNewUserPicture;
    Spinner spRole;
    int intNuRoleID, intNuTitleID;
    ImageButton ibNewPicture;
    RadioGroup rg_Nu_Confidential;
    RadioButton rb_Nu_RConfidentalInfo, rb_Nu_NConfidentalInfo;

    public void addNewUser() {
        try {

            int intCheckedID = rg_Nu_Confidential.getCheckedRadioButtonId();


            if (intCheckedID == rb_Nu_NConfidentalInfo.getId()) {
                serverInsertUser(etNuTitle.getText().toString()
                        , etNuFirstName.getText().toString()
                        , etNuLastName.getText().toString()

                        , newUserPictureChecker(strNewUserPicture)


                        , etNuUserName.getText().toString()
                        , etNuPassword.getText().toString()

                        , (int) spRole.getSelectedItemId()
                        , 2
                );
            } else if (intCheckedID == rb_Nu_RConfidentalInfo.getId()) {
                serverInsertUser(etNuTitle.getText().toString()
                        , etNuFirstName.getText().toString()
                        , etNuLastName.getText().toString()

                        , newUserPictureChecker(strNewUserPicture)


                        , etNuUserName.getText().toString()
                        , etNuPassword.getText().toString()

                        , (int) spRole.getSelectedItemId()
                        , 1
                );

            }

        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err += "";

            Toast.makeText(mainActivity, mainActivity.getText(R.string.Error_Something).toString() + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void clearNewUser() {
        etNuTitle.setText("");
        etNuFirstName.setText("");
        etNuLastName.setText("");
        strNewUserPicture = "";
        spRole.setSelection(1);
        etNuUserName.setText("");
        etNuPassword.setText("");
        ibNewPicture.setImageDrawable(null);
    }

    public String newUserPictureChecker(String strNewUserPicture) {
        String strEmpty = "NoPicture";
        if (strNewUserPicture != null) {
            if (!strNewUserPicture.isEmpty()) {
                return strNewUserPicture;
            } else {
                return strEmpty;
            }

        } else {
            return strEmpty;
        }
    }

    // New User Setters


    public void setSpRole(Spinner spRole) {
        if (spRole != null) {
            this.spRole = spRole;
        }
    }

    public void setEtNuFirstName(EditText etNuFirstName) {
        if (etNuFirstName != null) {
            this.etNuFirstName = etNuFirstName;
        }
    }

    public void setRgConfidential(RadioGroup rg_Nu_Confidential) {
        this.rg_Nu_Confidential = rg_Nu_Confidential;
    }

    public void setRb_Nu_RConfidentalInfo(RadioButton rb_Nu_RConfidentalInfo) {
        this.rb_Nu_RConfidentalInfo = rb_Nu_RConfidentalInfo;
    }

    public void setRb_Nu_NConfidentalInfo(RadioButton rb_Nu_NConfidentalInfo) {
        this.rb_Nu_NConfidentalInfo = rb_Nu_NConfidentalInfo;
    }


    public void setIBNewPicture(ImageButton ibNewPicture) {
        if (ibNewPicture != null) {
            this.ibNewPicture = ibNewPicture;
        }
    }

    public void setEtNuLastName(EditText etNuLastName) {
        if (etNuLastName != null) {
            this.etNuLastName = etNuLastName;
        }
    }

    public void setEtNuPassword(EditText etNuPassword) {
        if (etNuPassword != null) {
            this.etNuPassword = etNuPassword;
        }
    }

    public void setEtNuUserName(EditText etNuUserName) {
        if (etNuUserName != null) {
            this.etNuUserName = etNuUserName;
        }
    }

    public void setStrNewUserPicture(String strNewUserPicture) {
        if (!strNewUserPicture.isEmpty()) {
            this.strNewUserPicture = strNewUserPicture;
        }
    }


    public void setIntNuRoleID(int intNuRoleID) {
        this.intNuRoleID = intNuRoleID;

    }

    public void setIntNuTitleID(int intNuTitleID) {
        this.intNuTitleID = intNuTitleID;
    }

    public void setEtNuTitle(EditText etNuTitle) {
        if (etNuTitle != null) {
            this.etNuTitle = etNuTitle;
        }
    }


    public void showInformation(Message msgSys) {
        String strMessage;
        if (tvLoginInformation != null) {
            try {
                alertDialogBuilder = new AlertDialog.Builder(mainActivity);

                        alertDialogBuilder.setTitle("PepperMRI");
                        alertDialogBuilder.setMessage(msgSys.toString());
                        alertDialogBuilder.setPositiveButton(mainActivity.getText(R.string.alertD_OK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            } catch (Exception ex) {
                String err = "";
                err = ex.getMessage();
                err += "";
            }
        }
    }

}
