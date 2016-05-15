package com.zabozhanov.tickets.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by z0rgoyok on 15.05.16.
 */
public class Event extends RealmObject {

    private RealmList<Ticket> tickets;

    @PrimaryKey
    private String name;

    public String getName() {
        return name;
    }

    public RealmList<Ticket> getTickets() {
        return tickets;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Event(String name) {
        this.name = name;
    }

    public Event() {
    }
}
