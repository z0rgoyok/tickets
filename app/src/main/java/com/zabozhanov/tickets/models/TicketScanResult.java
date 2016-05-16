package com.zabozhanov.tickets.models;

import io.realm.RealmObject;

/**
 * Created by z0rgoyok on 16.05.16.
 */
public class TicketScanResult extends RealmObject {

    private Ticket ticket;

    private int ticketScanResult = Ticket.STATE_NEW;

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
