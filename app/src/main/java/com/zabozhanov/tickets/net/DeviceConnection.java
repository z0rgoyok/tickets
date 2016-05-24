package com.zabozhanov.tickets.net;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.zabozhanov.tickets.TicketApp;
import com.zabozhanov.tickets.models.StringBufferEvent;
import com.zabozhanov.tickets.models.TicketScanResult;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by z0rgoyok on 18.05.16.
 */
public class DeviceConnection implements IConnection {

    private static final int WRITE_TIMEOUT = 5000;

    private UsbSerialDriver driver;
    private UsbDeviceConnection connection;
    private int deviceID;
    private UsbSerialPort port;
    private ByteBuffer store;
    private boolean canceled;
    private long readCountAll = 0;

    private List<TicketScanResult> writeQueue = new ArrayList<>();
    //private Realm realm;

    public boolean initConnection(Context context, int deviceID) {
        this.deviceID = deviceID;

        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

        //realm = Realm.getDefaultInstance();
        if (availableDrivers.isEmpty()) {
            return false;
        }

        driver = availableDrivers.get(0);
        connection = manager.openDevice(driver.getDevice());
        try {
            port = driver.getPorts().get(0);
            port.open(connection);
            port.setParameters(UsbSerialPort.PORT_SPEED, 8,
                    UsbSerialPort.STOPBITS_1,
                    UsbSerialPort.PARITY_NONE);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (connection == null || port == null) {
            return false;
        }

        store = ByteBuffer.allocate(100 * 1000 * 1000);

        return true;
    }

    public void close() {
        try {
            canceled = true;
            port.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean writePackage(Package p) {
        byte[] bytes = makePackageData(p);

        ByteBuffer buffer = ByteBuffer.allocate(1 + bytes.length);
        buffer.put((byte) 3);
        buffer.put(bytes);

        try {
            writeData(buffer.array());
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private int writeData(byte[] bytes) throws Exception {
        if (driver == null || connection == null || port == null) {
            throw new Exception("Init it first");
        }
        try {
            int numBytesWrite = port.write(bytes, WRITE_TIMEOUT);
            return numBytesWrite;
        } catch (IOException e) {
            // Deal with error.
            return -1;
        }
    }

    private byte[] makePackageData(Package p) {
        //составляем данные пакета
        //отправляем
        //заголовок 0x3
        //массив таких данных
        //device_id(1) + time(8) + ticket_number(8) + scanNumber + type(1)
        //конец \r\b
        int itemLengthInBytes = 4 + 8 + 8 + 1 + 2; //deviceID, time, ticked_id, type, \r\n
        long time = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate(1 + p.results.size() * itemLengthInBytes + 2);
        byte type = 1;
        buffer.put((byte) 3);
        for (TicketScanResult result : p.results) {
            byte[] bytes = ByteBuffer.allocate(itemLengthInBytes).
                    putInt(deviceID).
                    putLong(time).
                    putLong(result.getTicket().getId()).
                    //putLong(result.get)
                            put(type).array();
            buffer.put(bytes);
        }
        buffer.putChar('\r');
        buffer.putChar('\n');
        return buffer.array();
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Блокирующий метод
     *
     * @return
     */
    public DeviceTicket readResult() {
        int maxSize = 64;
        byte[] buffer = new byte[maxSize];
        try {
            int readCount = 0;
            try {
                readCount = port.read(buffer, 1000);
                store.put(buffer, 0, readCount);
                readCountAll += readCount;

                if (readCount > 0) {
                    //печатаем вывод
                    Log.d("hex", bytesToHex(buffer));

                    final byte[] notNullPart = new byte[readCount];
                    System.arraycopy(buffer, 0, notNullPart, 0, readCount);

                    TicketApp.getMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new StringBufferEvent(bytesToHex(notNullPart)));
                        }
                    });

                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            for (int i = 0; i < readCount - 1; i++) {
                if (
                        (buffer[i] == 0x0d || buffer[i] == 0x0a) &&
                                (buffer[i + 1] == 0x0d || buffer[i + 1] == 0x0a) &&
                                (buffer[i] != buffer[i + 1])) // сто пудов, можно проще, надо раскурить Карту Карно
                {
                    //получили конец строки
                    //копируем от начала до этой позиции вместе с переносом
                    //удаляем из буффера (большого) все до этой позиции + 2 = ждем новую строку
                    //копируем из буффера все с этой позиции +2
                    byte[] line = new byte[store.position() + i + 2];
                    store.position(0);
                    store.get(line);
                    removeBytesFromStart(store, line.length);


                    //в line теперь строка с переносом, надо разобрать
                    ByteBuffer lineBuffer = ByteBuffer.allocate(2 * line.length);
                    lineBuffer.put(line);

                    byte msgType = lineBuffer.get();


                    if (msgType == 3) { //получили от другого устройства
                        //надо сохранить
                        byte deviceId = lineBuffer.get();
                        long timestamp = lineBuffer.getLong();
                        long tickedId = lineBuffer.getLong();
                        byte ticketType = lineBuffer.get();

                        DeviceTicket ticket = new DeviceTicket();
                        ticket.deviceId = deviceId;
                        ticket.timestamp = timestamp;
                        ticket.ticketType = ticketType;
                        ticket.ticketId = tickedId;
                        Log.d("tag", "received");


                        //Log.d("")

                        /*Ticket scannedTicket = realm.where(Ticket.class)
                                .equalTo("id", tickedId)
                                .findFirst();

                        if (scannedTicket == null) {
                            return null;
                        }

                        realm.beginTransaction();
                        TicketScanResult result = new TicketScanResult();
                        result.setTicket(scannedTicket);
                        result.setTimestamp(timestamp);
                        result.setDeviceID(deviceId);
                        result.setTicketScanResult(ticketType);
                        realm.commitTransaction();*/

                    } else if (msgType == 2) { //получили от себя

                        Log.d("tag", "scanned");
                        //надо записать
                        /*TicketScanResult ticketScanResult = realm.createObject(TicketScanResult.class);
                        result.setTicket(scannedTicket);
                        result.setTimestamp(timestamp);
                        result.setDeviceID(deviceId);
                        result.setTicketScanResult(ticketType);*/
                    }
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private void removeBytesFromStart(ByteBuffer bf, int n) {
        int index = 0;
        for (int i = n; i < bf.position(); i++) {
            bf.put(index++, bf.get(i));
            bf.put(i, (byte) 0);
        }
        bf.position(index);
    }


    public void deviceProcessing() {
        while (!canceled) {
            if (writeQueue.size() > 0) { //тут еще проверку на минимальное число сканирований для пакета
                //есть что записать - пишем пока не запишем
                Package p = new Package();
                for (TicketScanResult result : writeQueue) {
                    p.getResults().add(result);
                }
                writePackage(p);
            } else {
                //читаем
                readResult();
            }
        }
    }
}
