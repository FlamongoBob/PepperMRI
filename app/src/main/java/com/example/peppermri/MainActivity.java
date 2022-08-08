package com.example.peppermri;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.peppermri.Service.LocalService;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.pepperDB.PepperDB;
import com.example.peppermri.utils.Manager;


public class MainActivity extends AppCompatActivity {//extends RobotActivity implements RobotLifecycleCallbacks {
    PepperDB pepperDB;
    Controller controller;
    MainActivity mainActivity = this;
    Manager manager;
    public LocalService mService;
    boolean mBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void safekeep() {
        mService.safeKeep(controller, pepperDB, manager);
    }


    public void retrieve() {
        try {
            this.controller = mService.getController();
            this.pepperDB = mService.getPepperDB();
            this.manager = mService.getManager();
            manager.resetMainActivity(this);
           // this.manager = new Manager(this, this.controller, this.getSupportFragmentManager());
            manager.goToLogin();

        }catch (Exception ex){
            String err ="";
            err = ex.getMessage();
            err+="";
        }
    }

    public void startServer() {

        mService.startServer(this.controller, this);
    }

    public void stopServer() {

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

            Intent newIntent = new Intent(this, LocalService.class);
            mainActivity.bindService(newIntent, connection, Context.BIND_AUTO_CREATE);

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
        mService.appClosedDisconnect();
        unbindService(connection);
        mBound = false;

    }

    public void maUnbindService() {

        unbindService(connection);
    }

    public boolean checkServerStatus() {
        return mService.checkServer();
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

            if (mService.isRunning()) {
                retrieve();
            } else {
                initiateObjects();
                Intent newIntent = new Intent(mainActivity, LocalService.class);
                startService(newIntent);
                safekeep();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void initiateObjects() {

        pepperDB = new PepperDB(this, this);
        this.controller = new Controller(pepperDB, this);
        manager = new Manager(this, controller, this.getSupportFragmentManager());
    }

    public Controller getController() {
        return controller;
    }
}