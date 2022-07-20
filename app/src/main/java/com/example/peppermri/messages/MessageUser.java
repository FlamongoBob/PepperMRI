package com.example.peppermri.messages;

import com.example.peppermri.crypto.Decryption;
import com.example.peppermri.crypto.Encryption;

public class MessageUser extends Message {
    private int intEmployeeID;
    private String strTitle;
    private String strFirstname;
    private String strLastname;

    private int intPictureID;
    private String strPicture;

    private int intRoleID;
    private String strRole;

    private int intUserID;
    private String strUserName;
    private String strPassword;

    private int intConfidentialID;
    private int intGetsConfidentialInfo;


    public MessageUser(int intEmployeeID
            , String strTitle
            , String strFirstname
            , String strLastname

            , int intPictureID
            , String strPicture

            , int intRoleID
            , String strRole

            , int intUserID
            , String strUserName
            , String strPassword

            , int intConfidentialID
            , int intGetsConfidentialInfo) {

        super(MessageType.User);

        this.intEmployeeID = intEmployeeID;
        this.strTitle = strTitle;
        this.strFirstname = strFirstname;
        this.strLastname = strLastname;

        this.intPictureID = intPictureID;
        this.strPicture = strPicture;

        this.intRoleID = intRoleID;
        this.strRole = strRole;

        this.intUserID = intUserID;
        this.strUserName = strUserName;
        this.strPassword = strPassword;

        this.intConfidentialID = intConfidentialID;
        this.intGetsConfidentialInfo = intGetsConfidentialInfo;



    }

    @Override
    public String toString() {
        return type.toString() +
                '|' + intEmployeeID +
                '|' + strTitle +
                '|' + strFirstname +
                '|' + strLastname +

                '|' + intPictureID +
                '|' + strPicture +

                '|' + intRoleID +
                '|' + strRole +

                '|' + intUserID +
                '|' + strUserName +
                '|' + strPassword +

                '|' + intConfidentialID+
                '|' + intGetsConfidentialInfo;
    }

    public int getIntEmployeeID() {
        return intEmployeeID;
    }

    public String getStrTitle() {
        return strTitle;
    }

    public String getStrFirstname() {
        return strFirstname;
    }

    public String getStrLastname() {
        return strLastname;
    }

    public int getIntPictureID() {
        return intPictureID;
    }

    public String getStrPicture() {
        return strPicture;
    }

    public int getIntRoleID() {
        return intRoleID;
    }

    public String getStrRole() {
        return strRole;
    }

    public int getIntUserID() {
        return intUserID;
    }

    public String getStrUserName() {
        return strUserName;
    }

    public String getStrPassword() {
        return strPassword;
    }

    public int getIntGetsConfidentialInfo() {
        return intGetsConfidentialInfo;
    }

    public int getIntConfidentialID() {
        return intConfidentialID;
    }
}
