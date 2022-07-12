package com.example.peppermri.model;

public class User {
    //private String strUserName;
    //private String strPassword;
    private int intUserID;
    private String strTitle;
    private String strFirstname;
    private String strLastname;

    public User(int intUserID ,String strTitle, String strFirstname, String strLastname){
        this.intUserID = intUserID;
        this.strTitle = strTitle;
        this.strFirstname = strFirstname;
        this.strLastname = strLastname;
    }

    @Override
    public String toString() {
        return intUserID +'|'+ strTitle+ '|' + strFirstname+ '|' + strLastname;
    }
    /*
    public String toStringLoginInfo(){
        return strUserName + '|' + strPassword;
    }
    public String toStringUserinfo(){
        return strTitle+ '|' + strFirstname+ '|' + strLastname;
    }*/

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

    /*
    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public void setStrFirstname(String strFirstname) {
        this.strFirstname = strFirstname;
    }


    public void setStrLastname(String strLastname) {
        this.strLastname = strLastname;
    }
    */

}
