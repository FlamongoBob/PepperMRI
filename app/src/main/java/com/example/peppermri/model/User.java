package com.example.peppermri.model;

public class User {
    private int intUserID;
    private String strTitle;
    private String strFirstname;
    private String strLastname;
    private String strPicture;
    private int intRoleID;

    public User(int intUserID ,String strTitle, String strFirstname, String strLastname, String strPicture, int intRoleID){
        this.intUserID = intUserID;
        this.strTitle = strTitle;
        this.strFirstname = strFirstname;
        this.strLastname = strLastname;
        this.strPicture = strPicture;
        this.intRoleID = intRoleID;
    }

    @Override
    public String toString() {
        return intUserID +'|'+ strTitle+ '|' + strFirstname+ '|' + strLastname+'|'+ strPicture+'|'+ intRoleID;
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

    public String getStrPicture() {
        return strPicture;
    }

    public int getIntRoleID() {
        return intRoleID;
    }

}
