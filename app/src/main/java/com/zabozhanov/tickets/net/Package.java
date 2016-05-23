package com.zabozhanov.tickets.net;

import com.zabozhanov.tickets.models.TicketScanResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by z0rgoyok on 18.05.16.
 */
public class Package  {
    private int id;
    private long length;
    private byte device;
    private boolean sended;
    List<TicketScanResult> results;

    public Package() {
        results = new ArrayList<>();
    }

    public List<TicketScanResult> getResults() {
        return results;
    }
}
