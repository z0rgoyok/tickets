package com.zabozhanov.tickets.models;

import io.realm.RealmObject;

/**
 * Created by z0rgoyok on 16.05.16.
 */
public class TicketScanResult extends RealmObject {

    private Ticket ticket;
    private int ticketScanResult = Ticket.STATE_NEW;

    private byte deviceID;

    private long timestamp;

    private boolean direction;

    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(byte deviceID) {
        this.deviceID = deviceID;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public int getTicketScanResult() {
        return ticketScanResult;
    }

    public void setTicketScanResult(int ticketScanResult) {
        this.ticketScanResult = ticketScanResult;
    }
}
