package com.example.peppermri;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.example.peppermri.Service.LocalService;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.fragment.Fragment_Login;
import com.example.peppermri.fragment.Fragment_NewUser;
import com.example.peppermri.fragment.Fragment_PepperInformation;
import com.example.peppermri.fragment.Fragment_UserManagement;
import com.example.peppermri.pepperDB.PepperDB;
import com.example.peppermri.utils.Manager;


public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {
    PepperDB pepperDB;
    Controller controller;

    Manager manager;
    LocalService mService;
    boolean mBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pepperDB = new PepperDB(this, this);
        this.controller = new Controller(pepperDB, this);

        manager = new Manager(this, controller);


        TextView tv = findViewById(R.id.tvWorld);
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> {

            controller.adminTestMessage(tv);
        });
    }



    public void shutDownServer() {
        //controller.ser
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

    @Override
    protected void onStart() {
        super.onStart();

        try {
            // Bind to LocalService
            Intent intent = new Intent(this, LocalService.class);
            startService(intent);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);

            mService.startServer(this.controller, this);

        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            err += "";

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mService.appClosedDisconnect();
        unbindService(connection);
        mBound = false;

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
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    public Controller getController() {
        return controller;
    }
}