package com.zabozhanov.tickets.models;

import io.realm.RealmObject;

/**
 * Created by z0rgoyok on 18.05.16.
 */
public class Scanner extends RealmObject {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
