package com.example.peppermri;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.example.peppermri.Service.LocalService;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.pepperDB.PepperDB;
import com.example.peppermri.utils.Manager;

public class MainActivity_Pepper extends RobotActivity implements RobotLifecycleCallbacks {

    public LocalService mService;
    boolean mBound = false;
    PepperDB pepperDB;
    Controller controller;
    Manager manager;
    /**
     * Controlls
     */
    Button btnBack, btnDone;


    TextView tvQuestion1, tvQuestion2, tvQuestion3;
    RadioButton rbQuestion1_Yes, rbQuestion1_No, rbQuestion2_Yes, rbQuestion2_No, rbQuestion3_Yes, rbQuestion3_No;

    ImageView ivEmployee;
    TextView tvEmployeeTitle, tvEmployeeFirstName, tvEmployeeLastName, tvEmployeeInfo;

    ConstraintLayout clPepper1, clPepper2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pepper);

        Intent intent = new Intent(this, LocalService.class);
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        initiateControls();
    }

    public void maUnbindService() {

        unbindService(connection);
    }

    public void initiateControls() {

        btnBack = findViewById(R.id.btnBack);

        clPepper1 = findViewById(R.id.clPepper1);
        clPepper2 = findViewById(R.id.clPepper2);

        tvQuestion1 = findViewById(R.id.tvQuestion1);
        tvQuestion2 = findViewById(R.id.tvQuestion2);
        tvQuestion3 = findViewById(R.id.tvQuestion3);

        rbQuestion1_Yes = findViewById(R.id.rbQuestion1_Yes);
        rbQuestion1_No = findViewById(R.id.rbQuestion1_No);
        rbQuestion1_No.setChecked(true);

        rbQuestion2_Yes = findViewById(R.id.rbQuestion2_Yes);
        rbQuestion2_No = findViewById(R.id.rbQuestion2_No);
        rbQuestion2_No.setChecked(true);

        rbQuestion3_Yes = findViewById(R.id.rbQuestion3_Yes);
        rbQuestion3_No = findViewById(R.id.rbQuestion3_No);
        rbQuestion3_No.setChecked(true);

        ivEmployee = findViewById(R.id.ivEmployee);
        tvEmployeeInfo = findViewById(R.id.tvEmployeeInfo);
        tvEmployeeTitle = findViewById(R.id.tvEmployeeTitle);
        tvEmployeeFirstName = findViewById(R.id.tvEmployeeFirstName);
        tvEmployeeLastName = findViewById(R.id.tvEmployeeLastName);

    }

    public void passControls() {

        if (this.controller != null) {

            controller.setRbQuestion1_Yes(rbQuestion1_Yes);
            controller.setRbQuestion1_No(rbQuestion1_No);

            controller.setRbQuestion2_Yes(rbQuestion2_Yes);
            controller.setRbQuestion2_No(rbQuestion2_No);

            controller.setRbQuestion3_Yes(rbQuestion3_Yes);
            controller.setRbQuestion3_No(rbQuestion3_No);

            controller.setIvEmployee(ivEmployee);

            controller.setTvEmployeeTitle(tvEmployeeTitle);
            controller.setTvEmployeeFirstName(tvEmployeeFirstName);
            controller.setTvEmployeeLastName(tvEmployeeLastName);
            controller.setTvEmployeeInfo(tvEmployeeInfo);

            controller.setTvQuestion1(tvQuestion1);
            controller.setTvQuestion2(tvQuestion2);
            controller.setTvQuestion3(tvQuestion3);
        }

    }

    public void retrieve() {
        this.controller = mService.getController();
        this.pepperDB = mService.getPepperDB();
        passControls();

        hideAllControls();
        btnBack.setOnClickListener(view -> {
            manager.goBack(this);
        });

        btnDone.setOnClickListener(view -> {

            controller.checkConfidentialInfoSender(1);

        });
    }

    public void showEmployee() {
        if (controller.isBlnHasAccepted()) {
            controller.controlsShowOrHideEmployee(View.VISIBLE);
        }
    }

    public void chatBotDone() {
        controller.resetEmployeeAndBool();
    }

    public void hideEmployee() {
        controller.controlsShowOrHideEmployee(View.INVISIBLE);
    }

    public void showQuestions() {
        controller.controlsShowOrHideEmployee(View.VISIBLE);
    }

    public void hideQuestions() {
        controller.controlsShowOrHideEmployee(View.INVISIBLE);
    }

    public void hideAllControls() {

        controller.controlsShowOrHideAll(View.INVISIBLE);
    }


    @Override
    public void onRobotFocusGained(QiContext qiContext) {

    }

    @Override
    public void onRobotFocusLost() {

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            retrieve();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}