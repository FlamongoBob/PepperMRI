package com.example.peppermri.pepperDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.peppermri.MainActivity;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.messages.MessageType;
import com.example.peppermri.model.User;

public class PepperDB {
    myDbHelper myhelper;
    MainActivity mainActivity;

    public PepperDB(Context context, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        myhelper = new myDbHelper(context);
    }
    //TABLE USER

    public String getStatementInsertTblUser(String strUserName, String strPassword) {
        return "INSERT INTO tblUser" +
                " VALUES(NULL" +
                ", '" + strUserName + "'" +
                ", '" + strPassword + "'" +
                ");";
    }

    public String getStatementUpdateTblUser(int intUserID, String strUserName, String strPassword) {
        return "UPDATE tblUser" +
                " SET" +
                " strUserName = '" + strUserName + "'" +
                ", strPassword = '" + strPassword + "'" +
                " WHERE intUserID = " + intUserID + ";";
    }

    public String getStatementDeleteTblUser(int intUserID) {
        return "DELETE FROM tblUser" +
                " WHERE intUserID = " + intUserID + ";";
    }

    public String selectIntUserID(String strUserName, String strPassword) {
        return "SELECT intUserID" +
                " FROM tblUser" +
                " WHERE strUserName = '" + strUserName + "'" +
                " AND strPassword = '" + strPassword + "';";
    }

    public int insertNewUser(String strUserName, String strPassword) {
        String insertStatement = getStatementInsertTblUser(strUserName, strPassword);

        SQLiteDatabase db = this.myhelper.getWritableDatabase();
        db.execSQL(insertStatement);
        int intUserID = -1;
        Cursor cursorUserID = db.rawQuery(selectIntUserID(strUserName, strPassword), null);

        if (cursorUserID.moveToFirst()) {
            intUserID = cursorUserID.getInt(0);

            cursorUserID.close();
            return intUserID;
        }
        return intUserID;
    }

    public void updateTblUser(int intUserID, String strUserName, String strPassword) {
        String updateStatement = getStatementUpdateTblUser(intUserID, strUserName, strPassword);

        SQLiteDatabase db = this.myhelper.getWritableDatabase();
        db.execSQL(updateStatement);

    }

    public void deleteFromTblUser(int intUserID) {
        String deleteStatement = getStatementDeleteTblUser(intUserID);

        SQLiteDatabase db = this.myhelper.getWritableDatabase();
        db.execSQL(deleteStatement);
    }

    /**
     * TABLE PICTURE
     */
    public String getStatementInsertTblPicture(String strPicture) {
        return "INSERT INTO tblPicture" +
                " VALUES(NULL" +
                ", '" + strPicture + "'" +
                ");";
    }

    public String getStatementUpdateTblPicture(int intPictureID, String strPicture) {
        return "UPDATE tblPicture" +
                " SET" +
                " strPicture = '" + strPicture + "'" +
                " WHERE intPictureID = " + intPictureID + ";";
    }

    public String getStatementDeleteTblPicture(int intPictureID) {
        return "DELETE FROM tblPicture" +
                " WHERE intPictureID = " + intPictureID + ";";
    }

    public String selectIntPictureID(String strPicture) {
        return "SELECT intPictureID" +
                " FROM tblPicture" +
                " WHERE strPicture = '" + strPicture + "';";
    }

    public int insertNewPicture(String strPicture) {
        String insertStatement = getStatementInsertTblPicture(strPicture);

        SQLiteDatabase db = this.myhelper.getWritableDatabase();
        db.execSQL(insertStatement);
        int intPictureID = -1;
        Cursor cursorPicture = db.rawQuery(selectIntPictureID(strPicture), null);

        if (cursorPicture.moveToFirst()) {
            intPictureID = cursorPicture.getInt(0);

            cursorPicture.close();
            return intPictureID;
        }
        return intPictureID;
    }

    public void updateTblPicture(int intPictureID, String strPicture) {
        String updateStatement = getStatementUpdateTblPicture(intPictureID, strPicture);

        SQLiteDatabase db = this.myhelper.getWritableDatabase();
        db.execSQL(updateStatement);

    }

    public void deleteFromTblPicture(int intPictureID) {
        String deleteStatement = getStatementDeleteTblPicture(intPictureID);

        SQLiteDatabase db = this.myhelper.getWritableDatabase();
        db.execSQL(deleteStatement);
    }


    public String getStatementSelectConfidentialInfoID(int intGetsConfidentialInfo) {
        return "SELECT intRConfidentialInfoID" +
                " FROM tblReceivesConfidentialInfo" +
                " WHERE getConfidentialInfo = " + intGetsConfidentialInfo + ";";

    }

    public String insertTBLReceivesConfidentialInfo(int intValue) {
        return "INSERT INTO tblReceivesConfidentialInfo" +
                "VALUES(NULL" +
                ", " + intValue +
                ");";
    }

    public String getStatementSelectRoleID(String strRole) {
        return "SELECT intRoleID" +
                " FROM tblRole" +
                " WHERE strRole = '" + strRole + "';";

    }

    public String getStatementSelectAllRoles() {
        return "SELECT *" +
                " FROM tblRole;";

    }

    public void getAllRoles(boolean blnIsClient,int intUserID) {

        SQLiteDatabase db = this.myhelper.getReadableDatabase();

        Cursor cursorRole = db.rawQuery(getStatementSelectAllRoles(), null);


        //ArrayList<User> dbData = new ArrayList<>();
        String strRole = "";
        int intRoleID = -1;

        Controller controller = mainActivity.getController();
        if (cursorRole.moveToFirst()) {
            User user;
            do {
                intRoleID = cursorRole.getInt(0);
                strRole = cursorRole.getString(1);
                if(blnIsClient) {
                    controller.clientSendRoles(intRoleID, strRole, intUserID);
                }else {
                    controller.populateArrRoles(strRole);
                }
            } while (cursorRole.moveToNext());
            cursorRole.close();
        }
    }


    public String insertTBLROLE(String strRoleName) {
        return "INSERT INTO tblRole" +
                "VALUES(NULL" +
                ", '" + strRoleName + "'" +
                ");";
    }


    /**
     * TABLE Employee
     */
    public String getStatementInsertTblEmployee(String strTitle
            , String strFirstName
            , String strLastName
            , int intUserID
            , int intPictureID
            , int intRoleID
            , int intConfidentialInfoID) {

        return "INSERT INTO tblEmployee" +
                " VALUES (NULL " +
                ", '" + strTitle + "'" +
                ", '" + strFirstName + "'" +
                ", '" + strLastName + "'" +
                ", " + intUserID +
                ", " + intPictureID +
                ", " + intRoleID +
                ", " + intConfidentialInfoID + ");";
    }

    public String getStatementUpdateTblEmployee(int intEmployeeID
            , String strTitle
            , String strFirstName
            , String strLastName
            , int intRoleID
            , int intConfidentialID) {

        return "UPDATE tblEmployee" +
                " SET" +
                " strEmployeeTitle = '" + strTitle + "'" +
                ", strFirstName = '" + strFirstName + "'" +
                ", strLastName = '" + strLastName + "'" +
                ", intRoleID = " + intRoleID +
                ", intRConfidentialInfoID = '" + intConfidentialID + "'" +
                " WHERE intEmployeeID = " + intEmployeeID + ";";
    }

    public String getStatementDeleteTblEmployee(int intEmployeeID) {
        return "DELETE FROM tblEmployee" +
                " WHERE intEmployeeID = " + intEmployeeID + ";";
    }

    public void insertNewEmployee(String strTitle
            , String strFirstName
            , String strLastName
            , int intUserID
            , int intPictureID
            , int intRoleID
            , int intConfidentialInfoID) {

        String insertStatement = getStatementInsertTblEmployee(strTitle, strFirstName, strLastName, intUserID, intPictureID, intRoleID, intConfidentialInfoID);

        SQLiteDatabase db = this.myhelper.getWritableDatabase();
        db.execSQL(insertStatement);
    }

    public void updateTblEmployee(int intEmployeeID
            , String strTitle
            , String strFirstName
            , String strLastName
            , int intRoleID
            , int intConfidentialID) {

        String updateStatement = getStatementUpdateTblEmployee(intEmployeeID, strTitle, strFirstName, strLastName, intRoleID, intConfidentialID);

        SQLiteDatabase db = this.myhelper.getWritableDatabase();
        db.execSQL(updateStatement);

    }

    public void deleteFromTblEmployee(int intEmployeeID) {
        String deleteStatement = getStatementDeleteTblEmployee(intEmployeeID);

        SQLiteDatabase db = this.myhelper.getWritableDatabase();
        db.execSQL(deleteStatement);
    }

    public void deleteEmployeeData(int intEmployeeID, int intPictureID, int intUserID) {

        deleteFromTblUser(intUserID);
        deleteFromTblPicture(intPictureID);
        deleteFromTblEmployee(intEmployeeID);

    }

    public void updateEmployeeData(int intEmployeeID
            , String strTitle
            , String strFirstName
            , String strLastName
            , int intRoleID
            , int intConfidentialID
            , int intPictureID
            , String strPicture
            , int intUserID
            , String strUserName
            , String strPassword) {

        updateTblUser(intUserID
                , strUserName
                , strPassword);

        updateTblPicture(intPictureID
                , strPicture);

        updateTblEmployee(intEmployeeID
                , strTitle
                , strFirstName
                , strLastName
                , intRoleID
                , intConfidentialID);


    }

    public void selectAllEmployeeData(boolean blnIsClientDemand, int intUserID) {

        SQLiteDatabase db = this.myhelper.getReadableDatabase();
        Cursor cursorselectAll = db.rawQuery("Select E.intEmployeeID AS intGlobalID" +
                ", E.strEmployeeTitle AS strTitle" +
                ", E.strFirstName AS strFirstName" +
                ", E.strLastName AS strLastName" +

                " , P.intPictureID AS intPictureID" +
                " , P.strPicture AS strPicture" +
                " , R.intRoleID AS intRoleID" +
                " , R.strRole As strRole" +
                " , U.intUserID AS intUserID" +
                " , U.strUserName As strUserName" +
                " , U.strPassword AS strPassword" +
                " , C.intRConfidentialInfoID AS intRConfidentialInfoID" +
                " , C.getsConfidentialInfo AS getsConfidentialInfo" +
                " FROM tblEmployee AS E" +
                "    INNER JOIN tblUser AS U" +
                "    ON E.intUserID = U.intUserID" +
                "    INNER JOIN tblPicture AS P" +
                "    ON E.intPictureID = P.intPictureID" +
                "    INNER JOIN tblRole AS R" +
                "    ON E.intRoleID = R.intRoleID " +
                "    INNER JOIN tblRConfidentialInfo AS C" +
                "    ON E.intRConfidentialInfoID = C.intRConfidentialInfoID ;", null);

        //ArrayList<User> dbData = new ArrayList<>();

        if (cursorselectAll.moveToFirst()) {
            Controller controller = mainActivity.getController();
            User user;
            do {
                user = new User(
                        cursorselectAll.getInt(0)
                        , cursorselectAll.getString(1)
                        , cursorselectAll.getString(2)
                        , cursorselectAll.getString(3)

                        , cursorselectAll.getInt(4)
                        , cursorselectAll.getString(5)

                        , cursorselectAll.getInt(6)
                        , cursorselectAll.getString(7)

                        , cursorselectAll.getInt(8)
                        , cursorselectAll.getString(9)
                        , cursorselectAll.getString(10)

                        , cursorselectAll.getInt(11)
                        , cursorselectAll.getInt(12)
                );
                if(blnIsClientDemand) {
                    controller.clientSendUser(user, intUserID, MessageType.AllUser);

                }else {
                    controller.serverCollectAllUser(user);
                }
            } while (cursorselectAll.moveToNext());
        }
    }

    public String Check() {
        String strCheck = "";
        SQLiteDatabase db = this.myhelper.getReadableDatabase();
        Cursor cursorLoginCred = db.rawQuery("Select * FROM tblUser", null);

        //ArrayList<User> dbData = new ArrayList<>();

        if (cursorLoginCred.moveToFirst()) {
            strCheck += cursorLoginCred.getString(0);

        }

        cursorLoginCred.close();
        return strCheck;
    }

    public String checkLoginString(String strUserName, String strPassword) {
        return "Select E.intEmployeeID AS intGlobalID" +
                ", E.strEmployeeTitle AS strTitle" +
                ", E.strFirstName AS strFirstName" +
                ", E.strLastName AS strLastName" +

                " , P.intPictureID AS intPictureID" +
                " , P.strPicture AS strPicture" +

                " , R.intRoleID AS intRoleID" +
                " , R.strRole As strRole" +

                " , U.intUserID AS intUserID" +
                " , U.strUserName As strUserName" +
                " , U.strPassword AS strPassword" +

                " , C.intRConfidentialInfoID AS intRConfidentialInfoID" +
                " , C.getsConfidentialInfo AS getsConfidentialInfo" +

                " FROM tblEmployee AS E" +

                " INNER JOIN tblUser AS U" +
                " ON E.intUserID = U.intUserID" +

                " INNER JOIN tblPicture AS P" +
                " ON E.intPictureID = P.intPictureID" +

                " INNER JOIN tblRole AS R" +
                " ON E.intRoleID = R.intRoleID" +

                " INNER JOIN tblRConfidentialInfo AS C" +
                " ON E.intRConfidentialInfoID = C.intRConfidentialInfoID" +

                " WHERE U.strUserName = '" + strUserName + "' AND U.strPassword = '" + strPassword + "';";
    }

    public String checkLoginStringWithoutPicture(String strUserName, String strPassword) {
        return "Select E.intEmployeeID AS intGlobalID" +
                ", E.strEmployeeTitle AS strTitle" +
                ", E.strFirstName AS strFirstName" +
                ", E.strLastName AS strLastName" +

                " , R.intRoleID AS intRoleID" +
                " , R.strRole As strRole" +

                " , U.intUserID AS intUserID" +
                " , U.strUserName As strUserName" +
                " , U.strPassword AS strPassword" +

                " , C.intRConfidentialInfoID AS intRConfidentialInfoID" +
                " , C.getsConfidentialInfo AS getsConfidentialInfo" +

                " FROM tblEmployee AS E" +

                " INNER JOIN tblUser AS U" +
                " ON E.intUserID = U.intUserID" +

                " INNER JOIN tblRole AS R" +
                " ON E.intRoleID = R.intRoleID" +

                " INNER JOIN tblRConfidentialInfo AS C" +
                " ON E.intRConfidentialInfoID = C.intRConfidentialInfoID" +

                " WHERE U.strUserName = '" + strUserName + "' AND U.strPassword = '" + strPassword + "';";
    }


    public User checkLoginCredential(String strUserName, String strPassword) {

        SQLiteDatabase db = this.myhelper.getReadableDatabase();
        Cursor cursorLoginCred = db.rawQuery(checkLoginString(strUserName, strPassword), null);

        //ArrayList<User> dbData = new ArrayList<>();
        User user;
        if (cursorLoginCred.moveToFirst()) {
            user = new User(
                    cursorLoginCred.getInt(0)
                    , cursorLoginCred.getString(1)
                    , cursorLoginCred.getString(2)
                    , cursorLoginCred.getString(3)

                    , cursorLoginCred.getInt(4)
                    , cursorLoginCred.getString(5)

                    , cursorLoginCred.getInt(6)
                    , cursorLoginCred.getString(7)

                    , cursorLoginCred.getInt(8)
                    , cursorLoginCred.getString(9)
                    , cursorLoginCred.getString(10)

                    , cursorLoginCred.getInt(11)
                    , cursorLoginCred.getInt(12)
            );
            cursorLoginCred.close();
            return user;

        } else {
            String strLogin = checkLoginStringWithoutPicture(strUserName, strPassword);
            cursorLoginCred = db.rawQuery(strLogin, null);

            if (cursorLoginCred.moveToFirst()) {

                user = new User(
                        cursorLoginCred.getInt(0)
                        , cursorLoginCred.getString(1)
                        , cursorLoginCred.getString(2)
                        , cursorLoginCred.getString(3)

                        , -1
                        , ""

                        , cursorLoginCred.getInt(4)
                        , cursorLoginCred.getString(5)

                        , cursorLoginCred.getInt(6)
                        , cursorLoginCred.getString(7)
                        , cursorLoginCred.getString(8)

                        , cursorLoginCred.getInt(9)
                        , cursorLoginCred.getInt(10)
                );

                cursorLoginCred.close();
                return user;
            }

            cursorLoginCred.close();
            return null;
        }

    }


    static class myDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "Pepper_MRI_DB";    // Database Name
        private static final String TABLE_NAME = "myTable";   // Table Name
        private static final int DATABASE_Version = 2;    // Database Version

        private static final String CREATE_TABLE_EMPLOYEE =
                "CREATE TABLE tblEmployee" +
                        " (intEmployeeID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" +
                        ", strEmployeeTitle VARCHAR(255) " +
                        ", strFirstName  VARCHAR(225) NOT NULL" +
                        ", strLastName  VARCHAR(225) NOT NULL" +
                        ", intUserID  INTEGER NOT NULL" +
                        ", intPictureID  INTEGER" +
                        ", intRoleID  INTEGER NOT NULL" +
                        ", intRConfidentialInfoID  INTEGER NOT NULL" +
                        ", FOREIGN KEY (intUserID) REFERENCES tblUser(intUserID)" +
                        ", FOREIGN KEY (intPictureID) REFERENCES tblPicture(intPictureID)" +
                        ", FOREIGN KEY (intRoleID) REFERENCES tblRole(intRoleID)" +
                        ", FOREIGN KEY (intRConfidentialInfoID) REFERENCES tblReceivesConfidentialInfo(intRConfidentialInfoID)" +
                        ");";

        public static final String insertAdminEmployee = "INSERT INTO tblEmployee" +
                " VALUES ( NULL " +
                ", 'Dr. Med. ADMIM'" +
                ", 'ADMIN'" +
                ", 'ADMIN'" +
                ", '1'" +
                ", 1" +
                ", 1" +
                ", '1'" +
                ");";

        public static final String insertAdminPic = "INSERT INTO tblPicture" +
                " VALUES ( NULL" +
                ", 'NoPicture' );";

        private static final String CREATE_TABLE_USER = "CREATE TABLE tblUser" +
                " (intUserID INTEGER NOT NUll PRIMARY KEY AUTOINCREMENT" +
                ", strUserName VARCHAR(255) NOT NUll " +
                ", strPassword  VARCHAR(225) NOT NUll );";

        private static final String INSERT_TBL_USER = "INSERT INTO tblUser VALUES(NULL , 'ADMIN','ADMIN');";

        private static final String CREATE_TABLE_PICTURE = "CREATE TABLE tblPicture" +
                " (intPictureID INTEGER NOT NUll PRIMARY KEY AUTOINCREMENT" +
                ", strPicture VARCHAR(255) NOT NUll );";

        private static final String CREATE_TABLE_ROLE = "CREATE TABLE tblRole" +
                " (intRoleID INTEGER NOT NUll PRIMARY KEY AUTOINCREMENT" +
                ", strRole VARCHAR(255) NOT NUll );";

        private static final String INSERT_TBL_ROLE1 = "INSERT INTO tblRole VALUES(NULL, 'Admin');";
        private static final String INSERT_TBL_ROLE2 = "INSERT INTO tblRole VALUES(NULL , 'User');";

        private static final String CREATE_TABLE_RECEIVES_CONFIDENTIAL_INFO = "CREATE TABLE tblRConfidentialInfo" +
                " (intRConfidentialInfoID INTEGER NOT NUll PRIMARY KEY AUTOINCREMENT" +
                ", getsConfidentialInfo BIT NOT NUll " +
                ", strINFO VARCHAR(255) NOT NUll );";

        private static final String INSERT_TBL_REC_CONFIDENTIAL_INFO1 = "" +
                "INSERT INTO tblRConfidentialInfo VALUES(NULL , 1, '1 = Receive');";
        private static final String INSERT_TBL_REC_CONFIDENTIAL_INFO2 = "" +
                "INSERT INTO tblRConfidentialInfo VALUES(NULL , 0, '0 = Not Receive');";

        private static final String CREATE_TABLE_DOCUMENTS =
                "CREATE TABLE tblDocument" +
                        " (intDocumentID INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", strDocumentName VARCHAR(255) NOT NUll " +
                        ", strPath  VARCHAR(225) NOT NUll" +
                        ", intTypeID  INTEGER NOT NUll" +
                        ", intLanguageID  INTEGER NOT NUll" +
                        ", FOREIGN KEY (intTypeID) REFERENCES tblType(intTypeID)" +
                        ", FOREIGN KEY (intLanguageID) REFERENCES tblLanguage(intLanguageID));";

        private static final String CREATE_TABLE_LANGUAGE = "CREATE TABLE tblLanguage" +
                " (intLanguageID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", strLanguage VARCHAR(255) );";

        private static final String CREATE_TABLE_TYPE = "CREATE TABLE tblType" +
                " (intTypeID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", strType VARCHAR(255) );";


        private static final String DROP_TABLE = "DROP TABLE IF EXISTS  tblDocument";
        private static final String DROP_TABLE1 = "DROP TABLE IF EXISTS  tblLanguage";
        private static final String DROP_TABLE2 = "DROP TABLE IF EXISTS  tblType";
        private static final String DROP_TABLE3 = "DROP TABLE IF EXISTS  tblUser";
        private static final String DROP_TABLE4 = "DROP TABLE IF EXISTS  tblRole";
        private static final String DROP_TABLE5 = "DROP TABLE IF EXISTS  tblEmployee";
        private static final String DROP_TABLE6 = "DROP TABLE IF EXISTS  tblPicture";
        private static final String DROP_TABLE7 = "DROP TABLE IF EXISTS  tblConfidentialInfo";

        private Context context;


        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                //USER RELATED
                db.execSQL(CREATE_TABLE_USER);
                db.execSQL(INSERT_TBL_USER);

                db.execSQL(CREATE_TABLE_RECEIVES_CONFIDENTIAL_INFO);
                db.execSQL(INSERT_TBL_REC_CONFIDENTIAL_INFO1);
                db.execSQL(INSERT_TBL_REC_CONFIDENTIAL_INFO2);

                db.execSQL(CREATE_TABLE_PICTURE);
                db.execSQL(insertAdminPic);

                db.execSQL(CREATE_TABLE_ROLE);
                db.execSQL(INSERT_TBL_ROLE1);
                db.execSQL(INSERT_TBL_ROLE2);

                db.execSQL(CREATE_TABLE_EMPLOYEE);
                db.execSQL(insertAdminEmployee);

                //DOCUMENTS RELATED
                db.execSQL(CREATE_TABLE_LANGUAGE);
                db.execSQL(CREATE_TABLE_TYPE);
                db.execSQL(CREATE_TABLE_DOCUMENTS);

            } catch (Exception ex) {
                String err = "";
                err = ex.getMessage();
                err += "";

                //Toast.makeText(context, "onCreate  ER: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                //Message.message(context, "OnUpgrade");
                Toast.makeText(context, "OnUpgrade", Toast.LENGTH_SHORT).show();
                db.execSQL(DROP_TABLE);
                db.execSQL(DROP_TABLE1);
                db.execSQL(DROP_TABLE2);
                db.execSQL(DROP_TABLE3);
                db.execSQL(DROP_TABLE4);
                db.execSQL(DROP_TABLE5);
                db.execSQL(DROP_TABLE6);
                db.execSQL(DROP_TABLE7);
                onCreate(db);
            } catch (Exception ex) {
                String err = "";
                err = ex.getMessage();
                err += "";

                Toast.makeText(context, "OnUpgrade  ER: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

