package com.example.peppermri.pepperDB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.peppermri.controller.Controller;
import com.example.peppermri.model.User;

import java.util.ArrayList;

public class PepperDB {
    myDbHelper myhelper;
    Controller controller;


    public PepperDB(Context context) {
        myhelper = new myDbHelper(context);
    }
/*
    public long insertData(String name, String pass) {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.NAME, name);
        contentValues.put(myDbHelper.MyPASSWORD, pass);

        long id = dbb.insert(myDbHelper.TABLE_NAME, null, contentValues);
        return id;
    }*/
/*
    @SuppressLint("Range")
    public String getData() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID, myDbHelper.NAME, myDbHelper.MyPASSWORD};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME, columns, null, null, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            try {
                int cid = 0;
                if (cursor.getColumnIndex(myDbHelper.UID) > -1) {
                    cid = cursor.getInt(cursor.getColumnIndex(myDbHelper.UID));
                }
                String name = cursor.getString(cursor.getColumnIndex(myDbHelper.NAME));
                String password = cursor.getString(cursor.getColumnIndex(myDbHelper.MyPASSWORD));
                buffer.append(cid + "   " + name + "   " + password + " \n");

            } catch (Exception ex) {
                String err = ex.getMessage();
                err += "";
            }
        }
        return buffer.toString();

    }
    */
/*
    public int delete(String uname) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs = {uname};

        int count = db.delete(myDbHelper.TABLE_NAME, myDbHelper.NAME + " = ?", whereArgs);
        return count;
    }

 */
/*
    public int updateName(String oldName, String newName) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.NAME, newName);
        String[] whereArgs = {oldName};
        int count = db.update(myDbHelper.TABLE_NAME, contentValues, myDbHelper.NAME + " = ?", whereArgs);
        return count;
    }
    */

    public String insertTBLUSER(String strUserName, String strPassword) {
        return "INSERT INTO tblUSER" +
                "VALUES(NULL" +
                ", " + strUserName +
                ", " + strPassword +
                ");";
    }

    public String insertTBLPICTURE(String strPicture) {
        return "INSERT INTO tblPicture" +
                "VALUES(NULL" +
                ", " + strPicture +
                ");";
    }

    public String insertTBLEmployee(String strTitle, String strFirstName, String strLastName, String strUserName, String intConfidentialInfo) {
        return "INSERT INTO tblEmployee" +
                "VALUES (NULL " +
                ", '" + strTitle + "'" +
                ", '" + strFirstName + "'" +
                ", '" + strLastName + "'" +
                ", (SELECT intUserID FROM tblUser WHERE strUserName = 'RAJU')" +
                ", (SELECT intPictureID FROM tblPicture WHERE intUserID = (SELECT intUserID FROM tblUser WHERE strUserName = '" + strUserName + "'))" +
                ", (SELECT intConfidentialID FROM tblConfidence WHERE getsConfidentialInfo = " + intConfidentialInfo + ")" +
                ");";
    }

    public String insertTBLReceivesConfidentialInfo(int intValue){
        return "INSERT INTO tblReceivesConfidentialInfo" +
                "VALUES(NULL" +
                ", " + intValue +
                ");";
    }

    public String insertTBLROLE(String strRoleName){
        return "INSERT INTO tblRole" +
                "VALUES(NULL" +
                ", " + strRoleName +
                ");";
    }

    public User checkLoginCredential(String strUserName, String strPassword) {

        SQLiteDatabase db = this.myhelper.getReadableDatabase();
        Cursor cursorLoginCred = db.rawQuery("Select E.strEmployeeTitle AS strTitle\n" +
                "    , E.strFirstName AS strFirstName" +
                "    , E.strLastName AS strLastName" +
                "    , P.strPicture AS strPicture" +
                "    , R.intRoleID AS intRoleID" +
                "    , U.intUserID AS intUserID" +
                "    FROM tblEmployee AS E" +
                "    INNER JOIN tblUser AS U" +
                "    ON E.intUserID = U.intUserID" +
                "    INNER JOIN tblPicture AS P" +
                "    ON E.intPictureID = P.intPictureID" +
                "    INNER JOIN tblRole AS R" +
                "    ON E.intRoleID = R.intRoleID " +
                "    WHERE U.strUserName = " + strUserName + " AND U.strPassword =" + strPassword + ";", null);

        ArrayList<User> dbData = new ArrayList<>();

        if (cursorLoginCred.moveToFirst()) {
            User user = new User(cursorLoginCred.getInt(6)
                    , cursorLoginCred.getString(1)
                    , cursorLoginCred.getString(2)
                    , cursorLoginCred.getString(3)
                    , cursorLoginCred.getString(4)
                    , cursorLoginCred.getInt(5)

            );
            cursorLoginCred.close();
            return user;
        }
        return null;
    }


    static class myDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "Pepper_MRI_DB";    // Database Name
        private static final String TABLE_NAME = "myTable";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version

        private static final String CREATE_TABLE_EMPLOYEE =
                "CREATE TABLE tblEmployee" +
                        " (intEmployeeID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" +
                        ", strEmployeeTitle VARCHAR(255) " +
                        ", strFirstName  VARCHAR(225) NOT NULL" +
                        ", strLastName  VARCHAR(225) NOT NULL" +
                        ", intUserID  INTEGER NOT NULL" +
                        ", intPictureID  INTEGER" +
                        ", intRoleID  INTEGER NOT NULL" +
                        ", intConfidentialID  INTEGER NOT NULL" +
                        ", FOREIGN KEY (intUserID) REFERENCES tblUser(intUserID)" +
                        ", FOREIGN KEY (intPictureID) REFERENCES tblPicture(intPictureID)" +
                        ", FOREIGN KEY (intRoleID) REFERENCES tblRole(intRoleID)" +
                        ", FOREIGN KEY (intConfidentialID) REFERENCES tblReceivesConfidentialInfo(intRConfidentialInfoID)" +
                        ");";

        private static final String CREATE_TABLE_USER = "CREATE TABLE tblUser" +
                " (intUserID INTEGER NOT NUll PRIMARY KEY AUTOINCREMENT" +
                ", strUserName VARCHAR(255) NOT NUll " +
                ", strPassword  VARCHAR(225) NOT NUll );";
        private static final String INSERT_TBL_USER="INSERT INTO tblRole VALUES(NULL , 'ADMIN','ADMIN');";

        private static final String CREATE_TABLE_PICTURE = "CREATE TABLE tblPicture" +
                " (intPictureID INTEGER NOT NUll PRIMARY KEY AUTOINCREMENT" +
                ", strPicture VARCHAR(255) NOT NUll );";

        private static final String CREATE_TABLE_ROLE = "CREATE TABLE tblRole" +
                " (intRoleID INTEGER NOT NUll PRIMARY KEY AUTOINCREMENT" +
                ", strRole VARCHAR(255) NOT NUll );";

        private static final String INSERT_TBL_ROLE1="INSERT INTO tblRole VALUES(NULL, Admin);";
        private static final String INSERT_TBL_ROLE2="INSERT INTO tblRole VALUES(NULL , User);";

        private static final String CREATE_TABLE_RECEIVES_CONFIDENTIAL_INFO = "CREATE TABLE tblReceivesConfidentialInfo" +
                " (intRConfidentialInfoID INTEGER NOT NUll PRIMARY KEY AUTOINCREMENT" +
                ", getsConfidentialInfo BIT NOT NUll " +
                ", strINFO VARCHAR(255) NOT NUll );";
        private static final String INSERT_TBL_REC_CONFIDENTIAL_INFO1="" +
                "INSERT INTO tblReceivesConfidentialInfo VALUES(NULL , 1, '1=Receive');";
        private static final String INSERT_TBL_REC_CONFIDENTIAL_INFO3="" +
                "INSERT INTO tblReceivesConfidentialInfo VALUES(NULL , 2, '2=PriorityReceive');";
        private static final String INSERT_TBL_REC_CONFIDENTIAL_INFO2="" +
                "INSERT INTO tblReceivesConfidentialInfo VALUES(NULL , 0, '0=Not Receive');";

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


        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        private Context context;


        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                //USER RELATED
                db.execSQL(CREATE_TABLE_USER);
                db.execSQL(INSERT_TBL_USER);

                db.execSQL(CREATE_TABLE_RECEIVES_CONFIDENTIAL_INFO);
                db.execSQL(INSERT_TBL_REC_CONFIDENTIAL_INFO1);
                db.execSQL(INSERT_TBL_REC_CONFIDENTIAL_INFO2);
                db.execSQL(INSERT_TBL_REC_CONFIDENTIAL_INFO3);

                db.execSQL(CREATE_TABLE_PICTURE);

                db.execSQL(CREATE_TABLE_ROLE);
                db.execSQL(INSERT_TBL_ROLE1);
                db.execSQL(INSERT_TBL_ROLE2);

                db.execSQL(CREATE_TABLE_EMPLOYEE);

                //DOCUMENTS RELATED
                db.execSQL(CREATE_TABLE_LANGUAGE);
                db.execSQL(CREATE_TABLE_TYPE);
                db.execSQL(CREATE_TABLE_DOCUMENTS);

            } catch (Exception e) {
                //Message.message(context, "" + e.getMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                //Message.message(context, "OnUpgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);
            } catch (Exception e) {
                //Message.message(context, "" + e);
            }
        }
    }
}

