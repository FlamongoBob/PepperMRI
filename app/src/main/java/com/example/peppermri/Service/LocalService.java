package com.example.peppermri.Service;

import static android.app.Service.START_NOT_STICKY;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.peppermri.MainActivity;
import com.example.peppermri.controller.Controller;
import com.example.peppermri.messages.MessageSystem;
import com.example.peppermri.messages.MessageType;
import com.example.peppermri.server.Server;

import java.net.InetAddress;

public class LocalService extends Service {

    Server server;
    Controller controller;
    final private int intPortNr = 6666;//80; //= 10284;
    final private String strIPAdress = "10.0.2.15";// = "127.10.10.15";
    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Class used for the client Binder.
     */
    public class LocalBinder extends Binder {
        public LocalService getService() {
            return LocalService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }



    public void startServer(Controller controller, MainActivity mainActivity) {
        try {
            if (this.controller == null) {
                this.controller = controller;
            }
            InetAddress inetAddress = InetAddress.getByName(strIPAdress);
            server = controller.startServer(intPortNr, controller, inetAddress, mainActivity);
        }catch (Exception exception){
            String err ="";
            err = exception.getMessage();
            err +="";
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public void stopServer(){
        if(controller.isServerStarted) {

            MessageSystem msgDisc = new MessageSystem("Disconnect");
            msgDisc.setType(MessageType.Disconnect);
            controller.serverShutDown(msgDisc);

        }
    }



    @Override
    public final void onTaskRemoved(Intent rootIntent){
        stopServer();
    }

    public void appClosedDisconnect(){

        stopServer();
    }






}
