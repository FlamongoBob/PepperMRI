package com.example.peppermri.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.peppermri.MainActivity;
import com.example.peppermri.R;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.utils.Manager;

public class Fragment_ServerConnection extends Fragment {
    private Button btnStartServer, btnStopServer;
    private TextView tvStatusServerConnection;
    private Controller controller;
    private MainActivity mainActivity;;
    Manager manager;

    public Fragment_ServerConnection(Controller controller, MainActivity mainActivity, Manager manager) {
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

        View vRoot = inflater.inflate(R.layout.fragment__server_connection, container, false);
        initiateServerControls(vRoot);
        return vRoot;
    }

    public void initiateServerControls(View vRoot) {
        try {


            btnStartServer = vRoot.findViewById(R.id.btnStartServer);
            btnStartServer.setOnClickListener(view -> {
                mainActivity.startServer();
                getInformation();
            });

            btnStopServer = vRoot.findViewById(R.id.btnStopServer);
            btnStopServer.setOnClickListener(view -> {
                mainActivity.stopServer();
                getInformation();
            });


            //isCreatedServer = true;
        } catch (Exception ex) {
            String err = "";
            err = ex.getMessage();
            //isCreatedServer = false;
        }
    }


    public void getInformation() {
        ImageView iv = mainActivity.findViewById(R.id.iv_Robot);

        if (controller.isServerStarted) {
            iv.setColorFilter(mainActivity.getColor(R.color.connected_Green));

            tvStatusServerConnection.setText("Server is running");
        } else {

            iv.setColorFilter(mainActivity.getColor(R.color.disconnected_red));
            tvStatusServerConnection.setText("Server is not running");
        }
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
    }
*/
}