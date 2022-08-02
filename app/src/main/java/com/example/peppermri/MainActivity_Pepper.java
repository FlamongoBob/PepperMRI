package com.example.peppermri;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;

public class MainActivity_Pepper extends RobotActivity implements RobotLifecycleCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pepper);
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