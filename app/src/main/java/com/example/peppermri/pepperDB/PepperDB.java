package com.example.peppermri.pepperDB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PepperDB {
    myDbHelper myhelper;

    /** TODO: GET USERNAME & PASSWORD SELECT STATEMENT TO CHECK LOGIN
    Select *
    FROM tblUser
    WHERE intUserName = "";
    */

    /** TODO: GET USERDATA WITH PICTURE  & SEND IT
    Select E.strEmployeeTitle AS strTitle
    , E.strFirstName AS strFirstName
    , E.strLastName AS strLastName
    , P.strPath AS strPath
    FROM tblEmployee AS E
    INNER JOIN tblUser AS U
    ON E.intUserID = U.intUserID
    INNER JOIN tblPicture AS P
    ON E.intPictureID = P.intPictureID
    WHERE U.intUserID = "";
     */

    public PepperDB(Context context) {
        myhelper = new myDbHelper(context);
    }

    public long insertData(String name, String pass) {
       /* SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.NAME, name);
        contentValues.put(myDbHelper.MyPASSWORD, pass);

        long id = dbb.insert(myDbHelper.TABLE_NAME, null, contentValues);
        return id;

        */
        return -1;
    }

    @SuppressLint("Range")
    public String getData() {
       /* SQLiteDatabase db = myhelper.getWritableDatabase();
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
        */

        return "";
    }

    public int delete(String uname) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs = {uname};

        int count = db.delete(myDbHelper.TABLE_NAME, myDbHelper.NAME + " = ?", whereArgs);
        return count;
    }

    public int updateName(String oldName, String newName) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.NAME, newName);
        String[] whereArgs = {oldName};
        int count = db.update(myDbHelper.TABLE_NAME, contentValues, myDbHelper.NAME + " = ?", whereArgs);
        return count;
    }

    static class myDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "Pepper_MRI_DB";    // Database Name
        private static final String TABLE_NAME = "myTable";   // Table Name
        private static final String TABLE_NAME_Employee = "Employee";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version
        private static final String UID = "_id";     // Column I (Primary Key)
        private static final String NAME = "Name";    //Column II
        private static final String MyPASSWORD = "Password";    // Column III

        private static final String CREATE_TABLE_EMPLOYEE =
                "CREATE TABLE tblEmployee" +
                        " (intEmployeeID INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", strEmployeeTitle VARCHAR(255) " +
                        ", strFirstName  VARCHAR(225)" +
                        ", strLastName  VARCHAR(225)" +
                        ", intUserID  INTEGER" +
                        ", intPictureID  INTEGER" +
                        ", intPresentID  INTEGER" +
                        ", intRoleID  INTEGER" +
                        ", FOREIGN KEY (intUserID) REFERENCES tblUser(intUserID)" +
                        ", FOREIGN KEY (intPictureID) REFERENCES tblPicture(intPictureID)" +
                        ", FOREIGN KEY (intRoleID) REFERENCES tblRole(intRoleID));";


        private static final String CREATE_TABLE_USER = "CREATE TABLE tblUser" +
                " (intUserID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", strUserName VARCHAR(255) " +
                ", strPassword  VARCHAR(225) );";

        private static final String CREATE_TABLE_PICTURE = "CREATE TABLE tblPicture" +
                " (intPictureID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", strPath VARCHAR(255) );";

        private static final String CREATE_TABLE_ROLE = "CREATE TABLE tblPicture" +
                " (intRoleID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", strRole VARCHAR(255) );";

        private static final String CREATE_TABLE_DOCUMENTS =
                "CREATE TABLE tblDocument" +
                        " (intDocumentID INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", strDocumentName VARCHAR(255) " +
                        ", strPath  VARCHAR(225)" +
                        ", intTypeID  INTEGER" +
                        ", intLanguageID  INTEGER"+
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
                db.execSQL(CREATE_TABLE_PICTURE);
                db.execSQL(CREATE_TABLE_ROLE);
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

