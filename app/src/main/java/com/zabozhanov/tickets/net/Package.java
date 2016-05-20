package com.zabozhanov.tickets.net;

import com.zabozhanov.tickets.models.TicketScanResult;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by z0rgoyok on 18.05.16.
 */
public class Package extends RealmObject {
    private int id;
    private long length;
    private byte device;
    private boolean sended;
    RealmList<TicketScanResult> results;
}
