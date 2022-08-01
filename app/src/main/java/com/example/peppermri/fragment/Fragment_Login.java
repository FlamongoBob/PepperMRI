package com.example.peppermri.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.peppermri.MainActivity;
import com.example.peppermri.R;
import com.example.peppermri.controller.Controller;

public class Fragment_Login extends Fragment {

    Controller controller;
    Button btnLogin;
    MainActivity mainActivity;
    public boolean isLoggedIn = false;

    public Fragment_Login(Controller controller,MainActivity mainActivity) {
        // Required empty public constructor
        this.controller = controller;
        this.mainActivity = mainActivity;

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

               isLoggedIn= controller.serverCheckLoginCredential(etLoginUserName.getText().toString()
                        , etLoginPassword.getText().toString());

               if(isLoggedIn){
                   tvLoginInformation.setText(R.string.msg_SucLogin);
               }else {
                   tvLoginInformation.setText(R.string.msg_UnSucLogin);
               }

            });

        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
        }
    }



}