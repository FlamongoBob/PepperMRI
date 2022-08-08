package com.example.peppermri.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.peppermri.MainActivity;
import com.example.peppermri.R;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.utils.Manager;

public class Fragment_ServerConnection extends Fragment {
    private Button btnStartServer, btnStopServer;
    private Controller controller;
    private MainActivity mainActivity;
    ;
    Manager manager;
    View vRoot;
    ImageView iv;
    TextView tvStatusServerConnection;
    TextView tvStatusServerConnectionIP;
    TextView tvStatusServerConnectionPort;

    public Fragment_ServerConnection(Controller controller, MainActivity mainActivity, Manager manager) {
        this.mainActivity = mainActivity;
        this.controller = controller;
        this.manager = manager;

    }

    public void resetFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vRoot = inflater.inflate(R.layout.fragment__server_connection, null);
        initiateServerControls(vRoot);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vRoot = inflater.inflate(R.layout.fragment__server_connection, container, false);
        initiateServerControls(vRoot);
        return vRoot;
    }

    public void initiateServerControls(View vRoot) {

        btnStartServer = vRoot.findViewById(R.id.btnStartServer);
        btnStartServer.setOnClickListener(view -> {
            mainActivity.startServer();
            getInformation();
            tvStatusServerConnection.setText(R.string.Online);
        });

        btnStopServer = vRoot.findViewById(R.id.btnStopServer);
        btnStopServer.setOnClickListener(view -> {

            mainActivity.stopServer();
            getInformation();

            tvStatusServerConnectionIP.setText(R.string.Offline);
            tvStatusServerConnectionPort.setText(R.string.Offline);
            tvStatusServerConnection.setText(R.string.Offline);
        });


        iv = vRoot.findViewById(R.id.iv_Robot);
        tvStatusServerConnection = vRoot.findViewById(R.id.tvStatusServerConnection);
        tvStatusServerConnectionIP = vRoot.findViewById(R.id.tvStatusServerConnectionIP);
        tvStatusServerConnectionPort = vRoot.findViewById(R.id.tvStatusServerConnectionPort);

    }


    public void getInformation() {

        if (controller.isServerStarted) {

            iv.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.connected_Green));
            tvStatusServerConnection.setText("Server is running");
        } else {

            iv.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.disconnected_red));
            tvStatusServerConnection.setText("Server is not running");
        }

        tvStatusServerConnectionIP.setText(mainActivity.mService.getIP());

        tvStatusServerConnectionPort.setText(mainActivity.mService.getPort());
    }
}