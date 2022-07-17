package com.example.peppermri;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.pepperDB.PepperDB;


public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {
    PepperDB pepperDB;
Controller controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pepperDB = new PepperDB(this);
        this.controller = new Controller(pepperDB);

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
}