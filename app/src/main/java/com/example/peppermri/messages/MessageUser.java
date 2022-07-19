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
                '|' + strPassword
                +
                '|' + intGetsConfidentialInfo;
    }

}
