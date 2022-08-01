package com.example.peppermri.utils;

import android.content.DialogInterface;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.peppermri.MainActivity;
import com.example.peppermri.R;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.fragment.Fragment_Login;
import com.example.peppermri.fragment.Fragment_NewUser;
import com.example.peppermri.fragment.Fragment_PepperInformation;
import com.example.peppermri.fragment.Fragment_UserManagement;

public class Manager extends AppCompatActivity {
    MainActivity mainActivity;
    Controller controller;


    Fragment_Login frgLogin;
    Fragment_NewUser frnUser;
    Fragment_PepperInformation frgPepper;
    Fragment_UserManagement frgMgmt;
    Fragment activeFragment ;
    int intPos=-1;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    public FragmentManager frgMng = getSupportFragmentManager();
    //Fragment activeFragment = frgSplashImage;

    public Manager(MainActivity mainActivity, Controller controller){
        this.mainActivity = mainActivity;
        this.controller = controller;

        frgLogin = new Fragment_Login(this.controller,   this.mainActivity);
        frnUser = new Fragment_NewUser(this.controller,   this.mainActivity);
        frgPepper = new Fragment_PepperInformation(this.controller,   this.mainActivity, this);
        frgMgmt = new Fragment_UserManagement(this.controller,   this.mainActivity);
        activeFragment = frgPepper;

        frgMng.beginTransaction().add(R.id.container, frgLogin, "frgLogin").hide(frgLogin).commit();
        frgMng.beginTransaction().add(R.id.container, frnUser, "frnUser").hide(frnUser).commit();
        frgMng.beginTransaction().add(R.id.container, frgMgmt, "frgMgmt").hide(frgMgmt).commit();
        frgMng.beginTransaction().add(R.id.container, frgPepper, "frgPepper").commit();

    }

    public boolean manageFragmentView(MenuItem menuItem){

        alertDialogBuilder = new AlertDialog.Builder(this);

        switch (menuItem.getItemId()) {
            case R.id.New_User:
                try {
                    if (controller.isLoggedIn) {
                        if (controller.getIntRoleID() == 1) {
                            if (!activeFragment.getTag().equals(frgMng.findFragmentByTag("frnUser").getTag())) {
                                frgMng.beginTransaction().hide(activeFragment).show(frnUser).commit();
                                activeFragment = frnUser;
                                return true;
                            }

                        } else {
                            alertAdminRights();
                            return false;
                        }
                    }else {
                        alertNotLoggedIn();
                    }

                    return false;
                } catch (Exception ex) {
                    String err = ex.getMessage();
                    err += "";
                }
            case R.id.UserManagement:
                try {
                    if (controller.isLoggedIn) {
                        if (controller.getIntRoleID() == 1) {
                            if (!activeFragment.getTag().equals(frgMng.findFragmentByTag("frgMgmt").getTag())) {
                                frgMng.beginTransaction().hide(activeFragment).show(frgMgmt).commit();
                                activeFragment = frgMgmt;

                                controller.serverGetAllEmployeeData();

                                intPos = controller.starFillUserManagement(0);
                                return true;
                            }
                        } else {
                            alertAdminRights();
                            return false;
                        }

                    }else {
                        alertNotLoggedIn();
                    }

                } catch (Exception ex) {
                    String err = ex.getMessage();
                    err += "";
                }
            case R.id.Login:
                try {
                    if(!controller.isLoggedIn) {
                        if (!activeFragment.getTag().equals(frgMng.findFragmentByTag("frgLogin").getTag())) {
                            frgMng.beginTransaction().hide(activeFragment).show(frgLogin).commit();
                            activeFragment = frgLogin;

                            return true;
                        }
                    }else{

                        alertDialogBuilder.setTitle("Already Logged IN");
                        alertDialogBuilder.setMessage(getText(R.string.Not_Logged_In_Text));
                        alertDialogBuilder.setPositiveButton(getText(R.string.alertD_OK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Toast.makeText(mainActivity, getText(R.string.Page_not_Changed), Toast.LENGTH_SHORT).show();
                            }
                        });
                        alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }

                    return false;

                } catch (Exception ex) {
                    String err = ex.getMessage();
                    err += "";
                }

            case R.id.LogOut:
                try {
                    if (controller.isLoggedIn) {
                        /**TODO Logout*/

                        frgMng.beginTransaction().hide(activeFragment).show(frgPepper).commit();
                        activeFragment = frgPepper;
                        return true;
                    }else {

                        alertNotLoggedIn();
                    }

                    return false;

                } catch (Exception ex) {
                    String err = ex.getMessage();
                    err += "";
                }
            default:

                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void alertAdminRights(){


        alertDialogBuilder.setTitle("Missing Admin rights");
        alertDialogBuilder.setMessage("You need the admin rights to select this");
        alertDialogBuilder.setPositiveButton(getText(R.string.alertD_OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(mainActivity, getText(R.string.Page_not_Changed), Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void alertNotLoggedIn(){
        alertDialogBuilder.setTitle(getText(R.string.Not_Logged_In_Title));
        alertDialogBuilder.setMessage(getText(R.string.Not_Logged_In_Text));
        alertDialogBuilder.setPositiveButton(getText(R.string.alertD_OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(mainActivity, getText(R.string.Page_not_Changed), Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void replaceFragment(Fragment fragment){
        //frgMng.beginTransaction().replace(R.id.stateFragmentHolder, fragment).commit();
    }

    public void removeLastFragment(Fragment lastFragment){
        frgMng.beginTransaction().remove(lastFragment).commit();
    }
}
