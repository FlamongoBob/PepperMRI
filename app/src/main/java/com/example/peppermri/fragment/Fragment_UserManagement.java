package com.example.peppermri.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.peppermri.MainActivity;
import com.example.peppermri.R;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.utils.Manager;

public class Fragment_UserManagement extends Fragment {

    MainActivity mainActivity;
    Controller controller;
    Manager manager;
    int intPos = 0;
    View vRoot;

    public Fragment_UserManagement(Controller controller, MainActivity mainActivity, Manager manager) {
        this.mainActivity = mainActivity;
        this.controller = controller;
        this.manager = manager;
    }

    public void resetFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vRoot = inflater.inflate(R.layout.fragment__user_management, null);
        initiateUserManagementControls(vRoot);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vRoot = inflater.inflate(R.layout.fragment__user_management, container, false);
        initiateUserManagementControls(vRoot);
        return vRoot;
    }

    private void initiateUserManagementControls(View vRoot) {

        ImageView ivUMPicture = vRoot.findViewById(R.id.ivUMPicture);
        controller.setUMNewPicture(ivUMPicture);

        EditText etUMFirstName = vRoot.findViewById(R.id.etUMFirstName);
        controller.setEtUMFirstName(etUMFirstName);

        EditText etUMLastName = vRoot.findViewById(R.id.etUMLastName);
        controller.setEtUMLastName(etUMLastName);

        EditText etUMPassword = vRoot.findViewById(R.id.etUMPassword);
        controller.setEtUMPassword(etUMPassword);

        EditText etUMUserName = vRoot.findViewById(R.id.etUMUserName);
        controller.setEtUMUserName(etUMUserName);

        EditText etUMTitle = vRoot.findViewById(R.id.etUMTitle);
        controller.setEtUMTitle(etUMTitle);

        Spinner spUMRole = vRoot.findViewById(R.id.spUMRole);
        controller.setSpUMRole(spUMRole);

        Button btnUMSaveChanges = vRoot.findViewById(R.id.btnUMSaveChanges);
        btnUMSaveChanges.setOnClickListener(view -> {
            controller.serverUpdateUser(intPos);
        });

        Button btnUMDeleteUser = vRoot.findViewById(R.id.btnUMDeleteUser);
        btnUMDeleteUser.setOnClickListener(view -> {
            controller.serverDeleteEmployee();
            controller.serverGetAllEmployeeData();
            intPos = 0;
            intPos = controller.starFillUserManagement(intPos);
        });

        Button btnUMPrevious = vRoot.findViewById(R.id.btnUMPrevious);
        btnUMPrevious.setOnClickListener(view -> {
            intPos = intPos - 1;
            intPos = controller.starFillUserManagement(intPos);
        });

        Button btnUMNext = vRoot.findViewById(R.id.btnUMNext);
        btnUMNext.setOnClickListener(view -> {
            intPos = intPos + 1;
            intPos = controller.starFillUserManagement(intPos);
        });

        Button btnRefresh = vRoot.findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(view -> {
            intPos = 0;
            controller.serverGetAllEmployeeData();
            intPos = controller.starFillUserManagement(intPos);
        });

        RadioButton rb_RConfidentialUM = vRoot.findViewById(R.id.rb_RConfidentialUM);
        controller.setRb_RConfidentialUM(rb_RConfidentialUM);

        RadioButton rb_NConfidentialUM = vRoot.findViewById(R.id.rb_NConfidentialUM);
        controller.setRb_NConfidentialUM(rb_NConfidentialUM);
    }
}