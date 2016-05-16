package com.zabozhanov.tickets;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by z0rgoyok on 14.05.16.
 */
public class TicketApp extends Application {

    private static TicketApp mInstance;

    public static TicketApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

}
