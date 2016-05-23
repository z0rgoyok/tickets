package com.zabozhanov.tickets.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.zabozhanov.tickets.net.DeviceConnection;

public class DeviceService extends Service {

    private byte deviceID;
    private DeviceConnection connection;

    public DeviceService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        deviceID = intent.getByteExtra("device_id", (byte) 0);

        connection = new DeviceConnection();
        boolean result = connection.initConnection(this, deviceID);

        if (result) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //тут осуществляется весь обмен данными
                    //читаем читаем, если есть что записать - пишем
                    connection.deviceProcessing();
                }
            }).run();
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
