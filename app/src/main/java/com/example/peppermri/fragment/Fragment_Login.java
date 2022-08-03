package com.example.peppermri.fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peppermri.MainActivity;
import com.example.peppermri.R;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.utils.Manager;

public class Fragment_Login extends Fragment {

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    Controller controller;
    Button btnLogin;
    MainActivity mainActivity;

    Manager manager;
    public boolean isLoggedIn = false;

    public Fragment_Login(Controller controller, MainActivity mainActivity, Manager manager) {
        this.mainActivity = mainActivity;
        this.controller = controller;
        this.manager = manager;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vRoot = inflater.inflate(R.layout.fragment__login, container, false);

        initiateLoginControls(vRoot);
        return vRoot;
    }


    public void initiateLoginControls(View vRoot) {
        try {

            EditText etLoginUserName = vRoot.findViewById(R.id.etLoginUserName);
            controller.setEtLoginUsername(etLoginUserName);
            etLoginUserName.setText("ADMIN");

            EditText etLoginPassword = vRoot.findViewById(R.id.etLoginPassword);
            controller.setEtLoginPassword(etLoginPassword);
            etLoginPassword.setText("ADMIN");

            btnLogin = vRoot.findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(view -> {
                TextView tvLoginInformation = vRoot.findViewById(R.id.tvLoginInformation);
                controller.setTvLoginInformation(tvLoginInformation);
                if (!isLoggedIn) {
                    isLoggedIn = controller.serverCheckLoginCredential(etLoginUserName.getText().toString()
                            , etLoginPassword.getText().toString());
                } else {

                    alertDialogBuilder = new AlertDialog.Builder(mainActivity);
                    alertDialogBuilder.setTitle(R.string.Allready_Logged_In_Title);
                    alertDialogBuilder.setMessage(R.string.Allready_Logged_In_Text);
                    alertDialogBuilder.setPositiveButton(R.string.alertD_OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Toast.makeText(mainActivity, R.string.Page_not_Changed, Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                if (isLoggedIn) {
                    tvLoginInformation.setText(R.string.msg_SucLogin);
                } else {
                    tvLoginInformation.setText(R.string.msg_UnSucLogin);
                }

            });

        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
        }
    }
}