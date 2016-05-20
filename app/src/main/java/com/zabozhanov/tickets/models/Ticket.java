package com.zabozhanov.tickets.models;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by z0rgoyok on 13.05.16.
 */
public class Ticket extends RealmObject {

    public static final int STATE_NEW = 0;
    public static final int STATE_IN = 1;
    public static final int STATE_OUT = 2;

    //todo: не забыть сделать неуникальным (уникальный в рамках мероприятия)
    @PrimaryKey
    private long id;

    private int cost;

    private String type;

    private Event event;

    private String direction;

    private int state = STATE_NEW;

    private boolean sold;

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }


}
