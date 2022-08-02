package com.example.peppermri;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity{//extends RobotActivity implements RobotLifecycleCallbacks {
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

        manager = new Manager(this, controller, this.getSupportFragmentManager());


        //TextView tv = findViewById(R.id.tvWorld);
        /*Button btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> {

            controller.adminTestMessage(tv);
        });*/
    }

    public void startServer(){

        mService.startServer(this.controller, this);
    }

    public void stopServer(){

        mService.stopServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_options_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return manager.manageFragmentView(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        try {
            // Bind to LocalService
            Intent intent = new Intent(this, LocalService.class);
            startService(intent);
            this.bindService(intent, connection, Context.BIND_AUTO_CREATE);


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