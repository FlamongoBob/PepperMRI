package com.example.peppermri.utils;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.peppermri.MainActivity;
import com.example.peppermri.MainActivity_Pepper;
import com.example.peppermri.R;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.fragment.Fragment_Login;
import com.example.peppermri.fragment.Fragment_NewUser;
import com.example.peppermri.fragment.Fragment_PepperInformation;
import com.example.peppermri.fragment.Fragment_ServerConnection;
import com.example.peppermri.fragment.Fragment_UserManagement;

public class Manager extends AppCompatActivity {
    MainActivity mainActivity;
    Controller controller;

    Fragment_Login frgLogin;
    Fragment_NewUser frgUser;
    Fragment_PepperInformation frgPepper;
    Fragment_UserManagement frgMgmt;
    Fragment_ServerConnection frgServer;
    Fragment activeFragment;
    int intPos = -1;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    public FragmentManager frgMng;

    public Manager(MainActivity mainActivity, Controller controller, FragmentManager frgMng) {
        this.mainActivity = mainActivity;
        this.controller = controller;
        this.frgMng = frgMng;

        frgLogin = new Fragment_Login(this.controller, this.mainActivity, this);
        frgUser = new Fragment_NewUser(this.controller, this.mainActivity, this);
        frgPepper = new Fragment_PepperInformation(this.controller, this.mainActivity, this);
        frgMgmt = new Fragment_UserManagement(this.controller, this.mainActivity, this);
        frgServer = new Fragment_ServerConnection(this.controller, this.mainActivity, this);
        activeFragment = frgLogin;

        frgMng.beginTransaction().add(R.id.container, frgPepper, "frgPepper").hide(frgPepper).commit();
        frgMng.beginTransaction().add(R.id.container, frgUser, "frgUser").hide(frgUser).commit();
        frgMng.beginTransaction().add(R.id.container, frgMgmt, "frgMgmt").hide(frgMgmt).commit();
        frgMng.beginTransaction().add(R.id.container, frgServer, "frgServer").hide(frgServer).commit();
        frgMng.beginTransaction().add(R.id.container, frgLogin, "frgLogin").commit();
    }

    public boolean manageFragmentView(MenuItem menuItem) {
            alertDialogBuilder = new AlertDialog.Builder(mainActivity);

            switch (menuItem.getItemId()) {
                case R.id.New_User:
                    if (controller.isLoggedIn) {
                        if (controller.getIntRoleID() == 1) {
                            if (!activeFragment.getTag().equals(frgMng.findFragmentByTag("frgUser").getTag())) {
                                frgMng.beginTransaction().hide(activeFragment).show(frgUser).commit();
                                activeFragment = frgUser;
                                return true;
                            }
                        } else {
                            alertAdminRights();
                            return false;
                        }
                    } else {
                        alertNotLoggedIn();
                    }

                    return false;

                case R.id.UserManagement:
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
                    } else {
                        alertNotLoggedIn();
                    }

                    return false;

                case R.id.Login:
                    if (!activeFragment.getTag().equals(frgMng.findFragmentByTag("frgLogin").getTag())) {
                        frgMng.beginTransaction().hide(activeFragment).show(frgLogin).commit();
                        activeFragment = frgLogin;

                        return true;
                    }

                    return false;

                case R.id.LogOut:
                    if (controller.isLoggedIn) {
                        frgMng.beginTransaction().hide(activeFragment).show(frgLogin).commit();
                        activeFragment = frgLogin;
                        controller.serverLogout();
                        frgLogin.setTvLogOutText();
                        frgLogin.isLoggedIn = false;

                    } else {

                        alertNotLoggedIn();
                    }

                    return false;

                case R.id.Server:

                    if (controller.isLoggedIn) {
                        if (controller.getIntRoleID() == 1) {
                            if (!activeFragment.getTag().equals(frgMng.findFragmentByTag("frgServer").getTag())) {
                                frgMng.beginTransaction().hide(activeFragment).show(frgServer).commit();
                                activeFragment = frgServer;
                                frgServer.getInformation();
                                return true;
                            }
                        }
                    }
                    return false;

                case R.id.Pepper:
                    if (mainActivity.checkServerStatus()) {
                        Intent intent = new Intent(mainActivity, MainActivity_Pepper.class);

                        controller.serverLogout();
                        mainActivity.maUnbindService();
                        mainActivity.startActivity(intent);

                    } else {

                        NotificationUtil.createChannel(
                                mainActivity
                                , "Server Not Started"
                        );

                        NotificationUtil.setNotification(mainActivity
                                , mainActivity.getText(R.string.ServerNotStarted_Title).toString()
                                , mainActivity.getText(R.string.ServerNotStarted_Content).toString()
                        );
                    }
                    break;

                default:

                    return super.onOptionsItemSelected(menuItem);
            }
        return super.onOptionsItemSelected(menuItem);
    }

    public void resetMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        frgMng = mainActivity.getSupportFragmentManager();

        frgMng.beginTransaction().add(R.id.container, frgPepper, "frgPepper").hide(frgPepper).commit();
        frgPepper.resetFragment(mainActivity);

        frgMng.beginTransaction().add(R.id.container, frgUser, "frgUser").hide(frgUser).commit();
        frgUser.resetFragment(mainActivity);

        frgMng.beginTransaction().add(R.id.container, frgMgmt, "frgMgmt").hide(frgMgmt).commit();
        frgMgmt.resetFragment(mainActivity);

        frgMng.beginTransaction().add(R.id.container, frgServer, "frgServer").hide(frgServer).commit();
        frgServer.resetFragment(mainActivity);
        frgServer.getInformation();

        frgMng.beginTransaction().add(R.id.container, frgLogin, "frgLogin").hide(frgLogin).commit();
        frgLogin.resetFragment(mainActivity);
    }


    public void alertAdminRights() {

        alertDialogBuilder.setTitle(mainActivity.getText(R.string.Missing_Admin_Title));
        alertDialogBuilder.setMessage(mainActivity.getText(R.string.Missing_Admin_Text));
        alertDialogBuilder.setPositiveButton(mainActivity.getText(R.string.alertD_OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(mainActivity, mainActivity.getText(R.string.Page_not_Changed), Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void alertNotLoggedIn() {
        alertDialogBuilder.setTitle(mainActivity.getText(R.string.Not_Logged_In_Title));
        alertDialogBuilder.setMessage(mainActivity.getText(R.string.Not_Logged_In_Text));
        alertDialogBuilder.setPositiveButton(mainActivity.getText(R.string.alertD_OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(mainActivity, mainActivity.getText(R.string.Page_not_Changed), Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void goBack(MainActivity_Pepper mainActivity_pepper) {

        Intent intent = new Intent(mainActivity_pepper, MainActivity.class);
        mainActivity_pepper.maUnbindService();
        mainActivity_pepper.startActivity(intent);
    }

    public void goToLogin() {
        frgMng.beginTransaction().hide(activeFragment).show(frgLogin).commit();
        activeFragment = frgLogin;
    }

}
