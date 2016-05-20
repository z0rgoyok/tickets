package com.zabozhanov.tickets.net;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;

/**
 * Created by z0rgoyok on 18.05.16.
 */
public class DeviceConnection implements IConnection {

    private static final int WRITE_TIMEOUT = 5000;

    private UsbSerialDriver driver;
    private UsbDeviceConnection connection;
    private int deviceID;

    public boolean initConnection(Context context, int deviceID) {
        this.deviceID = deviceID;
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return false;
        }
        driver = availableDrivers.get(0);
        connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            return false;
        }
        return true;
    }

    public boolean writePackage(Package p) {
        byte[] bytes = makePackageData(p);
        try {
            writeData(bytes);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private void writeData(byte[] bytes) throws Exception {
        if (driver == null || connection == null) {
            throw new Exception("Init it first");
        }
        UsbSerialPort port = driver.getPorts().get(0);
        port.open(connection);
        try {
            //byte buffer[] = new byte[16];
            //int numBytesRead = port.read(buffer, 1000);

            int numBytesWrite = port.write(bytes, WRITE_TIMEOUT);

        } catch (IOException e) {
            // Deal with error.
        } finally {
            port.close();
        }
    }

    private byte[] makePackageData(Package p) {

        //составляем данные пакета
        //
        int resultsCount = p.results.size();

        //отправляем
        //device_id(1) + time(8) + ticket_number(8) + type(1)

        byte[] bytes = new byte[1024];

        return bytes;
    }

}
