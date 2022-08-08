package com.example.peppermri.controller;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

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
    final private String strIPAdress = "127.10.10.15"; //= "10.0.2.15";

    private ArrayList<User> clientArrAllUser = new ArrayList<>();
    Encryption e = new Encryption();

    public Controller(PepperDB pepperDB, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.pepperDB = pepperDB;

        this.alertDialogBuilder = new AlertDialog.Builder(mainActivity);
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

    /**
     * PatientInfo
     */

    String strBufferPatientInfo;
    int intDeniedCounter = 0, intRequestRound = 1, intPossibleResponse = 0;
    boolean blnHasAccepted = false, hasChoiceStarted = false;

    RadioButton rbQuestion1_Yes, rbQuestion1_No, rbQuestion2_Yes, rbQuestion2_No, rbQuestion3_Yes, rbQuestion3_No;
    Button btnDone;
    ImageView ivEmployee;
    TextView tvEmployeeTitle, tvEmployeeFirstName, tvEmployeeLastName, tvEmployeeInfo, tvQuestion1, tvQuestion2, tvQuestion3;


    public void controlsShowOrHideQuestions(int intVisibilityID) {

        tvQuestion1.setVisibility(intVisibilityID);
        tvQuestion2.setVisibility(intVisibilityID);
        tvQuestion3.setVisibility(intVisibilityID);

        //RadioButton
        rbQuestion1_Yes.setVisibility(intVisibilityID);
        rbQuestion1_No.setVisibility(intVisibilityID);

        rbQuestion2_Yes.setVisibility(intVisibilityID);
        rbQuestion2_No.setVisibility(intVisibilityID);

        rbQuestion3_Yes.setVisibility(intVisibilityID);
        rbQuestion3_No.setVisibility(intVisibilityID);

    }

    public void controlsShowOrHideEmployee(int intVisibilityID) {

        ivEmployee.setVisibility(intVisibilityID);
        tvEmployeeTitle.setVisibility(intVisibilityID);
        tvEmployeeFirstName.setVisibility(intVisibilityID);
        tvEmployeeLastName.setVisibility(intVisibilityID);
    }

    public void resetEmployeeAndBool() {
            blnHasAccepted = false;
            hasChoiceStarted = false;

        tvEmployeeTitle.setText("");
        tvEmployeeFirstName.setText("");
        tvEmployeeLastName.setText("");
    }

    public void controlsShowOrHideAll(int intVisibilityID) {
        controlsShowOrHideQuestions(intVisibilityID);
        controlsShowOrHideEmployee(intVisibilityID);
    }


    public void checkConfidentialInfoSender(int intRequestRound) {
        for (int i = 0; i < arrLoggedInUsers.size(); i++) {
            User user = arrLoggedInUsers.get(i);
            if (user.getIntGetsConfidentialInfo() == 1) {
                int intUserID = user.getIntUserID();
                hasChoiceStarted  =true;
                MessageSystem msgSys = new MessageSystem(intRequestRound + mainActivity.getText(R.string.Request_Round).toString());
                msgSys.setType(MessageType.Choose);
                server.sendMessage(msgSys, intUserID);
            }
        }
        hasChoiceStarted = true;
    }

    public void responseConfidentialInfo(int intUserID, MessageType msgType) {
        if (hasChoiceStarted) {
            if (msgType == MessageType.Accept && !blnHasAccepted) {
                blnHasAccepted = sendPatientInformation(intUserID);

                if (blnHasAccepted) {
                    setEmployeeReceivesInfo(intUserID);
                    resetCounters();
                }

            } else if (msgType == MessageType.Accept && blnHasAccepted | msgType == MessageType.Deny && blnHasAccepted) {
                resetCounters();
            } else if (!blnHasAccepted && msgType == MessageType.Deny) {

                if (intPossibleResponse == 0) {
                    for (int i = 0; i < arrLoggedInUsers.size(); i++) {
                        User user = arrLoggedInUsers.get(i);
                        if (user.getIntGetsConfidentialInfo() == 1) {
                            intPossibleResponse++;
                        }
                    }
                }

                if (intPossibleResponse == intDeniedCounter) {
                    intRequestRound++;
                    checkConfidentialInfoSender(intRequestRound);
                }
            }
        }
    }

    public void resetCounters() {
        intPossibleResponse = 0;
        intDeniedCounter = 0;
        intRequestRound = 1;
        hasChoiceStarted=false;
    }

    public boolean sendPatientInformation(int intUserID) {
        try {
            /**TODO boolReturn*/
            MessageSystem msgSys = new MessageSystem(
                    setPatientInfoMessageText()
            );
            msgSys.setType(MessageType.Patient);

            server.sendMessage(msgSys, intUserID);
            return true;
        } catch (Exception ex) {

            String err = "";
            err = ex.getMessage();
            err = "";
            return false;
        }
    }

    public String setPatientInfoMessageText() {

        String strMessageCode = "";

        if (rbQuestion1_Yes.isChecked()) {
            strMessageCode += "1";
        } else {
            strMessageCode += "0";
        }

        if (rbQuestion2_Yes.isChecked()) {
            strMessageCode += "1";
        } else {
            strMessageCode += "0";
        }

        if (rbQuestion3_Yes.isChecked()) {
            strMessageCode += "1";
        } else {
            strMessageCode += "0";
        }

        return strMessageCode;
    }


    public void setEmployeeReceivesInfo(int intUserID) {
        User user = getUserFromArrLoggedIn(intUserID);
        if (user != null) {
            String strEmployeeTitle = user.getStrTitle();
            tvEmployeeTitle.setText(strEmployeeTitle);

            String strEmployeeFirstName = user.getStrFirstname();
            tvEmployeeFirstName.setText(strEmployeeFirstName);

            String strEmployeeLastName = user.getStrLastname();
            tvEmployeeLastName.setText(strEmployeeLastName);

            String strEmployeePicture = user.getStrPicture();
            Bitmap bmPicture = StringToBitMap(strEmployeePicture);
            if (bmPicture != null) {
                setPicture(bmPicture, ivEmployee);
            }
        }
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            String strSubstring = encodedString.substring(0, 9);
            if (!strSubstring.equals("NoPicture")) {
                if (!encodedString.isEmpty()) {
                    byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    return bitmap;

                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public void setPicture(Bitmap bmpPicture, ImageView ivPicture) {
        if (ivPicture != null && bmpPicture != null) {
            ivPicture.setImageBitmap(bmpPicture);
        }
    }

    public User getUserFromArrLoggedIn(int intUserID) {
        for (int i = 0; i < arrLoggedInUsers.size(); i++) {
            User user = arrLoggedInUsers.get(i);
            if (user.getIntUserID() == intUserID) {
                return user;
            }
        }
        return null;
    }

    public boolean isBlnHasAccepted() {
        return blnHasAccepted;
    }

    public void setIvEmployee(ImageView ivEmployee) {
        this.ivEmployee = ivEmployee;
    }

    public void setTvEmployeeTitle(TextView tvEmployeeTitle) {
        this.tvEmployeeTitle = tvEmployeeTitle;
    }

    public void setTvEmployeeFirstName(TextView tvEmployeeFirstName) {
        this.tvEmployeeFirstName = tvEmployeeFirstName;
    }

    public void setTvEmployeeLastName(TextView tvEmployeeLastName) {
        this.tvEmployeeLastName = tvEmployeeLastName;
    }

    public void setTvEmployeeInfo(TextView tvEmployeeInfo) {
        this.tvEmployeeInfo = tvEmployeeInfo;
    }

    public void setTvQuestion1(TextView tvQuestion1) {
        this.tvQuestion1 = tvQuestion1;
    }

    public void setTvQuestion2(TextView tvQuestion2) {
        this.tvQuestion2 = tvQuestion2;
    }

    public void setTvQuestion3(TextView tvQuestion3) {
        this.tvQuestion3 = tvQuestion3;
    }

    public void setRbQuestion1_Yes(RadioButton rbQuestion1_Yes) {
        this.rbQuestion1_Yes = rbQuestion1_Yes;
    }

    public void setRbQuestion1_No(RadioButton rbQuestion1_No) {
        this.rbQuestion1_No = rbQuestion1_No;
    }

    public void setRbQuestion2_Yes(RadioButton rbQuestion2_Yes) {
        this.rbQuestion2_Yes = rbQuestion2_Yes;
    }

    public void setRbQuestion2_No(RadioButton rbQuestion2_No) {
        this.rbQuestion2_No = rbQuestion2_No;
    }

    public void setRbQuestion3_Yes(RadioButton rbQuestion3_Yes) {
        this.rbQuestion3_Yes = rbQuestion3_Yes;
    }

    public void setRbQuestion3_No(RadioButton rbQuestion3_No) {
        this.rbQuestion3_No = rbQuestion3_No;
    }

    /**
     * Client Response Messages
     */
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

            MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Suc_Update).toString());
            msgSys.setType(MessageType.Suc_IUD);
            server.sendMessage(msgSys, intUserID);

        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err += "";


            MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Error_UpdateUser).toString() + ex.getMessage());
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
        pepperDB.getAllRoles(true, intUserID);
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
    ImageView ivUMPicture;
    RadioGroup rgConfidentialUM;
    RadioButton rb_RConfidentialUM, rb_NConfidentialUM;
    User userCurrentSelectedUm;

    private ArrayList<User> serverArrAllUser = new ArrayList<>();
    ArrayList<String> arrRoles = new ArrayList<>();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    public void populateArrRoles(String strRole) {
        arrRoles.add(strRole);
    }

    public void serverCollectAllUser(User user) {
        serverArrAllUser.add(user);
    }

    public void serverGetRoles() {
        arrRoles.clear();
        pepperDB.getAllRoles(false, -1);
    }


    public void serverGetAllEmployeeData() {
        serverArrAllUser.clear();
        pepperDB.selectAllEmployeeData(false, loggedInUser.getIntUserID());
    }


    public int starFillUserManagement(int intPos) {
        if (intPos >= serverArrAllUser.size()) {
            intPos = 0;
        }

        if (intPos < 0) {

            intPos = serverArrAllUser.size() - 1;
        }

        userCurrentSelectedUm = serverArrAllUser.get(intPos);
        populateUserManagementControlls(userCurrentSelectedUm);
        return intPos;
    }

    private void populateUserManagementControlls(User user) {
        etUMTitle.setText(user.getStrTitle());
        etUMFirstName.setText(user.getStrFirstname());
        etUMLastName.setText(user.getStrLastname());


        String strEmployeePicture = user.getStrPicture();
        Bitmap bmPicture = StringToBitMap(strEmployeePicture);
        if (bmPicture != null) {
            setPicture(bmPicture, ivUMPicture);
        }

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


    //User Management Setters

    public void setEtUMFirstName(EditText etUMFirstName) {
        if (etUMFirstName != null) {
            this.etUMFirstName = etUMFirstName;
        }
    }

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
            serverSetRoles(spUMRole);
        }
    }


    public void setUMNewPicture(ImageView ivUMPicture) {
        if (ivUMPicture != null) {
            this.ivUMPicture = ivUMPicture;
        }
    }

    public void setRb_RConfidentialUM(RadioButton rb_RConfidentialUM) {
        this.rb_RConfidentialUM = rb_RConfidentialUM;
    }

    public void setRb_NConfidentialUM(RadioButton rb_NConfidentialUM) {
        this.rb_NConfidentialUM = rb_NConfidentialUM;
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
    public boolean isLoggedIn = false;

    public User getLoggedInUser() {
        return loggedInUser;
    }

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

    public void serverLogout() {
        loggedInUser = null;
        isLoggedIn = false;
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


    public void serverDeleteEmployee() {

        if (loggedInUser.getIntEmployeeID() == userCurrentSelectedUm.getIntEmployeeID()) {
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

            pepperDB.deleteEmployeeData(intEmployeeID, intPictureID, intUserID);
            MessageSystem msgSys = new MessageSystem(mainActivity.getText(R.string.Suc_Delete).toString());
            msgSys.setType(MessageType.Suc_IUD);

            showInformation(msgSys);
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
    Spinner spNURole;
    int intNuRoleID, intNuTitleID;
    ImageButton ibNewPicture;
    RadioGroup rg_Nu_Confidential;
    RadioButton rb_Nu_RConfidentalInfo, rb_Nu_NConfidentalInfo;

    public void addNewUser() {
        try {
            long intName = spNURole.getSelectedItemId();


            int intCheckedID = rg_Nu_Confidential.getCheckedRadioButtonId();

            if (intCheckedID == rb_Nu_NConfidentalInfo.getId()) {
                serverInsertUser(etNuTitle.getText().toString()
                        , etNuFirstName.getText().toString()
                        , etNuLastName.getText().toString()


                        , etNuUserName.getText().toString()
                        , etNuPassword.getText().toString()


                        , newUserPictureChecker(strNewUserPicture)

                        , ((int) spNURole.getSelectedItemId()) + 1
                        , 2
                );
            } else if (intCheckedID == rb_Nu_RConfidentalInfo.getId()) {
                serverInsertUser(etNuTitle.getText().toString()
                        , etNuFirstName.getText().toString()
                        , etNuLastName.getText().toString()


                        , etNuUserName.getText().toString()
                        , etNuPassword.getText().toString()

                        , newUserPictureChecker(strNewUserPicture)

                        , ((int) spNURole.getSelectedItemId()) + 1
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

    public void serverSetRoles(Spinner spRole) {
        serverGetRoles();
        populateSpinner(spRole, arrRoles);

    }

    public void populateSpinner(Spinner spinner, ArrayList arrayList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_spinner_item, arrayList);
        spinner.setAdapter(adapter);
    }


    public void clearNewUser() {
        etNuTitle.setText("");
        etNuFirstName.setText("");
        etNuLastName.setText("");
        strNewUserPicture = "";
        spNURole.setSelection(1);
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


    public void setSpNURole(Spinner spNURole) {
        if (spNURole != null) {
            this.spNURole = spNURole;
            serverSetRoles(spNURole);
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

                alertDialogBuilder.setTitle("Pepper MRI");
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
