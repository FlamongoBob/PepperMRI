package com.example.peppermri.messages;

import com.example.peppermri.crypto.Decryption;
import com.example.peppermri.crypto.Encryption;

public class MessageUser extends Message{
    private int intUserID;
    private String strUserID;
    private String strTitle;
    private String strFirstname;
    private String strLastname;
    private String strPicture;
    private int intRoleID;
    Encryption e = new Encryption();
    Decryption d = new Decryption();


    public MessageUser(int intUserID,String strTitle, String strFirstname, String strLastname,String strPicture, int intRoleID) {
        super(MessageType.User);
        this.strUserID = e.encrypt(Integer.toString(intUserID));
        this.strTitle = e.encrypt(strTitle);
        this.strFirstname = e.encrypt(strFirstname);
        this.strLastname = e.encrypt(strLastname);
        this.strPicture = e.encrypt(strPicture);
        this.intRoleID = intRoleID;
    }

    @Override
    public String toString() {
        return type.toString() + '|' + strUserID + '|' + strTitle + '|' + strFirstname+ '|' + strLastname+ '|' + strPicture+ '|' + intRoleID;
    }

    public int getIntUserID() {
        return intUserID;
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




}
