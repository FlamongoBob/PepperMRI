package com.example.peppermri.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.peppermri.MainActivity;
import com.example.peppermri.R;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.utils.Manager;

public class Fragment_UserManagement extends Fragment {

    MainActivity mainActivity;
    Controller controller;
    Manager manager;
    int intPos = -1;

    public Fragment_UserManagement(Controller controller, MainActivity mainActivity, Manager manager) {
        this.mainActivity = mainActivity;
        this.controller = controller;
        this.manager = manager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vRoot = inflater.inflate(R.layout.fragment__user_management, container, false);
        initiateUserManagementControls(vRoot);
        // Inflate the layout for this fragment
        return vRoot;
    }

    private void initiateUserManagementControls(View vRoot) {

        ImageButton ibUMPicture = vRoot.findViewById(R.id.ibUMPicture);
        controller.setUMNewPicture(ibUMPicture);

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
            controller.deleteEmployee();
        });

        Button btnUMPrevious = vRoot.findViewById(R.id.btnUMPrevious);
        btnUMPrevious.setOnClickListener(view -> {
            intPos = intPos-1;
            controller.starFillUserManagement(intPos);
        });

        Button btnUMNext = vRoot.findViewById(R.id.btnUMNext);
        btnUMNext.setOnClickListener(view -> {
            intPos = intPos+1;
            controller.starFillUserManagement(intPos);
        });

    }

/*
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.admin_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return manager.manageFragmentView(item);
    }*/
}