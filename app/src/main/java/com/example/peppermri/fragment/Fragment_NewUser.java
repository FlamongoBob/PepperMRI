package com.example.peppermri.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.peppermri.MainActivity;
import com.example.peppermri.R;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.utils.Manager;

import java.util.Random;

public class Fragment_NewUser extends Fragment {
    private Controller controller;
    private MainActivity mainActivity;
    Manager manager;
    EditText etNuUserName;
    String strFirstName = "";
    String strLastName = "";
    View vRoot;

    public Fragment_NewUser(Controller controller, MainActivity mainActivity, Manager manager) {
        this.mainActivity = mainActivity;
        this.controller = controller;
        this.manager = manager;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public void resetFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vRoot = inflater.inflate(R.layout.fragment__new_user, null);
        initiateNewUserControls(vRoot);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vRoot = inflater.inflate(R.layout.fragment__new_user, container, false);
        initiateNewUserControls(vRoot);
        return vRoot;
    }


    public void initiateNewUserControls(View vRoot) {
        ImageButton ibNewPicture = vRoot.findViewById(R.id.ibNewPicture);
        controller.setIBNewPicture(ibNewPicture);


        EditText etNuTitle = vRoot.findViewById(R.id.etNuTitle);
        controller.setEtNuTitle(etNuTitle);

        EditText etNuFirstName = vRoot.findViewById(R.id.etNuFirstName);
        etNuFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String strChar = charSequence.toString();
                generateUserName(strChar, "");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        controller.setEtNuFirstName(etNuFirstName);

        EditText etNuLastName = vRoot.findViewById(R.id.etNuLastName);
        etNuLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String strChar = charSequence.toString();
                generateUserName("", strChar);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        controller.setEtNuLastName(etNuLastName);

        EditText etNuPassword = vRoot.findViewById(R.id.etNuPassword);
        controller.setEtNuPassword(etNuPassword);

        etNuUserName = vRoot.findViewById(R.id.etNuUserName);
        etNuUserName.setKeyListener(null);
        etNuUserName.setFocusable(false);
        controller.setEtNuUserName(etNuUserName);

        RadioGroup rg_Nu_Confidential = vRoot.findViewById(R.id.rg_Nu_Confidential);
        controller.setRgConfidential(rg_Nu_Confidential);

        RadioButton rb_Nu_RConfidentalInfo = vRoot.findViewById(R.id.rb_Nu_RConfidentalInfo);
        controller.setRb_Nu_RConfidentalInfo(rb_Nu_RConfidentalInfo);

        RadioButton rb_Nu_NConfidentalInfo = vRoot.findViewById(R.id.rb_Nu_NConfidentalInfo);
        rb_Nu_NConfidentalInfo.setChecked(true);
        controller.setRb_Nu_NConfidentalInfo(rb_Nu_NConfidentalInfo);

        Spinner spNuRole = vRoot.findViewById(R.id.spNuRole);
        controller.setSpNURole(spNuRole);

        Button btnAddNewUser = vRoot.findViewById(R.id.btnAddNewUser);
        btnAddNewUser.setOnClickListener(view -> {
            controller.addNewUser();
        });

        Button btnNewUserClear = vRoot.findViewById(R.id.btnNewUserClear);
        btnNewUserClear.setOnClickListener(view -> {
            controller.clearNewUser();
        });
    }

    private void generateUserName(String strFirstName, String strLastName) {
        String strUserName = "";
        Random random = new Random();
        try {

            if (!strFirstName.isEmpty()) {
                this.strFirstName = strFirstName;
                this.strFirstName = this.strFirstName.replace(" ", "");
            }

            if (!strLastName.isEmpty()) {
                this.strLastName = strLastName;
                this.strLastName = this.strLastName.replace(" ", "");

            }

            if (this.strFirstName.length() >= 2 && this.strLastName.length() >= 2) {
                if (etNuUserName != null) {

                    int intFirstNameLength = this.strFirstName.length();
                    int intLastNameLength = this.strLastName.length();

                    if (intFirstNameLength >= 2 && intLastNameLength >= 4) {

                        etNuUserName.setText(this.strFirstName.substring(0, 2) + "." + this.strLastName.substring(0, 4));

                    } else if (intFirstNameLength >= 2 && intLastNameLength < 4) {
                        strUserName = this.strFirstName.substring(0, 2) + "." + this.strLastName.substring(0, 2);

                        for (int i = intLastNameLength; i < 4; i++) {
                            strUserName += "" + random.nextInt(10);
                        }

                        etNuUserName.setText(strUserName);

                    }
                }

            }
        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err += "";
        }


    }
}